package Indexing.Index;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * takes fully parsed and stemmed documents and indexes them.
 * Calculates tf, tags terms to indicate their importance...
 * will index {@value #partialGroupSize} documents at a time.
 */
public class Indexer implements Runnable {

    //the size of the group of documents that will be indexed every time.
    private static final int partialGroupSize = 10;

    private String pathToOutputFolder;
    private BlockingQueue<TermDocument> stemmedTermDocumentsBuffer;
    private AIndexMaker mainIndex;
    //private boolean withSteming=false; @TODO seport this

    public Indexer(String pathToOutputFolder, BlockingQueue<TermDocument> stemmedTermDocumentsBuffer) {
        this.pathToOutputFolder = pathToOutputFolder;
        this.stemmedTermDocumentsBuffer = stemmedTermDocumentsBuffer;
        mainIndex = new MainIndexMaker();
}

    /**
     * takes fully parsed and stemmed documents and indexes them.
     * Calculates tf, tags terms to indicate their importance...
     * will index {@value #partialGroupSize} documents at a time.
     */
    private void index(){
        Boolean done = false;
        try {
            while (!done) {
                TermDocument document = stemmedTermDocumentsBuffer.take();
                mainIndex.addToIndex(document);
                if(document.getSerialID()==-1){
                    done=true;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        index();
    }


    //@TODO change it to getMainDictionary
    public Map<String , TempIndexEntry> getMainMap(){

        return ((MainIndexMaker)mainIndex).getTempDictionary();
    }

    public void merge(){
        ((MainIndexMaker)mainIndex).mergeIndex(getMainMap().keySet());
    }

}
