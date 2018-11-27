package Indexing.Index.IO;

import Indexing.Index.Posting;
import javafx.geometry.Pos;
import sun.awt.Mutex;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PostingOutputStream extends APostingOutputStream implements IPostingOutputStream {

//    protected List<byte[]> buffer = new ArrayList<>();
    private Mutex m_postingsFile = new Mutex();

    /**
     * if the file doesn't exist, creates it.
     * if the file exists, clears it!
     * @param pathToFile
     * @throws IOException
     */
    public PostingOutputStream(String pathToFile) throws IOException {
        super(pathToFile);
        postingsFile = new BufferedOutputStream(new FileOutputStream(pathToFile));
    }

    @Override
    public long write(List<Posting> postings) throws NullPointerException, IOException {
        long startIdx = getCursor();

        byte[] outBytes = postingsArrayToByteArray(postings);
//        buffer.add(outBytes);
        postingsFile.write(outBytes);

        filePointer += outBytes.length;

        return startIdx;
    }


    @Override
    public void flush() throws IOException {
        postingsFile.flush();
//        int totalByte = 0;
//        for (byte[] arr: buffer
//                ) {
//            totalByte += arr.length;
//        }
//        byte[] data = new byte[totalByte];
//
//        int idx = 0;
//        for (byte[] arr: buffer
//                ) {
//            for (byte b: arr
//                    ) {
//                data[idx] = b;
//                idx++;
//            }
//        }
//        writeOut(data);
//        buffer.clear();
    }

//    protected void writeOut(byte[] bytes) throws IOException {
//        Thread t = new Thread(() -> {
//            m_postingsFile.lock();
//            try {
//                postingsFile.write(bytes);
//                postingsFile.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            m_postingsFile.unlock();
//        });
//        t.run();
//    }

    protected byte[] postingsArrayToByteArray(List<Posting> postings){
        int numPostings = postings.size();
        byte[] data = new byte[4 + numPostings *
                (Posting.getNumberOfShortFields()*2 + Posting.getNumberOfIntFields()*4 + 1 /*holds 8 bools*/  )];

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

    /**
     * big endian:  In this order, the bytes of a multibyte value are ordered from most significant to least significant.
     * @param i
     * @param bytes
     * @param startIdx
     * @throws IndexOutOfBoundsException
     */
    protected void intToBytes(int i, byte[] bytes, int startIdx) throws IndexOutOfBoundsException{
        bytes[startIdx] = (byte)(i >> 24);
        startIdx++;
        bytes[startIdx] = (byte)(i >> 16);
        startIdx++;
        bytes[startIdx] = (byte)(i >> 8);
        startIdx++;
        bytes[startIdx] = (byte)(i);
    }


    @Override
    public void close() throws IOException {
        postingsFile.close();
    }

    protected static byte extractBoolsAsByte(Posting p){
        byte isInTitle = p.isInTitle() ? Byte.MIN_VALUE+1 : Byte.MIN_VALUE;
        byte isInBeggining = p.isInBeginning() ? Byte.MIN_VALUE+2 : Byte.MIN_VALUE;
        // next will have +4

        return (byte)(isInTitle | isInBeggining);
    }


}
