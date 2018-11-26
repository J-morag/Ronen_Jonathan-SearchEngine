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

    private static final int documentBufferSize = 500;
    private static final int termBufferSize = 500;
    private static final int stemmedTermBufferSize = 500;

    //private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Documents\\לימודים\\שנה ג\\איחזור מידע\\עבודות\\מסמכים מנוע חיפוש\\corpus"; //TODO temporary! should come from UI
    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Desktop\\FB396001";
    @Test
    void testMainIndex() throws InterruptedException {

        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);



        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));

        HashSet<String> stopwords = Parse.getStopWords("");
        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer));
        Indexer indexer =new Indexer("//",termDocumentsBuffer);
        Thread tIndexer = new Thread(indexer);

        long start=System.currentTimeMillis();


        tReader.start();

        tParser.start();

        tIndexer.start();
        tIndexer.join();
        System.out.println(((double) System.currentTimeMillis()-start)/1000);


        indexer.merge();

        Map<String,TempIndexEntry> map = indexer.getMainMap();
/*
        for (Term term : map.keySet())
        {
            System.out.println(map.get(term).getPointerList()+"\n");
        }
   */

        String path = "C:\\Users\\ronen\\Desktop\\a.txt";

        try {
            File file = new File(path);
            OutputStream fo = new FileOutputStream(file);


        for (String term : map.keySet()) {
             //(term+"->"+map.get(term).getPosting()+"\n");
            fo.write((term+"->"+map.get(term).getPointerList()+"\n").getBytes());//term+"->"+map.get(term).getPosting()+"\n").getBytes());
            //System.out.println(map.get(term).getPointerList()+"\n");
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