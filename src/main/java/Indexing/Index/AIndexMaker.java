package Indexing.Index;

import Indexing.DocumentProcessing.Term;
import Indexing.DocumentProcessing.TermDocument;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AIndexMaker {

    protected Map<Term,IndexEntry> index;

    public AIndexMaker(){
        this.index= new LinkedHashMap<>();

    }

    abstract public void  addToIndex(TermDocument doc);


}
