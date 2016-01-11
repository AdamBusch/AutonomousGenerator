package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("res/main.fxml"));
        primaryStage.setTitle("Autonomous Generator");
        primaryStage.setScene(new Scene(root, 950, 500));
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("res/logo.jpg"))); // test test
    }


    public static void main(String[] args) {
        launch(args);
    }
}
