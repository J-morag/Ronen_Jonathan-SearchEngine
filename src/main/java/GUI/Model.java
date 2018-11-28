package GUI;

import Indexing.Index.IndexEntry;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private Controller controller;
    private Map<String, IndexEntry> mainDictionary;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Map<String, IndexEntry> getDictionary() {
//        return mainDictionary;
        //TODO remove
        HashMap<String, IndexEntry> map = new HashMap<>();
        for (int i = 0; i < 60 ; i++) {
            map.put("test" + i, new IndexEntry("terms1", 34353, 324, 90, 87));

        }
        return map;
    }
}
