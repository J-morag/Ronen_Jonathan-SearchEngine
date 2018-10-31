package Indexing;

import Elements.Term;

import java.util.concurrent.BlockingQueue;

public class Indexer implements Runnable {

    private String pathToOutputFolder;
    private BlockingQueue<Term> stemmedTermListsBuffer;

    public Indexer(String pathToOutputFolder, BlockingQueue<Term> stemmedTermListsBuffer) {
        this.pathToOutputFolder = pathToOutputFolder;
        this.stemmedTermListsBuffer = stemmedTermListsBuffer;
    }

    private void index(){
        //TODO not implemented
    }

    public void run() {
        index();
    }
}
