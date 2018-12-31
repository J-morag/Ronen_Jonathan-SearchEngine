package Querying;

import Indexing.Index.Posting;

/**
 * Extends {@link BM25Ranker BM25Ranker} by considering additional information about a term's appearance in a document.
 */
public class ExpandedBM25Ranker extends BM25Ranker{

    public ExpandedBM25Ranker(RankingParameters rankingParameters, int numDocsInCorpus, double averageDocumentLengthInCorpus) {
        super(rankingParameters, numDocsInCorpus, averageDocumentLengthInCorpus);
    }

    @Override
    protected double calculateRankForExplicitPosting(ExpandedPosting ePosting) {
        return (super.calculateRankForImplicitPosting(ePosting) * rankingParameters.frequencyWeight + getMetadataBonuses(ePosting));
    }

    @Override
    protected double calculateRankForImplicitPosting(ExpandedPosting ePosting) {
        return ((super.calculateRankForImplicitPosting(ePosting) * rankingParameters.frequencyWeight)
                + getMetadataBonuses(ePosting) ) *
                /* slightly higher weight for closer neighbors*/
                (rankingParameters.exactTermMatchWeight + 0.1*((double)1-Math.abs(queryNeighbors.get(ePosting.term.toLowerCase()))));
    }

}
