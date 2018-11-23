package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.RandomAccessFile;

public class BasicPostingOutputStream extends APostingOutputStream {

    //TESTING
    int dummyCounter = 0;

    public BasicPostingOutputStream(RandomAccessFile postingsFile) {
        super(postingsFile);
    }

    @Override
    public int write(Posting p) {
        return dummyCounter++;
    }

    @Override
    public int writeln(Posting p) {
        return dummyCounter++;
    }

    @Override
    public int write(Posting[] postings) throws NullPointerException {
        return dummyCounter++;
    }
}
