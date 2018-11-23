package Indexing.Index;

import sun.util.locale.provider.SPILocaleProviderAdapter;

import java.util.List;

public class Posting {

    private String docID;
    private short tf;
    private short maxTf;
    private short uniqueWord;
    private String city;
    private String language;
    private boolean [] specialInfo;

    public Posting(String docID , short tf , short maxTf , short uniqueWord, String city ,  String language){
        this.docID = docID;
        this.tf = tf;
        this.maxTf = maxTf;
        this.uniqueWord= uniqueWord;
        this.city=city;
        this.language=language;
        this.specialInfo = new boolean[2];//(in the title,in beginning of the text)
    }
//GETTERS
    public short getTf() {
        return tf;
    }

    public short getMaxTf() {
        return maxTf;
    }

    public short getUniqueWord() {
        return uniqueWord;
    }

    public String getCity() {
        return city;
    }

    public String getLanguage() {
        return language;
    }

    public String getDocID() {
        return docID;
    }

    public boolean [] getSpecialInfo(){
        return specialInfo;
    }

    //SETTERS
    public void setDocID(String docID) {
        this.docID = docID;
    }

    public void setTf(short tf) {
        this.tf = tf;
    }

    public void setMaxTf(short maxTf) {
        this.maxTf = maxTf;
    }

    public void setUniqueWord(short uniqueWord) {
        this.uniqueWord = uniqueWord;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setSpecialInfo(boolean isInTitel , boolean isInBeginning){
        specialInfo[0]=isInTitel;
        specialInfo[1]=isInBeginning;
    }


    @Override
    public String toString(){
        return docID+","+tf+","+","+maxTf+","+uniqueWord+","+city+","+specialInfo[0]+","+specialInfo[1];
    }
}
