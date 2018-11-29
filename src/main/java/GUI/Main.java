package GUI;

import Elements.Document;
import Elements.TermDocument;
import Indexing.Index.Indexer;
import Indexing.Parse;
import Indexing.ReadFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();

        primaryStage.setTitle("!GOOGLE");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("View.fxml").openStream());
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);

        View view = fxmlLoader.getController();
        Controller controller = new Controller(model, view);

        model.setController(controller);
        view.setController(controller);
//        viewC = view;
//        view.setResizeEvent(scene);
//        view.setViewModel(viewModel);
//        viewModel.addObserver(view);
//        SetStageCloseEvent(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
//        createIndex();
    }


}
