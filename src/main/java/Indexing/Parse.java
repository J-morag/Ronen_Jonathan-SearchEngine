package Indexing;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;
import com.sun.istack.internal.NotNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * takes Documents, tokenizes and parses them. does not perform stemming.
 */
public class Parse implements Runnable{

    public static boolean debug = false;
    private HashSet<String> stopWords;
    private BlockingQueue<Document> sourceDocumentsQueue;
    private BlockingQueue<TermDocument> sinkTermDocumentQueue;
    private static final String keepDelimiters = "((?<=%1$s)|(?=%1$s))";
    private String currString = "";
    private HashMap<String, String> months;


    /**
     * @param stopWords - a set of stopwords to ignore when parsing. if a term is generated when parsing and it consists of just a stopword, it will be eliminated.
     *                  the set is copied to a local copy.
     * @param sourceDocumentsQueue - a blocking queue of documents to parse. End of queue will be marked by a "poison" Document with null text field.
     * @param sinkTermDocumentQueue - a blocking queue to be filled with lists of Term. Each List representing the Terms from a single documents.
     *                     End of queue will be marked by a "poison" List with just a null Term.
     */
    public Parse(@NotNull HashSet<String> stopWords,@NotNull  BlockingQueue<Document> sourceDocumentsQueue,@NotNull  BlockingQueue<TermDocument> sinkTermDocumentQueue) {
        this.stopWords = new HashSet<>(stopWords);
        this.sourceDocumentsQueue = sourceDocumentsQueue;
        this.sinkTermDocumentQueue = sinkTermDocumentQueue;
        this.months = new HashMap<String, String>(24);
        months.put("JANUARY", "01"); months.put("January", "01");
        months.put("FEBRUARY", "02");months.put("February", "02");
        months.put("MARCH", "03");months.put("March", "03");
        months.put("APRIL", "04");months.put("April", "04");
        months.put("MAY", "05");months.put("May", "05");
        months.put("JUNE", "06");months.put("June", "06");
        months.put("JULY", "07");months.put("July", "07");
        months.put("AUGUST", "08");months.put("August", "08");
        months.put("SEPTEMBER", "09");months.put("September", "09");
        months.put("OCTOBER", "10");months.put("October", "10");
        months.put("NOVEMBER", "11");months.put("November", "11");
        months.put("DECEMBER", "12");months.put("December", "12");
    }

    /**
     * takes Documents, tokenizes and parses them into terms. does not perform stemming.
     * End of queue will be marked by a "poison" TermDocument with null docID.
     */
    private void parse() throws InterruptedException {

        boolean done = false;
        while (!done) { //extract from buffer until poison element is encountered
            Document currDoc = sourceDocumentsQueue.take();
            if (null == currDoc.getText()) done=true; //end of files (poison element)
            else{
                sinkTermDocumentQueue.put(parseOneDocument(currDoc));
            }
        }
        TermDocument poison = new TermDocument(-1,null);
        sinkTermDocumentQueue.put(poison);

    }

    /**
     * fully parses a single Document and returns a Term Document.
     * Should be used if wanting to parse in a serial manner, rather than in a separate thread.
     * @param doc - the Document to parse.
     * @return - a parsed TermDocument.
     */
    public TermDocument parseOneDocument(@NotNull Document doc){

        String[] originalFields = doc.getAllParsableFields();
        List<Term>[] parsedFields = new List[originalFields.length];

        for (int i = 0; i < originalFields.length; i++) {
            parsedFields[i] = parseWorker(tokenize(originalFields[i]));
        }

        return new TermDocument(doc, parsedFields);
    }

    /**
     * Tokenizes the strings within the given String.
     * @param string - the string to tokenize
     * @return a list of strings (tokens).
     */
    private List<String> tokenize(String string){
//        final String splitterRegex = "[\t-&(-,.-/:-@\\x5B-`{-~]"; //marks chars to split on. with '.' '$'

        List<String> lTokens = new ArrayList<>();
        int from = 0;
        int to = 0;
        int length = string.length();

        boolean foundLetters = false;
        boolean foundDigits = false;

        while (to < length) {

            //check alphanumeric
            if(isLetter(string.charAt(to))){
                foundLetters = true;
            }
            else if((string.charAt(to)>='0' && string.charAt(to)<='9')){
                foundDigits = true;
            }
            //split if alphanumeric
            if(foundLetters && foundDigits){
                lTokens.add(string.substring(from, to));
                from = to;
                foundDigits = false;
                foundLetters = false;
            }

            // split and keep delimiter if delimiter
            if(isDelimiter(string.charAt(to))){
                //add token before delimiter
                lTokens.add(string.substring(from, to));
                from = to;
                to++;
                //add the delimiter
                lTokens.add(string.substring(from, to));
                from = to;
            }
            else
                to++;
        }

        lTokens.add((string.substring(from,to)));

        return tokenizeSecondPass(lTokens);
    }


    private boolean isDelimiter(char c){
//        return ("" + c).matches("[\t-&(-,.-/:-@\\x5B-`{-~]");
        return (c <= '&') || (c >= '(' && c <= ',')|| (c >= '.' && c <= '/')|| (c >= ':' && c <= '@')
                || (c >= '[' && c <= '`') || (c >= '{' && c <= '~');
    }

    /**
     * helper funtion for {@code tokenize} which cleans up empty strings, and separates or cleans leftover delimiters.
     * @param textAsTokens - a list of tokenized strings to clean up
     * @return - a cleaned up list of tokens.
     */
    private ArrayList<String> tokenizeSecondPass(List<String> textAsTokens) {

        ArrayList<String> listOfTokens = new ArrayList<>(textAsTokens.size()/2);

        //clean up empty strings and strings that only contain a delimiter
        for (String string: textAsTokens
             ) {
            if((string.length() > 1 || (string.length() == 1 && isProtectedChar(string.charAt(0))) ) )
                listOfTokens.add(string);

        }

        //TESTING
        if(debug){
            System.out.println("-----------start tokenize output-------------");
            for (String t:
                    listOfTokens) {
                System.out.println(t);
            }
            System.out.println("-----------end tokenize output-------------");
        }
        //TESTING

        return listOfTokens;
    }

    private static boolean isProtectedChar(char c){
        return (isSymbol(c) || isWhitespace(c) || (c>='0' && c<='9') || (isLetter(c)));
        //TODO optimize by reversing this statement? (check that it is not a trash char like ',')
    }

    private static boolean isWhitespace(char c){
        return c == ' ' || c == '\n' || c == '\t';
    }
    private static boolean isSymbol(char c){
        return c == '-' || c == '.' ||  c == '$' || c == '%' || c == '/';
    }
    private static boolean isLetter(char c){ return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');}


    private List<Term> parseWorker(List<String> lStrings){
        List<Term> terms = new ArrayList<>();
        ListIterator<String> iterator = lStrings.listIterator(0);

         if (iterator.hasNext()) // check for case of parsing something empty
             currString = iterator.next();

        while (iterator.hasNext()) {
            TokenType type = TokenType.classify(currString);


            //             ROOT CASES
            // whitespace
            if(type == TokenType.WHITESPACE) {
                currString = iterator.next(); //whitespace, do nothing
            }
            // WORD ->
            else if (TokenType.WORD == type){
                String term = parseWord(iterator, currString, new StringBuilder()).toString();
                if(!term.isEmpty()){
                    terms.add(new Term(term));
                }
            }
            // NUMBER ->
            else if(type == TokenType.NUMBER){
                terms.add(new Term(( parseNumber(iterator, currString, new StringBuilder(), false).toString() )));
            }
            // $ -> NUMBER(price) ->
            else if(TokenType.SYMBOL == type && currString.equals("$")){
                currString = iterator.next();
                type = TokenType.classify(currString);
                terms.add(new Term(( parseNumber(iterator, currString, new StringBuilder(), true).toString() )));
            }
            //TODO not finished with root cases. add betweens. add something original?

            else //if completely failed to identify a token (unlikely)
                currString = iterator.next();

        }

        if(debug){
            System.out.println("-----------start parse output-------------");
            for (Term t:
                    terms) {
                System.out.println(t);
            }
            System.out.println("-----------end parse output-------------");
        }
        return terms;
    }


    /**
     * parses a number. always assignes currString to the next token to be parsed (wasn't successfully parsed here).
     * @param iterator iterator from which to get strings to work on
     * @param number - the number to work on.
     * @param result - a string builder to add the result onto. may be empty or contain prior information.
     * @param has$ - indicates that the number should be treated as a price, regardless of the next token.
     *                should be set to true if a '$' was encountered before the number. should be set to false if unsure.
     * @return - the same string builder given in {@param result}, with parsed number, and any relevant tokens like "Dollars" or 'M'.
     */
    private StringBuilder parseNumber(@NotNull ListIterator<String> iterator,@NotNull String number,@NotNull StringBuilder result, boolean has$){
        /*TODO move number formatting to after knowing if price or number.
        TODO keep K/M/B/T as a number representing how much the decimal point should be moved.
        TODO add parameter determining if the number should be formatted for a price or a number, or split into two functions.
        TODO mark fraction as such
         */

        long kmbtMultiplier = 1;
        String formattedNumber;
        String decimals = null;
        StringBuilder unformattedNumber = new StringBuilder(); //start concatenating number parts to build full number
        boolean isFractional = false;
        boolean isPercent = false;
        boolean isPrice = has$;
        boolean isDate = false;

        currString = number;
        TokenType type = TokenType.NUMBER;

        while(type == TokenType.NUMBER){
            unformattedNumber.append(currString);
            currString = iterator.next();
            type = TokenType.classify(currString); //may result in classifying the same string twice
        }

        // NUMBER -> . -> NUMBER
        if(type == TokenType.SYMBOL && currString.equals(".")){ //decimal point or end of line
            currString = iterator.next();
            type = TokenType.classify(currString);
            if(type == TokenType.NUMBER){ // decimal digits
                decimals = currString;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
        }
        // NUMBER -> " " + NUMBER/NUMBER
        else if(type == TokenType.WHITESPACE && currString.equals(" ")) {
            isFractional = tryParseFraction(iterator, unformattedNumber);
        }

        // NUMBER -> %
        if(type == TokenType.SYMBOL && currString.equals("%")){
            isPercent = true;
            currString = iterator.next();
        }
        // NUMBER ->  " "
        else if(type == TokenType.WHITESPACE && !currString.equals("\n")){
            currString = iterator.next();
            type = TokenType.classify(currString);

            // NUMBER + " " -> WORD
            if(type == TokenType.WORD && (currString.equalsIgnoreCase("Thousand") || currString.equalsIgnoreCase("K")) ){
                kmbtMultiplier = 1000;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
            else if(type == TokenType.WORD && (currString.equalsIgnoreCase("Million") || currString.equalsIgnoreCase("M")) ) {
                kmbtMultiplier = 1000000;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
            else if(type == TokenType.WORD &&
                    (currString.equalsIgnoreCase("Billion") || currString.equalsIgnoreCase("B") || currString.equals("bn")) ){
                kmbtMultiplier = 1000000000;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
            else if(type == TokenType.WORD && (currString.equalsIgnoreCase("Trillion") || currString.equalsIgnoreCase("T")) ){
                kmbtMultiplier = 1000000000000L;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
            // NUMBER + " " -> DOLLAR (and not NUMBER M/B/K DOLLARS)
            else if( has$ || (type == TokenType.WORD && (currString.equalsIgnoreCase("Dollar") || currString.equalsIgnoreCase("Dollars")))){
                if (!has$){ //string contains a form of "dollars", not a '$'
                    currString = iterator.next(); // skip that "dollars"
                }
                isPrice = true; // mark term as price term
                type = TokenType.classify(currString);
            }
            // NUMBER + " " -> PERCENT
            else if(type == TokenType.WORD && (currString.equals("percent") || currString.equals("percentage"))){
                isPercent = true;
                currString = iterator.next(); //end of parsing number
            }

            // NUMBER + " " + K/M/B -> U.S. Dollars  or  NUMBER + " " + K/M/B -> DOLLARS
            // there is a " " again because of parsing the K/M/B
            if(type == TokenType.WHITESPACE && currString.equals(" ")){
                currString = iterator.next();
                type = TokenType.classify(currString);
                // NUMBER + " " + K/M/B -> U.S. Dollars
                if (type == TokenType.WORD && currString.equals("U")){
                    isPrice = tryParseUSDollars(iterator);
                }
                // NUMBER + " " + K/M/B -> DOLLARS
                else if( has$ || (type == TokenType.WORD && (currString.equalsIgnoreCase("Dollar") || currString.equalsIgnoreCase("Dollars")))){
                    if (!has$){ //string contains a form of "dollars", not a '$'
                        currString = iterator.next(); // skip that "dollars"
                    }
                    isPrice = true;
                }
                //end of number parsing. string at currString has not yet been successfully parsed
            }
            // NUMBER + " " -> U.S. Dollars
            else if (type == TokenType.WORD && currString.equals("U")){
                isPrice = tryParseUSDollars(iterator);
            }
            // NUMBER -> MONTH
            else if(TokenType.WORD == type && months.containsKey(currString)){
                isDate = true;
                currString = iterator.next();
            }
        }
        // "$" + NUMBER -> NUMBER Dollars
        else if(has$){
            isPrice = true;
        }
        // NUMBER -> NUMBER + K/M/B
        else if (type == TokenType.WORD){
            if(currString.equalsIgnoreCase("K") ){
                kmbtMultiplier = 1000;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
            else if(currString.equalsIgnoreCase("M") ) {
                kmbtMultiplier = 1000000;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
            else if(currString.equalsIgnoreCase("B") || currString.equals("bn") ){
                kmbtMultiplier = 1000000000;
                currString = iterator.next();
                type = TokenType.classify(currString);
            }
        }

        finalizeNumber(unformattedNumber, result, kmbtMultiplier, decimals, isPrice, isPercent, isFractional, isDate);
        return result;
    }

    private void safeIterateAndCheckType(ListIterator<String> iterator){
        //TODO not implemented
    }

    /**
     * assumes the previously encountered string was " ".
     * if successfull, also appends the " " at the start of the result.
     * * if unsuccessful, reverts to currString == " ", with iterator.next() pointing to the next token after " ".
     * @param iterator
     * @param result
     */
    private boolean tryParseFraction(@NotNull ListIterator<String> iterator,@NotNull StringBuilder result) {
        // assumes the previously encountered string was " ".
        currString = iterator.next();
        TokenType type = TokenType.classify(currString);
        String firstNumber = "";
        if(type == TokenType.NUMBER){
            firstNumber = currString;
            currString = iterator.next();
            type = TokenType.classify(currString);
            if(currString.equals("/")){
                currString = iterator.next();
                type = TokenType.classify(currString);
                if(TokenType.NUMBER == type){
                    result.append(' ');
                    result.append(firstNumber);
                    result.append('/');
                    result.append(currString);
                    currString = iterator.next();
                    return true;
                }
                currString = iterator.previous(); // currString == unknown
            }
            currString = iterator.previous(); // currString == "/"
        }
        //this weird section resets the iterator to the way it was before starting this function
        currString = iterator.previous(); // currString == some number
        currString = iterator.previous(); // currString == " "
        currString = iterator.next(); // currString == " "
        return false;
    }

    /**
     * assumes the previously encountered string was "U".
     * if successful, also appends the "U" at the start of the result.
     * if unsuccessful, reverts to currString == "U", with iterator.next() pointing to the next token after "U".
     * @param iterator
     */
    private boolean tryParseUSDollars(@NotNull ListIterator<String> iterator) {
        //assumes the previously encountered string was "U".
        currString = iterator.next();
        TokenType type = TokenType.classify(currString);
        if(currString.equals(".")){
            currString = iterator.next();
            if (currString.equals("S")){
                currString = iterator.next();
                if(currString.equals(".")){
                    currString = iterator.next();
                    if(currString.equals(" ")){
                        currString = iterator.next();
                        type = TokenType.classify(currString);
                        if((type == TokenType.WORD && (currString.equalsIgnoreCase("Dollar") || currString.equalsIgnoreCase("Dollars")))){ //TODO more dollar cases
                            currString = iterator.next(); // move to next because parsing this term is idone
                            return true;
                        }
                        currString = iterator.previous(); // currString == unknown
                    }
                    currString = iterator.previous();// currString == " "
                }
                currString = iterator.previous(); // currString == "."
            }
            currString = iterator.previous(); // currString == "S"
        }
        //this weird section resets the iterator to the way it was before starting this function
        currString = iterator.previous(); // currString == "."
        currString = iterator.previous(); // currString == "U"
        currString = iterator.next(); // currString == "U"
        return false;
    }

    /**
     * after all the information regarding the number being parsed has been collected, finalize it into a properly formatted number.
     * may lose some rightmost digits if a number is both a fraction and very large (trillions)
     * @param unformattedNumber
     * @param result - formatting result will be appended onto here. will not override existing content.
     * @param kmbtMultiplier >= 1
     * @param decimals - if null, will be treated as no decimals exist.
     * @param isPrice
     * @param isPercent
     * @param isFractional
     * @param isDate
     */
    private void finalizeNumber(@NotNull StringBuilder unformattedNumber,@NotNull StringBuilder result, long kmbtMultiplier,
                                String decimals, boolean isPrice, boolean isPercent, boolean isFractional, boolean isDate)
    {
        String stringDollars = " Dollars";

        if(isFractional){
            result.append(unformattedNumber);
            if(isPercent){
                result.append('%');
            }
            else if (isPrice){
                result.append(stringDollars);
            }
        }
        else {
            if(null != decimals){
                unformattedNumber.append('.');
                unformattedNumber.append(decimals);
            }
            double number = Double.parseDouble(unformattedNumber.toString());
            number *= kmbtMultiplier;

            if(isPrice){
                if (number<1000){ // too small to matter
                    appendWhileTrimmingDecimals(result, number);
                }
                else if (number>=1000 && number<1000000){ //thousands
                    number /= 1000;
                    appendWhileTrimmingDecimals(result, number);
                    result.append(" K");
                }
                else { //millions or larger
                    number /= 1000000;
                    appendWhileTrimmingDecimals(result, number);
                    result.append(" M");
                }

                result.append(stringDollars);

            }
            else if(isDate){
                if(unformattedNumber.length() > 2){ // number is year
                    result.append(unformattedNumber); //insert number without format
                    result.append("-");
                    result.append(months.get(currString));
                }
                else if (unformattedNumber.length() == 2){ //number is day
                    result.append(months.get(currString));
                    result.append("-");
                    result.append(unformattedNumber);
                }
                else{ // number is day and <=9
                    result.append(months.get(currString));
                    result.append("-0");
                    result.append(unformattedNumber);
                }
            }
            else{ //regular number
                if (number<1000){ // too small to matter
                    appendWhileTrimmingDecimals(result, number);
                }
                else if (number>=1000 && number<1000000){ //thousands
                    number /= 1000;
                    appendWhileTrimmingDecimals(result, number);
                    result.append("K");
                }
                else if (number>=1000000 && number<1000000000){ //millions
                    number /= 1000000;
                    appendWhileTrimmingDecimals(result, number);
                    result.append("M");
                }
                else { //billions or larger
                    number /= 1000000000;
                    appendWhileTrimmingDecimals(result, number);
                    result.append("B");
                }

                if(isPercent){
                    result.append('%');
                }
            }
        }
    }

    private void appendWhileTrimmingDecimals(@NotNull StringBuilder result, double number){
        if( number % 1 == 0 ){
            long numberWithoutDecimals = ((long)number);
            result.append(numberWithoutDecimals);
        }
        else result.append(number);
    }

    private StringBuilder parseWord(@NotNull ListIterator<String> iterator,@NotNull String word,@NotNull StringBuilder result){
        //TODO add cases for words with '-'
        //TODO add cases for words containing more than one word, containing (or just starting with?) stopwords

        TokenType type;
        // MONTH -> MM-DD
        if (months.containsKey(word)){
            String month = months.get(word);
            currString = iterator.next();
            type = TokenType.classify(currString);
            if(TokenType.WHITESPACE == type && currString.equals(" ")){
                currString = iterator.next();
                type = TokenType.classify(currString);
                if(TokenType.NUMBER == type){
                    if(currString.length() > 2){ // number is year
                        result.append(currString);
                        result.append("-");
                        result.append(month);
                    }
                    else if (currString.length() == 2){ //number is day
                        result.append(month);
                        result.append("-");
                        result.append(currString);
                    }
                    else{ // number is day and <=9
                        result.append(month);
                        result.append("-0");
                        result.append(currString);
                    }
                    if(iterator.hasNext()) currString = iterator.next();
                }
                else{ // just some word
                    result.append(currString);
                    currString = iterator.next();
                }
            }
            else{ // just some word
                result.append(currString);
                currString = iterator.next();
            }
        }
        else if(stopWords.contains(currString)){ // just a stopword alone, throw it away
            currString = iterator.next();
        }
        else{ // just some word
            result.append(currString);
            currString = iterator.next();
        }
        return result;
    }


    /**
     * slphanumerics are also treated as a word
      */
    private enum TokenType {
        NUMBER, WORD, SYMBOL, WHITESPACE;

        /**
         * classifies a token (string) to a type of token
         * @param str - token to classify
         * @return - a TokenType enum value
         */
        public static TokenType classify(String str){
            if(str == null || str.isEmpty()) return null;
            int length = str.length();
            if (1 == length){ // one char long
                if(isWhitespace(str.charAt(0)))
                    return WHITESPACE;
                else if (str.charAt(0) <= '9' && str.charAt(0) >= '0')  //number
                    return NUMBER;
                else if(isLetter(str.charAt(0))) // one letter word
                    return WORD;
                else //is one char and not whitespace or number or letter, must be a symbol.
                    return SYMBOL;
            }
            else{ //more than one character
                if(str.charAt(0) <= '9' && str.charAt(0) >= '0'){ //first char is digit
                    for(int i=0; i<length; i++){
                        if(isLetter(str.charAt(i)) || isSymbol(str.charAt(i))) return WORD;
                    }
                    return NUMBER; // all chars are digits
                }
                else { //first char is letter
                    return WORD; //all chars are letters
                }
            }
        }
    }


    public void run() {
        try {
            parse();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static HashSet<String> getStopWords(String pathTostopwordsFile) {
        HashSet<String> stopWords = new HashSet<>();

        try {
            InputStream is = null;
            BufferedReader buffer = new BufferedReader(new FileReader(pathTostopwordsFile));
            String line = null;
            line = buffer.readLine();
            while(line != null){
                stopWords.add(line);
                line = buffer.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("stopwords file not found in the specified path. running without stopwords");
        } catch (IOException e){
            e.printStackTrace();
        }


        return stopWords;
    }
}
