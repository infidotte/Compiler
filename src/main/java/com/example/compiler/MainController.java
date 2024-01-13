package com.example.compiler;

import com.example.compiler.Analyzer.Lexical;
import com.example.compiler.Analyzer.Semantic;
import com.example.compiler.Analyzer.Syntactic;
import com.example.compiler.Tables.Tables;
import com.example.compiler.Util.Bracket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class MainController {
    @FXML
    public TextArea TN;
    @FXML
    public TextArea output;
    @FXML
    public TextArea TI;
    @FXML
    public TextArea TL;
    @FXML
    public TextArea TW;
    @FXML
    public Button analyze;
    @FXML
    public Button input;
    @FXML
    private TextArea programText;
    private File file;

    @FXML
    protected void onInputButtonClick(ActionEvent event) {
        clear();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(stage);
        programText.textProperty().setValue("file is successfully opened, click on the analyze button to continue");
    }

    @FXML
    public void onAnalyzeButtonClick() {
        clear();
        Lexical lexical = new Lexical(file);
        int lex, syn, sem;
        try {
            lex = lexical.analyzing();
            setProgramText();
            setTables();
            addTextToOutput("Lexical successfully completed");
            addTextToOutput("LEXICAL_RESULT: " + Tables.LEXICAL_RESULT.toString());
            if (lex == 0) {
                try {
                    Syntactic syntactic = new Syntactic();
                    syn = syntactic.analyzing();
                    addTextToOutput("Syntactic successfully completed");
                    addTextToOutput("ID_TYPES: " + Tables.ID_TYPES.toString());
                    addTextToOutput("NUM_TYPES: " + Tables.NUM_TYPES.toString());
                    if (syn == 0) {
                        try {
                            Semantic semantic = new Semantic();
                            sem = semantic.analyzing();
                            if (sem == 0) {
                                addTextToOutput("Semantic successfully completed");
                                addTextToOutput("ASSIGMENT: " + Tables.ASSIGMENT.toString());
                                addTextToOutput("EXPRESSION: " + Tables.EXPRESSION.toString());
                                addTextToOutput("Analyze successfully completed");
                            }
                        } catch (Exception e) {
                            addTextToOutput(e.toString());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    addTextToOutput(e.toString());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            addTextToOutput(e.toString());
            e.printStackTrace();
        }


    }

    private void addTextToOutput(String text) {
        output.appendText(text + "\n");
    }

    private void clear() {
        Tables.clear();
        TW.clear();
        TL.clear();
        TI.clear();
        TN.clear();
        programText.clear();
        output.clear();
    }

    private void setTables() {
        for (Map.Entry<Integer, String> entry :
                Tables.TW.getValues().entrySet()) {
            TW.appendText("(" + entry.getKey() + "," + entry.getValue() + ")\n");
        }
        for (Map.Entry<Integer, String> entry :
                Tables.TL.getValues().entrySet()) {
            TL.appendText("(" + entry.getKey() + "," + entry.getValue() + ")\n");
        }
        for (Map.Entry<Integer, String> entry :
                Tables.TI.getValues().entrySet()) {
            TI.appendText("(" + entry.getKey() + "," + entry.getValue() + ")\n");
        }
        for (Map.Entry<Integer, String> entry :
                Tables.TN.getValues().entrySet()) {
            TN.appendText("(" + entry.getKey() + "," + entry.getValue() + ")\n");
        }
    }

    private void setProgramText() {
        StringBuilder builder = new StringBuilder();
        for (String bracket : Tables.LEXICAL_RESULT) {
            builder.append(new Bracket(bracket).word());
        }
        programText.textProperty().set(builder.toString());
    }
}