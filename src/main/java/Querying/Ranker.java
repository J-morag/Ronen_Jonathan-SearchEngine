package Querying;

import java.util.*;

/**
 * ranks documents according to relevance in the context of a query.
 * uses a {@link RankingParameters RankingParameters} to give weights to the different parameters considered in the algorithm.
 */
public abstract class Ranker {

    protected RankingParameters rankingParameters;
    protected int numDocsInCorpus;
    protected double averageDocumentLengthInCorpus;

    public Ranker(RankingParameters rankingParameters, int numDocsInCorpus, double averageDocumentLengthInCorpus) {
        this.rankingParameters = rankingParameters;
        this.numDocsInCorpus = numDocsInCorpus;
        this.averageDocumentLengthInCorpus = averageDocumentLengthInCorpus;
    }

    /**
     * Takes a list of postings for terms in the query, and a list of postings for terms derived semantically from the
     * query. Ranks them according to relevance.
     * Returns an array of unique document serialIDs, sorted by rank (first is most relevant).
     * @param postingsExplicit postings for terms mentioned explicitly in the query. may contain duplicates
     *                        internally or from postingsImplicit.
     * @param postingsImplicit postings for terms derived semantically from the query. may contain duplicates
     *                        internally or from postingsExplicit.
     * @param query a vector of words appearing in the query. should not contain duplicates.
     * @param queryNeighbors a vector of words that are semantically similar to words appearing in the query. should not contain duplicates.
     * @return an array of unique ints, each being the serialID of a document, sorted most to least relevant.
     */
    public int[] rank(List<ExpandedPosting> postingsExplicit, List<ExpandedPosting> postingsImplicit, String[] query, String[] queryNeighbors){

        Map<Integer, Double> rankedDocs = rankDocs(postingsExplicit, postingsImplicit, query, queryNeighbors);

        return sortDocsByRank(rankedDocs);
    }

    private int[] sortDocsByRank(Map<Integer, Double> rankedDocs) {
        //sort by rank
        Map.Entry[] docsAsEntries = new Map.Entry[rankedDocs.size()];
        rankedDocs.entrySet().toArray(docsAsEntries);
        Arrays.sort(docsAsEntries, ((o1, o2) -> Collections.reverseOrder().compare(o1, o2)));

        //to int array
        int[] docsAsInts = new int[docsAsEntries.length];
        for (int i = 0; i < docsAsEntries.length; i++) {
            docsAsInts[i] = (Integer)docsAsEntries[i].getKey();
        }
        return docsAsInts;
    }

    /**
     * give every document in the given postings a rank that represents it's relevance to the query.
     * @param postingsExplicit postings for terms mentioned explicitly in the query. may contain duplicates
     *                        internally or from postingsImplicit.
     * @param postingsImplicit postings for terms derived semantically from the query. may contain duplicates
     *                        internally or from postingsExplicit.
     * @param query a vector of words appearing in the query. should not contain duplicates.
     * @param queryNeighbors a vector of words that are semantically similar to words appearing in the query. should not contain duplicates.
     * @return a mapping of document IDs (Integer) to document ranks (Double).
     */
    protected Map<Integer, Double> rankDocs(List<ExpandedPosting> postingsExplicit, List<ExpandedPosting> postingsImplicit, String[] query, String[] queryNeighbors) {
        Map<Integer, Double> rankedDocs = new HashMap<>(postingsExplicit.size());
        for (ExpandedPosting ePosting: postingsExplicit
             ) {
            double rank = calculateRankForExplicitPosting(ePosting);
            if(rankedDocs.containsKey(ePosting.posting.getDocSerialID())){
                rank = addNewPostingRankToExistingDocRank(rankedDocs.get(ePosting.posting.getDocSerialID()), rank);
            }
            rankedDocs.put(ePosting.posting.getDocSerialID(), rank);
        }
        for (ExpandedPosting ePosting: postingsImplicit
             ) {
            double rank = calculateRankForImplicitPosting(ePosting);
            if(rankedDocs.containsKey(ePosting.posting.getDocSerialID())){
                rank = addNewPostingRankToExistingDocRank(rankedDocs.get(ePosting.posting.getDocSerialID()), rank);
            }
            rankedDocs.put(ePosting.posting.getDocSerialID(), rank);
        }

        return rankedDocs;
    }

    /**
     * get the IDF for a term.
     * @param p information about the term and the document it appears in. Document information is irrelevant of IDF.
     * @return the IDF of a term.
     */
    protected double getIDF(ExpandedPosting p){
        //compute numerator
        double numerator = (double)numDocsInCorpus - (double)p.df_term + 0.5;
        //compute denominator
        double denominator = (double)p.df_term + 0.5;

        return numerator/denominator;
    }

    /**
     * After a rank was calculated for a term appearing in a document, it is necessary to to add that rank to the
     * current rank assigned to the document. A basic implementation would simply return the sum of both ranks.
     * @param existingRank the rank currently assigned to the document. Should be same document as newPostingRank.
     * @param newPostingRank the rank assigned to the term appearing in the document. Should be same document as existingRank.
     * @return the new rank for the document.
     */
    protected abstract double addNewPostingRankToExistingDocRank(double existingRank, double newPostingRank);

    /**
     * calculates the rank contribution of a term appearing in a document, that also appeared in the query.
     * @param ePosting information about the term and the document it appears in.
     * @return the rank contribution of the term appearing in a document, according to relevance.
     */
    protected abstract double calculateRankForExplicitPosting(ExpandedPosting ePosting);

    /**
     * calculates the rank contribution of a term appearing in a document, that did not necessarily appear explicitly
     * in the query.
     * @param ePosting information about the term and the document it appears in.
     * @return the rank contribution of the term appearing in a document, according to relevance.
     */
    protected abstract double calculateRankForImplicitPosting(ExpandedPosting ePosting);

}
