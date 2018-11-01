package Indexing;

import Elements.TermDocument;

import java.util.concurrent.BlockingQueue;

/**
 * stems terms using Porter's Stemmer.
 */
public class Stemmer implements Runnable {

    BlockingQueue<TermDocument> termDocumentsBuffer;
    BlockingQueue<TermDocument> stemmedTermDocumentsBuffer;

    /**
     *
     * @param termDocumentsBuffer - a buffer of parsed document's terms to be stemmed
     * @param stemmedTermDocumentsBuffer - a buffer of document's stemmed terms.
     */
    public Stemmer(BlockingQueue<TermDocument> termDocumentsBuffer, BlockingQueue<TermDocument> stemmedTermDocumentsBuffer) {
        this.termDocumentsBuffer = termDocumentsBuffer;
        this.stemmedTermDocumentsBuffer = stemmedTermDocumentsBuffer;
    }

    /**
     * stems terms using Porter's Stemmer.
     */
    private void stem(){
        //TODO not implemented
    }

    public void run() {
        stem();
    }
}
