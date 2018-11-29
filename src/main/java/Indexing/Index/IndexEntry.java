package Indexing.Index;

public class IndexEntry {
    public final int totalTF;
    public final int df;
    public final int postingPointer;


    public IndexEntry(int postingPointer , int df ,int totalTF) {
        this.postingPointer = postingPointer;
        this.df = df;
        this.totalTF = totalTF;
    }


    @Override
    public String toString() {
        return "totalTF=" + totalTF +
                ", df=" + df ;
    }
}
