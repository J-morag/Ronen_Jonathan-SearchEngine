package Querying;

import Indexing.DocumentProcessing.Term;
import Indexing.Index.CityIndexEntry;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IndexEntry;
import Indexing.Index.Indexer;
import Querying.Ranking.Ranker;
import Querying.Ranking.RankingParameters;
import Querying.Ranking.WeightedBM25Ranker;
import Querying.Semantics.SemanticEngine;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class SearcherTest {

    private final String pathToPostingsFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\indexing";
    private final String pathToResultsOutputFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults";
    private final String pathToGloVeFilesFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\GloVe\\customVectors";


    Ranker ranker;
    Searcher searcher;
    Map<String, IndexEntry> mainDictionaryWithStemming;
    ArrayList<DocIndexEntery> docDictionaryWithStemming;
    Map<String, IndexEntry> mainDictionaryNoStemming;
    ArrayList<DocIndexEntery> docDictionaryNoStemming;
    Map<String , CityIndexEntry> cityDictionary;
    Set<String> languages;

    void initialize(boolean useStemming, int kNeighbors, HashSet<String> cityList, RankingParameters rankingParameters) throws IOException, ClassNotFoundException {
        loadDictionaries(useStemming, pathToPostingsFolder);
        int numDocsInCorpus = useStemming? docDictionaryWithStemming.size() : docDictionaryNoStemming.size();

        double averageDocLength = 0.0;
        for (DocIndexEntery doc: useStemming? docDictionaryWithStemming : docDictionaryNoStemming
                ) {
            averageDocLength += doc.getLength();
        }
        averageDocLength /= numDocsInCorpus;

        ranker = new WeightedBM25Ranker(rankingParameters, numDocsInCorpus, averageDocLength);

        searcher = new Searcher(useStemming? mainDictionaryWithStemming : mainDictionaryNoStemming, cityDictionary,
                useStemming? docDictionaryWithStemming : docDictionaryNoStemming, useStemming,
                pathToPostingsFolder + (useStemming? "/postingWithStemming/Postings" : "/postingWithOutStemming/Postings"),
                new SemanticEngine(pathToGloVeFilesFolder, kNeighbors), ranker, cityList);
    }

    @Test
    void EBM25Test() throws IOException, ClassNotFoundException, InterruptedException {
        initialize(true, 5, new HashSet<>(),
                new RankingParameters(1.2, 0.2, 1, 0.35, 0, 1.6, 0.75));
        boolean withSemantics = true;

        List<QueryResult> qRes = new ArrayList<>();

        qRes.add(new QueryResult("351", convertFromSerialIDtoDocID( searcher.answerquery("Falkland petroleum exploration", withSemantics))));
        qRes.add(new QueryResult("352" , convertFromSerialIDtoDocID(searcher.answerquery("British Chunnel impact", withSemantics))));
        qRes.add(new QueryResult("358" ,convertFromSerialIDtoDocID( searcher.answerquery("blood-alcohol fatalities", withSemantics))));
        qRes.add(new QueryResult("359" , convertFromSerialIDtoDocID( searcher.answerquery("mutual fund predictors ", withSemantics))));
        qRes.add(new QueryResult("362" , convertFromSerialIDtoDocID(searcher.answerquery("human smuggling ", withSemantics))));
        qRes.add(new QueryResult("367" , convertFromSerialIDtoDocID(searcher.answerquery("piracy ", withSemantics))));
        qRes.add(new QueryResult("373" , convertFromSerialIDtoDocID (searcher.answerquery("encryption equipment export ", withSemantics))));
        qRes.add(new QueryResult("374" , convertFromSerialIDtoDocID (searcher.answerquery("Nobel prize winners ", withSemantics))));
        qRes.add(new QueryResult("377" , convertFromSerialIDtoDocID(searcher.answerquery("cigar smoking ", withSemantics))));
        qRes.add(new QueryResult("380" , convertFromSerialIDtoDocID(searcher.answerquery("obesity medical treatment ", withSemantics))));
        qRes.add(new QueryResult("384" , convertFromSerialIDtoDocID(searcher.answerquery("space station moon ", withSemantics))));
        qRes.add(new QueryResult("385" , convertFromSerialIDtoDocID(searcher.answerquery("hybrid fuel cars ", withSemantics))));
        qRes.add(new QueryResult("387" , convertFromSerialIDtoDocID(searcher.answerquery("radioactive waste ", withSemantics))));
        qRes.add(new QueryResult("388" , convertFromSerialIDtoDocID(searcher.answerquery("organic soil enhancement ", withSemantics))));
        qRes.add(new QueryResult("390" , convertFromSerialIDtoDocID(searcher.answerquery("orphan drugs ", withSemantics))));
        Searcher.outputResults(qRes, pathToResultsOutputFolder);

        Runtime rt = Runtime.getRuntime();
//        rt.exec("cd C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\");
        String[] commands = {"C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\treceval.exe",
                "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\qrels",
                "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\results.txt"};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    @Test
    void statisticsForReport() throws IOException, ClassNotFoundException {
        boolean useStemming = true;
        boolean withSemantics = false;
        initialize(useStemming, 5, new HashSet<>(),
                new RankingParameters(1.2, 0.2, 1, 0.35, 0, 1.6, 0.75));

        List<QueryResult> qRes = new ArrayList<>();
        List<String> qTexts = new ArrayList<>();

        qRes.add(new QueryResult("351", convertFromSerialIDtoDocID( searcher.answerquery("Falkland petroleum exploration", withSemantics))));
        qTexts.add("Falkland petroleum exploration");
        qRes.add(new QueryResult("352" , convertFromSerialIDtoDocID(searcher.answerquery("British Chunnel impact", withSemantics))));
        qTexts.add("British Chunnel impact");
        qRes.add(new QueryResult("358" ,convertFromSerialIDtoDocID( searcher.answerquery("blood-alcohol fatalities", withSemantics))));
        qTexts.add("blood-alcohol fatalities");
        qRes.add(new QueryResult("359" , convertFromSerialIDtoDocID( searcher.answerquery("mutual fund predictors ", withSemantics))));
        qTexts.add("mutual fund predictors ");
        qRes.add(new QueryResult("362" , convertFromSerialIDtoDocID(searcher.answerquery("human smuggling ", withSemantics))));
        qTexts.add("human smuggling ");
        qRes.add(new QueryResult("367" , convertFromSerialIDtoDocID(searcher.answerquery("piracy ", withSemantics))));
        qTexts.add("piracy ");
        qRes.add(new QueryResult("373" , convertFromSerialIDtoDocID (searcher.answerquery("encryption equipment export ", withSemantics))));
        qTexts.add("encryption equipment export ");
        qRes.add(new QueryResult("374" , convertFromSerialIDtoDocID (searcher.answerquery("Nobel prize winners ", withSemantics))));
        qTexts.add("Nobel prize winners ");
        qRes.add(new QueryResult("377" , convertFromSerialIDtoDocID(searcher.answerquery("cigar smoking ", withSemantics))));
        qTexts.add("cigar smoking ");
        qRes.add(new QueryResult("380" , convertFromSerialIDtoDocID(searcher.answerquery("obesity medical treatment ", withSemantics))));
        qTexts.add("obesity medical treatment ");
        qRes.add(new QueryResult("384" , convertFromSerialIDtoDocID(searcher.answerquery("space station moon ", withSemantics))));
        qTexts.add("space station moon ");
        qRes.add(new QueryResult("385" , convertFromSerialIDtoDocID(searcher.answerquery("hybrid fuel cars ", withSemantics))));
        qTexts.add("hybrid fuel cars ");
        qRes.add(new QueryResult("387" , convertFromSerialIDtoDocID(searcher.answerquery("radioactive waste ", withSemantics))));
        qTexts.add("radioactive waste ");
        qRes.add(new QueryResult("388" , convertFromSerialIDtoDocID(searcher.answerquery("organic soil enhancement ", withSemantics))));
        qTexts.add("organic soil enhancement ");
        qRes.add(new QueryResult("390" , convertFromSerialIDtoDocID(searcher.answerquery("orphan drugs ", withSemantics))));
        qTexts.add("orphan drugs ");


        PrintWriter cleaner = new PrintWriter("C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\statisticForReport"
                + (useStemming? "withStemming" : "") + (withSemantics? "withSemantics" : "") + ".txt");
        cleaner.close();

        int textsIndex = 0;
        PrintWriter csvOut = new PrintWriter(new FileOutputStream(
                new File("C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\statisticForReport"
                        + (useStemming? "withStemming" : "") + (withSemantics? "withSemantics" : "") + ".csv"),false));
        csvOut.println("qid,qtext,qPrecision,qRecall,pAt5,pAt15,pAt30,pAt50,Retrieved,Relevant,Rel_ret");
        for (QueryResult queryResult: qRes
             ) {
            List<QueryResult> queryInList = new ArrayList<>();
            queryInList.add(queryResult);
            Searcher.outputResults(queryInList, pathToResultsOutputFolder);

            Runtime rt = Runtime.getRuntime();
//        rt.exec("cd C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\");
            String[] commands = {"C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\treceval.exe",
                    "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\qrels",
                    "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\results.txt"};
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            // read the output from the command and write to file

            //                  qid,qtext,qPrecision,qRecall,pAt5,pAt15,pAt30,pAt50,Retrieved,Relevant,Rel_ret
            String[] csvLine = {",",",",",",",",",",",",",",",",",",",",",", ""};
            PrintWriter out = new PrintWriter(new FileOutputStream(
                    new File("C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\queryResults\\statisticForReport"
                            + (useStemming? "withStemming" : "") + (withSemantics? "withSemantics" : "") + ".txt"),true));
            out.println("Actual Query ID = " + queryResult.getQueryNum());
            csvLine[0] = queryResult.getQueryNum()+",";
            csvLine[1] = qTexts.get(textsIndex);
            textsIndex++;
            System.out.println("Actual Query ID = " + queryResult.getQueryNum());
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                out.println(s);
                System.out.println(s);
                if(s.contains("At    5 docs:")){
                    s = s.replace("  At    5 docs:", "");
                    s = s.trim();
                    csvLine[5] =(s + ",");
                }
                if(s.contains("  At   15 docs:")){
                    s = s.replace("  At   15 docs:", "");
                    s = s.trim();
                    csvLine[6] = (s + ",");
                }
                if(s.contains("  At   30 docs:")){
                    s = s.replace("  At   30 docs:", "");
                    s = s.trim();
                    csvLine[7] = (s + ",");
                }
                if(s.contains("Retrieved:")){
                    s = s.replace("Retrieved:", "");
                    s = s.trim();
                    csvLine[9] = (s + ",");
                }
                if(s.contains("Relevant:")){
                    s = s.replace("Relevant:", "");
                    s = s.trim();
                    csvLine[10] = (s + ",");
                }
                if(s.contains("Rel_ret:")){
                    s = s.replace("Rel_ret:", "");
                    s = s.trim();
                    csvLine[11] = (s);
                }
            }
            out.flush();
            out.close();
            csvOut.println(csvLine[0]+csvLine[1]+csvLine[2]+csvLine[3]+csvLine[4]+csvLine[5]+csvLine[6]+csvLine[7]+csvLine[8]+csvLine[9]+csvLine[10]+csvLine[11]);

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }

        csvOut.flush();
        csvOut.close();


    }

    private List<String> convertFromSerialIDtoDocID(List<String> docsSerialID){
        List<String> toReturn = new ArrayList<>();
        for (String st: docsSerialID) {
            toReturn.add(docDictionaryWithStemming.get(Integer.parseInt(st)).getDocID());
        }
        return toReturn;
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