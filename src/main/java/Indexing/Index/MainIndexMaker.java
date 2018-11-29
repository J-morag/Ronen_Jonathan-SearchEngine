package Indexing.Index;

import Elements.Term;
import Elements.TermDocument;
import Indexing.Index.IO.*;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

import static javafx.application.Platform.exit;

public class MainIndexMaker extends AIndexMaker {

    //the size of the group of documents that will be indexed every time.
    private static final short partialGroupSize = 10000;


    private Map<String, TempIndexEntry> tempDictionary;
    private Map <String, IndexEntry> mainDictionary;
    private Map <Integer,DocIndexEntery> docsDictionary;
    private int numOfDocs;
    private short tempFileNumber;
    private String path="";

    public MainIndexMaker (String path ){
        super();
        this.tempDictionary=new LinkedHashMap<>();
        this.mainDictionary = new LinkedHashMap<>();
        this.docsDictionary = new LinkedHashMap<>();
        numOfDocs=0;
        tempFileNumber=0;
        this.path=path;

    }

    @Override
    public void addToIndex(TermDocument doc) {
        if(doc.getSerialID() != -1){

            Set<String> uniqueWords=new HashSet<>();// set of all unique words in a doc
            Map<String,Integer> tfMap=new HashMap<>(); // map  term to his tf value in this doc
            List<Term> title = doc.getTitle();
            List<Term> text = doc.getText();
            int maxTf = getMaxTf(uniqueWords,tfMap,title,text);
            int numOfUniqueWords = uniqueWords.size();
            String docId = doc.getDocId();
            String city ="";
            String language = "";
            if(doc.getCity()!=null) {
                city = doc.getCity().toString();
            }
            if(doc.getLanguage()!=null) {
                language = doc.getLanguage();
            }

// add a document to the DocIndex
            DocIndexEntery docIndexEntery = new DocIndexEntery(docId,numOfUniqueWords,maxTf,city,language);
            docsDictionary.put(doc.getSerialID(),docIndexEntery);
            docIndexEntery=null;


// create the posting to the doc and add is to the index entery or creat a new index entery if not exist
            for(String term : uniqueWords){
                short tf =  tfMap.get(term).shortValue();
                Posting posting = new Posting(doc.getSerialID(), tf);

                int beginning=0;
                try {
                    beginning = (int)(text.size()*0.1);
                }catch (NullPointerException e ){
                    System.out.println(numOfDocs);
                    exit();

                }

                if (title.contains(new Term(term))){
                    posting.setInTitle(true);
                }

                boolean isInBeginning=false;
                for (int i = 0; i <beginning && !isInBeginning ; i++) {
                    if(term.equals(text.get(i).toString()))
                        isInBeginning=true;
                }

                posting.setInBeginning(isInBeginning);

                if(!tempDictionary.containsKey(term)) {
                    TempIndexEntry tmp = new TempIndexEntry();
                    tmp.addPosting(posting);
                    tmp.increaseTfByN(tfMap.get(term).shortValue());
                    posting=null;
                    tempDictionary.put(term,tmp);
                }else {
                    TempIndexEntry tmp = tempDictionary.get(term);
                    tmp.increaseTfByN(tfMap.get(term).shortValue());
                    tmp.addPosting(posting);
                    posting=null;
                }
            }
            numOfDocs++;
            if(numOfDocs==partialGroupSize){
                dumpToDisk();
            }
            tfMap=null;
        }else {
            dumpToDisk();
        }


    }


    /**
     * this function go over every term in a list and calculate the maxTF for the doc
     * also it filling the set of unique words with every unique word that in the document
     * and also fillingg the map of terms that saves the tf for every word in the doc
     * and fiinaly check if a term is in the title or in the first 20% of a document and update the spacial map
     * @param uniqueWords - an empty Set that will contain every unique term in the doc
     * @param tfMap - an empty Hash map that counts , for each Term it's tf in the doc
     * @return the maxTF of a term in the Document
     */
    private int getMaxTf( Set<String> uniqueWords , Map<String,Integer> tfMap, List<Term> title , List<Term> text){
        int maxTf=0;
        int beginning=0;
        try {
            beginning = (int)(text.size()*0.1);
        }catch (NullPointerException e ){
            System.out.println(numOfDocs);
            exit();

        }

        for(Term term : title){
            String t = term.toString();
            uniqueWords.add(t);
            if(tfMap.containsKey(t)){
                tfMap.put(t, tfMap.get(t)+1);
            }
            else{
                tfMap.put(t, 1);
            }

            int value = tfMap.get(t);
            if(value > maxTf){
                maxTf = value;
            }

        }
        int count =0;
        for(Term term : text){
            String t = term.toString();
            uniqueWords.add(t);
            if(tfMap.containsKey(t)){
                tfMap.put(t, tfMap.get(t)+1);
            }
            else{
                tfMap.put(t, 1);
            }

            int value = tfMap.get(t);
            if(value > maxTf){
                maxTf = value;
            }
            count++;
        }
        return maxTf;
    }



    public Map<String , TempIndexEntry> getTempDictionary(){

        return tempDictionary;
    }

    public Map<String , IndexEntry> getMainDictionary(){
        return mainDictionary;
    }


    public Map<Integer , DocIndexEntery> getDocsDictionary(){
        return docsDictionary;
    }



    public void  dumpToDisk()
    {

        try {
            IPostingOutputStream outputStream = new PostingOutputStream(path+"\\temp"+tempFileNumber+".txt");//@todo change to other postingOutPutStream
            TempIndexEntry tmp =null;
            numOfDocs=0;
            for (String term : tempDictionary.keySet()) {
                tmp = tempDictionary.get(term);
                if (tmp.getPostingSize() > 0) {
                    tmp.sortPosting();
                    int pointer = (int)outputStream.write(tmp.getPosting());
                    tmp.addPointer(tempFileNumber, pointer);
                    tmp.deletePostingList();
                }
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempFileNumber++;

    }



   //@TODO
    public void mergeIndex()
    {
        Set<String> uniqueWords = tempDictionary.keySet();
        String[] allTerms = uniqueWords.stream().toArray(String[]::new);
       // Arrays.parallelSort(allTerms);
        //TESTING
        System.out.println("num Of Terms before merge: "+ allTerms.length+"\n");
        //\TESTING


        try {
            IPostingOutputStream postingOutputStream=new PostingOutputStream(path+"\\Postings");
            for (String term: allTerms ) {
                if(!tempDictionary.containsKey(term)){
                    continue;
                }
                List<Posting> postingToWrite = new ArrayList<>();
               String finalTerm = addTermToDictionary(term , postingToWrite);
                int pointer =(int)postingOutputStream.write(postingToWrite);
                mainDictionary.get(finalTerm).setPostingPointer(pointer);

            }
            postingOutputStream.flush();
            postingOutputStream.close();

        } catch (NullPointerException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        //TESTING
        System.out.println("num Of Terms after merge: "+ mainDictionary.keySet().size()+"\n");
        //\TESTING
        tempDictionary=null;



       try {

           File file = new File(path);
           for (File fi : file.listFiles()) {
              if (!(fi.getName().equals("Postings"))) {
                  fi.delete();
              }
           }
       }catch (NullPointerException e){
           e.printStackTrace();

       }


    }


    private String addTermToDictionary(String term , List<Posting> finalPosting) {
        String termToWrite=term;

        try {

            int totalTF = tempDictionary.get(term).getTfTotal();
            char c = term.charAt(0);
            List<Posting> postingList = getTermPostings(term);
            if (c >= 'a' && c <= 'z') { //if first letter is lower case
                termToWrite = term;
                char[] termArray = term.toCharArray();
                termArray[0] = (char) (c - 32);
                String newTerm =new String(termArray);


                if (tempDictionary.containsKey(newTerm)) { // if there is also the same term with upper case in the corpus
                    List<Posting> newTermPostings = getTermPostings(newTerm);
                    totalTF += tempDictionary.get(newTerm).getTfTotal();
                    finalPosting.addAll(mergePostings(postingList, newTermPostings));
                    tempDictionary.remove(newTerm);
                }else{
                    finalPosting.addAll(postingList);
                }


            } else if (c >= 'A' && c <= 'Z') { // if firs letter is a upper case
                char[] termArray = term.toCharArray();
                termArray[0] = (char) (c + 32);

                String newTerm =new String(termArray);

                if (tempDictionary.containsKey(newTerm)) { // if there is also the same term with LOWER case in the corpus
                    List<Posting> newTermPostings = getTermPostings(newTerm);
                    totalTF += tempDictionary.get(newTerm).getTfTotal();
                    finalPosting.addAll(mergePostings(postingList, newTermPostings));
                    tempDictionary.remove(newTerm);
                    termToWrite = term.toLowerCase();
                } else {
                    termToWrite = term.toUpperCase();
                    finalPosting.addAll(postingList);
                }
            } else {
                termToWrite = term;
                finalPosting.addAll(postingList);
            }

            tempDictionary.remove(term);
            IndexEntry indexEntry = new IndexEntry(totalTF, finalPosting.size());
            mainDictionary.put(termToWrite, indexEntry);

        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return termToWrite;
    }


    private List<Posting>getTermPostings(String term)
    {
        List<Posting> postingList = new ArrayList<>();
        int[] pointers = tempDictionary.get(term).getPointerList();
        int length = pointers.length;
        for (int i = 0; i < length; i++) {
            if (pointers[i] != -1) {
                IPostingInputStream inputStream = null;
                try {
                    inputStream = new PostingInputStream(path + "\\temp" + i + ".txt");
                    List<Posting> tempPostings = inputStream.readTermPostings((long)pointers[i]);
                    ((PostingInputStream) inputStream).close();

                    postingList.addAll(tempPostings);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return postingList;
    }

    private List<Posting> mergePostings(List<Posting>postingList , List<Posting>newTermPostings )
    {

        int maxTF;
        List<Posting> finalList = new ArrayList<>();
        Map<Integer,Posting> newTermPostingMap = new HashMap<>();

        for (Posting post : newTermPostings) {
            newTermPostingMap.put(post.getDocSerialID(),post);
        }



        for (Posting posting: postingList) {
            int docID=posting.getDocSerialID();
            int tf = posting.getTf();

            if(!newTermPostingMap.containsKey(docID)){
                finalList.add(posting);
            }
            else { // if there is the same doc in two different lists of the same term
                Posting samePosting = newTermPostingMap.get(docID);
                tf+=samePosting.getTf();
                Posting newPosting = new Posting(posting.getDocSerialID() ,(short)tf ,posting.isInTitle()||samePosting.isInTitle() ,posting.isInBeginning() || samePosting.isInBeginning());
                finalList.add(newPosting);

                DocIndexEntery docIndexEntery = docsDictionary.get(docID);
                maxTF = docIndexEntery.getMaxTF();
                if(tf>maxTF){
                    docIndexEntery.setMaxTF(tf);
                }
                docIndexEntery.setNumOfUniqueWords(docIndexEntery.getNumOfUniqueWords()-1);
            }
        }
        return finalList;


    }



}
