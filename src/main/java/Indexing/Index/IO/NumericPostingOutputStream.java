package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class NumericPostingOutputStream extends APostingOutputStream implements IPostingOutputStream {

    public NumericPostingOutputStream(String pathToFile) throws IOException {
        super(pathToFile);
    }
//
//    @Override
//    public long write(Posting p) throws IOException {
//        return 0;
//    }
//
//    @Override
//    public long writeln(Posting p) throws IOException {
//        return 0;
//    }

    @Override
    public long write(Posting[] postings) throws NullPointerException, IOException {
        long startIdx = postingsFile.getFilePointer();

        postingsFile.write(postingsArrayToByteArray(postings));

        return startIdx;
    }

    protected byte[] postingsArrayToByteArray(Posting[] postings){
        int numPostings = postings.length;
        int numFields = extractShortFields(postings[0]).length;
        byte[] data = new byte[((numPostings * numFields * 2) + 4 )];
        byte[] numPostingsByteArray = ByteBuffer.allocate(4).putInt(numPostings).array();
        data[0] = numPostingsByteArray[0];
        data[1] = numPostingsByteArray[1];
        data[2] = numPostingsByteArray[2];
        data[3] = numPostingsByteArray[3];
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

        return data;
    }


    protected byte short8MSB(short s){
        return (byte)(s >> 8);
    }

    protected byte short8LSB(short s){
        return (byte)s;
    }

    @Override
    public void setCursor(long pos) throws IOException {
        postingsFile.seek(pos);
    }

    @Override
    public void close() throws IOException {
        postingsFile.close();
    }


}
