package relevant_craft.vento.r_launcher.gui.notify;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.icons525.Icons525View;
import javafx.animation.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;

public class NotifyWindow {
    private NotifyType type;
    private String title_text;
    private String message_text;
    private boolean YesOrNo = false;
    private boolean answer;

    public NotifyWindow(NotifyType type) {
        this.type = type;
    }

    public void setTitle(String title_text) {
        this.title_text = title_text;
    }

    public void setMessage(String message_text) {
        this.message_text = message_text;
    }

    public void setYesOrNo(boolean YesOrNo) {
        this.YesOrNo = YesOrNo;
    }

    public boolean getAnswer() {
        return answer;
    }

    public void showNotify() {
        Stage window = new Stage();
        window.initStyle(StageStyle.TRANSPARENT);
        window.initModality(Modality.WINDOW_MODAL);
        window.initOwner(VENTO.window);

        AnchorPane layout = new AnchorPane();
        layout.setStyle("-fx-background-color: transparent;");
        layout.setPrefWidth(VENTO.window.getWidth());
        layout.setPrefHeight(VENTO.window.getHeight());

        AnchorPane notify = new AnchorPane();
        notify.setPrefWidth(VENTO.window.getWidth() - VENTO.window.getWidth() / 5);
        notify.setPrefHeight(250);
        notify.setStyle("-fx-background-color: #3a3a3a;");
        notify.setLayoutX(layout.getPrefWidth() / 2 - notify.getPrefWidth() / 2);
        notify.setLayoutY(layout.getPrefHeight() / 2 - notify.getPrefHeight() / 2);
        layout.getChildren().addAll(notify);

        Pane darkTitle = new Pane();
        darkTitle.setPrefWidth(notify.getPrefWidth());
        darkTitle.setPrefHeight(85);
        darkTitle.setStyle("-fx-background-color: #333333;");
        darkTitle.setLayoutX(0);
        darkTitle.setLayoutY(0);
        notify.getChildren().add(darkTitle);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(13);
        shadow.setColor(Color.valueOf("rgba(0, 0, 0, 0.75);"));
        notify.setEffect(shadow);

        window.setScene(new Scene(layout, layout.getPrefWidth(), layout.getPrefHeight()));
        window.getScene().setFill(null);
        window.setY(VENTO.window.getY());
        window.setX(VENTO.window.getX());

        Icons525View icon = new Icons525View(type.getIcon());
        icon.setFill(Paint.valueOf("#ffffff"));
        icon.setGlyphSize(50);
        icon.setLayoutY(65);
        icon.setLayoutX(30);
        notify.getChildren().add(icon);

        FontAwesomeIconView title = new FontAwesomeIconView();
        title.setText(title_text);
        title.setFill(Paint.valueOf("#ffffff"));
        title.setGlyphSize(40);
        title.setLayoutY(58);
        title.setLayoutX(icon.getLayoutX() + icon.getGlyphSize().intValue() + 25);
        notify.getChildren().add(title);

        int startY = (int) (darkTitle.getPrefHeight() + 20);    //«»
        String[] lines = message_text.split("\n");
        for (String text : lines) {
            Label line = new Label();
            line.setLayoutY(startY);
            line.setLayoutX(icon.getLayoutX());

            if (text.contains("«") && text.contains("»") || text.contains("`")) {
                TextFlow textFlow = new TextFlow();
                boolean doHighlight = false;
                for (String letter : text.split("")) {
                    if (letter.equals("«")) {
                        doHighlight = true;
                    }
                    if (letter.equals("`")) {
                        doHighlight = !doHighlight;
                        continue;
                    }

                    Text t = new Text(letter);
                    if (doHighlight) {
                        t.setFont(Font.font("System", FontWeight.BOLD, 17));
                        t.setFill(Paint.valueOf("#f0f051"));
                    } else {
                        t.setFont(Font.font("System", FontPosture.REGULAR, 17));
                        t.setFill(Paint.valueOf("#ffffff"));
                    }
                    textFlow.getChildren().add(t);

                    if (letter.equals("»")) {
                        doHighlight = false;
                    }
                }
                line.setGraphic(textFlow);
            } else {
                line.setText(text);
                line.setTextFill(Paint.valueOf("#ffffff"));
                line.setFont(Font.font(17));
            }

            startY += 25;
            notify.getChildren().add(line);
        }

        JFXButton confirm = new JFXButton();
        confirm.setPrefHeight(37);
        confirm.setPrefWidth(100);
        String confirm_unfocused = "-fx-border-color: white; -fx-border-width: 3px; -fx-background-color: transparent;";
        String confirm_focused = "-fx-border-color: #57c65c; -fx-border-width: 3px; -fx-background-color: #57c65c;";
        confirm.setStyle(confirm_unfocused);
        confirm.setText((YesOrNo ? "Да" : "ОК"));
        confirm.setTextFill(Paint.valueOf("#ffffff"));
        confirm.setFont(Font.font("System", FontWeight.BOLD, 15));
        confirm.setCursor(Cursor.HAND);
        confirm.setLayoutX(notify.getPrefWidth() - confirm.getPrefWidth() - 25);
        confirm.setLayoutY(notify.getPrefHeight() - confirm.getPrefHeight() - 25);
        confirm.setFocusTraversable(false);
        confirm.setOnAction(event -> {
            answer = true;
            close(window, notify);
        });
        confirm.setOnMouseEntered(event -> {
            confirm.setStyle(confirm_focused);
        });
        confirm.setOnMouseExited(event -> {
            confirm.setStyle(confirm_unfocused);
        });
        notify.getChildren().add(confirm);

        if (YesOrNo) {
            JFXButton deny = new JFXButton();
            deny.setPrefHeight(37);
            deny.setPrefWidth(100);
            String deny_unfocused = "-fx-border-color: white; -fx-border-width: 3px; -fx-background-color: transparent;";
            String deny_focused = "-fx-border-color: #f76750; -fx-border-width: 3px; -fx-background-color: #f76750;";
            deny.setStyle(deny_unfocused);
            deny.setText("Нет");
            deny.setTextFill(Paint.valueOf("#ffffff"));
            deny.setFont(Font.font("System", FontWeight.BOLD, 15));
            deny.setCursor(Cursor.HAND);
            deny.setLayoutX(confirm.getLayoutX() - deny.getPrefWidth() - 25);
            deny.setLayoutY(confirm.getLayoutY());
            deny.setFocusTraversable(false);
            deny.setOnAction(event -> {
                answer = false;
                close(window, notify);
            });
            deny.setOnMouseEntered(event -> {
                deny.setStyle(deny_focused);
            });
            deny.setOnMouseExited(event -> {
                deny.setStyle(deny_unfocused);
            });
            notify.getChildren().add(deny);
        }

        GaussianBlur blur = new GaussianBlur(0);
        VENTO.root.setEffect(blur);
        Animation animation = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(blur.radiusProperty(), 12)));
        animation.setDelay(Duration.millis(50));
        animation.play();

        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(notify);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();

        window.showAndWait();
    }

    private void close(Stage window, AnchorPane notify) {
        GaussianBlur blur = (GaussianBlur) VENTO.root.getEffect();
        Animation animation = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(blur.radiusProperty(), 0)));
        animation.play();
        animation.setOnFinished(e -> VENTO.root.setEffect(null));

        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(notify);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.play();
        transition.setOnFinished(e -> {
            window.close();
        });
    }
}