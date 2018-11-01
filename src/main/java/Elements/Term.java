package Elements;

public class Term {
    String string;
    boolean isInHeader = false;
    boolean isInTitle = false;
    boolean isInBold = false;
    boolean isInOpening = false;
    boolean isInEnding = false;

    public Term(String string) {
        this.string = string;
    }
}
