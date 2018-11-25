package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferedShortsOnlyPostingOutputStream extends ShortsOnlyPostingOutputStream implements IBufferedPostingOutputStream{

    List<byte[]> buffer = new ArrayList<>();

    public BufferedShortsOnlyPostingOutputStream(RandomAccessFile postingsFile) {
        super(postingsFile);
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

        postingsFile.write(data);

        buffer.clear();
    }

    @Override
    public long write(Posting p) throws IOException {
        return super.write(p);
    }

    @Override
    public long writeln(Posting p) throws IOException {
        return super.writeln(p);
    }
}
