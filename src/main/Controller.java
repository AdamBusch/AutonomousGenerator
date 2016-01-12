package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import main.parse.ParseException;
import main.parse.Parser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML public CodeArea codeArea;
    @FXML public Label errorMessage;

    private static final String[] KEYWORDS = new String[] {
            "set", "move", "wait"
    };

    private static final String[] DEVICES = new String[] {
            "servo", "motor", "servos", "motors"
    };

    private static final String[] COMMANDS = new String[] {
            "position", "direction", "speed"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String DEVICE_PATTERN = "\\b(" + String.join("|", DEVICES) + ")\\b";
    private static final String COMMAND_PATTERN = "\\b(" + String.join("|", COMMANDS) + ")\\b";
    private static final String HASH_PATTERN = "^#[\\s]*?"+Pattern.compile("group")+ "[^\n]*"; /// TODO fix multiple lines
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|/R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<HASH>" + HASH_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<DEVICE>" + DEVICE_PATTERN + ")"
                    + "|(?<COMMAND>" + COMMAND_PATTERN + ")"
    );


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setTextFill(Paint.valueOf("firebrick"));

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.richChanges().subscribe(change -> {
            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
        });
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

        alert.getDialogPane().getStylesheets().add(this.getClass().getResource("res/style.css").toExternalForm());
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

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("HASH") != null ? "hash" :
                                matcher.group("DEVICE") != null ? "device" :
                                    matcher.group("COMMAND") != null ? "command" :
                                        matcher.group("COMMENT") != null ? "comment" :
                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


}
