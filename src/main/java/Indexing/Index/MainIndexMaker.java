package Indexing.Index;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;
import com.sun.xml.internal.bind.v2.TODO;

import java.util.*;

public class MainIndexMaker extends AIndexMaker {
    private Map<Term,tempIndexEntry> tempDictionary;
    private int numOfDocs;

    public MainIndexMaker (){
        super();
        this.tempDictionary=new LinkedHashMap<>();
        numOfDocs=0;

    }

    @Override
    public void addToIndex(TermDocument doc) {
        if(doc != null){

            Set<Term> uniqueWords=new HashSet<>();
            Map<Term,Integer> tfMap=new HashMap<>();
            Map<Term , Byte> special = new HashMap<Term ,Byte>();
            List<Term> title = doc.getTitle();
            List<Term> text = doc.getText();
            int maxTf = getMaxTf(uniqueWords,tfMap,title,text);
            int numOfUniqueWords = uniqueWords.size();








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
        int beginning = (int)(text.size()*0.1);

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

        for(Term t : text){
            int count =0;
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
                special.put(t,(byte)(special.get(t)+1));
            }
            beginning++;
        }
        return maxTf;
    }


   //TODO
    private void mergeIndex(){

    }



}
