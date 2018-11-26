package Indexing.Index.IO;

import Indexing.Index.Posting;
import javafx.geometry.Pos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PostingOutputStream extends APostingOutputStream implements IPostingOutputStream {

    //TODO booleans!
    //TODO add ints!
    protected List<byte[]> buffer = new ArrayList<>();

    public PostingOutputStream(String pathToFile) throws IOException {
        super(pathToFile);
    }

    @Override
    public long write(List<Posting> postings) throws NullPointerException, IOException {
        long startIdx = getCursor();

        byte[] outBytes = postingsArrayToByteArray(postings);
        buffer.add(outBytes);

        filePointer += outBytes.length;

        return startIdx;
    }


    @Override
    public void flush() throws IOException {
        int totalByte = 0;
        for (byte[] arr: buffer
                ) {
            totalByte += arr.length;
        }
        byte[] data = new byte[totalByte];

        int idx = 0;
        for (byte[] arr: buffer
                ) {
            for (byte b: arr
                    ) {
                data[idx] = b;
                idx++;
            }
        }

        writeOut(data);

        buffer.clear();
    }

    protected void writeOut(byte[] bytes) throws IOException {

        Thread t = new Thread(() -> {
//            m_postingsFile.lock();
            synchronized (postingsFile){
                try {
                    postingsFile.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            m_postingsFile.unlock();
        });
        t.run();

    }

    protected byte[] postingsArrayToByteArray(List<Posting> postings){
        int numPostings = postings.size();
        byte[] data = new byte[4 + numPostings *
                (Posting.getNumberOfShortFields()*2 + Posting.getNumberOfIntFields()*4 + Posting.getNumberOfBooleanFields()/8 /* rounded down*/ )];

        intToBytes(numPostings, data, 0);

        int dataIdx = 4;

        for (Posting p :postings
             ) {
            int[] intFieldsFori = extractIntegerFields(p);
            for (int integer: intFieldsFori
                    ) {
                intToBytes(integer, data, dataIdx);
                dataIdx += 4;
            }

            short[] shortFieldsFori = extractShortFields(p);
            for (short s: shortFieldsFori
                    ) {
                data[dataIdx] = short8MSB(s);
                dataIdx++;
                data[dataIdx] = short8LSB(s);
                dataIdx++;
            }

//            boolean[] booleanFieldsFori = extractBooleanFields(postings.get(i));
            // THIS HAS TO CHANGE IF POSTING CHANGES //
            data[dataIdx] = extractBoolsAsByte(p);
            dataIdx++;
            // THIS HAS TO CHANGE IF POSTING CHANGES //
        }

        return data;
    }


    protected byte short8MSB(short s){
        return (byte)(s >> 8);
    }

    protected byte short8LSB(short s){
        return (byte)s;
    }

    protected void intToBytes(int i, byte[] bytes, int startIdx) throws IndexOutOfBoundsException{
        bytes[startIdx] = (byte)(i >> 24);
        bytes[startIdx] = (byte)(i >> 16);
        bytes[startIdx] = (byte)(i >> 8);
        bytes[startIdx] = (byte)(i);
    }


    @Override
    public void close() throws IOException {
        postingsFile.close();
    }

    protected static byte extractBoolsAsByte(Posting p){
        int idx = 0;
        byte isInTitle = p.isInTitle() ? (byte)1 : 0;
        isInTitle = (byte)(isInTitle << idx++);
        byte isInBeggining = p.isInBeginning() ? (byte)1 : 0;
        isInBeggining = (byte)(isInBeggining << idx++);

        return (byte)(isInTitle | isInBeggining);
    }


}
