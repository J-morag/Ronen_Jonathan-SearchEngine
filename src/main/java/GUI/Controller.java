package GUI;

import Indexing.Index.IndexEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.*;

/**
 * MVC controller
 */
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

    public Alert generateIndex(boolean memorySaver) {
        String corpusLocation = view.getCorpusLocation().toString();
        String stopwordsLocation = view.getStopwordsLocation().toString();
        String outputLocation = view.getOutputLocation().toString();
        if(corpusLocation.isEmpty() ) return new Alert(Alert.AlertType.ERROR, "Please specify corpus location.");
        else if (stopwordsLocation.isEmpty()) return new Alert(Alert.AlertType.ERROR, "Please specify stopwords file location.");
        else if (outputLocation.isEmpty()) return new Alert(Alert.AlertType.ERROR, "Please specify output location.");
        else{
            try {

                String information;
                if(memorySaver){
                    information = model.generateIndexTwoPhase(view.isUseStemming() , corpusLocation, outputLocation, stopwordsLocation );
                }
                else
                    information = model.generateIndex(view.isUseStemming() , corpusLocation, outputLocation, stopwordsLocation );


                //TODO get languages
                view.setLanguages();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, information);
                alert.setHeaderText("Index Generated!");
                return alert;
            } catch (InterruptedException e) {
                return new Alert(Alert.AlertType.ERROR, "IO error. Please check the paths and try again.");
            }
            catch (Exception e){
                e.printStackTrace();
                return new Alert(Alert.AlertType.ERROR, "Fatal error encountered during index generation: " + e.getMessage());
            }
        }
    }

    private void setLanguages(){

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
        String outputLocation = view.getOutputLocation().toString();

        if(outputLocation.isEmpty() ) return new Alert(Alert.AlertType.ERROR, "Please specify output folder location.");
        try {
            model.loadDictionary(view.isUseStemming(), outputLocation);
        } catch (Exception e) {
            return new Alert(Alert.AlertType.ERROR, "No valid dictionary file found in the given output folder.");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Dictionary loaded successfully.");
        alert.setHeaderText("Dictionary Loaded");

        //TODO display languages

        return alert;
    }
}
