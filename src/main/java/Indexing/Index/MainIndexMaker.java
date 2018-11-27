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
    private Map <String, TempIndexEntry> mainDictionary;
    private Map <Integer,DocIndexEntery> docsDictionary;
    private int numOfDocs;
    private byte tempFileNumber;
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
            Map<String , Byte> special = new HashMap<String, Byte>(); // map that indicates if a term is in the tile to in a beginning of the text or none
            List<Term> title = doc.getTitle();
            List<Term> text = doc.getText();
            int maxTf = getMaxTf(uniqueWords,tfMap,special,title,text);
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
                if(special.get(term)==0){
                    posting.setInTitle(false);
                    posting.setInBeginning(false);
                }else if(special.get(term)==1){
                    posting.setInTitle(true);
                    posting.setInBeginning(false);
                }else if(special.get(term)==2){
                    posting.setInTitle(false);
                    posting.setInBeginning(true);
                }else {
                    posting.setInTitle(true);
                    posting.setInBeginning(true);
                }

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
            special = null;
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
     * @param special - an empty Hash map that indicate if a term is in the title or in the first 10% of the document
     *                if the value is
     * @return the maxTF of a term in the Document
     */
    private int getMaxTf( Set<String> uniqueWords , Map<String,Integer> tfMap , Map<String,Byte> special, List<Term> title , List<Term> text){
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
            if(!special.containsKey(t)){
                special.put(t,new Byte((byte)1));
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
            if((count<=beginning) && (!special.containsKey(t))){
                special.put(t , new Byte((byte)2));
            }
            else if(count<= beginning && special.containsKey(t)){
                if(special.get(t) == 1){
                    special.put(t, new Byte((byte)3));
                }
            } else if(count>beginning && !special.containsKey(t)){
                special.put(t,new Byte((byte)0));
            }
            count++;
        }
        return maxTf;
    }



    public Map<String , TempIndexEntry> getTempDictionary(){

        return tempDictionary;
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
    public void mergeIndex(Set<String> uniqueWords){
        String[] allTerms = uniqueWords.stream().toArray(String[]::new);

        Arrays.parallelSort(allTerms);

    }




}
