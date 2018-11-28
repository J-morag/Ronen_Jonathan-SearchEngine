package GUI;

import Indexing.Index.IndexEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

import java.util.*;

public class Controller {

    Model model;

    public Controller(Model model, GUI view) {
        this.model = model;
    }

    public void reset() {
    }

    public void generateIndex() {
    }

    public List<GUI.ObservableTuple> getDictionary() {
        Map<String, IndexEntry> dictionary = model.getDictionary();
        List<GUI.ObservableTuple> res = new LinkedList<>();
        Object[] keysAsObject = (dictionary.keySet().toArray());
        String[] keys = new String[keysAsObject.length];
        for (int i = 0; i < keysAsObject.length ; i++) {
            keys[i] = (String) keysAsObject[i];
        }
        Arrays.sort(keys);

        Object[] values = dictionary.values().toArray();
        String[] valuesAsStrings = new String[values.length];
        for (int i = 0; i < values.length ; i++) {
            valuesAsStrings[i] = values[i].toString();
        }
        Arrays.sort(valuesAsStrings);

        for (int i = 0; i < keys.length ; i++) {
            res.add(new GUI.ObservableTuple(new SimpleStringProperty(keys[i]), new SimpleStringProperty(valuesAsStrings[i])));
        }

        return res;
    }
}
