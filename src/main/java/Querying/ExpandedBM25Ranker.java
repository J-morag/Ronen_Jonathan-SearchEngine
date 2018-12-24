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
        return (super.calculateRankForImplicitPosting(ePosting) * rankingParameters.frequencyWeight)* rankingParameters.exactTermMatchWeight
                + getMetadataBonuses(ePosting);
    }

    @Override
    protected double calculateRankForImplicitPosting(ExpandedPosting ePosting) {
        return (super.calculateRankForImplicitPosting(ePosting) * rankingParameters.frequencyWeight)
                + getMetadataBonuses(ePosting);
    }

    /**
     * calculate additional bonuses to a posting's relevance by whether a posting appeared in the title of a document,
     * or in the document's beginning.
     * @param ePosting information about a term's appearance in a document.
     * @return bonuses to a posting's relevance ranking.
     */
    protected double getMetadataBonuses(ExpandedPosting ePosting){
        Posting p = ePosting.posting;
        return rankingParameters.titleWeight * (p.isInTitle() ? 1 : 0) +
                rankingParameters.beginningWeight * (p.isInBeginning() ? 1 : 0);
    }
}
