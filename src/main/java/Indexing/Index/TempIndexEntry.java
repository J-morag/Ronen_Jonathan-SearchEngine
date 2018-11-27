package Indexing.Index;

import javafx.geometry.Pos;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TempIndexEntry {
    private int tfTotal;
    private int df;
    private List<Posting> posting;
    private List<Long> pointerList;

    public TempIndexEntry(){
        tfTotal = 0;
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

    public void increaseTfByN(int n){

        tfTotal+=n;
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
                if(o1.getTf()>o2.getTf()){
                    return -1;
                }else if(o1.getTf()==o2.getTf()){
                    return 0;
                }else
                    return 1;
            }
        });
    }

    public void addPointer(byte fileIndex, long pointer){
        int size = pointerList.size();
        int i=size;
        if(size>fileIndex){
            pointerList.add(fileIndex,pointer);
        }
        else {
            while (i<fileIndex){
                pointerList.add(i,new Long(-1));
                i++;
            }
            pointerList.add(i,pointer);
        }

    }

    public List<Long> getPointerList(){
        return pointerList;
    }

    public void deletePostingList(){
        posting=null;
        posting= new ArrayList<>();
    }
    public int getPostingSize(){
        return posting.size();
    }


}
