package Querying;

import Indexing.Index.Posting;

import java.util.*;

/**
 * ranks documents according to relevance.
 * uses a {@link RankingAlgorithm RankingAlgorithm} for the rank calculation (strategy design pattern).
 */
public class Ranker {

    RankingAlgorithm rankingAlgorithm;

    public Ranker(RankingAlgorithm rankingAlgorithm) {
        this.rankingAlgorithm = rankingAlgorithm;
    }

    /**
     * Takes a list of postings for terms in the query, and a list of postings for terms derived semantically from the
     * query. Ranks them according to relevance (determined by {@see #rankingAlgorithm rankingAlgorithm}). Returns
     * a list of unique document serialIDs, sorted by rank (first is most relevant).
     * @param postingsExplicit postings for terms mentioned explicitly in the query. may contain duplicates
     *                        internally or from postingsImplicit.
     * @param postingsImplicit postings for terms derived semantically from the query. may contain duplicates
     *                        internally or from postingsExplicit.
     * @return a list of unique Integers, each being the serialID of a document, sorted most to least relevant.
     */
    public int[] rank(List<Posting> postingsExplicit, List<Posting> postingsImplicit){
        Map<Integer, Double> rankedDocs = new HashMap<>(postingsExplicit.size());

        rankAndInsertToMap(postingsExplicit, rankedDocs, true);
        rankAndInsertToMap(postingsImplicit, rankedDocs, false);

        //sort by rank
        Map.Entry[] docsAsEntries = new Map.Entry[rankedDocs.size()];
        rankedDocs.entrySet().toArray(docsAsEntries);
        Arrays.sort(docsAsEntries, Comparator.comparingDouble(Map.Entry<Integer, Double>::getValue).reversed());

        //to int array
        int[] docsAsInts = new int[docsAsEntries.length];
        for (int i = 0; i < docsAsEntries.length; i++) {
            docsAsInts[i] = (Integer)docsAsEntries[i].getKey();
        }

        return docsAsInts;
    }

    private void rankAndInsertToMap(List<Posting> postings, Map<Integer, Double> rankedDocs, boolean isExplicitTermPostings ) {
        for (Posting posting: postings
             ) {
            double rank = rankingAlgorithm.rank(posting, isExplicitTermPostings);
            if(rankedDocs.containsKey(posting.getDocSerialID())){
                rank = rankedDocs.get(posting.getDocSerialID()) + rank;
            }
            rankedDocs.put(posting.getDocSerialID(), rank);
        }
    }
}
