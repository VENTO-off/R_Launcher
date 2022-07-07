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
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;

public class SideMenuButton {
    private final int BUTTON_SIZE = 51;
    private final int LAYOUT_WIDTH = 35;
    private final int LAYOUT_HEIGHT = 43;
    private final int BUTTON_X = 935;

    private JFXButton button = new JFXButton();
    private Pane layout = new Pane();
    private GlyphIcon icon = null;
    private Label text = new Label();

    private Animation animation1 = null;
    private Animation animation2 = null;
    private Animation animation3 = null;

    public SideMenuButton(String name, FontAwesomeIcon fa_icon, String color, String rippler, VENTO.GUI gui) {
        createMenuButton(name, fa_icon, null, color, rippler, gui);
    }

    public SideMenuButton(String name, EmojiOne eo_icon, String color, String rippler, VENTO.GUI gui) {
        createMenuButton(name, null, eo_icon, color, rippler, gui);
    }


    private void createMenuButton(String name, FontAwesomeIcon fa_icon, EmojiOne eo_icon, String color, String rippler, VENTO.GUI gui) {
        button.setPrefWidth(BUTTON_SIZE);
        button.setPrefHeight(BUTTON_SIZE);
        button.setStyle("-fx-background-color: " + color + ";");
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER);
        button.setRipplerFill(Paint.valueOf(rippler));
        button.setCursor(Cursor.HAND);
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setLayoutX(BUTTON_X);

        layout.setPrefWidth(LAYOUT_WIDTH);
        layout.setPrefHeight(LAYOUT_HEIGHT);

        if (fa_icon != null) {
            icon = new FontAwesomeIconView(fa_icon);
        } else if (eo_icon != null) {
            icon = new EmojiOneView(eo_icon);
        }
        if (icon != null) {
            icon.setGlyphSize(25);
            icon.setFill(Paint.valueOf("#ffffff"));
            icon.setLayoutX(layout.getPrefWidth() / 2 - icon.getGlyphSize().intValue() / 2 - 1);
            icon.setLayoutY(layout.getPrefHeight() / 2 - icon.getGlyphSize().intValue() / 2 + 20);
        }

        text.setText(name);
        text.setFont(Font.font("System", FontWeight.BOLD, 18));
        text.setTextFill(Paint.valueOf("#ffffff"));
        text.setLayoutX(icon.getGlyphSize().intValue() + 15);
        text.setLayoutY(LAYOUT_HEIGHT / 2 - text.getFont().getSize() / 2 - 4);
        text.setAlignment(Pos.CENTER);
        text.setOpacity(0);
        text.setVisible(false);

        layout.getChildren().setAll(icon, text);
        button.setGraphic(layout);

        button.setOnMouseEntered(e -> showMenuButton());

        button.setOnMouseExited(e -> hideMenuButton());

        button.setOnAction(e -> {
            LauncherManager.checkModalWindow();
            clickMenuButton(gui);
        });
    }

    private void showMenuButton() {
        if (button.getLayoutX() != BUTTON_X) {
            return;
        }

        final float NEW_WIDTH = LauncherManager.fontLoader.computeStringWidth(text.getText(), text.getFont()) + 10;

        animation1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.prefWidthProperty(), BUTTON_SIZE + NEW_WIDTH)));
        animation1.play();

        animation2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.layoutXProperty(), button.getLayoutX() - NEW_WIDTH)));
        animation2.play();

        animation3 = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(text.opacityProperty(), 1)));
        animation3.setDelay(Duration.millis(100));
        animation3.play();
        text.setVisible(true);
    }

    private void hideMenuButton() {
        if (animation3.getStatus() == Animation.Status.RUNNING) {
            animation3.stop();
        }
        if (animation2.getStatus() == Animation.Status.RUNNING) {
            animation2.stop();
        }
        if (animation1.getStatus() == Animation.Status.RUNNING) {
            animation1.stop();
        }

        animation3 = new Timeline(new KeyFrame(Duration.millis(50), new KeyValue(text.opacityProperty(), 0)));
        animation3.play();
        text.setVisible(false);

        animation2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.layoutXProperty(), BUTTON_X)));
        animation2.setDelay(Duration.millis(50));
        animation2.play();

        animation1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(button.prefWidthProperty(), BUTTON_SIZE)));
        animation1.setDelay(Duration.millis(50));
        animation1.play();
    }

    private void clickMenuButton(VENTO.GUI gui) {
        VENTO.startGUI(gui);
    }

    public JFXButton build() {
        return button;
    }
}
