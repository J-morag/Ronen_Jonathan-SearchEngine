package Indexing.Index;

import java.io.IOException;
import java.io.OutputStream;

public interface IPostingOutputStream{

//    void write(Posting p);
    void write(Posting[] postings);
}
