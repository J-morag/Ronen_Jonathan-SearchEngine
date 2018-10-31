package Indexing;

import Elements.Term;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * stems terms using Porter's Stemmer.
 */
public class Stemmer implements Runnable {

    BlockingQueue<List<Term>> termListsBuffer;
    BlockingQueue<Term> stemmedTermListsBuffer;

    /**
     *
     * @param termListsBuffer - a buffer of parsed document's terms to be stemmed
     * @param stemmedTermListsBuffer - a buffer of document's stemmed terms.
     */
    public Stemmer(BlockingQueue<List<Term>> termListsBuffer, BlockingQueue<Term> stemmedTermListsBuffer) {
        this.termListsBuffer = termListsBuffer;
        this.stemmedTermListsBuffer = stemmedTermListsBuffer;
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
