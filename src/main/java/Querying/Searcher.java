package Querying;

import Indexing.DocumentProcessing.Document;
import Indexing.DocumentProcessing.Parse;
import Indexing.DocumentProcessing.Term;
import Indexing.DocumentProcessing.TermDocument;
import Indexing.Index.CityIndexEntry;
import Indexing.Index.DocIndexEntery;
import Indexing.Index.IO.PostingInputStream;
import Indexing.Index.IndexEntry;
import Indexing.Index.Posting;
import Querying.Semantics.SemanticEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Searcher {

    private Map<String , IndexEntry> mainDictionary;
    private Map<String , CityIndexEntry> cityDictionary;
    private List<DocIndexEntery> docsDictionary;
    private Parse parser;
    private boolean isUsedStemming;
    private String pathToPostings;
    private SemanticEngine semanticEngine;
    private Ranker ranker;
    private Set<String> cityListFilter;


    //                                                                                                                                                                        ..\\..\\Postings
    public Searcher(Map<String, IndexEntry> mainDictionary, Map<String, CityIndexEntry> cityDictionary, List<DocIndexEntery> docsDictionary , boolean isUsedStemming, String pathToPostingsDir,SemanticEngine semanticEngine , Ranker ranker , HashSet<String> cityList) {
        this.mainDictionary = mainDictionary;
        this.cityDictionary = cityDictionary;
        this.docsDictionary = docsDictionary;
        this.isUsedStemming = isUsedStemming;
        this.parser = new Parse(new HashSet<String>(), new ArrayBlockingQueue<Document>(1) , new ArrayBlockingQueue<TermDocument>(1) , isUsedStemming);
        this.pathToPostings= pathToPostingsDir;
        this.semanticEngine = semanticEngine;
        this.ranker =ranker;
        this.cityListFilter=cityList;
    }

    /**
     * This method answer's a query given from the user and returns a sorted List of documents that are relevant to the query
     * it considers if the user wants to use a semantic analysis or not.
     * @param query - the query needed to be answer
     * @param withSemantics - a boolean variable  true - if need to use semantic analysis. otherwise
     * @return releventDocumants - list of docs id sorted by their relevancy
     */
    public List<String> answerquery(String query , Boolean withSemantics){

        List<String> relevantDocuments=new ArrayList<>();
        Set<String> noStemmingTermSet=null;
        String [] synonymArr= new String[0];
        if(withSemantics) {
            // list to send to Semantic Engine if needed
            parser.useStemming = false;
            List<String> notStemmedListOfStrings = parser.tokenize(query);
            List<Term> notStemmedListOfTerms = parser.parseWorker(notStemmedListOfStrings);
            noStemmingTermSet = new HashSet<>();
            for (int i = 0; i < notStemmedListOfTerms.size(); i++) {
                noStemmingTermSet.add(notStemmedListOfTerms.get(i).toString());
            }
            notStemmedListOfTerms.clear();
            notStemmedListOfTerms = null;
            parser.useStemming = isUsedStemming;
            //
        }

        List<String> listOfStrings =parser.tokenize(query);
        List<Term> listOfTerms = parser.parseWorker(listOfStrings);
        Set<String> termSet = new HashSet<>();
        for (int i = 0; i <listOfTerms.size() ; i++) {
            termSet.add(listOfTerms.get(i).toString());
        }
        listOfTerms.clear();
        listOfTerms=null;

        List<ExpandedPosting> queryPostingList = new ArrayList<>();
        List<ExpandedPosting> synonymPostingList = new ArrayList<>();

        try {
            PostingInputStream postingInputStream = new PostingInputStream(pathToPostings);


            for (String term : termSet) {
                List<Posting> tempPosting = new ArrayList<>();
                String stringTerm = term;
                if (mainDictionary.containsKey(stringTerm.toUpperCase())){
                    stringTerm = stringTerm.toUpperCase();
                }
                else if(mainDictionary.containsKey(stringTerm.toLowerCase())) {
                    stringTerm = stringTerm.toLowerCase();
                }
                else {
                    continue;
                }
                int pointer= mainDictionary.get(stringTerm).getPostingPointer();
                tempPosting = postingInputStream.readTermPostings(pointer);
                for (Posting posting : tempPosting ) {
                    int totalTF = mainDictionary.get(stringTerm).getTotalTF();
                    int df = mainDictionary.get(stringTerm).getDf();
                    int numOfUniqueWords = docsDictionary.get(posting.getDocSerialID()).getNumOfUniqueWords();
                    int maxTFdoc = docsDictionary.get(posting.getDocSerialID()).getMaxTF();
                    int docLength = docsDictionary.get(posting.getDocSerialID()).getLength();
                    queryPostingList.add(new ExpandedPosting(posting,totalTF,df,numOfUniqueWords,maxTFdoc,docLength,stringTerm));
                }


            }
            if(withSemantics){
                List<String> synonymList= semanticEngine.getNearestNeighbors(noStemmingTermSet);
                synonymArr = synonymList.toArray(new String[synonymList.size()]);
                for (String synonym : synonymList ) {
                    List<Posting> tempPosting = new ArrayList<>();
                    String stringTerm = synonym;
                    if (mainDictionary.containsKey(stringTerm.toUpperCase())){
                        stringTerm = stringTerm.toUpperCase();
                    }
                    else if(mainDictionary.containsKey(stringTerm.toLowerCase())) {
                        stringTerm = stringTerm.toLowerCase();
                    }
                    else {
                        continue;
                    }
                    int pointer= mainDictionary.get(stringTerm).getPostingPointer();
                    tempPosting = postingInputStream.readTermPostings(pointer);
                    for (Posting posting : tempPosting ) {
                        int totalTF = mainDictionary.get(stringTerm).getTotalTF();
                        int df = mainDictionary.get(stringTerm).getDf();
                        int numOfUniqueWords = docsDictionary.get(posting.getDocSerialID()).getNumOfUniqueWords();
                        int maxTFdoc = docsDictionary.get(posting.getDocSerialID()).getMaxTF();
                        int docLength = docsDictionary.get(posting.getDocSerialID()).getLength();
                        synonymPostingList.add(new ExpandedPosting(posting,totalTF,df,numOfUniqueWords,maxTFdoc,docLength,stringTerm));
                    }
                }

            }
            postingInputStream.close();

            String [] queryArr =termSet.toArray(new String[termSet.size()]);
            List<Integer> filterdRankedDocs =null;

            List<Integer>  renkedDocsList= ranker.rank(queryPostingList,synonymPostingList,queryArr ,synonymArr);
            if(cityListFilter.size()>0){
                 filterdRankedDocs = filterDocsByCity(renkedDocsList);
                for (Integer docSerialKye : filterdRankedDocs ) {
                    relevantDocuments.add(docsDictionary.get(docSerialKye).getDocID());
                }
            }
            else
            {
                for (Integer docSerialKye : renkedDocsList ) {
                    relevantDocuments.add(docsDictionary.get(docSerialKye).getDocID());
                }
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return relevantDocuments.size()>50 ? relevantDocuments.subList(0,50) : relevantDocuments ;
    }

    /**
     * takes out all Docs that don't represented by the city filter
     * @param renkedeDocsList- a sorted List of docs by their relevance
     * @return -  sorted list of docs after removing all docs that wasn't represented by any city in the #cityFilter List
     */
    private List<Integer> filterDocsByCity(List<Integer> renkedeDocsList){
        List<Integer> toReturn = new ArrayList<>();
        Set<Integer> docsWithCityInText = getAllDocsWithCityInText();
        for (int i = 0; i <renkedeDocsList.size() ; i++) {
            Integer docKey = renkedeDocsList.get(i);
            if((cityListFilter.contains(docsDictionary.get(docKey).getCity())) || docsWithCityInText.contains(docKey) ) {
                toReturn.add(docKey);
            }
        }

        return toReturn;
    }



    /**
     * get all docs number that one or more then the cities in #cityFilter appeared in text
     * @return set of all doc number that contain a city name from #cityFilter in the text
     */
    private Set<Integer> getAllDocsWithCityInText() {
        Set<Integer> allDocsWithCityInText = new HashSet<>();
        try {
            PostingInputStream postingInputStream = new PostingInputStream(pathToPostings);


            for (String term : cityListFilter) {
                List<Posting> tempPosting = new ArrayList<>();
                String stringTerm = term;
                if (mainDictionary.containsKey(stringTerm.toUpperCase())){
                    stringTerm = stringTerm.toUpperCase();
                }
                else if(mainDictionary.containsKey(stringTerm.toLowerCase())) {
                    stringTerm = stringTerm.toLowerCase();
                }
                else {
                    continue;
                }
                int pointer= mainDictionary.get(stringTerm).getPostingPointer();

                tempPosting = postingInputStream.readTermPostings(pointer);
                for (Posting posting : tempPosting ) {
                    allDocsWithCityInText.add(new Integer(posting.getDocSerialID()));
                }



            }
            postingInputStream.close();
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allDocsWithCityInText;

    }

    /**
     * Prints the results of any number of queries to a file.
     * Prints in the format of TREC_EVAL.
     * @param l_queryResults the results of any number of queries. Pairs of (query number, returned documents).
     * @param pathToOutputFolder the folder to put the results file in.
     * @throws FileNotFoundException if the pathToOutputFolder is invalid.
     */
    public static void outputResults(List<QueryResult> l_queryResults, String pathToOutputFolder) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(pathToOutputFolder+"/results.txt");

        for (QueryResult queryResult: l_queryResults
             ) {
            String queryID = queryResult.getQueryNum();
            for (String docID: queryResult.getRelevantDocs()) {
                printWriter.println(queryID + " 0 " + docID + " 0 0 Run_id");
            }
        }
        printWriter.flush();
        printWriter.close();
    }
}
