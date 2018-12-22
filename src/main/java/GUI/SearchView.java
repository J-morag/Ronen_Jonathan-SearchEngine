package GUI;

import Querying.QueryResult;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.net.URL;
import java.util.*;

public class SearchView  {

    private Controller myController;

    public TextField search_queryText;
    public TextField search_queryFile;
    public Button search_browse;
    public MenuButton search_cityComboBox;
    public CheckBox search_semantic;
    public Button search_run;
    public Button search_clear;

    private List<QueryResult> result;
    private boolean useStemming;

    public void setUseStemming(boolean useStemming) {
        this.useStemming = useStemming;
    }

    public boolean isUseStemming() {
        return useStemming;
    }

    public void setView() {
     Set<String> citiesSet = myController.getAllCities();
     for (String city : citiesSet) {
         CheckMenuItem checkMenuItem = new CheckMenuItem(city);
         search_cityComboBox.getItems().add(checkMenuItem);
     }
 }

    public void setController(Controller controller){
        this.myController=controller;
    }


    public void onBrowseCliked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Query File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File queryFile = fileChooser.showOpenDialog( search_browse.getScene().getWindow());
        if(null != queryFile) { //directory chosen
            String mimetype = new MimetypesFileTypeMap().getContentType(queryFile);
            String type = mimetype.split("/")[0];
            if (!type.equals("text")) {
                sendOnlyTextAlert();
                search_queryFile.setText("");
            } else {
                search_queryFile.textProperty().setValue(queryFile.getAbsolutePath());
            }
        }
    }



    public void onClearCliked(){
     search_semantic.setSelected(false);
     search_queryText.clear();
     search_queryFile.clear();
        for (MenuItem menuItem : search_cityComboBox.getItems() ) {
            CheckMenuItem checkMenuItem = (CheckMenuItem)menuItem;
            checkMenuItem.setSelected(false);
        }
    }

    public void onRunCliked(){
     if(search_queryText.getText().equals("")){
         if (search_queryFile.getText().equals("")){ //if both fields are empty -bad
              sendNoQueryAlert();
              onClearCliked();
         } else { // if only the fileQuery filed is not empty  - good (multiple queries)
//             List<String> queryList = getQueriesFromFile();

         }
     }
     else {
         if (!(search_queryFile.getText().equals(""))){ // if both filed are not empty - bad
             sendBothFildeAlert();
             onClearCliked();
         }
         else { // only the queryText field is full- good (only one query)


         }
     }
    }

    public void answerSingelQuery(String query ){

     result = myController.aswerSingelQuery(query , getCityFilter() , search_semantic.isSelected() ,useStemming);

    }

    private Set<String>  getCityFilter(){
     Set<String> cityFilterSet = new HashSet<>();
        for (MenuItem menuItem : search_cityComboBox.getItems() ) {
            CheckMenuItem checkMenuItem = (CheckMenuItem)menuItem;
            if (checkMenuItem.isSelected()){
                cityFilterSet.add(checkMenuItem.getText());
            }
        }
        return cityFilterSet;
    }


    private void sendNoQueryAlert() {
     Alert alert = new Alert(Alert.AlertType.ERROR);
     alert.setHeaderText("NO QUERY ALERT");
     alert.setContentText("NOT QUERY OR QUERY FILE ENTERED , TRY AGING ");
     alert.showAndWait();
    }

    private void sendBothFildeAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Both Filed are Full");
        alert.setContentText("Choose only one method to set a query\n by entering a file path or manually but not both! ");
        alert.showAndWait();
    }


    private void sendOnlyTextAlert() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Wrong File Type");
        alert.setContentText("you can choose only a text file Type");
        alert.showAndWait();
    }


}
