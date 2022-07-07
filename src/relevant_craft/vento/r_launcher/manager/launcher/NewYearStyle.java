package relevant_craft.vento.r_launcher.manager.launcher;

import javafx.animation.Animation;
import javafx.animation.TranslateTransitionBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.updater.UpdaterManager;

import java.util.Random;

public class NewYearStyle {

    private static Random rand = new Random();

    public static void startSnow() {
        if (!SettingsManager.showNewYear) {
            return;
        }

        for (int i = 0; i < 1500; i++) {
            Circle snow = new Circle(1, 1, 1);
            snow.setRadius(rand.nextDouble() * 3);
            snow.setFill(Color.rgb(255, 255, 255, rand.nextDouble()));
            VENTO.launcherManager.background.getChildren().add(snow);
            animateSnow(snow);
        }
        VENTO.launcherManager.background.getChildren().add(new ImageView(PictureManager.loadImage("newyear_style.png")));
    }

    private static void animateSnow(Circle snow) {
        snow.setCenterX(rand.nextInt((int) VENTO.launcherManager.background.getPrefWidth()));
        int time = 10 + rand.nextInt(50);

        Animation fall = TranslateTransitionBuilder.create()
                .node(snow)
                .fromY(-100 + (rand.nextInt(200) * -1))
                .toY((int) VENTO.launcherManager.background.getPrefHeight() + 50)
                .toX(rand.nextDouble() * snow.getCenterX())
                .duration(Duration.seconds(time))
                .onFinished(event -> animateSnow(snow)).build();
        fall.play();
    }
}
