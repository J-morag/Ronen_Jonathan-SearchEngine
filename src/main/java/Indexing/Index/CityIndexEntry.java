package Indexing.Index;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CityIndexEntry implements Serializable {

    private String countryName;
    private String currency;
    private String population;
    private boolean isPartOfCorpus=false;
    private Map<Integer , int[]> docsMap;


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
}
