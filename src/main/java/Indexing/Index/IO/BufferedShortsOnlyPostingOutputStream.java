package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class BufferedShortsOnlyPostingOutputStream extends ShortsOnlyPostingOutputStream{

    List<byte[]> buffer = new ArrayList<>();

    public BufferedShortsOnlyPostingOutputStream(RandomAccessFile postingsFile) {
        super(postingsFile);
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
        buffer.add(data);

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
}
