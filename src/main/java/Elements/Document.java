package Elements;

public class Document {
    private int serialID;
    private  String docId;
    private String title = "";
    private String text = "";
    private  String date = "";


    public Document(/*int serialID*/){
        //this.serialID = serialID;
    }

    public void setTitle(String header) {
        this.title = header;
    }
    public String[] getAllParsableFields(){
        String[] fields = new String[3];
        fields[0] = title;
        fields[1] = text;
        fields[2] = date;
        return fields;
    }


    public void setText(String text) {
        this.text = text;
    }
    public void setDate(String date){
        this.date = date;
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
        return title;
    }

    public String getText() {
        return text;
    }

    public String getDate(){
        return date;
    }


    @Override
    public String toString(){
        return "docID : " + docId+"\n" + "title : " + title+"\n" +"date : "+date+"\n" + "text : " + text;
    }
}

