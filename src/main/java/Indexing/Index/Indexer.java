package Indexing.Index;

import Indexing.DocumentProcessing.TermDocument;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * takes fully parsed and stemmed documents and indexes them.
 * Calculates tf, tags terms to indicate their importance...
 * will index {@value # partialGroupSize} documents at a time.
 */
public class Indexer implements Runnable {

    public static String withStemmingOutputFolderName = "postingWithStemming";
    public static String noStemmingOutputFolderName = "postingWithOutStemming";
    public static String dictionarySaveName = "Index";
    public static String docsDictionaryName="DocsIndex";


    private String pathToOutputFolder;
    private BlockingQueue<TermDocument> stemmedTermDocumentsBuffer;
    private AIndexMaker mainIndex;
    private AIndexMaker cityIndex;
    private int numIndexedDocs;
    private String finalPath="";

    //private boolean withSteming=false;

    public Indexer(String pathToOutputFolder, BlockingQueue<TermDocument> stemmedTermDocumentsBuffer,boolean withSteming) {
        numIndexedDocs = 0;
        this.pathToOutputFolder = pathToOutputFolder;
        this.stemmedTermDocumentsBuffer = stemmedTermDocumentsBuffer;
        if(withSteming) {
            finalPath=pathToOutputFolder +"\\postingWithStemming";
            new File(finalPath).mkdir();
        }
        else {
            finalPath=pathToOutputFolder +"\\postingWithOutStemming";
            new File(finalPath).mkdir();
            mainIndex = new MainIndexMaker(finalPath);
        }

        mainIndex = new MainIndexMaker(finalPath);
        //cityIndex = new CityIndexMaker(finalPath);
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
//                cityIndex.addToIndex(document);
                if(document.getSerialID()==-1){
                    done=true;
                }
                else numIndexedDocs++;
            }
            mergeMainIndex();
            dumpDictionaryToDisk();


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
     * @return - View.Main index
     */
    public Map<String , IndexEntry> getMainMap(){

        return ((MainIndexMaker)mainIndex).getMainDictionary();
    }


    public Map<String , TempIndexEntry> getTempMap(){

        return ((MainIndexMaker)mainIndex).getTempDictionary();
    }

    public int getNumIndexedDocs(){
        return numIndexedDocs;
    }

    /**
     * get the Document dictionary from MainIndexMaker
     * @return - Doc dictionary
     */
    public Map<Integer , DocIndexEntery> getDocsMap()
    {
        return ((MainIndexMaker)mainIndex).getDocsDictionary();
    }



    public void mergeMainIndex(){
            ((MainIndexMaker) mainIndex).mergeIndex();
    }


    public void dumpDictionaryToDisk(){
        try {
            OutputStream mainIndexFileOutputStream = new FileOutputStream(finalPath+"\\"+dictionarySaveName);
            ObjectOutputStream mainIndexObjectOutputStream  = new ObjectOutputStream(mainIndexFileOutputStream);

            OutputStream docsIndexFileOutputStream = new FileOutputStream(finalPath+"\\"+docsDictionaryName);
            ObjectOutputStream docsIndexObjectOutstream  = new ObjectOutputStream(docsIndexFileOutputStream);
            ((ObjectOutputStream) docsIndexObjectOutstream).writeObject(getDocsMap());
            mainIndexObjectOutputStream.writeObject(getMainMap());

            mainIndexFileOutputStream.flush();
            mainIndexObjectOutputStream.flush();

            mainIndexFileOutputStream.close();
            mainIndexObjectOutputStream.close();

            docsIndexObjectOutstream.flush();
            docsIndexFileOutputStream.flush();

            docsIndexFileOutputStream.close();
            docsIndexObjectOutstream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void pritDictionaryToFile(){
        try {
            OutputStream mainIndexFileOutputStream = new FileOutputStream(finalPath+"\\dictionaryFile.txt");
            OutputStreamWriter outputStream= new OutputStreamWriter(mainIndexFileOutputStream);
            Map<String , IndexEntry> dictionaryToPrint = getMainMap();
            outputStream.write("term,df,totalTF\n");
            for (String term : dictionaryToPrint.keySet() )  {
                outputStream.write(term+","+dictionaryToPrint.get(term).getDf()+','+dictionaryToPrint.get(term).getTotalTF()+"\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String , CityIndexEntry> getCityMap(){
        return ((CityIndexMaker)cityIndex).getCityDictionary();
    }




}
