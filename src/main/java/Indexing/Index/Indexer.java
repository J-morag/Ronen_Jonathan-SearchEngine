package Indexing.Index;

import Elements.Document;
import Elements.Term;
import Elements.TermDocument;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * takes fully parsed and stemmed documents and indexes them.
 * Calculates tf, tags terms to indicate their importance...
 * will index {@value # partialGroupSize} documents at a time.
 */
public class Indexer implements Runnable {




    private String pathToOutputFolder;
    private BlockingQueue<TermDocument> stemmedTermDocumentsBuffer;
    private AIndexMaker mainIndex;
    //private boolean withSteming=false;

    public Indexer(String pathToOutputFolder, BlockingQueue<TermDocument> stemmedTermDocumentsBuffer,boolean withSteming) {
        this.pathToOutputFolder = pathToOutputFolder;
        this.stemmedTermDocumentsBuffer = stemmedTermDocumentsBuffer;
        if(withSteming) {
            new File(pathToOutputFolder +"\\postingWithStemming").mkdir();
            mainIndex = new MainIndexMaker(pathToOutputFolder + "\\postingWithStemming");
        }
        else {
            new File(pathToOutputFolder +"\\postingWithOutStemming").mkdir();
            mainIndex = new MainIndexMaker(pathToOutputFolder + "\\postingWithOutStemming");
        }
}

    /**
     * takes fully parsed and stemmed documents and indexes them.
     * Calculates tf, tags terms to indicate their importance...
     * will index {@value # partialGroupSize} documents at a time.
     */
    private void index(){
        Boolean done = false;
        try {
            while (!done) {
                TermDocument document = stemmedTermDocumentsBuffer.take();
                mainIndex.addToIndex(document);
                if(document.getSerialID()==-1){
                    done=true;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stemmedTermDocumentsBuffer=null;
    }

    public void run() {
        index();
    }

    /**
     * get the index dictionary from MainIndexMaker
     * @return - GUI.Main index
     */
    //@TODO change it to getMainDictionary
    public Map<String , TempIndexEntry> getMainMap(){

        return ((MainIndexMaker)mainIndex).getTempDictionary();
    }

    /**
     * get the Document dictionary from MainIndexMaker
     * @return - Doc dictionary
     */
    public Map<Integer , DocIndexEntery> getDocsMap(){
        return ((MainIndexMaker)mainIndex).getDocsDictionary();
    }



    public void mergeMainIndex(){
        ((MainIndexMaker)mainIndex).mergeIndex(getMainMap().keySet());
    }

}
