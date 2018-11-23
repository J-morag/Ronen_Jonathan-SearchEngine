package Indexing.Index;

import javafx.geometry.Pos;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TempIndexEntry {
    private int df;
    private List<Posting> posting;
    private List<Pair<Integer , Integer >> pointerList; // pair<fileIndex , pointer>

    public TempIndexEntry(){
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
        df++;
    }

    public List<Posting> getPosting(){
        return posting;
    }

    public void sortPosting(){
        Collections.sort(posting, new Comparator<Posting>() {
            @Override
            public int compare(Posting o1, Posting o2) {
                if(o1.getTf()>=o2.getTf()){
                    return -1;
                }else
                    return 1;
            }
        });
    }
/*
    public void addPointer(){
        Pair<Integer,Integer>
    }
*/

}
