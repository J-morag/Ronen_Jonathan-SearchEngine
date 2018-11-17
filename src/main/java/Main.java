import Elements.Document;
import Elements.TermDocument;
import Indexing.Index.Indexer;
import Indexing.Parse;
import Indexing.ReadFile;

import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    // GLOBAL PARAMETERS
    private static final int documentBufferSize = 1000000;
    private static final int termBufferSize = 10;
    private static final int stemmedTermBufferSize = 10;

    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Desktop\\FB396001"; //TODO temporary! should come from UI
    private static final String pathToStopwordsFile = "/stopwords"; //TODO temporary! should come from UI
    private static final String pathToOutputFolder = "/output"; //TODO temporary! should come from UI

    public static void main(String[] args) throws InterruptedException {
        createIndex();
    }

    private static void createIndex() throws InterruptedException {

        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);


        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));
        tReader.start();

        HashSet<String> stopwords = Parse.getStopWords(pathToStopwordsFile);

        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer));
        tParser.start();

        Thread tIndexer = new Thread(new Indexer(pathToOutputFolder, stemmedTermDocumentsBuffer));
        tIndexer.start();
    }

}
