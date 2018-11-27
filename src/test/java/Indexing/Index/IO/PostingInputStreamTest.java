package Indexing.Index.IO;

import Indexing.Index.Posting;
import javafx.geometry.Pos;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PostingInputStreamTest {


    static final String path = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\PostingInputStreamTest";
    IPostingOutputStream out;
    IPostingInputStream in;


    public PostingInputStreamTest() throws IOException {

        try {
            out = new PostingOutputStream(path);
            in = new PostingInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shortTest() throws IOException {
        int numPostings = 2;
        ArrayList<Posting> postingsForOneTerm = new ArrayList<>(numPostings);
        Posting p1 = new Posting(66, (short)13, true, false);
        Posting p2 = new Posting(129, (short)28, false, false);
        postingsForOneTerm.add(p1);
        postingsForOneTerm.add(p2);

        long pointer = out.write(postingsForOneTerm);
        out.flush();
        out.close();

        List<Posting> postingsIn = in.readTermPostings(pointer);

        for (int i = 0; i < postingsForOneTerm.size() ; i++) {
            assertEquals(postingsForOneTerm.get(i), postingsIn.get(i));
            System.out.println(postingsIn.get(i));
        }

    }


    @Test
    void multiTerm() throws IOException {
        Random random = new Random();
        int numTerms = 4;
        ArrayList<ArrayList<Posting>> termsOut = new ArrayList<>(4);
        for (int i = 0; i <  numTerms ; i++) {
            int numPostings = random.nextInt(100);
            termsOut.add(new ArrayList<>());
            for (int j = 0; j < numPostings ; j++) {
                termsOut.get(i).add(new Posting((short)random.nextInt(Integer.MAX_VALUE), (short)random.nextInt(Short.MAX_VALUE), random.nextBoolean(), random.nextBoolean()));
            }
        }

        long[] pointers = new long[numTerms];

        for (int i = 0; i < numTerms ; i++) {
            pointers[i] = out.write(termsOut.get(i));
        }

        out.flush();
        out.close();

        List<Posting>[] termsIn = new List[numTerms];
        for (int i = 0; i < numTerms ; i++) {
            termsIn[i] = in.readTermPostings(pointers[i]);
        }

        for (int i = 0; i < numTerms ; i++) {
            List<Posting> postingsIn = termsIn[i];
            System.out.println(termsOut.get(i));
            System.out.println(termsIn[i]);
            assertArrayEquals(postingsIn.toArray(), termsOut.get(i).toArray());
        }


    }
}