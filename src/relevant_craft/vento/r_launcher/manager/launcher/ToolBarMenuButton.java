package relevant_craft.vento.r_launcher.manager.launcher;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.EmojiOneView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;

public class ToolBarMenuButton {
    private final int BUTTON_SIZE = 51;
    private final int BUTTON_SPACE = 11;
    private static int BUTTON_X = 0;

    private JFXButton button = new JFXButton();
    private GlyphIcon icon = null;

    private Animation animation1 = null;
    private Animation animation2 = null;

    public ToolBarMenuButton(String name, FontAwesomeIcon fa_icon, EmojiOne eo_icon, String color, String rippler, VENTO.GUI gui, int width) {
        if (BUTTON_X == 0) {
            BUTTON_X = width;
        }

        BUTTON_X -= BUTTON_SIZE - BUTTON_SPACE;

        button.setText(" " + name);
        button.setPrefWidth(BUTTON_SIZE);
        button.setPrefHeight(BUTTON_SIZE);
        button.setStyle("-fx-background-color: " + color + ";");
        button.setTextFill(Paint.valueOf("#FFFFFF"));
        button.setFont(Font.font("System", FontWeight.BOLD, 18.0));
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setTextOverrun(OverrunStyle.ELLIPSIS);
        button.setEllipsisString(null);
        button.setGraphicTextGap(0);
        button.setAlignment(Pos.CENTER);
        button.setRipplerFill(Paint.valueOf(rippler));
        button.setCursor(Cursor.HAND);
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setLayoutX(BUTTON_X);

        if (fa_icon != null) {
            icon = new FontAwesomeIconView(fa_icon);
        } else if (eo_icon != null) {
            icon = new EmojiOneView(eo_icon);
        }
        if (icon != null) {
            icon.setGlyphSize(25);
            icon.setFill(Paint.valueOf("#ffffff"));
            button.setGraphic(icon);
        }

        button.setOnMouseEntered(e -> {
            showMenuButton();
        });

        button.setOnMouseExited(e -> {
            hideMenuButton();
        });

        button.setOnAction(e -> {
            LauncherManager.checkModalWindow();
            clickMenuButton(gui);
        });
    }

    private void showMenuButton() {
        float NEW_WIDTH = LauncherManager.fontLoader.computeStringWidth(button.getText(), button.getFont()) + 10;

        animation1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.prefWidthProperty(), BUTTON_SIZE + NEW_WIDTH)));
        animation1.play();

        animation2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.layoutXProperty(), button.getLayoutX() - NEW_WIDTH)));
        animation2.play();
    }

    private void hideMenuButton() {
        if (animation2.getStatus() == Animation.Status.RUNNING) {
            animation2.stop();
        }
        if (animation1.getStatus() == Animation.Status.RUNNING) {
            animation1.stop();
        }

        animation2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.layoutXProperty(), BUTTON_X)));
        animation2.play();

        animation1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.prefWidthProperty(), BUTTON_SIZE)));
        animation1.play();
    }

    private void clickMenuButton(VENTO.GUI gui) {
        VENTO.startGUI(gui);
    }

    public JFXButton build() {
        return button;
    }
}
