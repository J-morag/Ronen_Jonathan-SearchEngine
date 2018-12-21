package Querying;

import Indexing.DocumentProcessing.Term;
import Indexing.Index.IndexEntry;
import Indexing.Index.Posting;

import java.util.Map;

public class BM25Ranker extends Ranker {

    public BM25Ranker(RankingParameters rankingParameters, Map<Term, IndexEntry> mainDictionary, int numDocsInCorpus, double averageDocumentLengthInCorpus) {
        super(rankingParameters, mainDictionary, numDocsInCorpus, averageDocumentLengthInCorpus);
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
     * @param posting the posting to calculate for
     * @return a rank score for one posting, according to the BM25 algorithm.
     */
    double calculateRankForPosting(Posting posting){
        return 1; //TODO implement
    }

    @Override
    double calculateRankForExplicitPosting(Posting posting) {
        return calculateRankForPosting(posting);
    }

    @Override
    double calculateRankForImplicitPosting(Posting posting) {
        return calculateRankForPosting(posting);
    }


}
