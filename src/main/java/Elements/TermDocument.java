package Elements;

import java.util.List;

public class TermDocument {
    private int serialID;
    private String docId;
    private List<Term> header;
    private List<Term> text;

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
        this.header = fields[0];
        this.text = fields[1];
    }

    public int getSerialID() {
        return serialID;
    }

    public String getDocId() {
        return docId;
    }

    public List<Term> getHeader() {
        return header;
    }

    public List<Term> getText() {
        return text;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setHeader(List<Term> header) {
        this.header = header;
    }

    public void setText(List<Term> text) {
        this.text = text;
    }
}
