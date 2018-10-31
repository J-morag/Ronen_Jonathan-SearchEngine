package Elements;

public class Document {
    public String docId;
    public String header;
    public String text;

    public Document(){

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

