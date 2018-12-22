package Querying;

import Indexing.Index.Posting;

public class ExpandedBM25Ranker extends BM25Ranker{

    public ExpandedBM25Ranker(RankingParameters rankingParameters, int numDocsInCorpus, double averageDocumentLengthInCorpus) {
        super(rankingParameters, numDocsInCorpus, averageDocumentLengthInCorpus);
    }

    @Override
    double calculateRankForExplicitPosting(ExpandedPosting ePosting) {
        return (super.calculateRankForImplicitPosting(ePosting) * rankingParameters.frequencyWeight)
                + rankingParameters.exactTermMatchWeight + getMetadataBonuses(ePosting);
    }

    @Override
    double calculateRankForImplicitPosting(ExpandedPosting ePosting) {
        return (super.calculateRankForImplicitPosting(ePosting) * rankingParameters.frequencyWeight)
                + getMetadataBonuses(ePosting);
    }

    protected double getMetadataBonuses(ExpandedPosting ePosting){
        Posting p = ePosting.posting;
        return rankingParameters.titleWeight * (p.isInTitle() ? 1 : 0) +
                rankingParameters.beginningWeight * (p.isInBeginning() ? 1 : 0);
    }
}
