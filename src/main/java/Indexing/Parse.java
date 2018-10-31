package Indexing;

import Elements.Document;
import Elements.Term;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * takes Documents, tokenizes and parses them. does not perform stemming.
 */
public class Parse implements Runnable{

    private String pathTostopwordsFile;
    private BlockingQueue<Document> sourceDocumentsQueue;
    private BlockingQueue<List<Term>> sinkTermLists;

    /**
     * @param sourceDocumentsQueue - a blocking queue of documents to parse. End of queue will be marked by a "poison" Document with null text field.
     * @param sinkTermLists - a blocking queue to be filled with lists of Term. Each List representing the Terms from a single documents.
     *                     End of queue will be marked by a "poison" List with just a null Term.
     */
    public Parse(String pathTostopwordsFile, BlockingQueue<Document> sourceDocumentsQueue, BlockingQueue<List<Term>> sinkTermLists) {
        this.pathTostopwordsFile = pathTostopwordsFile;
        this.sourceDocumentsQueue = sourceDocumentsQueue;
        this.sinkTermLists = sinkTermLists;
    }

    /**
     * takes Documents, tokenizes and parses them. does not perform stemming.
     * End of queue will be marked by a "poison" List with just a null Term.
     */
    public void parse(){

        //TODO not implemented
    }

    public void run() {
        parse();
    }
}
