package GUI;

import Querying.QueryResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResultView  {

    @FXML
    public TableColumn<SearchView.ObservableCell, String> result_docsCol;
    @FXML
    public TableColumn<SearchView.ObservableCell, String> result_queryCol;
    @FXML
    public TableView<SearchView.ObservableCell> result_Querytbl;
    @FXML
    public TableView<SearchView.ObservableCell> result_Docstbl;

    private List<QueryResult> result;


    @FXML
    public void initialize() {
        result_queryCol.setCellValueFactory(cellData -> cellData.getValue().data);
        result_Querytbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            QueryResult res = result.get(result_Querytbl.getSelectionModel().selectedIndexProperty().getValue());
            List<SearchView.ObservableCell> listOfdocs=new ArrayList<>();
            for (String s : res.getRelevantDocs()) {
                listOfdocs.add(new SearchView.ObservableCell(new SimpleStringProperty(s)));
            }
            result_Docstbl.setItems(FXCollections.observableArrayList(listOfdocs));
            result_docsCol.setCellValueFactory(cellData -> cellData.getValue().data);
        });


    }



    public void setTableData(List<SearchView.ObservableCell> queryList){
        result_Querytbl.setItems(FXCollections.observableArrayList(queryList));
        result_Querytbl.getSelectionModel().selectFirst();
    }


    //public void

    public void setResult(List<QueryResult> result) {
        this.result = result;
    }


    public List<QueryResult> getResult() {
        return result;
    }

}
