package relevant_craft.vento.r_launcher.manager.tutorial;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.utils.AnimationUtils;

public class TutorialNotify extends Stage {

    private Pane layout;
    private Pane notify;

    private final String COLOR = "#1e90ff";
    private double X;
    private double Y;

    public TutorialNotify() {
        super();
        this.initStyle(StageStyle.TRANSPARENT);
        this.initModality(Modality.NONE);
        this.initOwner(VENTO.window);
        this.setWidth(220);
        this.setHeight(90);
        this.updateCoords();

        layout = new Pane();
        layout.setPrefHeight(this.getWidth());
        layout.setPrefHeight(this.getHeight());
        layout.setStyle("-fx-background-color: transparent;");

        notify = new Pane();
        notify.setPrefWidth(200);
        notify.setPrefHeight(70);
        notify.setStyle("-fx-background-color: " + COLOR + "; -fx-background-radius: 3;");
        notify.setLayoutX(this.getWidth() / 2 - notify.getPrefWidth() / 2);
        notify.setLayoutY(this.getHeight() / 2 - notify.getPrefHeight() / 2);
        layout.getChildren().add(notify);

        this.renderClose();

        this.setScene(new Scene(layout));
        this.getScene().setFill(null);
        this.show();

        this.setOpacity(0);
    }

    public void renderTopArrow(int offset) {
        Pane arrow = new Pane();
        arrow.setPrefWidth(20);
        arrow.setPrefHeight(10);
        arrow.setStyle("-fx-background-color: " + COLOR + "; -fx-shape: \"M100 0 L200 200 L0 200 Z\";");
        arrow.setLayoutX(this.getWidth() / 2 - arrow.getPrefWidth() / 2 + offset);
        arrow.setLayoutY(0);
        layout.getChildren().add(arrow);
    }

    public void renderRightArrow(int offset) {
        Pane arrow = new Pane();
        arrow.setPrefWidth(10);
        arrow.setPrefHeight(20);
        arrow.setStyle("-fx-background-color: " + COLOR + "; -fx-shape: \"M0 0 L0 200 L200 100 Z\";");
        arrow.setLayoutX(this.getWidth() - arrow.getPrefWidth());
        arrow.setLayoutY(this.getHeight() / 2 - arrow.getPrefHeight() / 2 + offset);
        layout.getChildren().add(arrow);
    }

    public void renderBottomArrow(int offset) {
        Pane arrow = new Pane();
        arrow.setPrefWidth(20);
        arrow.setPrefHeight(10);
        arrow.setStyle("-fx-background-color: " + COLOR + "; -fx-shape: \"M0 0 L200 00 L100 200 Z\";");
        arrow.setLayoutX(this.getWidth() / 2 - arrow.getPrefWidth() / 2 + offset);
        arrow.setLayoutY(this.getHeight() - arrow.getPrefHeight());
        layout.getChildren().add(arrow);
    }

    public void renderLeftArrow(int offset) {
        Pane arrow = new Pane();
        arrow.setPrefWidth(10);
        arrow.setPrefHeight(20);
        arrow.setStyle("-fx-background-color: " + COLOR + "; -fx-shape: \"M200 0 L100 100 L200 200 Z\";");
        arrow.setLayoutX(0);
        arrow.setLayoutY(this.getHeight() / 2 - arrow.getPrefHeight() / 2 + offset);
        layout.getChildren().add(arrow);
    }

    public void setText(String info) {
        Label text = new Label();
        text.setText(info);
        text.setWrapText(true);
        text.setPrefWidth(180);
        text.setPrefHeight(30);
        text.setLayoutX(notify.getPrefWidth() / 2 - text.getPrefWidth() / 2);
        text.setLayoutY(35);
        text.setFont(Font.font(14));
        text.setTextFill(Color.WHITE);
        text.setAlignment(Pos.CENTER);
        text.setTextAlignment(TextAlignment.CENTER);
        notify.getChildren().add(text);
    }

    public void setIcon(FontAwesomeIcon fontAwesomeIcon) {
        FontAwesomeIconView icon = new FontAwesomeIconView(fontAwesomeIcon);
        icon.setGlyphSize(28);
        icon.setFill(Color.WHITE);
        icon.setLayoutX(notify.getPrefWidth() / 2 - icon.getGlyphSize().doubleValue() / 2);
        icon.setLayoutY(30);
        notify.getChildren().add(icon);
    }

    private void renderClose() {
        FontAwesomeIconView close = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
        close.setFill(Color.WHITE);
        close.setGlyphSize(16);
        close.setLayoutX(notify.getPrefWidth() - close.getGlyphSize().intValue());
        close.setLayoutY(close.getGlyphSize().intValue());
        close.setCursor(Cursor.HAND);
        close.setOpacity(0.75);
        close.setOnMouseEntered(e -> {
            AnimationUtils.applyEnterAnimation(close);
        });
        close.setOnMouseExited(e -> {
            AnimationUtils.applyExitAnimation(close);
        });
        close.setOnMouseClicked(e -> {
            TutorialManager.closeNotifies();
        });
        notify.getChildren().add(close);
    }

    public void renderNotify(int delay) {
        Animation animation = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(this.opacityProperty(), 1)));
        animation.setDelay(Duration.millis(delay));
        animation.play();
    }

    public void setCoords(double X, double Y) {
        this.X = X;
        this.Y = Y;
        updateCoords();
    }

    public void updateCoords() {
        this.setX(VENTO.window.getX() + X);
        this.setY(VENTO.window.getY() + Y);
    }

    @Override
    public void close() {
        Animation animation = new Timeline(new KeyFrame(Duration.millis(75), new KeyValue(this.opacityProperty(), 0)));
        animation.play();
        animation.setOnFinished(e -> {
            super.close();
        });
    }
}
