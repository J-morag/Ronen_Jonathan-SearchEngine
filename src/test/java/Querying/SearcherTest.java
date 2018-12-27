package Querying;

import Indexing.Index.CityIndexEntry;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IndexEntry;
import Indexing.Index.Indexer;
import Querying.Semantics.SemanticEngine;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

class SearcherTest {

    private final String pathToPostingsFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\indexing";
    private final String pathToResultsOutputFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults";
    private final String pathToGloVeFilesFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\GloVe";


    Ranker ranker;
    Searcher searcher;
    Map<String, IndexEntry> mainDictionaryWithStemming;
    ArrayList<DocIndexEntery> docDictionaryWithStemming;
    Map<String, IndexEntry> mainDictionaryNoStemming;
    ArrayList<DocIndexEntery> docDictionaryNoStemming;
    Map<String , CityIndexEntry> cityDictionary;
    Set<String> languages;

    void initialize(boolean useStemming, int kNeighbors, HashSet<String> cityList) throws IOException, ClassNotFoundException {
        RankingParameters rankingParameters = new RankingParameters(0.5, 0, 1, 3.5, 2, 0.75);

        loadDictionaries(useStemming, pathToPostingsFolder);
        int numDocsInCorpus = useStemming? docDictionaryWithStemming.size() : docDictionaryNoStemming.size();

        double averageDocLength = 0.0;
        for (DocIndexEntery doc: useStemming? docDictionaryWithStemming : docDictionaryNoStemming
                ) {
            averageDocLength += doc.getLength();
        }
        averageDocLength /= numDocsInCorpus;

        ranker = new ExpandedBM25Ranker(rankingParameters, numDocsInCorpus, averageDocLength);

        searcher = new Searcher(useStemming? mainDictionaryWithStemming : mainDictionaryNoStemming, cityDictionary,
                useStemming? docDictionaryWithStemming : docDictionaryNoStemming, useStemming,
                pathToPostingsFolder + (useStemming? "/postingWithStemming/Postings" : "/postingWithOutStemming/Postings"),
                new SemanticEngine(pathToGloVeFilesFolder, kNeighbors), ranker, cityList);
    }

    @Test
    void EBM25Test() throws IOException, ClassNotFoundException {
        initialize(true, 2, new HashSet<>());
        boolean withSemantics = true;

        List<QueryResult> qRes = new ArrayList<>();
        qRes.add(new QueryResult("351", searcher.answerquery("Falkland petroleum exploration", withSemantics)));
        qRes.add(new QueryResult("352" , searcher.answerquery("British Chunnel impact", withSemantics)));
        qRes.add(new QueryResult("358" , searcher.answerquery("blood-alcohol fatalities", withSemantics)));
        qRes.add(new QueryResult("359" , searcher.answerquery("mutual fund predictors ", withSemantics)));
        qRes.add(new QueryResult("362" , searcher.answerquery("human smuggling ", withSemantics)));
        qRes.add(new QueryResult("367" , searcher.answerquery("piracy ", withSemantics)));
        qRes.add(new QueryResult("373" , searcher.answerquery("encryption equipment export ", withSemantics)));
        qRes.add(new QueryResult("374" , searcher.answerquery("Nobel prize winners ", withSemantics)));
        qRes.add(new QueryResult("377" , searcher.answerquery("cigar smoking ", withSemantics)));
        qRes.add(new QueryResult("380" , searcher.answerquery("obesity medical treatment ", withSemantics)));
        qRes.add(new QueryResult("384" , searcher.answerquery("space station moon ", withSemantics)));
        qRes.add(new QueryResult("385" , searcher.answerquery("hybrid fuel cars ", withSemantics)));
        qRes.add(new QueryResult("387" , searcher.answerquery("radioactive waste ", withSemantics)));
        qRes.add(new QueryResult("388" , searcher.answerquery("organic soil enhancement ", withSemantics)));
        qRes.add(new QueryResult("390" , searcher.answerquery("orphan drugs ", withSemantics)));

        Searcher.outputResults(qRes, pathToResultsOutputFolder);
    }

    public void loadDictionaries(boolean useStemming, String outputFolder) throws IOException, ClassNotFoundException, ClassCastException {
        ObjectInputStream inDictionary = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' + (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.dictionarySaveName )));
        ObjectInputStream inDocDictionary = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.docsDictionaryName )));
        ObjectInputStream inCityDictionay = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.cityDictionaryName)));
        ObjectInputStream inLanguages = new ObjectInputStream(new BufferedInputStream(new FileInputStream(outputFolder + '/' +
                (useStemming ? Indexer.withStemmingOutputFolderName : Indexer.noStemmingOutputFolderName) +'/'+ Indexer.languages)));

        if(useStemming){
            this.mainDictionaryWithStemming = (Map<String, IndexEntry>) inDictionary.readObject();
            this.docDictionaryWithStemming = (ArrayList)inDocDictionary.readObject();
        }
        else{
            this.mainDictionaryNoStemming = (Map<String, IndexEntry>) inDictionary.readObject();
            this.docDictionaryNoStemming = (ArrayList) inDocDictionary.readObject();
        }
        this.cityDictionary = (Map<String , CityIndexEntry>) inCityDictionay.readObject();
        this.languages = (Set<String>) inLanguages.readObject();

        inDictionary.close();
        inDocDictionary.close();
        inCityDictionay.close();
        inLanguages.close();
    }
}