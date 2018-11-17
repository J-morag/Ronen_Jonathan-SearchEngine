package Elements;

import java.util.List;

public class TermDocument {
    private int serialID;
    private String docId;
    private Term city;
    private List<Term> title;
    private List<Term> text;
    private List<Term> date;

    public TermDocument(int serialID, String docId) {
        this.serialID = serialID;
        this.docId = docId;
    }

    public TermDocument(Document doc) {
        this.serialID = doc.getSerialID();
        this.docId = doc.getDocId();
    }

    public TermDocument(Document doc, List<Term>[] fields) {
        this.serialID = doc.getSerialID();
        this.docId = doc.getDocId();
        this.title = fields[0];
        this.text = fields[1];
        this.date = fields[2];
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

    public void setCity(Term city){this.city=city; }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setTitle(List<Term> title) {
        this.title = title;
    }

    public void setText(List<Term> text) {
        this.text = text;
    }
}
