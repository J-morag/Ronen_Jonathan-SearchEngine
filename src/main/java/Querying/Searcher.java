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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Searcher {

    private Map<String , IndexEntry> mainDictionary;
    private Map<String , CityIndexEntry> cityDictionary;
    private Map<Integer , DocIndexEntery> docsDictionary;
    private Parse parser;
    private boolean isUsedStemming;
    private String pathToPostings;

    //                                                                                                                                                                        ..\\..\\Postings
    public Searcher(Map<String, IndexEntry> mainDictionary, Map<String, CityIndexEntry> cityDictionary, Map<Integer, DocIndexEntery> docsDictionary , boolean isUsedStemming, String pathToPostings) {
        this.mainDictionary = mainDictionary;
        this.cityDictionary = cityDictionary;
        this.docsDictionary = docsDictionary;
        this.isUsedStemming = isUsedStemming;
        this.parser = new Parse(new HashSet<String>(), new ArrayBlockingQueue<Document>(0) , new ArrayBlockingQueue<TermDocument>(0) , isUsedStemming);
        this.pathToPostings=pathToPostings;
    }

    public String [] answerQuary(String quary){

        String [] releventDocumants=null;

        List<String> listOfStrings =parser.tokenize(quary);
        List<Term> listOfTerms = parser.parseWorker(listOfStrings);
       //listOfTerms.
        List<Posting> postingList = new ArrayList<>();
        try {
            PostingInputStream postingInputStream = new PostingInputStream(pathToPostings);


            for (Term term : listOfTerms) {
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


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }






        return releventDocumants ;
    }


}
