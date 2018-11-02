package Indexing;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * takes Documents, tokenizes and parses them. does not perform stemming.
 */
public class Parse implements Runnable{

    private static final boolean debug = false;
    private String pathTostopwordsFile;
    private BlockingQueue<Document> sourceDocumentsQueue;
    private BlockingQueue<TermDocument> sinkTermDocumentQueue;
    private static final String keepDelimiters = "((?<=%1$s)|(?=%1$s))";

    /**
     * @param sourceDocumentsQueue - a blocking queue of documents to parse. End of queue will be marked by a "poison" Document with null text field.
     * @param sinkTermDocumentQueue - a blocking queue to be filled with lists of Term. Each List representing the Terms from a single documents.
     *                     End of queue will be marked by a "poison" List with just a null Term.
     */
    public Parse(String pathTostopwordsFile, BlockingQueue<Document> sourceDocumentsQueue, BlockingQueue<TermDocument> sinkTermDocumentQueue) {
        this.pathTostopwordsFile = pathTostopwordsFile;
        this.sourceDocumentsQueue = sourceDocumentsQueue;
        this.sinkTermDocumentQueue = sinkTermDocumentQueue;
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

        List<String> lstringAsTokens = tokenizeSecondPass(stringAsTokens);

        return lstringAsTokens;
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
            if(!(string.isEmpty() || (string.length() == 1 && isProtectedChar(string.charAt(0))) ))
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
        return c == '-' || c == '.' || c == '$' || c == ' ' || c == '\n';
    }


    private List<Term> parseWorker(List<String> lStrings){
        //TODO not implemented
        return null;
    }

    public void run() {
        try {
            parse();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
