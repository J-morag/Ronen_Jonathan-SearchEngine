package Indexing.Index;

public interface IPostingInputStream {

//    Posting read(int numberOfConsecutivePostings);
//    Posting read(int numberOfConsecutivePostings, int pointerToStartOfPosting);

    /**
     * Reads all postings from the pointer, until the end of a pointer array.
     * The returned Posting array contains postings related to a single Term.
     * @param pointerToStartOfPostingArray
     * @return Posting array containing postings related to a single Term.
     */
    Posting[] readLine(int pointerToStartOfPostingArray);

    /**
     * Reads all postings until the end of a pointer array.
     * The returned Posting array contains postings related to a single Term.
     * @return Posting array containing postings related to a single Term.
     */
    Posting[] readLine();
}
