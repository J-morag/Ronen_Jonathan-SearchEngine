package Elements;

import java.util.List;

public class TermDocument {
    private int serialID;
    private String docId;
    private String language;
    private Term city;
    private List<Term> title;
    private List<Term> text;
    private List<Term> date;

    public TermDocument(int serialID, Document doc) {
        this.serialID = serialID;
        if (null != doc){
            this.docId = doc.getDocId();
//            this.language = doc.
        }
    }

    public TermDocument(int serialID, Document doc, List<Term>[] fields) {
        this.serialID = serialID;
        this.docId = doc.getDocId();
        this.title = fields[0];
        this.text = fields[1];
        this.date = fields[2];
        if(!fields[3].isEmpty()) this.city = fields[3].get(0);
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
