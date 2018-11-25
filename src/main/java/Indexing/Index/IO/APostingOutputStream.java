package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

public abstract class APostingOutputStream{

    RandomAccessFile postingsFile;
//    MappedByteBuffer postingsFile;

    public APostingOutputStream(String pathToFile) throws FileNotFoundException {

        this.postingsFile = new RandomAccessFile(pathToFile, "rw");

    }


    protected short[] extractShortFields(Posting posting){
        return posting.getShortFields();
    }
    protected String[] extractStringFields(Posting posting){
        return posting.getStringFields();
    }
    protected boolean[] extractBooleanFields(Posting posting){
        return posting.getBooleanFields();
    }


}
