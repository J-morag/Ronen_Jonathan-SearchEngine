package Indexing;

import Elements.Document;

import java.util.concurrent.BlockingQueue;

/**
 * Runnable.
 * Reads all the files in #pathToDocumentsFolder (recursively) into the given buffer.
 * When finished, will insert a Document with all fields equalling null, to represent "end of file".
 */
public class ReadFile implements Runnable{
    private String pathToDocumentsFolder;
    private BlockingQueue<Document> documentBuffer;

    /**
     *
     * @param pathToDocumentsFolder
     * @param documentBuffer - a blocking queue where parsed documents will be outputted for further processing.
     */
    public ReadFile(String pathToDocumentsFolder, BlockingQueue<Document> documentBuffer) {
        this.pathToDocumentsFolder = pathToDocumentsFolder;
        this.documentBuffer = documentBuffer;
    }

    /**
     * Reads all the files in #pathToDocumentsFolder (recursively) into the given buffer.
     * When finished, will insert a Document with all fields equalling null, to represent "end of file".
     */
    private void read(){
        //TODO not implemented
    }

    public void run() {
        read();
    }
}
