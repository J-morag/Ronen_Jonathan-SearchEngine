package GUI;

import Elements.Document;
import Elements.TermDocument;
import GUI.Controller;
import GUI.Model;
import Indexing.Index.Indexer;
import Indexing.Parse;
import Indexing.ReadFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main extends Application {

    // GLOBAL PARAMETERS
    private static final int documentBufferSize = 10;
    private static final int termBufferSize = 10;
    private static final int stemmedTermBufferSize = 10;

    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Desktop\\FB396001"; //TODO temporary! should come from UI
    private static final String pathToStopwordsFile = "/stopwords"; //TODO temporary! should come from UI
    private static final String pathToOutputFolder = "/output"; //TODO temporary! should come from UI

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();

        primaryStage.setTitle("!GOOGLE");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("GUI.fxml").openStream());
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);

        GUI view = fxmlLoader.getController();
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

    private static void createIndex() throws InterruptedException {

        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        BlockingQueue<Document> documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);


        //  Worker Threads:

        Thread tReader = new Thread(new ReadFile(pathToDocumentsFolder, documentBuffer));
        tReader.start();

        HashSet<String> stopwords = Parse.getStopWords(pathToStopwordsFile);

        Thread tParser = new Thread(new Parse(stopwords, documentBuffer, termDocumentsBuffer));
        tParser.start();

        Thread tIndexer = new Thread(new Indexer(pathToOutputFolder, stemmedTermDocumentsBuffer,true));
        tIndexer.start();
    }

}
