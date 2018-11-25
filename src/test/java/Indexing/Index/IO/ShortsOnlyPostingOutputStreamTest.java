package Indexing.Index.IO;

import Indexing.Index.Posting;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.*;

class ShortsOnlyPostingOutputStreamTest {

    private static final String outputPath = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\ShortsOnlyPostingOutputStreamTest";
    private static RandomAccessFile raf;

    static {
        try {
            raf = new RandomAccessFile(outputPath, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    IPostingOutputStream out = new ShortsOnlyPostingOutputStream(raf);

    void clean() throws IOException {
        raf.setLength(0);
    }

    @Test
    void testTime() throws IOException {
        Posting[] postingsForOneTerm = new Posting[100];
        Posting p1 = new Posting("dco01", (short)13, (short)78, (short)1900, "beer sheva", "hebrew", true, false);
        Posting p2 = new Posting("dco22", (short)2349, (short)0, (short)1910, "York", "English", false, false);
        for (int i = 0; i <50 ; i++) {
            postingsForOneTerm[i] = p1;
        }
        for (int i = 50; i <postingsForOneTerm.length ; i++) {
            postingsForOneTerm[i] = p2;
        }

        clean();

        long startTime = System.currentTimeMillis();

        for (int j = 0; j <100000 ; j++) {
                out.write(postingsForOneTerm);
        }

        long time = (System.currentTimeMillis() - startTime);

        System.out.println("time for 100,000 terms with 100 postings each (ms): " + (time));
        System.out.println("time for 100,000 terms with 100 postings each, fifty times (m): " + ((time)*50/1000)/60);

    }



}