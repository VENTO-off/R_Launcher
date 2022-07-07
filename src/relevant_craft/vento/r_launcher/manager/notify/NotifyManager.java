package relevant_craft.vento.r_launcher.manager.notify;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.*;
import java.net.URL;
import java.util.*;

public class NotifyManager {

    private static final File notify_cache = new File(VENTO.LAUNCHER_DIR + File.separator + "update-cache");

    private static List<Notify> notifyList = new ArrayList<>();

    public static List<Pane> rendered_notifies = new ArrayList<>();

    public static Notify currentNotify = null;

    public static void loadNotifies() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getNotifies();

                Platform.runLater(() -> {
                    readNotifies();
                    renderNotifiesAmount();
                    createNotifies();
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void getNotifies() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + "notify.php?lastRun=" + LauncherManager.lastRun);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка подключения");
            notify.setMessage("При попытке получить список уведомлений произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
            notify.showNotify();
            return;
        }

        if (response == null) {
            return;
        }

        if (response.isEmpty()) {
            return;
        }

        if (response.equals("none")) {
            return;
        }

        if (!notify_cache.exists()) {
            notify_cache.mkdir();
        }

        String[] notify_list = response.split("<::>");
        for (String ntf : notify_list) {
            try {
                String[] notify = ntf.split(";");
                String id = notify[0];
                long date = Long.parseLong(notify[1]);
                String title = notify[2];
                String short_text = notify[3];
                String full_text = notify[4];
                String url = notify[5];

                if (id.contains("cheat") && !SettingsManager.enableCheats) {
                    continue;
                }

                File notify_file = new File(notify_cache + File.separator + id);
                if (notify_file.exists()) {
                    continue;
                }

                FileWriter writer = new FileWriter(notify_file);
                new Gson().toJson(new Notify(id, date, title, short_text, full_text, url), writer);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void readNotifies() {
        if (!notify_cache.exists()) {
            return;
        }

        Gson gson = new Gson();

        for (File ntf : Objects.requireNonNull(notify_cache.listFiles())) {
            if (ntf.length() > 0) {
                try {
                    FileReader fileReader = new FileReader(ntf.getAbsolutePath());
                    notifyList.add(gson.fromJson(fileReader, Notify.class));
                    fileReader.close();
                } catch (Exception ignored) {}
            }
        }

        notifyList.sort((n1, n2) -> Long.compare(n2.getDate(), n1.getDate()));
    }

    public static int getNotifiesAmount() {
        return notifyList.size();
    }

    private static Label amount = null;

    private static void renderNotifiesAmount() {
        if (amount != null) {
            VENTO.launcherManager.toolBar.getChildren().remove(amount);
        }

        if (getNotifiesAmount() > 0) {
            amount = new Label();
            amount.setText(String.valueOf(getNotifiesAmount()));
            amount.setPrefWidth(12);
            amount.setPrefHeight(12);
            amount.setFont(Font.font("System", FontWeight.BOLD, 8));
            amount.setTextFill(Color.WHITE);
            amount.setAlignment(Pos.CENTER);
            amount.setStyle("-fx-background-color: #f44336; -fx-background-radius: 3;");
            amount.setLayoutX(VENTO.launcherManager.notifyButton.getLayoutX() - 5);
            amount.setLayoutY(VENTO.launcherManager.notifyButton.getLayoutY() / 2 + 5);
            VENTO.launcherManager.toolBar.getChildren().add(amount);
        }

        animateNotifyButton();
    }

    private static Thread bellAnimator = null;

    public static void animateNotifyButton() {
        if (bellAnimator == null  && getNotifiesAmount() > 0) {
            bellAnimator = new Thread(() -> {
               while (true) {
                   animateBell(30);

                   try {
                       Thread.sleep(2000);
                   } catch (InterruptedException e) {}
               }
            });
            bellAnimator.start();
        } else if (bellAnimator != null && getNotifiesAmount() > 0) {
            bellAnimator.stop();
        }
    }

    public static void animateBell(int angle) {
        animateBell(angle, false, null, null);
    }

    public static void animateBell(int angle, boolean doInvert, Rotate rotate, Duration duration) {
        if (rotate == null) {
            rotate = new Rotate();
            rotate.setPivotX(VENTO.launcherManager.notifyButton.getGlyphSize().intValue() / 2);
            rotate.setPivotY(-17);
            VENTO.launcherManager.notifyButton.getTransforms().add(rotate);
        }

        if (duration == null) {
            duration = Duration.millis(50);
        }

        duration = duration.subtract(Duration.millis(1));

        Timeline animation = new Timeline(
                new KeyFrame(duration, new KeyValue(rotate.angleProperty(), doInvert ? angle * -1 : angle))
        );
        animation.play();

        if (angle > 0) {
            Rotate finalRotate = rotate;
            Duration finalDuration = duration;
            animation.setOnFinished(e -> {
                animateBell(angle - 2, !doInvert, finalRotate, finalDuration);
            });
        }
    }

    private static void createNotifies() {
        if (!rendered_notifies.isEmpty()) {
            return;
        }

        for (Notify notify : notifyList) {
            Pane layout = new Pane();
            layout.setPrefWidth(VENTO.launcherManager.notifyList.widthProperty().getValue());
            layout.setPrefHeight(130);
            layout.setStyle("-fx-background-color: white;");

            Pane element = new Pane();
            element.setPrefWidth(240);
            element.setPrefHeight(110);
            element.setLayoutX(layout.getPrefWidth() / 2 - element.getPrefWidth() / 2);
            element.setLayoutY(layout.getPrefHeight() / 2 - element.getPrefHeight() / 2);
            element.setStyle("-fx-background-color: white;");
            DropShadow shadow = new DropShadow();
            shadow.setBlurType(BlurType.GAUSSIAN);
            shadow.setWidth(10);
            shadow.setHeight(10);
            shadow.setRadius(5);
            shadow.setColor(Color.color(0, 0, 0, 0.7));
            element.setEffect(shadow);
            layout.getChildren().add(element);

            Label title = new Label();
            title.setText(notify.getTitle());
            title.setFont(Font.font("System", FontWeight.BOLD, 14));
            title.setTextFill(Color.BLACK);
            title.setPrefWidth(220);
            title.setLayoutX(10);
            title.setLayoutY(8);
            element.getChildren().add(title);

            Label short_text = new Label();
            short_text.setText(notify.getShort_text());
            short_text.setFont(Font.font(13));
            short_text.setTextFill(Color.BLACK);
            short_text.setWrapText(true);
            short_text.setAlignment(Pos.TOP_LEFT);
            short_text.setPrefWidth(220);
            short_text.setPrefHeight(40);
            short_text.setLayoutX(10);
            short_text.setLayoutY(30);
            element.getChildren().add(short_text);

            JFXButton readButton = new JFXButton();
            readButton.setRipplerFill(Color.valueOf("#d0d0d0"));
            readButton.setText("Подробнее");
            readButton.setFont(Font.font("System", FontWeight.BOLD, 13));
            readButton.setTextFill(Paint.valueOf("#66bb6a"));
            readButton.setStyle("-fx-background-color: #f5f5f5;");
            readButton.setPrefWidth(100);
            readButton.setPrefHeight(29);
            readButton.setLayoutX(10);
            readButton.setLayoutY(73);
            readButton.setCursor(Cursor.HAND);
            readButton.setOnAction(e -> {
                rendered_notifies.remove(layout);
                currentNotify = notify;
                VENTO.startGUI(VENTO.GUI.Notify);

                if (VENTO.launcherManager.notifyList.isVisible()) {
                    VENTO.launcherManager.notifyList.setVisible(false);
                    VENTO.launcherManager.notifyList.getChildren().clear();
                }
            });
            element.getChildren().add(readButton);

            JFXButton closeButton = new JFXButton();
            closeButton.setRipplerFill(Color.valueOf("#d0d0d0"));
            closeButton.setText("Закрыть");
            closeButton.setFont(Font.font("System", FontWeight.BOLD, 13));
            closeButton.setTextFill(Paint.valueOf("#f44336"));
            closeButton.setStyle("-fx-background-color: #f5f5f5;");
            closeButton.setPrefWidth(100);
            closeButton.setPrefHeight(29);
            closeButton.setLayoutX(130);
            closeButton.setLayoutY(73);
            closeButton.setCursor(Cursor.HAND);
            closeButton.setOnAction(e -> {
                Animation animation1 = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(element.opacityProperty(), 0)));
                animation1.play();
                Animation animation2 = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(layout.prefHeightProperty(), 0)));
                animation2.setDelay(Duration.millis(300));
                animation2.play();
                animation2.setOnFinished(e2 -> {
                    rendered_notifies.remove(layout);
                    deleteNotify(notify);

                    if (rendered_notifies.isEmpty()) {
                        VENTO.launcherManager.notifyList.getChildren().setAll(createEmpty());
                    }
                });
            });
            element.getChildren().add(closeButton);

            rendered_notifies.add(layout);
        }
    }

    public static JFXButton createEmpty() {
        JFXButton empty = new JFXButton();
        empty.setButtonType(JFXButton.ButtonType.FLAT);
        empty.setMnemonicParsing(false);
        empty.setText("Нет новых уведомлений.");
        empty.setFont(Font.font(15));
        empty.setPrefWidth(VENTO.launcherManager.notifyList.widthProperty().getValue());
        empty.setPrefHeight(VENTO.launcherManager.notifyList.widthProperty().getValue() / 2);
        empty.setAlignment(Pos.CENTER);
        empty.setContentDisplay(ContentDisplay.BOTTOM);
        empty.setTextAlignment(TextAlignment.CENTER);
        empty.setDisable(true);
        empty.setWrapText(true);
        MaterialDesignIconView empty_icon = new MaterialDesignIconView(MaterialDesignIcon.BELL_OFF);
        empty_icon.setGlyphSize(30);
        empty.setGraphic(empty_icon);

        return empty;
    }

    public static void deleteNotify(Notify notify) {
        File notify_file = new File(notify_cache + File.separator + notify.getId());
        if (notify_file.exists()) {
            try {
                notify_file.delete();
                notify_file.createNewFile();
            } catch (Exception ignored) {}
        }

        notifyList.remove(notify);
        renderNotifiesAmount();
    }
}
