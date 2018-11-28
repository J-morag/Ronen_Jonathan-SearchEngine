package GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GUI {

    private Controller controller;

    public TextField txtfld_corpus_location;
    public TextField txtfld_stopwords_location;

    public Button btn_corpus_browse;
    public Button btn_stopwords_browse;

    public CheckBox chkbox_use_stemming;

    public void browseCorpusLocation(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Corpus Location");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File corpusDir = directoryChooser.showDialog( btn_corpus_browse.getScene().getWindow());
        if(null != corpusDir){ //directory chosen
            txtfld_corpus_location.textProperty().setValue(corpusDir.getAbsolutePath());
        }
    }

    public void browseStopwordsLocation(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Stopwords File Location");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File corpusDir = fileChooser.showOpenDialog( btn_stopwords_browse.getScene().getWindow());
        if(null != corpusDir){ //directory chosen
            txtfld_stopwords_location.textProperty().setValue(corpusDir.getAbsolutePath());
        }
    }

    public void reset(ActionEvent actionEvent) {
        controller.reset();
    }

    public void generateIndex(ActionEvent actionEvent) {
        controller.generateIndex();
    }

    public void displayDictionary(ActionEvent actionEvent) {


        Stage stage = new Stage();
        stage.setTitle("Dictionary");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("DictionaryView.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 400, 700);
        stage.setScene(scene);

        DictionaryView dictionaryView = fxmlLoader.getController();
        dictionaryView.setTableData(controller.getDictionary());

        stage.show();
    }



    static class ObservableTuple{
        StringProperty term;
        StringProperty temInformation;

        public ObservableTuple(StringProperty term, StringProperty temInformation) {
            this.term = term;
            this.temInformation = temInformation;
        }

//        public List<String> getFields(){
//            ArrayList<String> fields = new ArrayList<String>();
//            fields.add(id);
//            fields.add(username.getValue());
//            fields.add(password);
//            fields.add(birthDate.getValue());
//            fields.add(firstName.getValue());
//            fields.add(lastName.getValue());
//            fields.add(city.getValue());
//            return fields;
//        }
    }

    public void loadDictionary(ActionEvent actionEvent) {
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
