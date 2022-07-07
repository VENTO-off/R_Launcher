package relevant_craft.vento.r_launcher.manager.updater;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdaterManager {

    private static boolean LAUNCHER_CHECK_UPDATE;
    private static String LAUNCHER_VERSION;
    public static boolean SHOW_CHEATS;
    private static String __FREE__;
    private static boolean SHOW_NEWYEAR;

    public static void checkUpdate() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                if (!VENTO.ONLINE_MODE) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка подключения");
                        notify.setMessage("Отсутствует подключение к интернету. Пожалуйста, проверьте подключение к сети.\nБольшая часть функций лаунчера будет недоступна.\n\nПереход в `АВТОНОМНЫЙ РЕЖИМ`.");
                        notify.showNotify();
                    });
                    return null;
                }

                String response;
                try {
                    URL url = new URL(VENTO.WEB + "updater.php?r_launcher=" + LauncherManager.launcher_id.toString());
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
                    response = bufferedreader.readLine();
                    bufferedreader.close();
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка подключения");
                        notify.setMessage("При попытке проверить версию лаунчера произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
                        notify.showNotify();
                    });
                    return null;
                }

                String[] data = response.split("<::>");
                LAUNCHER_CHECK_UPDATE = Boolean.parseBoolean(data[0]);
                LAUNCHER_VERSION = data[1];
                SHOW_CHEATS = Boolean.parseBoolean(data[2]);
                __FREE__ = data[3];
                SHOW_NEWYEAR = Boolean.parseBoolean(data[4]);
                if (SettingsManager.showNewYear != SHOW_NEWYEAR) {
                    SettingsManager.showNewYear = SHOW_NEWYEAR;
                    SettingsManager.saveSettings();
                }

                if (LAUNCHER_CHECK_UPDATE) {
                    if (!VENTO.VERSION.equalsIgnoreCase(LAUNCHER_VERSION) && !VENTO.DEVELOPER) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                            notify.setTitle("Доступно обновление");
                            notify.setMessage("Пожалуйста, скачайте новую версию лаунчера с сайта.");
                            notify.showNotify();
                            DesktopUtils.openUrl(VENTO.SITE);
                            Platform.exit();
                        });
                    }
                }

                return null;
            }

        };
        new Thread(task).start();
    }

    public static void checkRUpdater() {
        if (VENTO.DEVELOPER) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("R-Launcher");
            alert.setHeaderText("РЕЖИМ РАЗРАБОТЧИКА");
            alert.setContentText("Лаунчер запущен в режиме разработчика.");

            //alert.showAndWait();
            return;
        }

        if (!VENTO.R_UPDATER && !VENTO.DEVELOPER) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("R-Launcher");
            alert.setHeaderText("Ошибка запуска");
            alert.setContentText("Пожалуйста, используйте официальный лаунчер для запуска.");

            alert.showAndWait();
            System.exit(0);
        }
    }
}
