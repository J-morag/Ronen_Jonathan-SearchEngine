package Indexing.Index;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AIndexMaker {

    protected Map<Term,IndexEntry> index;

    public AIndexMaker(){
        this.index= new LinkedHashMap<>();

    }

    abstract public void  addToIndex(TermDocument doc);


}
