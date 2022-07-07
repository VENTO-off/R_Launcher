package relevant_craft.vento.r_launcher.manager.background;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class BackgroundManager {

    public static void initBackground() {
        if (!VENTO.ONLINE_MODE) {
            setDefaultBackground();
            return;
        }

        if (SettingsManager.useAutoBackground) {
            setAutoBackground();
            File autoBackground = new File(System.getProperty("java.io.tmpdir") + File.separator + "background.png");
            autoBackground.delete();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Random random = new Random();
                    int number = random.nextInt(getBackgroundNumber());
                    if (number == -1) {
                        return null;
                    }
                    try {
                        DownloadManager.downloadFile(VENTO.REPOSITORY + "backgrounds/" + number + ".png", autoBackground.getAbsolutePath(), true);
                    } catch (IOException e) {}
                    return null;
                }
            };
            new Thread(task).start();
        } else if (SettingsManager.useCustomBackground) {
            setCustomBackground();
        } else {
            setDefaultBackground();
        }
    }


    public static void setDefaultBackground() {
        Image image = PictureManager.loadImage("background.png");
        VENTO.launcherManager.background.getChildren().setAll(new ImageView(image));
    }

    public static void setCustomBackground() {
        File customBackground = new File(SettingsManager.customBackground);
        if (customBackground.exists()) {
            Image image = new Image(customBackground.toURI().toString());
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setFitWidth(VENTO.launcherManager.background.getPrefWidth());
            imageView.setFitHeight(VENTO.launcherManager.background.getPrefHeight());
            VENTO.launcherManager.background.getChildren().setAll(imageView);
        } else {
            setDefaultBackground();
        }
    }

    public static void setAutoBackground() {
        File autoBackground = new File(System.getProperty("java.io.tmpdir") + File.separator + "background.png");
        if (autoBackground.exists()) {
            Image image = new Image(autoBackground.toURI().toString());
            VENTO.launcherManager.background.getChildren().setAll(new ImageView(image));
        } else {
            setDefaultBackground();
        }
    }

    private static int getBackgroundNumber() {
        String response = "";
        try {
            URL url = new URL(VENTO.WEB + "background.php");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            return -1;
        }

        return Integer.valueOf(response);
    }

}
