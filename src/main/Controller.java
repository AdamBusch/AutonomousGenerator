package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.parse.ParseException;
import main.parse.Parser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML public CodeArea codeArea;
    @FXML public Label errorMessage;

    private TextArea outputText;
    private Stage outputStage;
    private Stage helpStage;
    private TextArea helpText;

    private File currentFile;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setTextFill(Paint.valueOf("firebrick"));

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.richChanges().subscribe(change -> {
            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
        });

        outputText = new TextArea();
        outputText.setEditable(false);
        Scene scene = new Scene(outputText);
        outputStage = new Stage();
        outputStage.setScene(scene);
        outputStage.setTitle("Output");

        outputStage.setMinWidth(300);
        outputStage.setMinHeight(400);
        outputStage.hide();
        outputStage.getIcons().add(new Image(getClass().getResourceAsStream("res/logo.jpg")));

        helpText = new TextArea(help);
        helpText.setEditable(false);
        Scene scenee = new Scene(helpText);
        helpStage = new Stage();
        helpStage.setScene(scenee);
        helpStage.setTitle("Help");

        helpStage.setMinWidth(300);
        helpStage.setMinHeight(400);
        helpStage.hide();
        helpStage.getIcons().add(new Image(getClass().getResourceAsStream("res/logo.jpg")));

    }

    public void compile(ActionEvent actionEvent){
        try {


            outputText.setText(Parser.parseFileToCode(codeArea.getText()));

            outputStage.show();

            errorMessage.setText("");
        } catch (ParseException e) {
            errorMessage.setText(e.getMessage());
        }
    }

    public void help(ActionEvent actionEvent) {
        helpStage.show();
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
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "Save as...");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                currentFile = file;
                Scanner scanner = new Scanner(currentFile);

                String data = "";
                while (scanner.hasNextLine()){
                    data += scanner.nextLine() + "\n";
                }
                codeArea.replaceText(data);
                System.out.print(data);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAs(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "Save as...");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                currentFile = file;
                PrintStream fileWriter = new PrintStream(currentFile);

                Scanner scanner = new Scanner(codeArea.getText());
                while (scanner.hasNextLine()){
                    fileWriter.println(scanner.nextLine());

                }

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static void configureFileChooser(
            final FileChooser fileChooser, String title) {
        fileChooser.setTitle(title);
//        fileChooser.setInitialDirectory(
//                new File(System.getProperty("user.desktop"))
//        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
    }

    public void save(ActionEvent actionEvent) {
        if (currentFile == null) saveAs(actionEvent);
        try {
            PrintStream fileWriter = new PrintStream(currentFile);

            Scanner scanner = new Scanner(codeArea.getText());
            while (scanner.hasNextLine()){
                fileWriter.println(scanner.nextLine());

            }

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void createFile(ActionEvent actionEvent) {
    }


    private static String help = "Autonomous Generator\n" +
            "\n" +
            "\n" +
            "Commands:\n" +
            "\n" +
            "* wait <time> (optional: seconds)\n" +
            "   - waits the specified amount of time in mileseconds\n" +
            "\t(if the seconds modifier is present, the time will convert to seconds)\n" +
            "\n" +
            "* set servo <name> position <0-180 deg.>\n" +
            "   - Sets the position of the given servo\n" +
            "\t(the name is the corresponding name in the hardware config)\n" +
            "\n" +
            "* set servo <name> direction <forward or reverse>\n" +
            "   - Sets the direction of the given servo\n" +
            "\n" +
            "\n" +
            "* set motor <name> speed <0-100>\n" +
            "   - Sets the speed percentage of the given motor\n" +
            "\n" +
            "\n" +
            "* set motor <name> direction <forward or reverse>\n" +
            "   - Sets the direction of the given motor\n" +
            "\n" +
            " Example:\n" +
            "\tset servo climberArm position 90\n" +
            "\tset motor liftMotor speed 45\n" +
            "\twait 3 seconds\n" +
            "\n" +
            "Groups:\n" +
            "* Creating a group\n" +
            "   #group <name> <motor/servo name> <motor/servo name>...\n" +
            "   - Groups are used for saving time by allowing multiple motors\n" +
            "     or servos to be controlled at the same time with the \"motors\"\n" +
            "     \"servos\" command.\n" +
            "\n" +
            "Example:\n" +
            "\t#group DRIVETRAIN frontLeftMotor backLeftMotor frontRightMotor backRightMotor\n" +
            "\tset motors DRIVETRAIN speed 100\n" +
            "   - This example will set all 4 of the motor's speed to 100 (can also be used with servos)";


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
    private static final String HASH_PATTERN1 = "^#[\\s]*?"+Pattern.compile("group")+ "[^\n]*"; /// TODO fix multiple lines
    private static final String HASH_PATTERN = "[\\s]*?group[^\n#]*" + "|" + "/\\*(.|/R)*?\\*/";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|/R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<HASH>" + HASH_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<DEVICE>" + DEVICE_PATTERN + ")"
                    + "|(?<COMMAND>" + COMMAND_PATTERN + ")"
    );



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
