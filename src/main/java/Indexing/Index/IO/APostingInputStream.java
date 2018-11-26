package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public abstract class APostingInputStream implements IPostingInputStream {

    RandomAccessFile postingsFile;

    public APostingInputStream(String pathToFile) throws FileNotFoundException {

        this.postingsFile = new RandomAccessFile(pathToFile, "rw");
    }

    protected Posting fieldsToPosting(short[] shortFields, int[] ints , String[] stringFields, boolean[] booleanFields){
        return new Posting(ints, shortFields, booleanFields);
    }


}
