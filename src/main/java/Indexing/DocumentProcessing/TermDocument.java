package Indexing.DocumentProcessing;

import java.io.Serializable;
import java.util.List;

/**
 * this class is a data type that represents a document after it has been parsed into terms.
 */
public class TermDocument implements Serializable {
    final private int serialID;
    final private String docId;
    private Term city;
    private List<Term> title;
    private List<Term> text;
    private List<Term> date;
    public String language="";

    /**
     *
     * @param serialID - serial id of the Document that was parsed to create this object.
     * @param doc - fields that don't require parsing will be taken from here.
     * @param fields - a list of fields defined by the order that {@link Document} given in {@link Document#getAllParsableFields()}.
     */
    public TermDocument(int serialID, Document doc, List<Term>[] fields) {
        this.serialID = serialID;
        this.docId = doc.getDocId();
        this.language = doc.getLanguage();
        this.title = fields[0];
        this.text = fields[1];
        this.date = fields[2];
        if(!fields[3].isEmpty()) this.city = fields[3].get(0);
    }

    public TermDocument(int serialID, Document doc) {
        this.serialID = serialID;
        if (null != doc){
            this.docId = doc.getDocId();
            this.language = doc.getLanguage();

        }
        else{
            docId = null;
            language = null;
        }
    }

    public int getSerialID() {
        return serialID;
    }

    public String getDocId() {
        return docId;
    }

    public List<Term> getTitle() {
        return title;
    }

    public List<Term> getText() {
        return text;
    }

    public  Term getCity(){return this.city;}

    public String getLanguage(){return language;}

    public void setCity(Term city){this.city=city; }



    public void setTitle(List<Term> title) {
        this.title = title;
    }

    public void setText(List<Term> text) {
        this.text = text;
    }
}
