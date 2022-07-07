package relevant_craft.vento.r_launcher.manager.advertisement;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.server.Server;
import relevant_craft.vento.r_launcher.manager.server.ServerManager;
import relevant_craft.vento.r_launcher.minecraft.RunMinecraft;
import relevant_craft.vento.r_launcher.utils.ColorUtils;
import relevant_craft.vento.r_launcher.utils.MinecraftColor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AdvertisementManager {

    private static List<AdvertisementServer> menu_servers = new ArrayList<>();
    private static HashMap<Server, Boolean> force_servers = new HashMap<>();

    private static List<Pane> rendered_servers = new ArrayList<>();

    public static void loadAdvertisements() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getServers();

                Platform.runLater(() -> {
                    createAdvertisements();
                    showAdvertisements();
                });

                return null;
            }

        };
        new Thread(task).start();
    }

    private static void getServers() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + "servers.php");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка подключения");
            notify.setMessage("При попытке получить список серверов произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
            notify.showNotify();
            return;
        }

        String[] servers_list = response.split("<::>");
        for (String srv : servers_list) {
            String[] data = srv.split("#");
            String[] server = data[1].split(";");

            if (data[0].equalsIgnoreCase("SERVER")) {
                AdvertisementServer serv = new AdvertisementServer();
                serv.setName(server[0]);
                serv.setDescription(server[1].replace("\\n", System.lineSeparator()));
                serv.setAddress(server[2]);
                serv.setAvatar(server[3]);
                menu_servers.add(serv);
            } else if (data[0].equalsIgnoreCase("FORCE")) {
                Server serv = new Server();
                serv.setName(server[0]);
                serv.setAddress(server[1]);
                serv.setAcceptTextures(1);
                serv.setHideAddress(false);
                force_servers.put(serv, Boolean.valueOf(server[2]));
            }
        }
    }

    private static void createAdvertisements() {
        final int SPACE = 70;
        int START = ((VENTO.launcherManager.mainArea.heightProperty().getValue().intValue() - (SPACE * (Math.min(menu_servers.size(), 5)))) / 2) + 10;
        if (menu_servers.isEmpty()) {
            return;
        }

        List<AdvertisementServer> servers = getRandomServers();
        for (AdvertisementServer serv : servers) {
            Pane element = new Pane();
            element.setPrefWidth(450);
            element.setPrefHeight(60);
            element.setLayoutX(0);
            element.setLayoutY(START);
            //element.setStyle("-fx-background-color: linear-gradient(to right, rgba(0,0,0,0) 0%, rgba(0,0,0,0.45) 15%, rgba(0,0,0,0.75) 30%, rgba(0,0,0,0.77) 43%, rgba(0,0,0,0.8) 100%);");
            element.setStyle("-fx-background-color: " + createGradient(0, 0, 0) + ";");
            element.setCursor(Cursor.HAND);

            Label title = new Label();
            title.setFont(Font.font("System", FontWeight.BOLD, 16));
            if (serv.getName().startsWith("§u")) {
                title.setText(ColorUtils.removeColorCodes(serv.getName()));
                ColorUtils.addRainbowAnimation(title);
                ColorUtils.initRainbowColors();
            } else if (serv.getName().contains("§")) {
                title.setGraphic(ColorUtils.applyColorCodes(serv.getName(), title.getFont()));
            } else {
                title.setText(serv.getName());
            }
            title.setTextFill(Paint.valueOf("#ffffff"));
            title.setLayoutX(80);
            title.setLayoutY(5);
            title.setPrefWidth(400);
            element.getChildren().add(title);

            Label description = new Label();
            description.setFont(Font.font(12));
            if (serv.getDescription().contains("§")) {
                description.setGraphic(ColorUtils.applyColorCodes(serv.getDescription(), description.getFont()));
            } else {
                description.setText(serv.getDescription());
            }
            description.setWrapText(true);
            description.setStyle("-fx-line-spacing: -2;");
            description.setTextFill(Paint.valueOf("#ffffff"));
            description.setLayoutX(80);
            description.setLayoutY(25);
            description.setPrefWidth(350);
            description.setPrefHeight(35);
            element.getChildren().add(description);

            ImageView avatar = new ImageView();
            avatar.setFitWidth(50);
            avatar.setFitHeight(50);
            avatar.setLayoutX(15);
            avatar.setLayoutY(5);
            avatar.setPreserveRatio(true);
            GaussianBlur blur = new GaussianBlur(0);
            avatar.setEffect(blur);
            element.getChildren().add(avatar);

            loadAvatar(avatar, serv.getAvatar());

            JFXButton play = new JFXButton();
            play.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            play.setFont(Font.font("System", FontWeight.BOLD, 12));
            play.setStyle("-fx-background-color: transparent;");
            play.setDisable(true);
            play.setPrefWidth(60);
            play.setPrefHeight(60);
            play.setLayoutX(10);
            play.setLayoutY(0);
            play.setOpacity(0);

            FontAwesomeIconView play_icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY_CIRCLE);
            play_icon.setGlyphSize(52);
            play_icon.setFill(Paint.valueOf(getTitleColor(serv.getName())));
            play.setGraphic(play_icon);

            element.getChildren().add(play);

            RotateTransition animation1 = new RotateTransition(Duration.millis(200), avatar);
            animation1.setByAngle(360);
            animation1.setInterpolator(Interpolator.LINEAR);

            Animation animation2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(blur.radiusProperty(), 10)));

            Animation animation3 = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(avatar.opacityProperty(), 0)));
            animation3.setDelay(Duration.millis(75));

            Animation animation4 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(play.opacityProperty(), 1)));
            animation4.setDelay(Duration.millis(200));

            RotateTransition animation5 = new RotateTransition(Duration.millis(100), play);
            animation5.setFromAngle(-90);
            animation5.setToAngle(25);
            animation5.setDelay(Duration.millis(200));
            animation5.setInterpolator(Interpolator.LINEAR);

            RotateTransition animation6 = new RotateTransition(Duration.millis(50), play);
            animation6.setToAngle(0);
            animation6.setDelay(Duration.millis(310));
            animation6.setInterpolator(Interpolator.LINEAR);

            Animation animation7 = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(avatar.opacityProperty(), 1)));

            Animation animation8 = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(play.opacityProperty(), 0)));

            element.setOnMouseEntered(e -> {
                element.setStyle("-fx-background-color: " + createGradient(27, 94, 32) + ";");

                Rectangle clip = new Rectangle(avatar.getFitWidth(), avatar.getFitHeight());
                clip.setArcWidth(50);
                clip.setArcHeight(50);
                avatar.setClip(clip);

                animation1.play();

                animation2.play();

                animation3.play();

                animation4.play();

                animation5.play();

                animation6.play();
            });

            element.setOnMouseExited(e -> {
                if (animation6.getStatus() == Animation.Status.RUNNING) {
                    animation6.stop();
                }
                if (animation5.getStatus() == Animation.Status.RUNNING) {
                    animation5.stop();
                }
                if (animation4.getStatus() == Animation.Status.RUNNING) {
                    animation4.stop();
                }
                if (animation3.getStatus() == Animation.Status.RUNNING) {
                    animation3.stop();
                }
                if (animation2.getStatus() == Animation.Status.RUNNING) {
                    animation2.stop();
                }
                if (animation1.getStatus() == Animation.Status.RUNNING) {
                    animation1.stop();
                }

                element.setStyle("-fx-background-color: " + createGradient(0, 0, 0) + ";");

                avatar.setClip(null);

                avatar.setRotate(0);

                blur.setRadius(0);

                animation7.play();

                animation8.play();
            });

            element.setOnMouseClicked(event -> {
                ServerManager.addServerToList(convertToServer(serv));
                RunMinecraft.runMinecraft(serv.getAddress());
            });

            rendered_servers.add(element);

            START += SPACE;
        }
    }

    private static String createGradient(int r, int g, int b) {
        return "linear-gradient(to right, rgba(" + r + "," + g +"," + b + ",0.8) 0%, rgba(" + r + "," + g +"," + b + ",0.77) 57%, rgba(" + r + "," + g +"," + b + ",0.75) 70%, rgba(" + r + "," + g +"," + b + ",0.45) 85%, rgba(" + r + "," + g +"," + b + ",0) 100%)";
    }

    private static String getTitleColor(String title) {
        if (title.startsWith("§")) {
            return MinecraftColor.convertToHex(title.charAt(1));
        } else {
            return "#ffffff";
        }
    }

    private static void loadAvatar(final ImageView avatar, String md5) {
        File cache = new File(VENTO.LAUNCHER_DIR + File.separator + "avatar-cache");
        if (!cache.exists()) { cache.mkdir(); }

        if (md5 == null || md5.isEmpty()) {
            return;
        }

        if (md5.equals("none")) {
            avatar.setImage(loadDefaultImage());
            return;
        }

        File image = new File(cache + File.separator + md5 + ".png");
        if (image.exists()) {
            avatar.setImage(loadImage(image));
        } else {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        DownloadManager.downloadFile(VENTO.REPOSITORY + "servers" + '/' + md5 + ".png", image.getAbsolutePath(), true);
                        Platform.runLater(() -> {
                            avatar.setOpacity(0);
                            avatar.setImage(loadImage(image));
                            Animation animation = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(avatar.opacityProperty(), 1)));
                            animation.play();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> avatar.setImage(loadDefaultImage()));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    private static Image loadImage(File image) {
        return new Image(image.toURI().toString());
    }

    private static Image loadDefaultImage() {
        return PictureManager.loadImage("server.png");
    }

    public static void showAdvertisements() {
        if (rendered_servers.isEmpty()) {
            return;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
                Thread.sleep(500);
                for (Pane element : rendered_servers) {
                    Platform.runLater(() -> {
                        VENTO.launcherManager.mainArea.getChildren().add(element);
                        FadeTransition transition = new FadeTransition();
                        transition.setDuration(Duration.millis(200));
                        transition.setNode(element);
                        transition.setFromValue(0);
                        transition.setToValue(1);
                        transition.play();
                    });
                    Thread.sleep(50);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private static List<AdvertisementServer> getRandomServers() {
        List<AdvertisementServer> list = new ArrayList<>();
        int max_servers = (Math.min(menu_servers.size(), 5));

        for (int i = 0; i < max_servers; i++) {
            AdvertisementServer server;
            do {
                int number = new Random().nextInt(menu_servers.size());
                server = menu_servers.get(number);
            } while (list.contains(server));

            list.add(server);
        }

        return list;
    }

    public static void addForceServers() {
        for (Server server : force_servers.keySet()) {
            if (!ServerManager.hasCache(server) || force_servers.get(server)) {
                ServerManager.addServerToList(server);
                return;
            } else if (ServerManager.hasCache(server) && ServerManager.isCacheExpired(server)) {
                ServerManager.addServerToList(server);
                return;
            }
        }
    }

    private static Server convertToServer(AdvertisementServer adServer) {
        Server server = new Server();
        server.setName(adServer.getName());
        server.setAddress(adServer.getAddress());
        server.setAcceptTextures(1);
        server.setHideAddress(false);

        return server;
    }
}
