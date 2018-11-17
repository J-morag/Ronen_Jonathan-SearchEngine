package Indexing.Index;

import Elements.TermDocument;

import java.util.List;
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

    public Indexer(String pathToOutputFolder, BlockingQueue<TermDocument> stemmedTermDocumentsBuffer) {
        this.pathToOutputFolder = pathToOutputFolder;
        this.stemmedTermDocumentsBuffer = stemmedTermDocumentsBuffer;
}

    /**
     * takes fully parsed and stemmed documents and indexes them.
     * Calculates tf, tags terms to indicate their importance...
     * will index {@value #partialGroupSize} documents at a time.
     */
    private void index(){
        //TODO not implemented
    }

    public void run() {
        index();
    }

    private class indexEntry{
        int numOfDocs = 0;
        String term;
        String postingsFileName;
        int[] newPostings; //TODO cahnge from int to some data structure

        public indexEntry(String term, String postingsFileName) {
            this.term = term;
            this.postingsFileName = postingsFileName;
            this.newPostings = new int[partialGroupSize];
        }
    }
}
