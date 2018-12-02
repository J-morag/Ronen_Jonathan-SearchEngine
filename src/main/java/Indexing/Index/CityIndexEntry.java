package Indexing.Index;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data type containing the information pertaining to a single city in the city dictionary.
 */
public class CityIndexEntry implements Serializable {

    private String countryName;
    private String currency;
    private String population;
    private boolean isPartOfCorpus=false;
    private Map<Integer , int[]> docsMap;
    private int pointer;


    public CityIndexEntry(String countryName , String currency , String population , boolean isPartOfCorpus){
        this.countryName=countryName;
        this.currency=currency;
        this.population=population;
        this.isPartOfCorpus=isPartOfCorpus;
        docsMap=new LinkedHashMap<>();
    }

    public CityIndexEntry(String countryName , String currency , String population ){
        this.countryName=countryName;
        this.currency=currency;
        this.population=population;
        docsMap=new LinkedHashMap<>();
    }


    public void addDocToMap(int docNum , int [] positions ){
        if(!isPartOfCorpus){
            isPartOfCorpus=true;
        }
        docsMap.put(docNum,positions);
    }

    public void addDocToMap(int docNum , int index , int numLeft ){

        int [] arr =docsMap.get(docNum);
        int pointer = arr.length-numLeft;
        arr[pointer]=index;

    }

    public Map<Integer , int[]> getDocsMap (){
        return docsMap;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPopulation() {
        return population;
    }
    public Boolean isInCorpus(){
        return isPartOfCorpus;
    }

    public int getPointer(){return pointer;}

    public void setPointer(int newPointer){
        pointer=newPointer;
    }
}
