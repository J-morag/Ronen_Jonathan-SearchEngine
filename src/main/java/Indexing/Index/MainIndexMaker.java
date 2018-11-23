package Indexing.Index;

import Elements.Term;
import Elements.TermDocument;

import java.util.*;

import static javafx.application.Platform.exit;

public class MainIndexMaker extends AIndexMaker {
    private Map<Term, TempIndexEntry> tempDictionary;
    private int numOfDocs;

    public MainIndexMaker (){
        super();
        this.tempDictionary=new LinkedHashMap<>();
        numOfDocs=0;

    }

    @Override
    public void addToIndex(TermDocument doc) {
        if(doc.getSerialID() != -1){
            numOfDocs++;
            Set<Term> uniqueWords=new HashSet<>();
            Map<Term,Integer> tfMap=new HashMap<>();
            Map<Term , Byte> special = new HashMap<Term ,Byte>();
            List<Term> title = doc.getTitle();
            List<Term> text = doc.getText();
            int maxTf = getMaxTf(uniqueWords,tfMap,special,title,text);
            int numOfUniqueWords = uniqueWords.size();
            String city ="";
            if(doc.getCity()!=null) {
                city = doc.getCity().toString();
            }


            String docId = doc.getDocId();
            for(Term term : uniqueWords){
                short tf =  tfMap.get(term).shortValue();

                Posting posting = new Posting(docId,tf ,(short) maxTf,(short)numOfUniqueWords,city,"");
                if(special.get(term)==0){
                    posting.setSpecialInfo(false,false);
                }else if(special.get(term)==1){
                    posting.setSpecialInfo(true,false);
                }else if(special.get(term)==2){
                    posting.setSpecialInfo(false,true);
                }else {
                    posting.setSpecialInfo(true,true);
                }

                if(!tempDictionary.containsKey(term)) {
                    TempIndexEntry tmp = new TempIndexEntry();
                    tmp.addPosting(posting);
                    tempDictionary.put(term,tmp);
                }else {
                    TempIndexEntry tmp = tempDictionary.get(term);
                    tmp.addPosting(posting);
                }


            }


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
    private int getMaxTf( Set<Term> uniqueWords , Map<Term,Integer> tfMap , Map<Term,Byte> special, List<Term> title , List<Term> text){
        int maxTf=0;
        int beginning=0;
        try {
            beginning = (int)(text.size()*0.1);
        }catch (NullPointerException e ){
            System.out.println(numOfDocs);
            exit();

        }


        for(Term t : title){
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
        for(Term t : text){

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



    public Map<Term , TempIndexEntry> getTempDictionary(){
        return tempDictionary;
    }



   //TODO
    private void mergeIndex(){

    }



}
