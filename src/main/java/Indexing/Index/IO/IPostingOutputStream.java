package Indexing.Index.IO;

import Indexing.Index.Posting;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public interface IPostingOutputStream{

//    /**
//     * sets the file to write to.
//     * writes to wherever the file's cursor is set to.
//     * @param randomAccessFile
//     */
//    void setFile(@NotNull RandomAccessFile randomAccessFile);

    /**
     * sets the cursor to {@param pos}.
     * @param pos - a pointer to the location to write to in thr file.
     * @throws IOException - if pos is less than 0 or if an I/O error occurs.
     */
    void setCursor(long pos) throws IOException;
//
//    /**
//     * writes a single posting.
//     * @param p - a Posting to write.
//     * @return - the index where the first byte posting was written.
//     */
//    long write(@NotNull Posting p) throws IOException;
//
//    /**
//     * writes a single posting and ends the line of postings.
//     * if {@param p} is null, will end the line without writing any posting.
//     * @param p - a Posting to write.
//     *  @return - the index where the first byte of the posting was written.
//     */
//    long writeln(Posting p) throws IOException;

    /**
     * writes all the postings in {@param postings} and then ends the line.
     * @param postings  - an array of postings to write.
     * @return - the index where the first byte of the first posting was written.
     * @throws NullPointerException - if {@param postings} contains a null pointer
     */
    long write(@NotNull Posting[] postings) throws NullPointerException, IOException;

    void close() throws IOException;
}
