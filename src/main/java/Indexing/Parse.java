package Indexing;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

/**
 * takes Documents, tokenizes and parses them. does not perform stemming.
 */
public class Parse implements Runnable{

    private static final boolean debug = false;
    private String pathTostopwordsFile;
    private BlockingQueue<Document> sourceDocumentsQueue;
    private BlockingQueue<TermDocument> sinkTermDocumentQueue;

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
     * End of queue will be marked by a "poison" List with just a null Term.
     */
    public void parse() throws InterruptedException {
        boolean done = false;
        while (!done) { //extract from buffer until poison element is encountered
            Document currDoc = sourceDocumentsQueue.take();
            if (null == currDoc.getText()) done=true; //end of files (poison element)
            else{
                TermDocument currTermDoc = tokenize(currDoc);
                parseWorker(currTermDoc);

            }

        }

    }

    /**
     * Tokenizes the strings within the give Document, creating a TermDocument containing the token lists as Term Lists.
     * These Terms are still just tokens, not actual terms, at this stage.
     * @param doc - the Document to tokenize
     * @return a Term Document that is tokenized.
     */
    private TermDocument tokenize(Document doc){
        final String splitterRegex = "[\t-#%-&(-,/-/:-@\\x5B-`{-~]"; //marks chars to split on

        TermDocument termDocument = new TermDocument(doc);

        String[] headerAsTokens = doc.getHeader().split( splitterRegex /*delimiters regex*/);
        String[] textAsTokens = doc.getText().split( splitterRegex /*delimiters regex*/);

        termDocument.setHeader(tokenizeSecondPass(headerAsTokens));
        termDocument.setText(tokenizeSecondPass(textAsTokens));

        return termDocument;
    }

    /**
     * helper funtion for {@code tokenize} which cleans up empty strings, and separates or cleans leftover delimiters.
     * @param textAsTokens - a list of tokenized strings to clean up
     * @return - a cleaned up list of Terms.
     */
    private ArrayList<Term> tokenizeSecondPass(String[] textAsTokens) {

        final String keepDelimiters = "((?<=%1$s)|(?=%1$s))";

        ArrayList<Term> listOfTokens = new ArrayList<>();

        for (String string: textAsTokens
             ) {
            if(string.isEmpty()) continue; //clean up empty strings

            if(string.matches(".*[.\\-$].*")){ //string contains one of: '.' '-' '$'
                //split on delimiters and keep them as strings
                String[] splitStrings = string.split(String.format(keepDelimiters, "[.\\-$]"));
                for (String splitString: splitStrings
                     ) {
                    listOfTokens.add(new Term(splitString));
                }
            }
            else listOfTokens.add(new Term(string));
        }

        //TESTING
        if(debug){
            for (Term t:
                    listOfTokens) {
                System.out.println(t.toString());
            }
        }
        //TESTING

        return listOfTokens;
    }

    private void parseWorker(TermDocument doc){
        //TODO not implemented
    }

    public void run() {
        try {
            parse();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
