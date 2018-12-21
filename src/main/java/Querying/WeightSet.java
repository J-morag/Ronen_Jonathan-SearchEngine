package Querying;

/**
 * A set of weights for the {@link Ranker Ranker} class.
 * Is used to give different weights to the various factors considered when ranking a document's relevancy to a query.
 */
public class WeightSet {

    final int titleWeight;
    final int beginningWeight;
    final int frequencyWeight;
    final int exactTermMatchWeight;

    public WeightSet(int titleWeight, int beginningWeight, int frequencyWeight, int exactTermMatchWeight) {
        this.titleWeight = titleWeight;
        this.beginningWeight = beginningWeight;
        this.frequencyWeight = frequencyWeight;
        this.exactTermMatchWeight = exactTermMatchWeight;
    }

    @Override
    public String toString() {
        return "WeightSet{" +
                "titleWeight=" + titleWeight +
                ", beginningWeight=" + beginningWeight +
                ", frequencyWeight=" + frequencyWeight +
                ", exactTermMatchWeight=" + exactTermMatchWeight +
                '}';
    }
}
