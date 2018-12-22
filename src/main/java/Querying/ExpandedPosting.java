package Querying;

import Indexing.Index.Posting;

/**
 * adds more information to the {@link Posting Posting} to allow ranking of documents for queries.
 */
public class ExpandedPosting {

    public final Posting posting;
    public final int totalTF_term;
    public final int df_term;
    public final int numOfUniqueWords_doc;
    public final int maxTF_doc;
    public final String term;


    public ExpandedPosting(Posting posting, int totalTF_term, int df_term, int numOfUniqueWords_doc, int maxTF_doc, String term) {
        this.posting = posting;
        this.totalTF_term = totalTF_term;
        this.df_term = df_term;
        this.numOfUniqueWords_doc = numOfUniqueWords_doc;
        this.maxTF_doc = maxTF_doc;
        this.term = term;
    }
}
