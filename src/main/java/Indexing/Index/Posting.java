package Indexing.Index;

public class Posting {

    final private int docSerialID;
    private short tf;
    private boolean isInTitle = false;
    private boolean isInBeginning = false;
    //    private String language;
    //    private String city;
    //    private String docID;
    //    private short maxTf;
    //    private short uniqueWord;

    public Posting(int docSerialID, short tf, boolean isInTitle, boolean isInBeginning) {
        this.docSerialID = docSerialID;
        this.tf = tf;
        this.isInTitle = isInTitle;
        this.isInBeginning = isInBeginning;
    }

    public Posting(int docSerialID, short tf) {
        this.docSerialID = docSerialID;
        this.tf = tf;
    }

    public Posting(int[] ints, short[] shorts, boolean[] bools){
        docSerialID = ints[0];
        tf = shorts[0];
        isInTitle = bools[0];
        isInBeginning = bools[1];
    }

//GETTERS
    public short getTf() {
        return tf;
    }

    public boolean isInTitle(){
        return isInTitle;
    }

    public boolean isInBeginning() {
        return isInBeginning;
    }

    public short[] getShortFields(){
        return new short[]{this.tf};
    }

    public boolean[] getBooleanFields(){
        return new boolean[]{isInTitle, isInBeginning};
    }

    public int[] getIntegerFields(){ return new int[]{docSerialID} ; }

    public static int getNumberOfIntFields(){return 1;}

    public static int getNumberOfShortFields(){return 1;}

    public static int getNumberOfBooleanFields(){return 2;}

    //SETTERS

    public void setTf(short tf) {
        this.tf = tf;
    }





    public void setInTitle(boolean isInTitle){
        this.isInTitle = isInTitle;
    }

    public void setInBeginning(boolean inBeginning) {
        isInBeginning = inBeginning;
    }


    @Override
    public String toString(){
        return docSerialID+","+tf+","+isInTitle+","+isInBeginning;
    }

    @Override
    public boolean equals(Object o) {
        //TODO shorten this to just use docSerialID later! more efficient!
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        if (docSerialID != posting.docSerialID) return false;
        if (tf != posting.tf) return false;
        if (isInTitle != posting.isInTitle) return false;
        return isInBeginning == posting.isInBeginning;
    }

    @Override
    public int hashCode() {
        //TODO shorten this to just use docSerialID later! more efficient!
        int result = docSerialID;
        result = 31 * result + (int) tf;
        result = 31 * result + (isInTitle ? 1 : 0);
        result = 31 * result + (isInBeginning ? 1 : 0);
        return result;
    }
}
