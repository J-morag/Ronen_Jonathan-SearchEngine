package Querying;

import Indexing.DocumentProcessing.Term;
import Indexing.Index.IndexEntry;
import Indexing.Index.Posting;

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
     * query. Ranks them according to relevance. Returns
     * a list of unique document serialIDs, sorted by rank (first is most relevant).
     * @param postingsExplicit postings for terms mentioned explicitly in the query. may contain duplicates
     *                        internally or from postingsImplicit.
     * @param postingsImplicit postings for terms derived semantically from the query. may contain duplicates
     *                        internally or from postingsExplicit.
     * @return a list of unique Integers, each being the serialID of a document, sorted most to least relevant.
     */
    public int[] rank(List<ExpandedPosting> postingsExplicit, List<ExpandedPosting> postingsImplicit){

        Map<Integer, Double> rankedDocs = rankDocs(postingsExplicit, postingsImplicit);

        int[] docsAsInts = getDocsSortedByRank(rankedDocs);

        return docsAsInts;
    }

    private int[] getDocsSortedByRank(Map<Integer, Double> rankedDocs) {
        //sort by rank
        Map.Entry[] docsAsEntries = new Map.Entry[rankedDocs.size()];
        rankedDocs.entrySet().toArray(docsAsEntries);
//        Arrays.sort(docsAsEntries, Comparator.comparingDouble(Map.Entry<Integer, Double>::getValue));
        Arrays.sort(docsAsEntries, ((o1, o2) -> Collections.reverseOrder().compare(o1, o2)));

        //to int array
        int[] docsAsInts = new int[docsAsEntries.length];
        for (int i = 0; i < docsAsEntries.length; i++) {
            docsAsInts[i] = (Integer)docsAsEntries[i].getKey();
        }
        return docsAsInts;
    }

    protected Map<Integer, Double> rankDocs(List<ExpandedPosting> postingsExplicit, List<ExpandedPosting> postingsImplicit ) {
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

    protected double getIDF(ExpandedPosting p){
        //compute numerator
        double numerator = (double)numDocsInCorpus - (double)p.df_term + 0.5;
        //compute denominator
        double denominator = (double)p.df_term + 0.5;

        return numerator/denominator;
    }

    abstract double addNewPostingRankToExistingDocRank(double existingRank, double newPostingRank);

    abstract double calculateRankForExplicitPosting(ExpandedPosting ePosting);

    abstract double calculateRankForImplicitPosting(ExpandedPosting ePosting);

}
