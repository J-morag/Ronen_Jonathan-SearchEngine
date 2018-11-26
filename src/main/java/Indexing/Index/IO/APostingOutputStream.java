package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

public abstract class APostingOutputStream implements IPostingOutputStream{

    long filePointer = 0;
    RandomAccessFile postingsFile;
//    MappedByteBuffer postingsFile;

    /**
     * if the file doesn't exist, creates it.
     * if the file exists, clears it!
     * @param pathToFile
     * @throws IOException
     */
    public APostingOutputStream(String pathToFile) throws IOException {

        this.postingsFile = new RandomAccessFile(pathToFile, "rw");
        postingsFile.setLength(0);

    }

    @Override
    public long getCursor() {
        return filePointer;
    }



    protected short[] extractShortFields(Posting posting){
        return posting.getShortFields();
    }

    protected int[] extractIntFields(Posting posting){
        return posting.getIntegerFields();
    }
    protected int[] extractIntegerFields(Posting posting){
        return posting.getIntegerFields();
    }
    protected boolean[] extractBooleanFields(Posting posting){
        return posting.getBooleanFields();
    }


}
