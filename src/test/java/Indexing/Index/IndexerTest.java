package Indexing.Index;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;
import Indexing.Parse;
import Indexing.ReadFile;
import Indexing.StringAccumulator;
import Indexing.TermAccumulator;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

class IndexerTest {

    ArrayBlockingQueue<Document> docs = new ArrayBlockingQueue<Document>(10);
    ArrayBlockingQueue<TermDocument> termDocs = new ArrayBlockingQueue<TermDocument>(10);
    private static final String pathToStopwords = "C:/Users/John/Google Drive/Documents/1Uni/Semester E/information retrieval 37214406/Assignements/Ass1/stop_words.txt";
    private static final String pathToTestResultsFolder = "C:\\Users\\John\\Downloads\\infoRetrieval/test results";
    private static final String pathToDocumentsFolder = "C:\\Users\\John\\Downloads\\infoRetrieval/corpus";

    @Test
    void testDictionarySizeFeasibilityTermsAsStrings(){
        Parse p = new Parse(Parse.getStopWords(pathToStopwords),
                docs, termDocs);
        Parse.debug = false;
        p.useStemming = true;
        Thread parser1 = new Thread(p);

        SortedSet<String> terms = new TreeSet<>();

        Thread termAccumulator = new Thread(new StringAccumulator(terms, termDocs));

        ReadFile rf = new ReadFile(pathToDocumentsFolder, docs);
        Thread reader = new Thread(rf);

        long startingHeapSize = Runtime.getRuntime().totalMemory();
        long startingFreeMemory = Runtime.getRuntime().freeMemory();
        long startingUsedMemory = startingHeapSize-startingFreeMemory;
        System.out.println("Test with just the terms as simple strings");
        System.out.println("Max heap size (MBytes): " + toMB(Runtime.getRuntime().maxMemory()));
        System.out.println("Heap size before start (MBytes): " + toMB(startingHeapSize));
        System.out.println("Available memory in heap (MBytes): " + toMB(startingFreeMemory));
        System.out.println("Memory in use before starting (MByte): " + toMB(startingUsedMemory));
        System.out.println("Starting test of max dictionary size...");

        termAccumulator.start();
        reader.start();
        parser1.start();

        try {
            parser1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("Dictionary is in memory.");
        System.out.println("total number of terms: " + terms.size());
        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));
        System.out.println("Available memory in heap (MBytes): " + toMB(Runtime.getRuntime().freeMemory()));
        long endingUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Estimated Total memory consumed by dictionary (MBytes): " + toMB(endingUsedMemory - startingUsedMemory ) );


    }

    @Test
    void testDictionarySizeFeasibilityJustTerms(){
        Parse p = new Parse(Parse.getStopWords(pathToStopwords),
                docs, termDocs);
        Parse.debug = false;
        p.useStemming = true;
        Thread parser1 = new Thread(p);

        SortedSet<Term> terms = new TreeSet<>();

        Thread termAccumulator = new Thread(new TermAccumulator(terms, termDocs));

        ReadFile rf = new ReadFile(pathToDocumentsFolder, docs);
        Thread reader = new Thread(rf);

        long startingHeapSize = Runtime.getRuntime().totalMemory();
        long startingFreeMemory = Runtime.getRuntime().freeMemory();
        long startingUsedMemory = startingHeapSize-startingFreeMemory;
        System.out.println("Test with just the terms");
        System.out.println("Max heap size (MBytes): " + toMB(Runtime.getRuntime().maxMemory()));
        System.out.println("Heap size before start (MBytes): " + toMB(startingHeapSize));
        System.out.println("Available memory in heap (MBytes): " + toMB(startingFreeMemory));
        System.out.println("Memory in use before starting (MByte): " + toMB(startingUsedMemory));
        System.out.println("Starting test of max dictionary size...");

        termAccumulator.start();
        reader.start();
        parser1.start();

        try {
            parser1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("Dictionary is in memory.");
        System.out.println("total number of terms: " + terms.size());
        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));
        System.out.println("Available memory in heap (MBytes): " + toMB(Runtime.getRuntime().freeMemory()));
        long endingUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Estimated Total memory consumed by dictionary (MBytes): " + toMB(endingUsedMemory - startingUsedMemory ) );


    }

    @Test
    void testDictionarySizeFeasibilityArrays(){
        Parse p = new Parse(Parse.getStopWords(pathToStopwords),
                docs, termDocs);
        Parse.debug = false;
        p.useStemming = true;
        Thread parser1 = new Thread(p);
        final int sizeOfPointerArrays = 50;

        SortedSet<Pair<Term, int[]>> terms = new TreeSet<>(Comparator.comparing(o -> o.getKey().toString()));

        Thread termAccumulator = new Thread(() -> {
            try {
                boolean done = false;
                while( !done){
                    TermDocument termDoc = termDocs.take();
                    if(termDoc.getText() == null){
                        done = true;
                    }
                    else{
                        for (Term t: termDoc.getText()
                                ) {
                            terms.add(new Pair<>(t, new int[sizeOfPointerArrays]));
                        }
                        for (Term t: termDoc.getTitle()
                                ) {
                            terms.add(new Pair<>(t, new int[sizeOfPointerArrays]));
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ReadFile rf = new ReadFile(pathToDocumentsFolder, docs);
        Thread reader = new Thread(rf);

        long startingHeapSize = Runtime.getRuntime().totalMemory();
        long startingFreeMemory = Runtime.getRuntime().freeMemory();
        long startingUsedMemory = startingHeapSize-startingFreeMemory;
        System.out.println("Test with pointer arrays. size: " + sizeOfPointerArrays);
        System.out.println("Max heap size (MBytes): " + toMB(Runtime.getRuntime().maxMemory()));
        System.out.println("Heap size before start (MBytes): " + toMB(startingHeapSize));
        System.out.println("Available memory in heap (MBytes): " + toMB(startingFreeMemory));
        System.out.println("Memory in use before starting (MByte): " + toMB(startingUsedMemory));
        System.out.println("Starting test of max dictionary size...");

        termAccumulator.start();
        reader.start();
        parser1.start();

        try {
            parser1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("Dictionary is in memory.");
        System.out.println("total number of terms: " + terms.size());
        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));
        System.out.println("Available memory in heap (MBytes): " + toMB(Runtime.getRuntime().freeMemory()));
        long endingUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Estimated Total memory consumed by dictionary (MBytes): " + toMB(endingUsedMemory - startingUsedMemory ) );


    }

    @Test
    void testDictionarySizeFeasibilityArraylists(){
        Parse p = new Parse(Parse.getStopWords(pathToStopwords),
                docs, termDocs);
        Parse.debug = false;
        p.useStemming = true;
        Thread parser1 = new Thread(p);
        final int sizeOfPointerLists = 25;

        SortedSet<Pair<Term, ArrayList<Pair<Short, Integer>>>> terms = new TreeSet<>(Comparator.comparing(o -> o.getKey().toString()));

        Thread termAccumulator = new Thread(() -> {
            try {
                boolean done = false;
                while( !done){
                    TermDocument termDoc = termDocs.take();
                    if(termDoc.getText() == null){
                        done = true;
                    }
                    else{
                        for (Term t: termDoc.getText()
                                ) {
                            terms.add(new Pair<>(t, new ArrayList<>(sizeOfPointerLists)));
                        }
                        for (Term t: termDoc.getTitle()
                                ) {
                            terms.add(new Pair<>(t, new ArrayList<>(sizeOfPointerLists)));
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ReadFile rf = new ReadFile(pathToDocumentsFolder, docs);
        Thread reader = new Thread(rf);

        long startingHeapSize = Runtime.getRuntime().totalMemory();
        long startingFreeMemory = Runtime.getRuntime().freeMemory();
        long startingUsedMemory = startingHeapSize-startingFreeMemory;
        System.out.println("Test with pointer lists(list of pairs). size: " + sizeOfPointerLists);
        System.out.println("Max heap size (MBytes): " + toMB(Runtime.getRuntime().maxMemory()));
        System.out.println("Heap size before start (MBytes): " + toMB(startingHeapSize));
        System.out.println("Available memory in heap (MBytes): " + toMB(startingFreeMemory));
        System.out.println("Memory in use before starting (MByte): " + toMB(startingUsedMemory));
        System.out.println("Starting test of max dictionary size...");

        termAccumulator.start();
        reader.start();
        parser1.start();

        try {
            parser1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("Dictionary is in memory.");
        System.out.println("total number of terms: " + terms.size());
        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));
        System.out.println("Available memory in heap (MBytes): " + toMB(Runtime.getRuntime().freeMemory()));
        long endingUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Estimated Total memory consumed by dictionary (MBytes): " + toMB(endingUsedMemory - startingUsedMemory ) );


    }

    @Test
    void testDictionarySizeFeasibilityIndexEntry(){
        Parse p = new Parse(Parse.getStopWords(pathToStopwords),
                docs, termDocs);
        Parse.debug = false;
        p.useStemming = true;
        Thread parser1 = new Thread(p);
        final int sizeOfPointerLists = 50;

        SortedSet<IndexEntry> terms = new TreeSet<>(Comparator.comparing(IndexEntry::getTerm));

        Thread termAccumulator = new Thread(() -> {
            try {
                boolean done = false;
                while( !done){
                    TermDocument termDoc = termDocs.take();
                    if(termDoc.getText() == null){
                        done = true;
                    }
                    else{
                        for (Term t: termDoc.getText()
                                ) {
                            terms.add(new IndexEntry(t.toString(), 1, sizeOfPointerLists));
                        }
                        for (Term t: termDoc.getTitle()
                                ) {
                            terms.add(new IndexEntry(t.toString(), 1, sizeOfPointerLists));
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ReadFile rf = new ReadFile(pathToDocumentsFolder, docs);
        Thread reader = new Thread(rf);

        long startingHeapSize = Runtime.getRuntime().totalMemory();
        long startingFreeMemory = Runtime.getRuntime().freeMemory();
        long startingUsedMemory = startingHeapSize-startingFreeMemory;
        System.out.println("        Test with IndexEntry(with array of size: " + sizeOfPointerLists + ")");
        System.out.println("Max heap size (MBytes): " + toMB(Runtime.getRuntime().maxMemory()));
        System.out.println("Heap size before start (MBytes): " + toMB(startingHeapSize));
        System.out.println("Available memory in heap (MBytes): " + toMB(startingFreeMemory));
        System.out.println("Memory in use before starting (MByte): " + toMB(startingUsedMemory));
        System.out.println("    Starting test of max dictionary size...");

        termAccumulator.start();
        reader.start();
        parser1.start();

        try {
            parser1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("");
        System.out.println("    Dictionary is in memory.");
        System.out.println("total number of terms: " + terms.size());
        System.out.println("Current heap size (MBytes): " + toMB(Runtime.getRuntime().totalMemory()));
        System.out.println("Available memory in heap (MBytes): " + toMB(Runtime.getRuntime().freeMemory()));
        System.out.println("");
        long endingUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Estimated Total memory consumed by dictionary (MBytes): " + toMB(endingUsedMemory - startingUsedMemory ) );
        long timeBeforeGC = System.currentTimeMillis();
        System.gc();
        long timeAfterGC = System.currentTimeMillis();
        endingUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Estimated Total memory consumed by dictionary after garbage collection (more accurate) (MBytes): " + toMB(endingUsedMemory - startingUsedMemory ) );
        System.out.println("GC time (ms) = " + (timeAfterGC-timeBeforeGC));
    }

    public static double toMB(long bytes){
        return bytes/(Math.pow(2, 20));
    }
}