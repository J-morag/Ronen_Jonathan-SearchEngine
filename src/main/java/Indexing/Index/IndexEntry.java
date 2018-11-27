package Indexing.Index;

public class IndexEntry {
    int totalTF;
    int df = 0;
    String term;
    int postingsFileSerial;
    int[] postingPointers;


    public IndexEntry(String term, int postingsFileName, int numTempFiles , int df ,int totalTF) {
        this.term = term;
        this.postingsFileSerial = postingsFileSerial;
        this.postingPointers = new int[numTempFiles];
        this.df=df;
        this.totalTF=totalTF;
    }

    public String getTerm() {
        return term;
    }
}
