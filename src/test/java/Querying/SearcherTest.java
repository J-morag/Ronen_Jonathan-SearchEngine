package Querying;

import Indexing.Index.CityIndexEntry;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IndexEntry;
import Indexing.Index.Indexer;
import javafx.util.Pair;
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
        RankingParameters rankingParameters = new RankingParameters(0.1, 0.1, 0.9, 0.5, 1.5, 0.75);

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
    void printResultsToFile() throws IOException, ClassNotFoundException {
        initialize(true, 5, new HashSet<>());

        List<Pair<Integer, List<String>>> qRes = new ArrayList<>();
        qRes.add(new Pair<>(1, searcher.answerquery("Communist Party", false)));

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