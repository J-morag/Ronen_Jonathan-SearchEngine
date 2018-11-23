package Indexing.Index;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;
import Indexing.Parse;
import Indexing.ReadFile;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MainIndexerTest {

    private static final int documentBufferSize = 1000000;
    private static final int termBufferSize = 100000000;
    private static final int stemmedTermBufferSize = 10000000;

    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Desktop\\FB396001"; //TODO temporary! should come from UI

    @Test
    void testMainIndex() throws InterruptedException {
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);


        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));
        tReader.start();

        HashSet<String> stopwords = Parse.getStopWords("");

        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer));
        tParser.start();
        tParser.join();
        MainIndexMaker mainIndexMaker = new MainIndexMaker();
        for (TermDocument doc : termDocumentsBuffer) {
            if(doc!=null){
                mainIndexMaker.addToIndex(doc);
            }else {
                break;
            }

        }

        Map<Term,TempIndexEntry> map = mainIndexMaker.getTempDictionary();

        String path = "C:\\Users\\ronen\\Desktop\\test.txt";
        try {
            File file = new File(path);
            OutputStream fo = new FileOutputStream(file);


        for (Term term : map.keySet()) {
             //(term+"->"+map.get(term).getPosting()+"\n");
            fo.write((term+"->"+map.get(term).getPosting()+"\n").getBytes());
        }
        fo.flush();
        fo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
