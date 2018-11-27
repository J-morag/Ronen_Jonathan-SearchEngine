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

    private static final int documentBufferSize = 30;
    private static final int termBufferSize = 30;
    private static final int stemmedTermBufferSize = 30;

    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Documents\\לימודים\\שנה ג\\איחזור מידע\\עבודות\\מסמכים מנוע חיפוש\\corpus"; //TODO temporary! should come from UI
    //private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Desktop\\FB396001";
    private static final String pathToStopWordRONEN ="C:\\Users\\ronen\\Desktop\\stopWords.txt";

    private static final String pathToDocumentsFolderAtJM = "C:/Users/John/Downloads/infoRetrieval/corpus";
    private static final String patToStopwordsFileAtJM = "C:/Users/John/Google Drive/Documents/1Uni/Semester E/information retrieval 37214406/Assignements/Ass1/stop_words.txt";
    private static final String pathToOutputFolderAtJM = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\indexing";

    @Test
    void testMainIndex() throws InterruptedException {

        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);



        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));

        HashSet<String> stopwords = Parse.getStopWords(pathToStopWordRONEN);
        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer));
        Indexer indexer =new Indexer("C:\\Users\\ronen\\Desktop\\test",termDocumentsBuffer,true);
        Thread tIndexer = new Thread(indexer);

        long start=System.currentTimeMillis();


        tReader.start();

        tParser.start();

        tIndexer.start();
        tIndexer.join();
        System.out.println(((double) System.currentTimeMillis()-start)/1000);


        indexer.mergeMainIndex();

        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));

        Map<String,TempIndexEntry> map = indexer.getMainMap();
        indexer=null;
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
            int [] pointer=map.get(term).getPointerList();
            fo.write((term+"->[").getBytes());
            for (int i = 0; i <pointer.length ; i++) {
                if(i+1<pointer.length)
                    fo.write((pointer[i]+",").getBytes());
                else
                    fo.write((pointer[i]+"").getBytes());
            }
            fo.write(("]\n").getBytes());
            //fo.write((term+"->"+map.get(term).getPointerList()[0]+"\n").getBytes());//term+"->"+map.get(term).getPosting()+"\n").getBytes());
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

    @Test
    void testMainIndexClone() throws InterruptedException {

        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);



        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolderAtJM, documentBuffer));

        HashSet<String> stopwords = Parse.getStopWords(patToStopwordsFileAtJM);
        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer));
        Indexer indexer =new Indexer(pathToOutputFolderAtJM,termDocumentsBuffer,true);
        Thread tIndexer = new Thread(indexer);

        long start=System.currentTimeMillis();


        tReader.start();

        tParser.start();

        tIndexer.start();
        tIndexer.join();
        System.out.println(((double) System.currentTimeMillis()-start)/1000);


        indexer.mergeMainIndex();

        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));
//
//        Map<String,TempIndexEntry> map = indexer.getMainMap();
///*
//        for (Term term : map.keySet())
//        {
//            System.out.println(map.get(term).getPointerList()+"\n");
//        }
//   */
//
//        String path = "C:\\Users\\ronen\\Desktop\\a.txt";
//
//        try {
//            File file = new File(path);
//            OutputStream fo = new FileOutputStream(file);
//
//
//            for (String term : map.keySet()) {
//                //(term+"->"+map.get(term).getPosting()+"\n");
//                fo.write((term+"->"+map.get(term).getPointerList()+"\n").getBytes());//term+"->"+map.get(term).getPosting()+"\n").getBytes());
//                //System.out.println(map.get(term).getPointerList()+"\n");
//            }
//            fo.flush();
//            fo.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }




    public static double toMB(long bytes){
        return bytes/(Math.pow(2, 20));
    }


}
