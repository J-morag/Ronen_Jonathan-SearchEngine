package Indexing.Index;

import sun.util.locale.provider.SPILocaleProviderAdapter;

import java.util.Arrays;
import java.util.List;

public class Posting {

    private short tf;
    private short maxTf;
    private short uniqueWord;
    private String docID;
    private String city;
    private String language;
    private boolean isInTitle = false;
    private boolean isInBeginning = false;

    public Posting(String docID , short tf , short maxTf , short uniqueWord, String city ,  String language){
        this.docID = docID;
        this.tf = tf;
        this.maxTf = maxTf;
        this.uniqueWord= uniqueWord;
        this.city=city;
        this.language=language;
    }

    public Posting(String docID , short tf , short maxTf , short uniqueWord, String city ,  String language, boolean isInTitle, boolean isInBeginning){
        this.docID = docID;
        this.tf = tf;
        this.maxTf = maxTf;
        this.uniqueWord= uniqueWord;
        this.city=city;
        this.language=language;
        this.isInTitle = isInTitle;
        this.isInBeginning = isInBeginning;
    }

    public Posting(short[] shorts, String[] strings, boolean[] bools){
        tf = shorts[0];
        maxTf = shorts[1];
        uniqueWord = shorts[2];
        docID = strings[0];
        city = strings[1];
        language = strings[2];
        isInTitle = bools[0];
        isInBeginning = bools[1];
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

    public boolean isInTitle(){
        return isInTitle;
    }

    public boolean isInBeginning() {
        return isInBeginning;
    }

    public short[] getShortFields(){
        return new short[]{this.tf, this.maxTf, this.uniqueWord};
    }

    public String[] getStringFields(){
        return new String[]{docID, city, language};
    }
    public boolean[] getBooleanFields(){
        return new boolean[]{isInTitle, isInBeginning};
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

    public void setInTitle(boolean isInTitle){
        this.isInTitle = isInTitle;
    }

    public void setInBeginning(boolean inBeginning) {
        isInBeginning = inBeginning;
    }


    @Override
    public String toString(){
        return docID+","+tf+","+","+maxTf+","+uniqueWord+","+city+","+specialInfo[0]+","+specialInfo[1];
    }
}
