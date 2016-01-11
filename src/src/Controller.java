package src;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import src.parse.ParseException;
import src.parse.Parser;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML public TextArea codeArea;
    @FXML public Text errorMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setFill(Paint.valueOf("firebrick"));
    }

    public void compile(ActionEvent actionEvent){
        try {
            System.out.println(Parser.parseFileToCode(codeArea.getText()));
            errorMessage.setText("");
        } catch (ParseException e) {
            errorMessage.setText(e.getMessage());
        }
    }

    public void help(ActionEvent actionEvent) {

    }

    public void contact(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Us");
        alert.setHeaderText(null);
        alert.setContentText("Having issues or have any questions? Contact us at: \nTwitter: @FTC6210\nEmail: ftc6210@gmail.com");

        alert.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        alert.showAndWait();
    }

    public void load(ActionEvent actionEvent) {
    }

    public void saveAs(ActionEvent actionEvent) {
    }

    public void save(ActionEvent actionEvent) {
    }

    public void createFile(ActionEvent actionEvent) {
    }


}
