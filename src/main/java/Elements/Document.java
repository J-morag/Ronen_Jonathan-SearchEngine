package Elements;

public class Document {
    private  String docId;
    private String title = "";
    private String text = "";
    private  String date = "";
    private String city="";


    public Document(/*int serialID*/){
        //this.serialID = serialID;
    }

    public Document(String docId, String title, String text, String date) {
        this.docId = docId;
        this.title = title;
        this.text = text;
        this.date = date;
    }

    public Document(String docId, String title, String text, String date , String city) {
        this.docId = docId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.city=city;
    }

    public void setTitle(String header) {
        this.title = header;
    }
    public String[] getAllParsableFields(){
        String[] fields = new String[4];
        fields[0] = title;
        fields[1] = text;
        fields[2] = date;
        fields[3] = city;
        return fields;
    }


    public void setText(String text) {
        this.text = text;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setCity(String city){this.city=city;}

    public void setDocId(String id){
        docId=id;
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
    public String getCity(){return city;}

    public String getDate(){
        return date;
    }


    @Override
    public String toString(){
        return "docID : " + docId+"\n" + "title : " + title+"\n" +"date : "+date+"\n" + "text : " + text + "city : "+ city;
    }
}

