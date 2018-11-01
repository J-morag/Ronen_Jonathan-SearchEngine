package Elements;

public class Document {
    private int serialID;
    public String docId;
    public String header;
    public String text;

    public Document(/*int serialID*/){
        //this.serialID = serialID;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setDocId(String id){
        docId=id;
    }
}

