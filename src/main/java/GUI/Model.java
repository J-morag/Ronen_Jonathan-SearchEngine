package GUI;

import Indexing.DocumentProcessing.Document;
import Indexing.DocumentProcessing.TermDocument;
import Indexing.Index.CityIndexEntry;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IndexEntry;
import Indexing.Index.Indexer;
import Indexing.DocumentProcessing.Parse;
import Indexing.DocumentProcessing.ReadFile;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * MVC model.
 * holds program state and logic.
 */
public class Model {

    // GLOBAL PARAMETERS
    private static final int documentBufferSize = 3;
    private static final int termBufferSize = 3;

    private Controller controller;
    private Map<String, IndexEntry> mainDictionaryWithStemming;
    private Map<Integer, DocIndexEntery> docDictionaryWithStemming;
    private Map<String, IndexEntry> mainDictionaryNoStemming;
    private Map<Integer, DocIndexEntery> docDictionaryNoStemming;
    private Map<String , CityIndexEntry> cityDictionary;
    private Set<String> languages;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Set<String> getLanguages(){
        return null; //TODO implement
    }

    public Map<String, IndexEntry> getDictionary(boolean useStemming) {
        return useStemming ? mainDictionaryWithStemming : mainDictionaryNoStemming;
    }

    public void loadDictionary(boolean useStemming, String outputFolder) throws IOException, ClassNotFoundException, ClassCastException {
        ObjectInputStream inDictionary = new ObjectInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.dictionarySaveName ));
        ObjectInputStream inDocDictionary = new ObjectInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.docsDictionaryName ));
        ObjectInputStream inCityDictionay = new ObjectInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.cityDictionaryName));
        ObjectInputStream inLanguages = new ObjectInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.languages));

        if(useStemming){
            this.mainDictionaryWithStemming = (Map<String, IndexEntry>) inDictionary.readObject();
            this.docDictionaryWithStemming = (Map<Integer, DocIndexEntery>) inDocDictionary.readObject();
        }
        else{
            this.mainDictionaryNoStemming = (Map<String, IndexEntry>) inDictionary.readObject();
            this.docDictionaryNoStemming = (Map<Integer, DocIndexEntery>) inDocDictionary.readObject();
        }
        this.cityDictionary = (Map<String , CityIndexEntry>) inCityDictionay.readObject();
        this.languages = (Set<String>) inLanguages.readObject();

    }

    public void reset(String outputFolder) {
        mainDictionaryWithStemming = null;
        docDictionaryWithStemming = null;
        mainDictionaryNoStemming = null;
        docDictionaryNoStemming  = null;
        
        cleanOutputFiles(outputFolder);
    }


    /**
     * within the given directory, deletes everything within folders created by this program, and the folders themselves.
     * @param outputFolder - the folder where program output was written, and is now to be deleted.
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

        time = (System.currentTimeMillis() - time)/1000;

        if(useStemming){
            this.mainDictionaryWithStemming = indexer.getMainMap();
            this.docDictionaryWithStemming = indexer.getDocsMap();
        }
        else{
            this.mainDictionaryNoStemming = indexer.getMainMap();
            this.docDictionaryNoStemming = indexer.getDocsMap();
        }
        this.cityDictionary = indexer.getCityMap();
        this.languages = indexer.getLanguages();

        int numIndexedDocs = indexer.getNumIndexedDocs();
        int numUniqueTerms = useStemming ? mainDictionaryWithStemming.size() : mainDictionaryNoStemming.size();

        return "Number of indexed documents = " + numIndexedDocs + "\n" +
                "Number of unique terms = " + numUniqueTerms + "\n" +
                "Total time (seconds) = " + time ;
    }
}
