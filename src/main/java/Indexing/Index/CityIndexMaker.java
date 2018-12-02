package Indexing.Index;

import Indexing.DocumentProcessing.Term;
import Indexing.DocumentProcessing.TermDocument;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * creates the city index.
 * holds the dictionary as a field. It can be retrieved with {@link #getCityDictionary()}.
 */
public class CityIndexMaker extends AIndexMaker {

    Map<String , CityIndexEntry> cityDictionary=null;

    public CityIndexMaker(String path) {
        super();
        getDictionaryFromDisk();

    }

    @Override
    public void addToIndex(TermDocument doc) {
        if (doc.getSerialID() != -1) {
            List<Term> title = doc.getTitle();
            List<Term> text = doc.getText();
            Map<String , Integer> counterMap = new LinkedHashMap<>();

            String cityDoc;
            try{
                cityDoc=doc.getCity().toString();
            }catch (NullPointerException e ){
                cityDoc="";
            }
            if(!cityDoc.equals("") && (cityDoc.charAt(0)>='A' && cityDoc.charAt(0)<='Z')) {
                if(!cityDictionary.containsKey(cityDoc)) {
                    CityIndexEntry cityIndexEntry = new CityIndexEntry(null, null, null);
                    cityDictionary.put(cityDoc,cityIndexEntry);
                }
                countApearance( text, counterMap, cityDoc);
                int index=0;
                for (Term term : text) {
                    String trm = term.toString().toUpperCase();
                    if (trm.equals(cityDoc)){
                        addToCityDictionary(trm , doc.getSerialID() , counterMap ,index);
                        index++;
                    }

                }

            }

        }
        else {
        dumpToDisk();
        }
    }

    private void dumpToDisk() {
        for ( String key : cityDictionary.keySet()  ) {

        }
    }

    private void addToCityDictionary(String trm, int serialID, Map<String, Integer> counterMap ,int index) {
        CityIndexEntry cityIndexEntry = cityDictionary.get(trm);
        Map<Integer , int[]> appearanceMap = cityIndexEntry.getDocsMap();

        if(!appearanceMap.containsKey(serialID)){
            int [] appearanceAray = new int[counterMap.get(trm)];
            appearanceAray[0]=index;
            counterMap.put(trm,counterMap.get(trm)-1);
            cityIndexEntry.addDocToMap(serialID,appearanceAray);
        }else {
            cityIndexEntry.addDocToMap(serialID,index,counterMap.get(trm));
            counterMap.put(trm,counterMap.get(trm)-1);
        }

    }





    private void countApearance( List<Term> text , Map<String , Integer> apearanceMap , String cityDoc){

        for (Term term : text ) {
            String trm= term.toString().toUpperCase();
            if(cityDoc.equals(trm)) {
                if (cityDictionary.containsKey(trm)) {
                    if (!apearanceMap.containsKey(trm)) {
                        apearanceMap.put(trm, 1);
                    } else {
                        apearanceMap.put(trm, new Integer(apearanceMap.get(trm) + 1));
                    }

                }
            }
        }
    }




    private void getDictionaryFromDisk(){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("resources\\citiesDictionary");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            cityDictionary= (Map<String, CityIndexEntry>) objectInputStream.readObject();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Map<String, CityIndexEntry> getCityDictionary() {
        return cityDictionary;
    }
}
