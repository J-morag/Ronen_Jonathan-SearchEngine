package Indexing.Index.IO;

import Indexing.Index.Posting;
import sun.awt.Mutex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferedShortsOnlyPostingOutputStream extends ShortsOnlyPostingOutputStream implements IBufferedPostingOutputStream{

    List<byte[]> buffer = new ArrayList<>();
//    Mutex m_postingsFile = new Mutex();

    public BufferedShortsOnlyPostingOutputStream(String pathToFile) throws IOException {
        super(pathToFile);
    }

    @Override
    public long write(Posting[] postings) throws NullPointerException, IOException {
        long startIdx = postingsFile.getFilePointer();

        buffer.add(postingsArrayToByteArray(postings));

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

}
