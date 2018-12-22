package Querying;

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
        double numerator = getBM25Numerator(ePosting);
        //compute denominator
        double denominator = getMB25Denominator(ePosting);

        return getIDF(ePosting)*(numerator/denominator);
    }

    protected double getMB25Denominator(ExpandedPosting ePosting) {
        return (double)ePosting.posting.getTf() + rankingParameters.k_BM25 * ((double)1 - rankingParameters.b_BM25 + rankingParameters.b_BM25*((double)ePosting.numOfUniqueWords_doc / averageDocumentLengthInCorpus));
    }

    protected double getBM25Numerator(ExpandedPosting ePosting) {
        return (double)ePosting.posting.getTf() * (rankingParameters.k_BM25 +1);
    }


    @Override
    double calculateRankForExplicitPosting(ExpandedPosting ePosting) {
        return calculateRankForPosting(ePosting);
    }

    @Override
    double calculateRankForImplicitPosting(ExpandedPosting ePosting) {
        return calculateRankForPosting(ePosting);
    }

}
