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
import javafx.geometry.Pos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Searcher {

    private Map<String , IndexEntry> mainDictionary;
    private Map<String , CityIndexEntry> cityDictionary;
    private Map<Integer , DocIndexEntery> docsDictionary;
    private Parse parser;
    private boolean isUsedStemming;
    private String pathToPostings;
    private SemanticEngine semanticEngine;


    //                                                                                                                                                                        ..\\..\\Postings
    public Searcher(Map<String, IndexEntry> mainDictionary, Map<String, CityIndexEntry> cityDictionary, Map<Integer, DocIndexEntery> docsDictionary , boolean isUsedStemming, String pathToPostings,SemanticEngine semanticEngine) {
        this.mainDictionary = mainDictionary;
        this.cityDictionary = cityDictionary;
        this.docsDictionary = docsDictionary;
        this.isUsedStemming = isUsedStemming;
        this.parser = new Parse(new HashSet<String>(), new ArrayBlockingQueue<Document>(0) , new ArrayBlockingQueue<TermDocument>(0) , isUsedStemming);
        this.pathToPostings=pathToPostings;
        this.semanticEngine = semanticEngine;
    }

    public String [] answerQuary(String quary , Boolean withSemantics){

        String [] releventDocumants=null;

        // list to send to Semantic Engine if needed
        parser.useStemming=false;
        List<String> notStemmedListOfStrings =parser.tokenize(quary);
        List<Term> notStemmedListOfTerms = parser.parseWorker(notStemmedListOfStrings);
        Set<Term> noStemmingTermSet = new HashSet<>();
        for (int i = 0; i <notStemmedListOfTerms.size() ; i++) {
            noStemmingTermSet.add(notStemmedListOfTerms.get(i));
        }
        notStemmedListOfTerms.clear();
        notStemmedListOfTerms=null;
        parser.useStemming=isUsedStemming;
        //

        List<String> listOfStrings =parser.tokenize(quary);
        List<Term> listOfTerms = parser.parseWorker(listOfStrings);
        Set<Term> termSet = new HashSet<>();
        for (int i = 0; i <listOfTerms.size() ; i++) {
            termSet.add(listOfTerms.get(i));
        }
        listOfTerms.clear();
        listOfTerms=null;

        List<Posting> quaryPostingList = new ArrayList<>();
        List<Posting> synonymPostingList = new ArrayList<>();

        try {
            PostingInputStream postingInputStream = new PostingInputStream(pathToPostings);


            for (Term term : termSet) {
                List<Posting> tempPosting = new ArrayList<>();
                String stringTerm = term.toString();
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
                quaryPostingList.addAll(tempPosting);


            }
//            if(withSemantics){
//
//            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return releventDocumants ;
    }


}
