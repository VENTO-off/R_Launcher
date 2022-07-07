package relevant_craft.vento.r_launcher;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.gui.accounts.Accounts;
import relevant_craft.vento.r_launcher.gui.analyzer.Analyzer;
import relevant_craft.vento.r_launcher.gui.cheats.Cheats;
import relevant_craft.vento.r_launcher.gui.main.Main;
import relevant_craft.vento.r_launcher.gui.modpacks.ManageModpacks;
import relevant_craft.vento.r_launcher.gui.modpacks.Modpacks;
import relevant_craft.vento.r_launcher.gui.mods.Mods;
import relevant_craft.vento.r_launcher.gui.notify.Notify;
import relevant_craft.vento.r_launcher.gui.settings.Settings;
import relevant_craft.vento.r_launcher.gui.textures.Textures;
import relevant_craft.vento.r_launcher.gui.versions.Versions;
import relevant_craft.vento.r_launcher.gui.worlds.Worlds;
import relevant_craft.vento.r_launcher.manager.account.AccountManager;
import relevant_craft.vento.r_launcher.manager.analyzer.AnalyzerManager;
import relevant_craft.vento.r_launcher.manager.cheat.CheatManager;
import relevant_craft.vento.r_launcher.manager.console.ConsoleManager;
import relevant_craft.vento.r_launcher.manager.download.SSLManager;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.mod.ModsManager;
import relevant_craft.vento.r_launcher.manager.modpack.ModpackManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.texture.TextureManager;
import relevant_craft.vento.r_launcher.manager.world.WorldManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;
import relevant_craft.vento.r_launcher.utils.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class VENTO extends Application {

    /******* DEVELOPER *******/
    public static final boolean DEVELOPER = false;
    /*************************/

    public static void main(String[] args) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("r_updater")) {
            R_UPDATER = true;
            UPDATER_PATH = args[1];
            if (args.length == 3) {
                ONLINE_MODE = !args[2].equals("offline");
            }
        }
        LoggerUtils.initLogger();
        launch(args);
    }

    public static Parent root = null;
    public static Stage window = null;
    public static Stage console = null;
    public static Stage viewer = null;

    public static AccountManager accountManager = null;
    public static LauncherManager launcherManager = null;
    public static ConsoleManager consoleManager = null;
    public static CheatManager cheatManager = null;
    public static TextureManager textureManager = null;
    public static ModsManager modsManager = null;
    public static WorldManager worldManager = null;
    public static ModpackManager modpackManager = null;
    public static AnalyzerManager analyzerManager = null;

    public final static String REPOSITORY = "https://files.r-launcher.su/";
    public final static String SITE = "https://www.r-launcher.su/";
    public final static String VK = "https://vk.com/rlauncher";
    public final static String WEB = SITE + "r-launcher/";
    public final static String VERSION = "1.4";

    public final static File LAUNCHER_DIR = new File(System.getProperty("user.home") + File.separator + ".r-launcher");

    public static boolean ONLINE_MODE = true;
    public static boolean R_UPDATER = false;
    public static String UPDATER_PATH;

    @Override
    public void start(Stage stage) throws Exception {
        LauncherManager.loadData();
        SettingsManager.loadSettings();
        SettingsManager.checkMinecraftDirectory();
        //SSLManager.ignoreCertificates();

        accountManager = new AccountManager();

        window = stage;
        root = FXMLLoader.load(getClass().getResource(GUI.Main.path));
        Main.show();

        stage.getIcons().setAll(PictureManager.loadImage("icon.png"));
    }

    public enum GUI {
        Main("gui/main/Main.fxml"),
        Settings("gui/settings/Settings.fxml"),
        Accounts("gui/accounts/Accounts.fxml"),
        Console("gui/console/Console.fxml"),
        Cheats("gui/cheats/Cheats.fxml"),
        Textures("gui/textures/Textures.fxml"),
        Mods("gui/mods/Mods.fxml"),
        ManageModpacks("gui/modpacks/ManageModpacks.fxml"),
        Modpacks("gui/modpacks/Modpacks.fxml"),
        Worlds("gui/worlds/Worlds.fxml"),
        Versions("gui/versions/Versions.fxml"),
        Notify("gui/notify/Notify.fxml"),
        Analyzer("gui/analyzer/Analyzer.fxml"),

        Screenshots(null),
        Folder(null),
        ;

        public String path;

        GUI(String _path) {
            path = _path;
        }
    }

    public static AnchorPane loadGUI(String path) {
        try {
            InputStream stream = VENTO.class.getResourceAsStream(path);
            return new FXMLLoader().load(stream);
            //return FXMLLoader.load(VENTO.class.getResource(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static void startGUI(GUI gui) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                ReadOnlyDoubleProperty width = VENTO.launcherManager.mainFrontArea.widthProperty();
                ReadOnlyDoubleProperty height = VENTO.launcherManager.mainFrontArea.heightProperty();

                AnchorPane gui_pane = null;
                if (gui == GUI.Settings) {
                    gui_pane = Settings.loadSettingsWindow(width, height);
                } else if (gui == GUI.Accounts) {
                    gui_pane = Accounts.loadAccountsWindow(width, height);
                } else if (gui == GUI.Cheats) {
                    gui_pane = Cheats.loadCheatsWindow(width, height);
                } else if (gui == GUI.Textures) {
                    gui_pane = Textures.loadTexturesWindow(width, height);
                } else if (gui == GUI.Mods) {
                    gui_pane = Mods.loadModsWindow(width, height);
                } else if (gui == GUI.ManageModpacks) {
                    gui_pane = ManageModpacks.loadManageModpacksWindow(width, height);
                } else if (gui == GUI.Modpacks) {
                    gui_pane = Modpacks.loadModpacksWindow(width, height);
                } else if (gui == GUI.Worlds) {
                    gui_pane = Worlds.loadWorldsWindow(width, height);
                } else if (gui == GUI.Versions) {
                    gui_pane = Versions.loadVersionsWindow(width, height);
                } else if (gui == GUI.Screenshots) {
                    DesktopUtils.openFolder(SettingsManager.minecraftDirectory + File.separator + "screenshots", true);
                } else if (gui == GUI.Folder) {
                    DesktopUtils.openFolder(SettingsManager.minecraftDirectory, false);
                } else if (gui == GUI.Notify) {
                    gui_pane = Notify.loadNotifyWindow(width, height);
                } else if (gui == GUI.Analyzer) {
                    gui_pane = Analyzer.loadAnalyzerWindow(width, height);
                }

                final AnchorPane final_gui_pane = gui_pane;
                Platform.runLater(() -> {
                    if (final_gui_pane != null) {
                        VENTO.launcherManager.darkLayer.setVisible(true);
                        VENTO.launcherManager.mainFrontArea.getChildren().setAll(final_gui_pane);

                        VENTO.launcherManager.darkLayer.setOpacity(0);

                        Animation animationGuiPane = new Timeline(new KeyFrame(Duration.millis(5), new KeyValue(VENTO.launcherManager.mainFrontArea.visibleProperty(), true)));
                        animationGuiPane.play();
                        animationGuiPane.setOnFinished(e -> {
                            Animation animationDarkLayer = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(VENTO.launcherManager.darkLayer.opacityProperty(), 1)));
                            animationDarkLayer.setDelay(Duration.millis(50));
                            animationDarkLayer.play();
                        });
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    public static void closeGUI() {
        if (VENTO.launcherManager.mainFrontArea.getChildren().get(0) instanceof AnchorPane) {
            AnchorPane gui_pane = (AnchorPane) VENTO.launcherManager.mainFrontArea.getChildren().get(0);

            Animation animationGuiPane = new Timeline(new KeyFrame(Duration.millis(75), new KeyValue(gui_pane.opacityProperty(), 0)));
            animationGuiPane.play();

            Animation animationDarkLayer = new Timeline(new KeyFrame(Duration.millis(75), new KeyValue(VENTO.launcherManager.darkLayer.opacityProperty(), 0)));
            animationDarkLayer.play();

            animationDarkLayer.setOnFinished(e -> {
                VENTO.launcherManager.darkLayer.setVisible(false);
                VENTO.launcherManager.mainFrontArea.setVisible(false);
                VENTO.launcherManager.mainFrontArea.getChildren().clear();
            });
        } else {
            VENTO.launcherManager.darkLayer.setVisible(false);
            VENTO.launcherManager.mainFrontArea.setVisible(false);
            VENTO.launcherManager.mainFrontArea.getChildren().clear();
        }
    }
}
