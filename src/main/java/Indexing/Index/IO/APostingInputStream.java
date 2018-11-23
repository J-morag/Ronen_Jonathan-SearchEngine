package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.RandomAccessFile;

public abstract class APostingInputStream implements IPostingInputStream {

    RandomAccessFile postingsFile;

    public APostingInputStream(RandomAccessFile postingsFile) {
        this.postingsFile = postingsFile;
    }

    protected Posting fieldsToPosting(short[] shortFields, String[] stringFields, boolean[] booleanFields){
        return new Posting(shortFields, stringFields, booleanFields);
    }


}
