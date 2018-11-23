package Indexing.Index.IO;

import Indexing.Index.Posting;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public interface IPostingOutputStream{

    /**
     * writes a single posting.
     * @param p - a Posting to write.
     * @return - the index where the first byte posting was written.
     */
    int write(@NotNull Posting p);

    /**
     * writes a single posting and ends the line of postings.
     * if {@param p} is null, will end the line without writing any posting.
     * @param p - a Posting to write.
     *  @return - the index where the first byte of the posting was written.
     */
    int writeln(Posting p);

    /**
     * writes all the postings in {@param postings} and then ends the line.
     * @param postings  - an array of postings to write.
     * @return - the index where the first byte of the first posting was written.
     * @throws NullPointerException - if {@param postings} contains a null pointer
     */
    int write(@NotNull Posting[] postings) throws NullPointerException;
}
