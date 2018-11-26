package Indexing.Index.IO;

import Indexing.Index.Posting;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;

public interface IPostingOutputStream{

    /**
     *
     * @return a pointer to the index of the next byte to be written.
     */
    long getCursor();

    /**
     * writes all the postings in {@param postings} and then ends the line.
     * @param postings  - an array of postings to write.
     * @return - the index where the first byte of the first posting was written.
     * @throws NullPointerException - if {@param postings} contains a null pointer
     */
    long write(@NotNull List<Posting> postings) throws NullPointerException, IOException;

    /**
     * flushes the buffer
     * @throws IOException
     */
    void flush() throws IOException;

    void close() throws IOException;
}