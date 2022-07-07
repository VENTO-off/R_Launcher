package relevant_craft.vento.r_launcher.manager.curseforge;

import com.jfoenix.controls.JFXSpinner;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CF_ImageCache {

    public static void loadImage(String url) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    File image = new File(VENTO.LAUNCHER_DIR + File.separator + "cf-cache" + File.separator + url.substring(url.lastIndexOf('/')));
                    DownloadManager.downloadFile(url, image.getAbsolutePath(), true);
                } catch (IOException e) {}
                return null;
            }
        };
        new Thread(task).start();
    }

    public static Image getImage(String url) {
        if (url.equals("none")) {
            return PictureManager.loadImage("noimage.png");
        }
        File image = new File(VENTO.LAUNCHER_DIR + File.separator + "cf-cache" + File.separator + url.substring(url.lastIndexOf('/')));
        if (image.exists()) {
            return new Image(image.toURI().toString());
        }

        return PictureManager.loadImage("noimage.png");
    }

    public static boolean hasImage(String url) {
        return new File(VENTO.LAUNCHER_DIR + File.separator + "cf-cache" + File.separator + url.substring(url.lastIndexOf('/'))).exists();
    }

    public static void clearCache() {
        //FileUtils.removeDirectory(new File(VENTO.LAUNCHER_DIR + File.separator + "cf-cache"));

        File cache = new File(VENTO.LAUNCHER_DIR + File.separator + "cf-cache");
        if (!cache.exists()) {
            return;
        }

        if (cache.listFiles() != null) {
            for (File image : cache.listFiles()) {
                if (Math.abs(System.currentTimeMillis() - FileUtils.getCreationTime(image.getAbsolutePath())) >= 1000L * 60 * 60 * 24 * 30) {   //30 days
                    image.delete();
                }
            }
        }
    }

    public static void renderAvatar(CF_Project cf_project, Pane image, JFXSpinner loader) {
        if (cf_project.getAvatar().equals("none")) {
            Image img = PictureManager.loadImage("noimage.png");
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitWidth(image.getPrefWidth());
            imageView.setFitHeight(image.getPrefHeight());
            image.getChildren().setAll(imageView);
            loader.setVisible(false);
        } else if (!CF_ImageCache.hasImage(cf_project.getAvatar())) {
            CF_ImageCache.loadImage(cf_project.getAvatar());

            loader.setVisible(true);
            Image img = new Image(cf_project.getAvatar(), true);
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitWidth(image.getPrefWidth());
            imageView.setFitHeight(image.getPrefHeight());
            image.getChildren().setAll(imageView);
            img.progressProperty().addListener((observable, oldValue, progress) -> {
                if ((Double) progress == 1.0 && !img.isError()) {
                    FadeTransition transition = new FadeTransition();
                    transition.setDuration(Duration.millis(300));
                    transition.setNode(imageView);
                    transition.setFromValue(0);
                    transition.setToValue(1);
                    transition.play();
                    loader.setVisible(false);
                }
            });
        } else {
            Image img = CF_ImageCache.getImage(cf_project.getAvatar());
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitWidth(image.getPrefWidth());
            imageView.setFitHeight(image.getPrefHeight());
            image.getChildren().setAll(imageView);
            loader.setVisible(false);
        }
    }
}
