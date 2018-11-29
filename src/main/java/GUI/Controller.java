package GUI;

import Indexing.Index.IndexEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.*;

public class Controller {

    Model model;
    View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void reset() {
        model.reset(view.getOutputLocation().toString());
    }

    public Alert generateIndex() {
        String corpusLocation = view.getCorpusLocation().toString();
        String stopwordsLocation = view.getStopwordsLocation().toString();
        String outputLocation = view.getOutputLocation().toString();
        if(corpusLocation.isEmpty() ) return new Alert(Alert.AlertType.ERROR, "Please specify corpus location.");
        else if (stopwordsLocation.isEmpty()) return new Alert(Alert.AlertType.ERROR, "Please specify stopwords file location.");
        else if (outputLocation.isEmpty()) return new Alert(Alert.AlertType.ERROR, "Please specify output location.");
        else{
            String information = model.generateIndex(view.isUseStemming() , corpusLocation, outputLocation, stopwordsLocation );
            view.setLanguages(); //TODO send something
            return new Alert(Alert.AlertType.INFORMATION, information);
        }
    }

    public List<View.ObservableTuple> getDictionary() {
        Map<String, IndexEntry> dictionary = model.getDictionary(view.isUseStemming());
        List<View.ObservableTuple> res = new LinkedList<>();
        if(null == dictionary){
            return res;
        }
        else{
            Object[] keysAsObject = (dictionary.keySet().toArray());
            String[] keys = new String[keysAsObject.length];
            for (int i = 0; i < keysAsObject.length ; i++) {
                keys[i] = (String) keysAsObject[i];
            }

            Object[] values = dictionary.values().toArray();
            String[] valuesAsStrings = new String[values.length];
            for (int i = 0; i < values.length ; i++) {
                valuesAsStrings[i] = values[i].toString();
            }

            for (int i = 0; i < keys.length ; i++) {
                res.add(new View.ObservableTuple(new SimpleStringProperty(keys[i]), new SimpleStringProperty(valuesAsStrings[i])));
            }

            return res;
        }
    }

    public Alert loadDictionary() {
        try {
            model.loadDictionary(view.isUseStemming(), view.getOutputLocation().toString());
        } catch (Exception e) {
            return new Alert(Alert.AlertType.ERROR, "No valid dictionary file found in given output folder.");
        }

        return new Alert(Alert.AlertType.INFORMATION, "Dictionary loaded");
    }
}
