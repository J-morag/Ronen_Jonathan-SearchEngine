package Indexing;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * takes Documents, tokenizes and parses them. does not perform stemming.
 */
public class Parse implements Runnable{

    public static boolean debug = false;
    private String pathTostopwordsFile;
    private BlockingQueue<Document> sourceDocumentsQueue;
    private BlockingQueue<TermDocument> sinkTermDocumentQueue;
    private static final String keepDelimiters = "((?<=%1$s)|(?=%1$s))";
    private static final HashSet<String> whiteSpaces = new HashSet<>();


    /**
     * @param sourceDocumentsQueue - a blocking queue of documents to parse. End of queue will be marked by a "poison" Document with null text field.
     * @param sinkTermDocumentQueue - a blocking queue to be filled with lists of Term. Each List representing the Terms from a single documents.
     *                     End of queue will be marked by a "poison" List with just a null Term.
     */
    public Parse(String pathTostopwordsFile, BlockingQueue<Document> sourceDocumentsQueue, BlockingQueue<TermDocument> sinkTermDocumentQueue) {
        this.pathTostopwordsFile = pathTostopwordsFile;
        this.sourceDocumentsQueue = sourceDocumentsQueue;
        this.sinkTermDocumentQueue = sinkTermDocumentQueue;
        this.whiteSpaces.add(" ");
        this.whiteSpaces.add("\t");
        this.whiteSpaces.add("\n");
    }

    /**
     * takes Documents, tokenizes and parses them into terms. does not perform stemming.
     * End of queue will be marked by a "poison" TermDocument with null docID.
     */
    private void parse() throws InterruptedException {

        HashSet<String> stopWords = getStopWords(pathTostopwordsFile);

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
    public TermDocument parseOneDocument(Document doc){
        List<String> tokenizedHeader = tokenize(doc.getHeader());
        List<String> tokenizedText = tokenize(doc.getText());

        List<Term> headerAsTerms = parseWorker(tokenizedHeader);
        List<Term> textAsTerms = parseWorker(tokenizedText);

        TermDocument termDocument = new TermDocument(doc);
        termDocument.setHeader(headerAsTerms);
        termDocument.setText(textAsTerms);

        return termDocument;
    }

    /**
     * Tokenizes the strings within the given String.
     * @param string - the string to tokenize
     * @return a list of strings (tokens).
     */
    private List<String> tokenize(String string){
//        final String splitterRegex = "[\t-#%-&(-,/-/:-@\\x5B-`{-~]"; //marks chars to split on. without . - $
        final String splitterRegex = "[\t-&(-,.-/:-@\\x5B-`{-~]"; //marks chars to split on. with '.' '$'

        String[] stringAsTokens = string.split(String.format(keepDelimiters, splitterRegex /*delimiters regex*/));

        List<String> lStringAsTokens = tokenizeSecondPass(stringAsTokens);

        return lStringAsTokens;
    }

    /**
     * helper funtion for {@code tokenize} which cleans up empty strings, and separates or cleans leftover delimiters.
     * @param textAsTokens - a list of tokenized strings to clean up
     * @return - a cleaned up list of tokens.
     */
    private ArrayList<String> tokenizeSecondPass(String[] textAsTokens) {

        ArrayList<String> listOfTokens = new ArrayList<>(textAsTokens.length/2);

        //clean up empty strings and strings that only contain a delimiter
        for (String string: textAsTokens
             ) {
            if((string.length() == 1 && isProtectedChar(string.charAt(0))) || (string.length() > 1) )
                listOfTokens.add(string);

        }

        //TESTING
        if(debug){
            for (String t:
                    listOfTokens) {
                System.out.println(t);
            }
        }
        //TESTING

        return listOfTokens;
    }

    private boolean isProtectedChar(char c){
        return (c == '-' || c == '.' ||  c == '$' || c == ' ' || c == '\n' || c == '%' || c == '/' || (c>='0' && c<='9'));
    }


    private List<Term> parseWorker(List<String> lStrings){
        List<Term> terms = new ArrayList<>();
        for (int i=0 ; i<lStrings.size(); i++) {
            String string = lStrings.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(string);

            if(isWhiteSpace(string)); //do nothing
            else if(string.matches(".*\\d.*")){ // contains digits
                if(string.matches("\\d+")){ //is number
                    while( lStrings.get(i+1).matches("\\d+")){
                        sb.append(lStrings.get(++i));

                    }
                    System.out.println(sb.toString());
                }
            }
        }
        return null;
    }

    private enum tokenType{
        NUMBER, WORD, ALPHANUMERIC, SYMBOL, WHITESPACE;

        public tokenType classify(String str){
            //TODO
            return null;
        }
    }


    public void run() {
        try {
            parse();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private HashSet<String> getStopWords(String pathTostopwordsFile) {
        HashSet<String> stopWords = new HashSet<>();

        InputStream is = null;
        try {
            is = new FileInputStream(pathTostopwordsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

        String line = null;
        try {
            line = buffer.readLine();

            while(line != null){
                stopWords.add(line);
                buffer.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopWords;
    }
    private boolean isWhiteSpace(String s) {return whiteSpaces.contains(s);}
}
