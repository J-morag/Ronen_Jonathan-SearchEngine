package Querying;

import Indexing.Index.Posting;

/**
 * An algorithm to give a document a rank, representing its relevance to a term in the context of a query.
 * uses a {@link RankingParameters RankingParameters} to give weights to the different parameters considered in the algorithm.
 */
public abstract class RankingAlgorithm {

    /**
     * give weights to the different parameters considered in the algorithm.
     */
    private RankingParameters rankingParameters;

    public RankingAlgorithm(RankingParameters rankingParameters) {
        this.rankingParameters = rankingParameters;
    }

    /**
     * Give  a numeric (float) rank to a document to represent its relevance to a term, in the context of a query.
     * The context of a query is only addressed by the boolean isTermExplicitlyInQuery. All other factors are inherent
     * To the relation between the document and a term (the Posting).
     * @param posting contains information about the relation between a document and a term.
     * @param isTermExplicitlyInQuery indicates whether the term was present in the query or derived from semantic analysis.
     * @return a float rank representing a documents relevance to a term, in the context of a query.
     */
    abstract float rank(Posting posting, boolean isTermExplicitlyInQuery);
}
