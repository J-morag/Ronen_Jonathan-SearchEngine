package Querying;

/**
 * A set of weights for the {@link Ranker Ranker} class.
 * Is used to give different weights to the various factors considered when ranking a document's relevancy to a query.
 */
public class RankingParameters {

    final int titleWeight;
    final int beginningWeight;
    final int frequencyWeight;
    final int exactTermMatchWeight;
    final int k_BM25;
    final int b_BM25;

    public RankingParameters(int titleWeight, int beginningWeight, int frequencyWeight, int exactTermMatchWeight, int k_BM25, int b_BM25) {
        this.titleWeight = titleWeight;
        this.beginningWeight = beginningWeight;
        this.frequencyWeight = frequencyWeight;
        this.exactTermMatchWeight = exactTermMatchWeight;
        this.k_BM25 = k_BM25;
        this.b_BM25 = b_BM25;
    }

    @Override
    public String toString() {
        return "RankingParameters{" +
                "titleWeight=" + titleWeight +
                ", beginningWeight=" + beginningWeight +
                ", frequencyWeight=" + frequencyWeight +
                ", exactTermMatchWeight=" + exactTermMatchWeight +
                ", k_BM25=" + k_BM25 +
                ", b_BM25=" + b_BM25 +
                '}';
    }
}
