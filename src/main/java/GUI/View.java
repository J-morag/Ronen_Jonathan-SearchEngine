package GUI;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * MVC view
 */
public class View {

    private Controller controller;

    public TextField txtfld_corpus_location;
    public TextField txtfld_stopwords_location;
    public TextField txtfld_output_location;

    public Button btn_corpus_browse;
    public Button btn_stopwords_browse;
    public Button btn_output_browse;
    public Button btn_reset;
    public Button btn_load_dictionary;
    public Button btn_display_dictionary;

    public CheckBox chkbox_use_stemming;

    public CharSequence getOutputLocation(){
        return txtfld_output_location.getCharacters();
    }

    public CharSequence getCorpusLocation() {
        return txtfld_corpus_location.getCharacters();
    }

    public boolean isUseStemming() {
        return chkbox_use_stemming.isSelected();
    }

    public void browseCorpusLocation(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Corpus Location");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File corpusDir = directoryChooser.showDialog( btn_corpus_browse.getScene().getWindow());
        if(null != corpusDir){ //directory chosen
            txtfld_corpus_location.textProperty().setValue(corpusDir.getAbsolutePath());
        }
    }

    public void browseOutputLocation(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Output Location");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File corpusDir = directoryChooser.showDialog( btn_output_browse.getScene().getWindow());
        if(null != corpusDir){ //directory chosen
            txtfld_output_location.textProperty().setValue(corpusDir.getAbsolutePath());
        }
    }

    public void browseStopwordsLocation(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output Location");
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
        Alert generatingAlert = new Alert(Alert.AlertType.INFORMATION, "Generating Index");
        generatingAlert.setHeaderText("Generating Index");
        generatingAlert.setTitle("Generating Index");
        generatingAlert.show();

        Alert alert = controller.generateIndex();

        generatingAlert.close();

        handleNewDictionary(alert);
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

    public CharSequence getStopwordsLocation() {
        return txtfld_stopwords_location.getCharacters();
    }

    public void setLanguages() {
        //TODO implement
    }


    static class ObservableTuple{
        StringProperty term;
        StringProperty temInformation;

        public ObservableTuple(StringProperty term, StringProperty temInformation) {
            this.term = term;
            this.temInformation = temInformation;
        }
    }

    public void loadDictionary(ActionEvent actionEvent) {
        Alert result = controller.loadDictionary();
        handleNewDictionary(result);
    }

    private void handleNewDictionary(Alert result) {
        if(result.getAlertType() == Alert.AlertType.ERROR){
            result.showAndWait();
        }
        else{
            result.show();
            btn_reset.setDisable(false);
            btn_display_dictionary.setDisable(false);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
