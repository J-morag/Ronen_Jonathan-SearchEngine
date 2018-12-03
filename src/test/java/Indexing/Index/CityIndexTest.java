package Indexing.Index;

import Indexing.DocumentProcessing.Document;
import Indexing.DocumentProcessing.TermDocument;
import Indexing.DocumentProcessing.Parse;
import Indexing.DocumentProcessing.ReadFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CityIndexTest {

    private static final int documentBufferSize = 3;
    private static final int termBufferSize = 3;
    private static final int stemmedTermBufferSize = 3;

    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Documents\\לימודים\\שנה ג\\איחזור מידע\\עבודות\\מסמכים מנוע חיפוש\\corpus"; //TODO temporary! should come from UI
    //private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Desktop\\FB396001";
    private static final String pathToStopWordRONEN ="C:\\Users\\ronen\\Desktop\\stopWords.txt";

    @Test
    public void testJASON()
    {
        JSONParser jp =  new JSONParser();
        Map<String, CityIndexEntry> cityMap =new LinkedHashMap<>();

        try {
            Object obj = jp.parse(new FileReader("C:\\Users\\ronen\\Desktop\\jason.txt"));
            JSONArray jasonArray = (JSONArray)obj;
            for (Object jo : jasonArray ) {
                JSONObject j = (JSONObject)jo;
                String capital = j.get("capital").toString();
                String pop = j.get("population").toString();
                String country = (String)j.get("name");
                JSONArray ja=  (JSONArray)j.get("currencies");
                String currancy =(String)((JSONObject)ja.get(0)).get("code");

                CityIndexEntry cityIndexEntry = new CityIndexEntry(country,currancy,pop);
                if(capital!=null && !capital.equals("")){
                    cityMap.put(capital.split(" ")[0].toUpperCase(),cityIndexEntry);
                }




                FileOutputStream fileOutputStream = new FileOutputStream("resources\\citiesDictionary");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(cityMap);

                fileOutputStream.flush();
                fileOutputStream.close();

                objectOutputStream.flush();
                objectOutputStream.close();





            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void readCitiesDictionary(){
        Map<String, CityIndexEntry> cityMap = null;

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("resources\\citiesDictionary");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
             cityMap= (Map<String, CityIndexEntry>) objectInputStream.readObject();
             
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (String string : cityMap.keySet() ) {
            System.out.println(string+","+cityMap.get(string).getCountryName());
        }

    }

    @Test
    public void testCityIndex() throws InterruptedException {

        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);



        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));

        HashSet<String> stopwords = Parse.getStopWords(pathToStopWordRONEN);
        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer, true));
        Indexer indexer =new Indexer("C:\\Users\\ronen\\Desktop\\test",termDocumentsBuffer,true);
        Thread tIndexer = new Thread(indexer);

        long start=System.currentTimeMillis();


        tReader.start();

        tParser.start();

        tIndexer.start();
        tIndexer.join();



    }
}
