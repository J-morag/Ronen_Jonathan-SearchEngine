package GUI;

import Indexing.Index.IndexEntry;

import java.util.Map;

public class Model {

    private Controller controller;
    private Map<String, IndexEntry> mainDictionaryWithStemming;
    private Map<String, IndexEntry> mainDictionaryNoStemming;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Map<String, IndexEntry> getDictionary(boolean useStemming) {
        return useStemming ? mainDictionaryWithStemming : mainDictionaryNoStemming;
//        HashMap<String, IndexEntry> map = new HashMap<>();
//        Random rnd = new Random();
//        for (int i = 0; i < 60 ; i++) {
//            map.put("test" + i, new IndexEntry( rnd.nextInt(30000), rnd.nextInt(30000), rnd.nextInt(30000)));
//
//        }
//        return map;
    }

    public void loadDictionary(boolean useStemming, String s) {

    }

    public void reset(String s) {
    }

    public String generateIndex(boolean useStemming, String corpusLocation, String outputLocation, String stopwordsLocation) {

        //TODO shoould return information about: number of indexed docs, number o funique terms in dictionary, total runtime in seconds
        return null;
    }
}
