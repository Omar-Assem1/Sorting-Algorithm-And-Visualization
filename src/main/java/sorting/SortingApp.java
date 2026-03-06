package sorting;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SortingApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sorting/MainView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("/sorting/styles.css").toExternalForm());
        primaryStage.setTitle("Sorting Algorithm Analyser");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
