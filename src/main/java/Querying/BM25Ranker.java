package Querying;

import Indexing.DocumentProcessing.Term;
import Indexing.Index.IndexEntry;
import Indexing.Index.Posting;

import java.util.Map;

public class BM25Ranker extends Ranker {

    public BM25Ranker(RankingParameters rankingParameters, int numDocsInCorpus, double averageDocumentLengthInCorpus) {
        super(rankingParameters, numDocsInCorpus, averageDocumentLengthInCorpus);
    }

    /**
     * implements a simple sum (the sum loop in BM25 algorithm).
     * @param existingRank the rank that is currently assigned to the document.
     * @param newPostingRank the rank that was calculated for the new posting. should be a posting for the same document.
     * @return the new rank that should be assigned to the document.
     */
    @Override
    double addNewPostingRankToExistingDocRank(double existingRank, double newPostingRank) {
        return existingRank + newPostingRank;
    }

    /**
     * calculates a rank with the BM25 algorithm's step (the inside of the sum loop)
     * @param ePosting the posting to calculate for
     * @return a rank score for one posting, according to the BM25 algorithm.
     */
    double calculateRankForPosting(ExpandedPosting ePosting){
        //compute numerator
        double numerator = (double)ePosting.posting.getTf() * (rankingParameters.k_BM25 +1);
        //compute denominator
        double denominator = (double)ePosting.posting.getTf() + (double)rankingParameters.k_BM25 * ((double)1 - (double)rankingParameters.b_BM25 + (double)rankingParameters.b_BM25*((double)ePosting.numOfUniqueWords_doc / averageDocumentLengthInCorpus));

        return getIDF(ePosting)*(numerator/denominator);
    }

    @Override
    double calculateRankForExplicitPosting(ExpandedPosting ePosting) {
        return calculateRankForPosting(ePosting);
    }

    @Override
    double calculateRankForImplicitPosting(ExpandedPosting ePosting) {
        return calculateRankForPosting(ePosting);
    }

    protected double getIDF(ExpandedPosting p){
        //compute numerator
        double numerator = (double)numDocsInCorpus - (double)p.df_term + 0.5;
        //compute denominator
        double denominator = (double)p.df_term + 0.5;

        return numerator/denominator;
    }


}
