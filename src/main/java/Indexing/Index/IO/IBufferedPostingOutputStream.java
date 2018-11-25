package Indexing.Index.IO;

import Indexing.Index.Posting;
import com.sun.istack.internal.NotNull;

import java.io.IOException;

public interface IBufferedPostingOutputStream {


    /**
     * writes all the postings in {@param postings} and then ends the line.
     * @param postings  - an array of postings to write.
     * @return - the index where the first byte of the first posting was written.
     * @throws NullPointerException - if {@param postings} contains a null pointer
     */
    long write(@NotNull Posting[] postings) throws NullPointerException, IOException;


    /**
     * flushes the buffer and closes the stream
     * @throws IOException
     */
    void flush() throws IOException;
}
