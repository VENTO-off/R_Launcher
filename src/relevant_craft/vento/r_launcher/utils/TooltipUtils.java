package relevant_craft.vento.r_launcher.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;

public class TooltipUtils {

    public static void addTooltip(Node node, String text) {
        addTooltip(node, text, null);
    }

    public static void addTooltip(Node node, String text, String warning) {
        node.setCursor(Cursor.HAND);

        Tooltip tooltip = new Tooltip(text);
        tooltip.setFont(Font.font(12));
        tooltip.setTextAlignment(TextAlignment.LEFT);

        double maxLength = 0;
        for (String line : text.split("\n")) {
            double currentLength = LauncherManager.fontLoader.computeStringWidth(line, Font.font(12));
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
        }

        if (warning != null) {
            Text t = new Text(warning);
            t.setFont(Font.font("System", FontWeight.BOLD, 12.5));
            t.setFill(Paint.valueOf("#f75c4f"));
            t.setWrappingWidth(maxLength);

            TextFlow textFlow = new TextFlow();
            textFlow.getChildren().add(t);

            VBox graphics = new VBox();
            graphics.getChildren().add(textFlow);
            graphics.setPrefHeight(15);
            graphics.setPrefWidth(maxLength);

            tooltip.setGraphic(graphics);
            tooltip.setContentDisplay(ContentDisplay.BOTTOM);
            tooltip.setGraphicTextGap(10);
        }

        node.setOnMouseMoved(e -> {
            if (!tooltip.isShowing()) {
                tooltip.setOpacity(0);
                tooltip.show(node, e.getScreenX() + 10, e.getScreenY() + 10);
                Animation animation = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(tooltip.opacityProperty(), 1.0)));
                animation.setDelay(Duration.millis(200));
                animation.play();
            } else {
                tooltip.show(node, e.getScreenX() + 10, e.getScreenY() + 10);
            }
        });

        node.setOnMouseExited(e -> {
            tooltip.hide();
        });
    }
}
