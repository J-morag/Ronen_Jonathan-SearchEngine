package Indexing.Index;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class tempIndexEntry {
    private int df;
    private List<Posting> posting;
    private List<Pair<Integer , Integer >> pointerList; // pair<fileIndex , pointer>

    public tempIndexEntry(){
        df=0;
        posting=new ArrayList<>();
        pointerList = new ArrayList<>();


    }

    public int getDf(){
        return df;
    }

    public void increaseDf(){
        df++;
    }

    public void addPosting(Posting pos){
        posting.add(pos);
    }
/*
    public void addPointer(){
        Pair<Integer,Integer>
    }
*/

}
