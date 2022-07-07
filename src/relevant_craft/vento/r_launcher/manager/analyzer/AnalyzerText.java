package relevant_craft.vento.r_launcher.manager.analyzer;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AnalyzerText extends VBox {

    private FlowPane line;
    private Label word;
    private boolean doHighlight;

    public AnalyzerText(ScrollPane scrollPane) {
        super();
        this.setStyle("-fx-background-color: white;");
        this.setPrefWidth(scrollPane.getPrefWidth() - 2);
        this.setPrefHeight(scrollPane.getPrefHeight() - 2);
        scrollPane.setContent(this);
    }

    public void addMessage(String msg) {
        if (msg.isEmpty()) {
            this.getChildren().add(new Text());
            return;
        }

        line = new FlowPane();

        doHighlight = false;
        word = new Label();

        String[] letters = msg.split("");
        for (String letter : letters) {
            if (letter.equals("«")) {
                addWord();
                doHighlight = true;
            }
            if (letter.equals("`")) {
                addWord();
                doHighlight = !doHighlight;
                continue;
            }
            if (letter.equals(" ")) {
                addWord();
                line.getChildren().add(new Text(" "));
                continue;
            }

            word.setText(word.getText() + letter);

            if (letter.equals("»")) {
                addWord();
                doHighlight = false;
            }
        }

        if (!word.getText().isEmpty()) {
            addWord();
        }

        this.getChildren().add(line);
    }

    private void addWord() {
        if (doHighlight) {
            word.setTextFill(Color.BLACK);
            word.setFont(Font.font("System", FontWeight.BOLD, 15));
        } else {
            word.setTextFill(Color.BLACK);
            word.setFont(Font.font(15));
        }

        line.getChildren().add(word);
        word = new Label();
    }
}