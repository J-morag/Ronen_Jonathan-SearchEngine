package GUI;

import Elements.Document;
import Elements.TermDocument;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IndexEntry;
import Indexing.Index.Indexer;
import Indexing.Parse;
import Indexing.ReadFile;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Model {

    // GLOBAL PARAMETERS
    private static final int documentBufferSize = 3;
    private static final int termBufferSize = 3;

    private Controller controller;
    private Map<String, IndexEntry> mainDictionaryWithStemming;
    private Map<Integer, DocIndexEntery> docDictionaryWithStemming;
    private Map<String, IndexEntry> mainDictionaryNoStemming;
    private Map<Integer, DocIndexEntery> docDictionaryNoStemming;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Set<String> getLanguages(){
        return null; //TODO implement
    }

    public Map<String, IndexEntry> getDictionary(boolean useStemming) {
        return useStemming ? mainDictionaryWithStemming : mainDictionaryNoStemming;
//        HashMap<String, IndexEntry> map = new HashMap<>();
//        Random rnd = new Random();
//        for (int i = 0; i < 60 ; i++) {
//            map.put("test" + i, new IndexEntry( rnd.nextInt(30000), rnd.nextInt(30000), rnd.nextInt(30000)));
//
//        }
//        return map;
    }

    public void loadDictionary(boolean useStemming, String outputFolder) throws IOException, ClassNotFoundException, ClassCastException {
        ObjectInputStream inDictionary = new ObjectInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.dictionarySaveName ));
        if(useStemming)
            this.mainDictionaryWithStemming = (Map<String, IndexEntry>) inDictionary.readObject();
        else
            this.mainDictionaryNoStemming = (Map<String, IndexEntry>) inDictionary.readObject();

    }

    public void reset(String outputFolder) {
        mainDictionaryWithStemming = null;
        docDictionaryWithStemming = null;
        mainDictionaryNoStemming = null;
        docDictionaryNoStemming  = null;
        
        cleanOutputFiles(outputFolder);
    }


    /**
     * deletes everything in the folder and everything in the folder's subfolders (one layer down).
     * @param outputFolder
     */
    private void cleanOutputFiles(String outputFolder) {
        File dir1 = new File(outputFolder + '/' + Indexer.withStemmingOutputFolderName);
        File dir2 = new File(outputFolder + '/' + Indexer.noStemmingOutputFolderName);
        if(dir1.exists()){
            for(File file : dir1.listFiles()){
                file.delete();
            }
            dir1.delete();
        }
        if(dir2.exists()){
            for(File file : dir2.listFiles()){
                file.delete();
            }
            dir2.delete();
        }
    }

    public String generateIndex(boolean useStemming, String corpusLocation, String outputLocation, String stopwordsLocation) throws InterruptedException {
        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);

        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(corpusLocation, documentBuffer));
        HashSet<String> stopwords = Parse.getStopWords(stopwordsLocation);
        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer, useStemming));
        Indexer indexer = new Indexer(outputLocation, termDocumentsBuffer,useStemming);
        Thread tIndexer = new Thread(indexer);

        long time = System.currentTimeMillis();

        tReader.start();
        tParser.start();
        tIndexer.start();

        tIndexer.join();
        indexer.mergeMainIndex();

        if(useStemming){
            this.mainDictionaryWithStemming = indexer.getMainMap();
            this.docDictionaryWithStemming = indexer.getDocsMap();
        }
        else{
            this.mainDictionaryNoStemming = indexer.getMainMap();
            this.docDictionaryNoStemming = indexer.getDocsMap();
        }

        int numIndexedDocs = indexer.getNumIndexedDocs();
        int numUniqueTerms = useStemming ? mainDictionaryWithStemming.size() : mainDictionaryNoStemming.size();
        time = (System.currentTimeMillis() - time)/1000;

        return "Number of indexed documents = " + numIndexedDocs + "\n" +
                "Number of unique terms = " + numUniqueTerms + "\n" +
                "Total time (seconds) = " + time ;
    }
}
