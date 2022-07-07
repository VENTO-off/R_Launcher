package relevant_craft.vento.r_launcher.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorUtils {

    public static TextFlow applyColorCodes(String text, Font font) {
        TextFlow textFlow = new TextFlow();
        String[] parts = text.split("§");
        for (String part : parts) {
            if (part != null && !part.isEmpty() && part.length() > 1) {
                Text tempText = new Text(part.substring(1));
                tempText.setFont(font);
                tempText.setFill(Paint.valueOf(MinecraftColor.convertToHex(part.charAt(0))));
                textFlow.getChildren().add(tempText);
            }
        }

        return textFlow;
    }

    public static String removeColorCodes(String line) {
        StringBuilder new_line = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '§') {
                i++;
                continue;
            }

            new_line.append(line.charAt(i));
        }

        return new_line.toString();
    }

    public static int[] convertToRgb(String hex) {
        try {
            return new int[]{
                    Integer.valueOf(hex.substring(1, 3), 16),
                    Integer.valueOf(hex.substring(3, 5), 16),
                    Integer.valueOf(hex.substring(5, 7), 16)
            };
        } catch (Exception e) {
            return new int[] {255, 255, 255 };
        }
    }

    private static Thread rainbow = null;
    private static List<Label> rainbowLabels = new ArrayList<>();

    public static void addRainbowAnimation(Label label) {
        rainbowLabels.add(label);
    }

    public static void initRainbowColors() {
        if (rainbow != null && rainbow.isAlive()) {
            return;
        }

        List<Color> colors = new ArrayList<>();
        for (int r = 0; r < 100; r++) colors.add(new Color(r * 255 / 100, 255, 0));
        for (int g = 100; g > 0; g--) colors.add(new Color(255, g * 255 / 100, 0));
        for (int b = 0; b < 100; b++) colors.add(new Color(255, 0, b * 255 / 100));
        for (int r = 100; r > 0; r--) colors.add(new Color(r * 255 / 100, 0, 255));
        for (int g = 0; g < 100; g++) colors.add(new Color(0, g * 255 / 100, 255));
        for (int b = 100; b > 0; b--) colors.add(new Color(0, 255, b * 255 / 100));
        colors.add(new Color(0, 255, 0));

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                while (true) {
                    if (rainbowLabels.isEmpty()) {
                        rainbow.stop();
                        return null;
                    }
                    for (Color awtColor : colors) {
                        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha() / 255);
                        Platform.runLater(() -> {
                            for (Label label : rainbowLabels) {
                                label.setTextFill(fxColor);
                            }
                        });
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        };
        rainbow = new Thread(task);
        rainbow.start();
    }

}
