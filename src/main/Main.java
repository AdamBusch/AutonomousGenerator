package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.fxmisc.richtext.demo.JavaKeywordsAsync;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("res/main.fxml"));

        primaryStage.setTitle("Autonomous Generator");
        Scene scene = new Scene(root, 950, 500);
        scene.getStylesheets().add(getClass().getResource("res/keyword.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("res/logo.jpg")));

    }


    public static void main(String[] args) {
        launch(args);
    }
}
