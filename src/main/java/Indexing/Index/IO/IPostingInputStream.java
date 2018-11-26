package Indexing.Index.IO;

import Indexing.Index.Posting;

import java.util.List;

public interface IPostingInputStream {

//    Posting read(int numberOfConsecutivePostings);
//    Posting read(int numberOfConsecutivePostings, int pointerToStartOfPosting);

    /**
     * Reads all postings from the pointer, until the end of a pointer array.
     * The returned Posting array contains postings related to a single Term.
     * @param pointerToStartOfPostingArray
     * @return Posting array containing postings related to a single Term.
     */
    List<Posting> readTermPostings(long pointerToStartOfPostingArray);

    /**
     * reads up to {@param maxNumPostings} postings.
     * The returned Posting array contains postings related to a single Term.
     * @param pointerToStartOfPostingArray
     * @return
     */
    List<Posting> readTermPostings(long pointerToStartOfPostingArray, int maxNumPostings);

//    /**
//     * Reads all postings until the end of a pointer array.
//     * The returned Posting array contains postings related to a single Term.
//     * @return Posting array containing postings related to a single Term.
//     */
//    List<Posting> readLine();
}
