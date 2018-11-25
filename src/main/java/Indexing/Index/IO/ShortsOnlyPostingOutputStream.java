package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ShortsOnlyPostingOutputStream extends APostingOutputStream {

    public ShortsOnlyPostingOutputStream(RandomAccessFile postingsFile) {
        super(postingsFile);
    }

    @Override
    public long write(Posting p) throws IOException {
        return 0;
    }

    @Override
    public long writeln(Posting p) throws IOException {
        return 0;
    }

    @Override
    public long write(Posting[] postings) throws NullPointerException, IOException {
        long startIdx = postingsFile.getFilePointer();

        short numPostings = (short)postings.length;
        int numFields = extractShortFields(postings[0]).length;
        byte[] data = new byte[((numPostings * numFields) + 1) *2];
        data[0] = short8MSB(numPostings);
        data[1] = short8LSB(numPostings);
        for (int i = 0; i <numPostings ; i++) {
            short[] fieldsFori = extractShortFields(postings[i]);
            for (int j = 0; j <numFields ; j++) {
                int idxInShortStream = 1+ (i*j) +j ; //skip first (indicates length) + jump to start of this posting + jump to field in this posting
                data[ idxInShortStream * 2 ] //put MSBs
                        = short8MSB(fieldsFori[j]);
                data[ idxInShortStream * 2  + 1]  //put LSBs
                        = short8LSB(fieldsFori[j]);
            }
        }
        postingsFile.write(data);

        return startIdx;
    }


    protected byte short8MSB(short s){
        return (byte)(s >> 8);
    }

    protected byte short8LSB(short s){
        return (byte)s;
    }
}
