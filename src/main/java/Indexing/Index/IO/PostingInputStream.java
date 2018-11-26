package Indexing.Index.IO;

import Indexing.Index.Posting;
import javafx.geometry.Pos;

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
        postingsFile.seek(pointerToStartOfPostingArray);
        int numberOfPostingsForTerm = readFourBytesAsInt();
        return readNPostings(pointerToStartOfPostingArray+4, numberOfPostingsForTerm);
    }

    @Override
    public List<Posting> readTermPostings(long pointerToStartOfPostingArray, int maxNumPostings) throws IOException {
        postingsFile.seek(pointerToStartOfPostingArray);
        int numberOfPostingsForTerm = readFourBytesAsInt();
        return readNPostings(pointerToStartOfPostingArray+4, Math.min(numberOfPostingsForTerm, maxNumPostings));
    }

    private List<Posting> readNPostings(long pointerToStartOfPostingsSection, int numberOfPostingsToRead) throws IOException {
        List<Posting>  postings = new ArrayList<>(numberOfPostingsToRead);
        for (int i = 0; i < numberOfPostingsToRead ; i++) {
            postings.add(readSinglePosting(pointerToStartOfPostingsSection));
            pointerToStartOfPostingsSection += byteLengthOfSinglePosting();
        }
        return postings;
    }

    private int readFourBytesAsInt() throws IOException {
        //TODO? can improve efficiency
        byte[] bytes = new byte[4];
        postingsFile.read(bytes, 0, 4);
        ByteBuffer bf = ByteBuffer.wrap(bytes);
        return bf.getInt();
    }

    private short readTwoBytesAsShort() throws IOException {
        byte[] bytes = new byte[2];
        postingsFile.read(bytes, 0 ,2);
        int res = ((int)bytes[0]) << 8; //add MSBs
        res = res | bytes[1]; // add LSBs
        return (short)res;
    }

    private Posting readSinglePosting(long pointerToStartOfPosting) throws IOException {
        postingsFile.seek(pointerToStartOfPosting); //not strictly necessary but better for clarity
        int[] intFields = new int[Posting.getNumberOfIntFields()];
        short[] shortFields = new short[Posting.getNumberOfShortFields()];
        for (int i = 0; i < intFields.length ; i++) {
            intFields[i] = readFourBytesAsInt();
        }
        for (int i = 0; i < shortFields.length ; i++) {
            shortFields[i] = readTwoBytesAsShort();
        }

        // THIS HAS TO CHANGE IF POSTING CHANGES //
        byte boolsAsByte = postingsFile.readByte();
        boolean[] boolFields = new boolean[2];
        boolFields[0] = (boolsAsByte & (Byte.MIN_VALUE+1)) > Byte.MIN_VALUE;
        boolFields[1] = (boolsAsByte & (Byte.MIN_VALUE+2)) > Byte.MIN_VALUE;
        // THIS HAS TO CHANGE IF POSTING CHANGES //

        return new Posting(intFields, shortFields, boolFields);
    }

    private int byteLengthOfSinglePosting(){
        return Posting.getNumberOfShortFields()*2 + Posting.getNumberOfIntFields()*4 +  1 /*holds 8 bools*/  ;
    }
}
