package GUI;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class DictionaryView {

    // DICTIONARY_VIEW
    @FXML
    public TableColumn<GUI.ObservableTuple, String> clmn_terms;
    @FXML
    public TableColumn<GUI.ObservableTuple, String> clmn_term_information;
    @FXML
    public TableView<GUI.ObservableTuple> tbl_dictionary;


    @FXML
    private void initialize(){
        clmn_terms.setCellValueFactory(cellData -> cellData.getValue().term);
        clmn_term_information.setCellValueFactory(cellData -> cellData.getValue().temInformation);

    }

    public void setTableData(List<GUI.ObservableTuple> dictionaryAsObservableTuples){
        tbl_dictionary.setItems(FXCollections.observableArrayList(dictionaryAsObservableTuples));
        //      stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
    }


}
