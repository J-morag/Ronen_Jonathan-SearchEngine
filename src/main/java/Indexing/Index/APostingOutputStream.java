package Indexing.Index;

import java.io.RandomAccessFile;

public abstract class APostingOutputStream implements IPostingOutputStream {

    RandomAccessFile postingsFile;

    public APostingOutputStream(RandomAccessFile postingsFile) {
        this.postingsFile = postingsFile;
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
