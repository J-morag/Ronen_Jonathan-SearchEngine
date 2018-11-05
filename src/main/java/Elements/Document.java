package Elements;

public class Document {
    private int serialID;
    private  String docId;
    private String header;
    private String text;

    public Document(/*int serialID*/){
        //this.serialID = serialID;
    }

    public String[] getAllParsableFields(){
        String[] fields = new String[2];
        fields[0] = header;
        fields[1] = text;
        return fields;
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

    public int getSerialID() {
        return serialID;
    }

    public String getDocId() {
        return docId;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }
}

