package Indexing.Index.IO;

import Indexing.Index.Posting;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import javafx.geometry.Pos;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PostingInputStream implements IPostingInputStream {

    RandomAccessFile postingsFile;

    public PostingInputStream(String pathToFile) throws FileNotFoundException {
        this.postingsFile = new RandomAccessFile(pathToFile, "rw");
    }

    protected Posting fieldsToPosting(short[] shortFields, int[] ints , String[] stringFields, boolean[] booleanFields){
        return new Posting(ints, shortFields, booleanFields);
    }


    @Override
    public List<Posting> readTermPostings(long pointerToStartOfPostingArray) throws IOException {
        //TODO clarify
        postingsFile.seek(pointerToStartOfPostingArray);
        int numPostingsToRead = readFourBytesAsInt(postingsFile);
        byte[] bytesFromDisk = new byte[numPostingsToRead*byteLengthOfSinglePosting()];
        postingsFile.read(bytesFromDisk, 0, bytesFromDisk.length);
        ByteInputStream input = new ByteInputStream(bytesFromDisk, bytesFromDisk.length);
        return readNPostings(input, numPostingsToRead);
    }

    @Override
    public List<Posting> readTermPostings(long pointerToStartOfPostingArray, int maxNumPostings) throws IOException {
        //TODO clarify
        postingsFile.seek(pointerToStartOfPostingArray);
        int numPostingsToRead = readFourBytesAsInt(postingsFile);
        byte[] bytesFromDisk = new byte[Math.min(numPostingsToRead, maxNumPostings)*byteLengthOfSinglePosting()];
        postingsFile.read(bytesFromDisk, 0, bytesFromDisk.length);
        ByteInputStream input = new ByteInputStream(bytesFromDisk, bytesFromDisk.length);
        return readNPostings(input, numPostingsToRead);
    }

    private List<Posting> readNPostings(ByteInputStream input, int numberOfPostingsToRead) throws IOException {
        List<Posting>  postings = new ArrayList<>(numberOfPostingsToRead);
        for (int i = 0; i < numberOfPostingsToRead ; i++) {
            postings.add(readSinglePosting(input));
        }
        return postings;
    }

    private int readFourBytesAsInt(ByteInputStream input) throws IOException {
        byte[] bytes = new byte[4];
        input.read(bytes, 0, 4);
        return  (bytes[0]<<24) & 0xff000000|
                (bytes[1]<<16) & 0x00ff0000|
                (bytes[2]<< 8) & 0x0000ff00|
                (bytes[3]<< 0) & 0x000000ff;
    }

    private int readFourBytesAsInt(RandomAccessFile input) throws IOException {
        byte[] bytes = new byte[4];
        input.read(bytes, 0, 4);
        return  (bytes[0]<<24) & 0xff000000|
                (bytes[1]<<16) & 0x00ff0000|
                (bytes[2]<< 8) & 0x0000ff00|
                (bytes[3]<< 0) & 0x000000ff;
    }

    private short readTwoBytesAsShort(ByteInputStream input) throws IOException {
        byte[] bytes = new byte[2];
        input.read(bytes, 0 ,2);
        int res = (((int)bytes[0]) << 8) & 0x0000ff00; //add MSBs
        int LSBs = ((int)bytes[1] & 0x000000ff); //this makes sure that after casting to int, all bits except those in 8 LSBs are off.
        res = res | LSBs ; // add LSBs
        return (short)res;
    }

    private Posting readSinglePosting(ByteInputStream input) throws IOException {
        int[] intFields = new int[Posting.getNumberOfIntFields()];
        short[] shortFields = new short[Posting.getNumberOfShortFields()];
        for (int i = 0; i < intFields.length ; i++) {
            intFields[i] = readFourBytesAsInt(input);
        }
        for (int i = 0; i < shortFields.length ; i++) {
            shortFields[i] = readTwoBytesAsShort(input);
        }

        // THIS HAS TO CHANGE IF POSTING CHANGES //
        byte[] boolsAsByte = new byte[1];
        input.read(boolsAsByte);
        boolean[] boolFields = new boolean[2];
        boolFields[0] = (boolsAsByte[0] & (0b00000001)) > 0b00000000;
        boolFields[1] = (boolsAsByte[0] & (0b00000010)) > 0b00000000;
        // THIS HAS TO CHANGE IF POSTING CHANGES //

        return new Posting(intFields, shortFields, boolFields);
    }

    private int byteLengthOfSinglePosting(){
        return Posting.getNumberOfShortFields()*2 + Posting.getNumberOfIntFields()*4 +  1 /*holds 8 bools*/  ;
    }
}
