package Indexing.Index;

public class IndexEntry {
    private int totalTF;
    private int df = 0;
    private int postingPointer;


    public IndexEntry(int totalTF , int df ) {
        this.totalTF = totalTF;
        this.df = df;
        this.postingPointer=postingPointer;
    }

    public void setTotalTF(int totalTF) {
        this.totalTF = totalTF;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public void setPostingPointer(int postingPointers) {
        this.postingPointer = postingPointers;
    }

    public int getTotalTF() {
        return totalTF;
    }

    public int getDf() {
        return df;
    }

    public int getPostingPointer() {
        return postingPointer;
    }
}
