package Indexing.Index;

public class IndexEntry {
    int df = 0;
    String term;
    int postingsFileSerial;
    int[] postingPointers;


    public IndexEntry(String term, int postingsFileName, int numTempFiles) {
        this.term = term;
        this.postingsFileSerial = postingsFileSerial;
        this.postingPointers = new int[numTempFiles];
    }

    public String getTerm() {
        return term;
    }
}
