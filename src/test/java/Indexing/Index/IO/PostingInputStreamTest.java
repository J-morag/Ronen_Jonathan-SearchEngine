package Indexing.Index.IO;

import Indexing.Index.Posting;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

}