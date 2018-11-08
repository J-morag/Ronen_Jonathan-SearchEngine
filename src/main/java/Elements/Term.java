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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        return string != null ? string.equals(term.string) : term.string == null;
    }

    @Override
    public int hashCode() {
        return string != null ? string.hashCode() : 0;
    }

    public int compareTo(Term other){
        return this.toString().compareTo(other.toString());
    }

}
