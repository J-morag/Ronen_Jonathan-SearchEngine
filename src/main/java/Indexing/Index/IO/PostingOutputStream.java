package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PostingOutputStream extends APostingOutputStream implements IPostingOutputStream {

    //TODO booleans!
    //TODO add ints!
    List<byte[]> buffer = new ArrayList<>();

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
        int numFields = extractShortFields(postings.get(0)).length;
        byte[] data = new byte[((numPostings * numFields * 2) + 4 )];
        byte[] numPostingsByteArray = ByteBuffer.allocate(4).putInt(numPostings).array();
        data[0] = numPostingsByteArray[0];
        data[1] = numPostingsByteArray[1];
        data[2] = numPostingsByteArray[2];
        data[3] = numPostingsByteArray[3];
        for (int i = 0; i <numPostings ; i++) {
            short[] fieldsFori = extractShortFields(postings.get(i));
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
    public void close() throws IOException {
        postingsFile.close();
    }


}
