package Elements;

import java.util.List;

public class TermDocument {
    private int serialID;
    private String docId;
    private List<Term> header;
    private List<Term> text;

    public TermDocument(int serialID) {
        this.serialID = serialID;
    }
}
