package Indexing;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import java.util.ArrayList;
import java.util.HashSet;
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
        TermDocument poison = new TermDocument(-1,null);
        sinkTermDocumentQueue.put(poison);

    }

    /**
     * Tokenizes the strings within the give Document, creating a TermDocument containing the token lists as Term Lists.
     * These Terms are still just tokens, not actual terms, at this stage.
     * @param doc - the Document to tokenize
     * @return a Term Document that is tokenized.
     */
    private TermDocument tokenize(Document doc){
//        final String splitterRegex = "[\t-#%-&(-,/-/:-@\\x5B-`{-~]"; //marks chars to split on. without . - $
        final String splitterRegex = "[\t-&(-,.-/:-@\\x5B-`{-~]"; //marks chars to split on. with '.' '$'

        TermDocument termDocument = new TermDocument(doc);

        String[] headerAsTokens = doc.getHeader().split(String.format(keepDelimiters, splitterRegex /*delimiters regex*/));
        String[] textAsTokens = doc.getText().split(String.format(keepDelimiters, splitterRegex /*delimiters regex*/));

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

        ArrayList<Term> listOfTokens = new ArrayList<>();

        //clean up empty strings and strings that only contain a delimiter
        for (String string: textAsTokens
             ) {
            if(!(string.isEmpty() || (string.length() == 1 && charIsToBeRemoved(string.charAt(0))) )) listOfTokens.add(new Term(string));;
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

    private boolean charIsToBeRemoved(char c){
        return c != '-' && c != '.' && c != '$';
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
