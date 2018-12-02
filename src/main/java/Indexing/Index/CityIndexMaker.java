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

public class CityIndexMaker extends AIndexMaker {

    Map<String , CityIndexEntry> cityDictionary=null;

    public CityIndexMaker(String path) {
        super();
        getDictionaryFromDisk();

    }

    @Override
    public void addToIndex(TermDocument doc) {
        if (doc.getSerialID() != -1) {
            String docCity = "";
            if (null != doc.getCity())
                docCity = doc.getCity().toString();

            Map<String, Integer> apearanceMap = new LinkedHashMap<>();
            List<Term> title = doc.getTitle();
            List<Term> text = doc.getText();
            countApearance(title, text, apearanceMap);
            int index = 1;
            for (Term term : title) {
                String trm = term.toString().toUpperCase();
                if (apearanceMap.containsKey(trm)) {
                    CityIndexEntry cityIndexEntry = cityDictionary.get(trm);
                    Map<Integer, int[]> docsMap = cityIndexEntry.getDocsMap();
                    if (!docsMap.containsKey(doc.getSerialID())) {
                        if (docCity.equals(trm)) {
                            cityIndexEntry.addDocToMap(doc.getSerialID(), new int[apearanceMap.get(trm) + 1]);
                            docsMap.get(doc.getSerialID())[0] = 0;
                            docsMap.get(doc.getSerialID())[1] = index;
                            apearanceMap.put(trm, apearanceMap.get(trm) - 1);
                        } else {
                            cityIndexEntry.addDocToMap(doc.getSerialID(), new int[apearanceMap.get(trm)]);
                            docsMap.get(doc.getSerialID())[0] = index;
                            apearanceMap.put(trm, apearanceMap.get(trm) - 1);
                        }
                    } else {

                        docsMap.get(doc.getSerialID())[docsMap.get(doc.getSerialID()).length - apearanceMap.get(trm)] = index;
                        apearanceMap.put(trm, apearanceMap.get(trm) - 1);
                    }
                } else {
                    index++;
                    continue;
                }
                index++;

            }


            for (Term term : text) {
                String trm = term.toString().toUpperCase();
                if (apearanceMap.containsKey(trm)) {
                    CityIndexEntry cityIndexEntry = cityDictionary.get(trm);
                    Map<Integer, int[]> docsMap = cityIndexEntry.getDocsMap();
                    if (!docsMap.containsKey(doc.getSerialID())) {
                        if (docCity.equals(trm)) {
                            cityIndexEntry.addDocToMap(doc.getSerialID(), new int[apearanceMap.get(trm) + 1]);
                            docsMap.get(doc.getSerialID())[0] = 0;
                            docsMap.get(doc.getSerialID())[1] = index;
                            apearanceMap.put(trm, apearanceMap.get(trm) - 1);
                        } else {
                            cityIndexEntry.addDocToMap(doc.getSerialID(), new int[apearanceMap.get(trm)]);
                            docsMap.get(doc.getSerialID())[0] = index;
                            apearanceMap.put(trm, apearanceMap.get(trm) - 1);
                        }
                    } else {

                        docsMap.get(doc.getSerialID())[docsMap.get(doc.getSerialID()).length - apearanceMap.get(trm)] = index;
                        apearanceMap.put(trm, apearanceMap.get(trm) - 1);
                    }
                } else {
                    index++;
                    continue;
                }
                index++;
            }


            title.clear();
            text.clear();
            apearanceMap.clear();

        }
    }









    private void countApearance(List<Term> title , List<Term> text , Map<String , Integer> apearanceMap){
        for (Term term : title ) {
            String trm= term.toString().toUpperCase();
            if(cityDictionary.containsKey(trm)){
                if (!apearanceMap.containsKey(trm)){
                    apearanceMap.put(trm,1);
                }else {
                    apearanceMap.put(trm,new Integer(apearanceMap.get(trm)+1));
                }

            }
        }

        for (Term term : text ) {
            String trm= term.toString().toUpperCase();
            if(cityDictionary.containsKey(trm)){
                if (!apearanceMap.containsKey(trm)){
                    apearanceMap.put(trm,1);
                }else {
                    apearanceMap.put(trm,new Integer(apearanceMap.get(trm)+1));
                }

            }
        }
    }




    private void getDictionaryFromDisk(){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("citiesDictionary");
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
