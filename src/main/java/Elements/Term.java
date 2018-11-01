package Elements;

import java.util.List;

public class Term {
    String string;
//    boolean isInHeader = false;
//    boolean isInTitle = false;
//    boolean isInBold = false;
//    boolean isInOpening = false;
//    boolean isInEnding = false;

    public Term(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
