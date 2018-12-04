package GUI;

import Indexing.DocumentProcessing.Document;
import Indexing.DocumentProcessing.TermDocument;
import Indexing.Index.CityIndexEntry;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IndexEntry;
import Indexing.Index.Indexer;
import Indexing.DocumentProcessing.Parse;
import Indexing.DocumentProcessing.ReadFile;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

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
    private ExecutorService threadPool;
    private Thread.UncaughtExceptionHandler exceptionHandler;
    private boolean isExceptionThrownDuringGeneration = false;
    private Exception exceptionThrownDuringGeneration;


    public Model(){
        exceptionHandler = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                isExceptionThrownDuringGeneration = true;
                threadPool.shutdownNow();
            }
        };
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Set<String> getLanguages(){
        return languages;
    }

    public Map<String, IndexEntry> getDictionary(boolean useStemming) {
        return useStemming ? mainDictionaryWithStemming : mainDictionaryNoStemming;
    }

    public void loadDictionary(boolean useStemming, String outputFolder) throws IOException, ClassNotFoundException, ClassCastException {
        ObjectInputStream inDictionary = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' + (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.dictionarySaveName )));
        ObjectInputStream inDocDictionary = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.docsDictionaryName )));
        ObjectInputStream inCityDictionay = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.cityDictionaryName)));
        ObjectInputStream inLanguages = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.languages)));

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
        cityDictionary = null;
        languages = null;
        
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

    public String generateIndex(boolean useStemming, String corpusLocation, String outputLocation, String stopwordsLocation) throws Exception {
        //setup
        setupToGenerateIndex(useStemming);
        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);

        //  Worker Threads:
        //reading
        threadPool.submit(new Thread(new ReadFile(corpusLocation, documentBuffer)));
        HashSet<String> stopwords = Parse.getStopWords(stopwordsLocation);
        //parsing
        threadPool.submit(new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer, useStemming)));
        //indexing
        Indexer indexer = new Indexer(outputLocation, termDocumentsBuffer,useStemming);
        threadPool.submit(new Thread(indexer));

        long time = System.currentTimeMillis();

        //wait for indexing to finish
        threadPool.shutdown();
        boolean timeoutReached = !(threadPool.awaitTermination(60, TimeUnit.MINUTES));

        time = (System.currentTimeMillis() - time)/1000;

        return handleNewIndexGeneration(indexer, useStemming, time, timeoutReached);
    }

    public String generateIndexTwoPhase(final boolean useStemming, final String corpusLocation, final String outputLocation, String stopwordsLocation) throws Exception {
        //setup
        setupToGenerateIndex(useStemming);
        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);

        long time = System.currentTimeMillis();

        //  Worker Threads:
        //reading
        threadPool.submit(new Thread(new ReadFile(corpusLocation, documentBuffer)));
        HashSet<String> stopwords = Parse.getStopWords(stopwordsLocation);
        //parsing
        threadPool.submit(new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer, useStemming)));

        // saving termDocs to file
        Thread termDocsToFile = new Thread(() -> {
            final String filePath = outputLocation +"\\tmp_termDocuments";
            try {
                ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath, true )));
                Boolean done = false;
                while (!done) { //extract from buffer until poison element is encountered
                    TermDocument currDoc = null;
                    try {
                        currDoc = termDocumentsBuffer.take();
                        if (null == currDoc.getText()) done=true; //end of files (poison element)
                        else {
                            out.writeObject(currDoc);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        threadPool.submit(termDocsToFile);

        // wait until all docs are parsed and written to file
        threadPool.shutdown();
        threadPool.awaitTermination(60, TimeUnit.MINUTES);
        threadPool = null;
        //clear memory
        System.gc();

        // read TermDocuments and feed them to indexer
        threadPool = Executors.newFixedThreadPool(4);
        Thread fileToTermDocs = new Thread(() -> {
            final String filePath = outputLocation +"\\tmp_termDocuments";
            try {
                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath )));
                TermDocument doc = (TermDocument) in.readObject();
                while (doc != null) { //insert to buffer until file end
                    try {
                        doc = (TermDocument) in.readObject();
                        doc.language = doc.language.intern();
                        termDocumentsBuffer.put(doc);
                    }catch(Exception e){
                        doc = null;
                    }
                }
                // poison element at end
                TermDocument poison = new TermDocument(-1,null);
                termDocumentsBuffer.put(poison);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPool.submit(fileToTermDocs);

        //indexing
        Indexer indexer = new Indexer(outputLocation, termDocumentsBuffer,useStemming);
        threadPool.submit(new Thread(indexer));

        //wait for indexing to finish
        threadPool.shutdown();
        boolean timeoutReached = !(threadPool.awaitTermination(60, TimeUnit.MINUTES));

        time = (System.currentTimeMillis() - time)/1000;

        return handleNewIndexGeneration(indexer, useStemming, time, timeoutReached);
    }

    private void setupToGenerateIndex(boolean useStemming){
        threadPool = Executors.newFixedThreadPool(4);
        cityDictionary = null;
        languages = null;
        mainDictionaryWithStemming = null;
        docDictionaryWithStemming = null;
        mainDictionaryNoStemming = null;
        docDictionaryNoStemming  = null;
    }

    private String handleNewIndexGeneration(Indexer indexer, boolean useStemming, long time, boolean timeoutReached) throws Exception {
        if(timeoutReached) throw new Exception("Timeout reached during execution");
        else if(this.isExceptionThrownDuringGeneration){
            isExceptionThrownDuringGeneration = false;
            throw exceptionThrownDuringGeneration;
        }

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
