package Indexing.Index;

import org.omg.CORBA.PRIVATE_MEMBER;

public class DocIndexEntery {

    private String docID;
    private int numOfUniqueWords;
    private int maxTF;
    private String city;
    private String language;

    public DocIndexEntery(String  docID, int numOfUniqueWords,int maxTF , String city, String language){

        this.docID=docID;
        this.numOfUniqueWords=numOfUniqueWords;
        this.maxTF=maxTF;
        this.city=city;
        this.language=language;
    }

    public String getDocID() {
        return docID;
    }

    public int getNumOfUniqueWords() {
        return numOfUniqueWords;
    }

    public int getMaxTF() {
        return maxTF;
    }

    public String getCity() {
        return city;
    }

    public String getLanguage() {
        return language;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public void setNumOfUniqueWords(int numOfUniqueWords) {
        this.numOfUniqueWords = numOfUniqueWords;
    }

    public void setMaxTF(int maxTF) {
        this.maxTF = maxTF;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}
