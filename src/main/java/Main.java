import Elements.Document;
import Elements.Term;
import Indexing.Indexer;
import Indexing.Parse;
import Indexing.ReadFile;
import Indexing.Stemmer;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    // GLOBAL PARAMETERS
    private static final int documentBufferSize = 10;
    private static final int termBufferSize = 10;
    private static final int stemmedTermBufferSize = 10;
    private static final String pathToDocumentsFolder = "/documents"; //TODO temporary! should come from UI
    private static final String pathToStopwordsFile = "/stopwords"; //TODO temporary! should come from UI
    private static final String pathToOutputFolder = "/output"; //TODO temporary! should come from UI

    public static void main(String[] args){
        createIndex();
    }

    private static void createIndex() {

        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<List<Term>> termListsBuffer = new ArrayBlockingQueue<List<Term>>(termBufferSize);
        BlockingQueue<Term> stemmedTermListsBuffer = new ArrayBlockingQueue<Term>(stemmedTermBufferSize);


        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));
        tReader.start();

        Thread tParser = new Thread(new Parse(pathToStopwordsFile, documentBuffer, termListsBuffer));
        tParser.start();

        Thread tStemmer = new Thread(new Stemmer(termListsBuffer, stemmedTermListsBuffer));
        tStemmer.start();

        Thread tIndexer = new Thread(new Indexer(pathToOutputFolder, stemmedTermListsBuffer));
        tIndexer.start();

    }
}
