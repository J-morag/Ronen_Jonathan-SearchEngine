package Indexing.Index.IO;

import Indexing.Index.Posting;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * a basic class for outputting Postings to a file.
 * writes all fields as strings.
 */
public class BasicPostingOutputStream extends APostingOutputStream {

    //TESTING
    int dummyCounter = 0;

    //ADMINISTRATIVE

    public BasicPostingOutputStream(RandomAccessFile postingsFile) {
        super(postingsFile);
    }

    //OUTPUT

    @Override
    public long write(@NotNull Posting p) throws IOException {
        // ';' denotes the end of a single posting
        postingsFile.writeBytes(p.toString() + ";");
        return postingsFile.getFilePointer();
    }

    @Override
    public long writeln(Posting p) throws IOException {
        String output = p!=null? (p+";\n") : "\n";
        postingsFile.writeBytes(output);
        return postingsFile.getFilePointer();
    }

    @Override
    public long write(@NotNull Posting[] postings) throws NullPointerException, IOException {
        StringBuilder output = new StringBuilder();
        for (Posting p : postings
             ) {
            output.append(p.toString());
            output.append(';');
        }
        output.append('\n');
        postingsFile.writeBytes(output.toString());
        return postingsFile.getFilePointer();
    }
}
