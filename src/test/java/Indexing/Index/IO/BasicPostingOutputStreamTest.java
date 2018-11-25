package Indexing.Index.IO;

import Indexing.Index.Posting;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BasicPostingOutputStreamTest {

    private static final String outputPath = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\BasicPostingOutputStreamTest";
    private static RandomAccessFile raf;

    static {
        try {
            raf = new RandomAccessFile(outputPath, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    IPostingOutputStream out = new BasicPostingOutputStream(raf);

    void clean() throws IOException {
        raf.setLength(0);
    }

    @Test
    void writePostings() throws IOException {
        Posting[] postings = new Posting[2];
        postings[0] = new Posting("dco01", (short)13, (short)78, (short)1900, "beer sheva", "hebrew", true, false);
        postings[1] = new Posting("dco22", (short)2349, (short)0, (short)1910, "York", "English", false, false);

        out.write(postings);
        out.write(postings);

        Scanner scanner = new Scanner(new File(outputPath));

        while (scanner.hasNext()){
            System.out.println(scanner.nextLine());
        }

        out.close();
    }

    @Test
    void writeln() throws IOException {
        Posting p  = new Posting("doc01", (short)13, (short)78, (short)1900, "Beer Sheva", "hebrew", true, false);

        clean();

        out.writeln(p);
        out.writeln(p);
        out.writeln(p);

        raf.seek(0);
        String line = raf.readLine();
        while (null != line){
            System.out.println(line);
            System.out.println("length: " + line.length());
            line = raf.readLine();
        }

        out.close();
    }

    @Test
    void write() throws IOException {
        Posting p  = new Posting("dco01", (short)13, (short)78, (short)1900, "Beer Sheva", "hebrew", true, false);

        clean();

        out.write(p);
        out.write(p);
        out.writeln(p);

        Scanner scanner = new Scanner(new File(outputPath));

        while (scanner.hasNext()){
            String line = scanner.nextLine();
            System.out.println(line);
            System.out.println("length: " + line.length());
        }

        out.close();
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
        System.out.println("time for 100,000 terms with 100 postings each, fifty times (m): " + (time)*50/1000/60);

    }
}