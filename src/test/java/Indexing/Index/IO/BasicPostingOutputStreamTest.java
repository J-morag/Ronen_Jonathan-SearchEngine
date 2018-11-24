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
        Posting p  = new Posting("dco01", (short)13, (short)78, (short)1900, "Beer Sheva", "hebrew", true, false);

        clean();

        out.writeln(p);
        out.writeln(p);
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
}