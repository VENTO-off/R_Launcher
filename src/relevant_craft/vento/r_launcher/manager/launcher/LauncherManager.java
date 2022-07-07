package relevant_craft.vento.r_launcher.manager.launcher;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.advertisement.AdvertisementManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.tutorial.TutorialManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.utils.AnimationUtils;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.prefs.Preferences;

public class LauncherManager {

    public AnchorPane mainArea;
    public Pane background;
    public JFXComboBox<Version> versionList;
    public Label currentAccount;
    public AnchorPane mainFrontArea;
    public Pane darkLayer;
    public Pane toolBar;
    public AnchorPane bottomToolbar;
    public HBox toolbarbox;
    public JFXButton MinecraftButton;
    public JFXButton MinecraftOptionsButton;
    public VBox accountList;
    public VBox optionsList;
    public VBox webList;
    public VBox notifyList;
    public MaterialDesignIconView notifyButton;

    public LauncherManager(AnchorPane _mainArea, Pane _background, JFXComboBox<Version> _versionList, Label _currentAccount, AnchorPane _mainFrontArea, Pane _darkLayer, Pane _toolBar, AnchorPane _bottomToolbar, HBox _toolbarbox, JFXButton _MinecraftButton, JFXButton _MinecraftOptionsButton, VBox _accountList, VBox _optionsList, VBox _webList, VBox _notifyList, MaterialDesignIconView _notifyButton) {
        mainArea = _mainArea;
        background = _background;
        versionList = _versionList;
        currentAccount = _currentAccount;
        mainFrontArea = _mainFrontArea;
        darkLayer = _darkLayer;
        toolBar = _toolBar;
        bottomToolbar = _bottomToolbar;
        toolbarbox = _toolbarbox;
        MinecraftButton = _MinecraftButton;
        MinecraftOptionsButton = _MinecraftOptionsButton;
        accountList = _accountList;
        optionsList = _optionsList;
        webList = _webList;
        notifyList = _notifyList;
        notifyButton = _notifyButton;
    }

    public static void unlockButtons(boolean all) {
        if (all) {
            VENTO.launcherManager.MinecraftButton.setDisable(false);
        } else {
            VENTO.launcherManager.MinecraftButton.setText("Играть");
        }
        VENTO.launcherManager.MinecraftOptionsButton.setDisable(false);
        VENTO.launcherManager.toolbarbox.getChildren().forEach(button -> button.setDisable(false));
        VENTO.launcherManager.versionList.setDisable(false);
        VENTO.launcherManager.currentAccount.setDisable(false);
        VENTO.launcherManager.notifyButton.setDisable(false);
        sideMenuButtons.forEach(btn -> btn.setDisable(false));
    }

    public static void lockButtons(boolean all) {
        VENTO.launcherManager.MinecraftButton.focusedProperty().addListener((observable, oldValue, newValue) -> {
            VENTO.launcherManager.mainArea.requestFocus();
        });
        if (all) {
            VENTO.launcherManager.MinecraftButton.setDisable(true);
        } else {
            VENTO.launcherManager.MinecraftButton.setText("Отмена");
        }
        VENTO.launcherManager.MinecraftOptionsButton.setDisable(true);
        VENTO.launcherManager.toolbarbox.getChildren().forEach(button -> button.setDisable(true));
        VENTO.launcherManager.versionList.setDisable(true);
        VENTO.launcherManager.currentAccount.setDisable(true);
        VENTO.launcherManager.notifyButton.setDisable(true);
        sideMenuButtons.forEach(btn -> btn.setDisable(true));
    }

    private static Preferences data = Preferences.userRoot().node("rlauncher");

    public static UUID launcher_id;
    public static String lastVersion;
    public static long lastRun;

    public static void loadData() {
        String id = data.get("launcherID", null);
        if (id == null) {
            launcher_id = UUID.randomUUID();
            data.put("launcherID", launcher_id.toString());
            Platform.runLater(TutorialManager::showTutorial);
        } else {
            launcher_id = UUID.fromString(id);
        }

        lastVersion = data.get("lastVersion", null);

        lastRun = data.getLong("lastRun", 0);
        data.put("lastRun", String.valueOf(System.currentTimeMillis()));
    }

    public static void saveData() {
        data.put("lastVersion", lastVersion);
    }

    public static FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

    public static void showMainMenu() {
        VENTO.launcherManager.mainArea.getChildren().clear();

        AdvertisementManager.showAdvertisements();

        LauncherManager.initSideMenu();

        LauncherManager.showFaqButton();
    }

    public static void checkModalWindow() {
        if (VENTO.launcherManager.accountList.isVisible()) {
            VENTO.launcherManager.accountList.setVisible(false);
            VENTO.launcherManager.accountList.getChildren().clear();
        }

        if (VENTO.launcherManager.optionsList.isVisible()) {
            VENTO.launcherManager.optionsList.getChildren().clear();
            VENTO.launcherManager.optionsList.setVisible(false);
        }

        if (VENTO.launcherManager.webList.isVisible()) {
            VENTO.launcherManager.webList.getChildren().clear();
            VENTO.launcherManager.webList.setVisible(false);
        }

        if (VENTO.launcherManager.notifyList.isVisible()) {
            VENTO.launcherManager.notifyList.getChildren().clear();
            VENTO.launcherManager.notifyList.setVisible(false);
        }
    }

    private static List<JFXButton> sideMenuButtons = new ArrayList<>();

    public static void showSideMenu() {
        Platform.runLater(LauncherManager::initSideMenu);
    }

    public static void reinitSideMenu() {
        VENTO.launcherManager.mainArea.getChildren().removeAll(sideMenuButtons);
        sideMenuButtons.clear();
        initSideMenu();
    }

    private static void initSideMenu() {
        if (sideMenuButtons.isEmpty()) {
            if (SettingsManager.enableCheats) {
                sideMenuButtons.add(new SideMenuButton("Читы", EmojiOne.SKULL_CROSSBONES, "rgba(224, 56, 34, 0.95)", "#FF8A84", VENTO.GUI.Cheats).build());
            }
            sideMenuButtons.add(new SideMenuButton("Текстуры", FontAwesomeIcon.PAINT_BRUSH, "rgba(56, 184, 235, 0.95)", "#A1E7FF", VENTO.GUI.Textures).build());
            sideMenuButtons.add(new SideMenuButton("Моды", FontAwesomeIcon.PUZZLE_PIECE, "rgba(214, 82, 102, 0.95)", "#FF90A6", VENTO.GUI.Mods).build());
            sideMenuButtons.add(new SideMenuButton("Карты", FontAwesomeIcon.MAP_ALT, "rgba(236, 166, 58, 0.95)", "#FAC471", VENTO.GUI.Worlds).build());
            sideMenuButtons.add(new SideMenuButton("Модпаки", FontAwesomeIcon.FILE_ARCHIVE_ALT, "rgba(103, 58, 183, 0.95)", "#9A73DE", VENTO.GUI.Modpacks).build());
        }

        final int SPACE = 61;
        int START = ((VENTO.launcherManager.mainArea.heightProperty().getValue().intValue() - (SPACE * sideMenuButtons.size())) / 2);

        for (JFXButton btn : sideMenuButtons) {
            btn.setLayoutY(START);
            VENTO.launcherManager.mainArea.getChildren().add(btn);

            START += SPACE;
        }
    }

    private static List<JFXButton> toolbarMenuButtons = new ArrayList<>();

    public static void showToolBarMenu() {
        Platform.runLater(LauncherManager::initToolBarMenuButtons);
    }

    private static void initToolBarMenuButtons() {
        final int width = VENTO.launcherManager.bottomToolbar.widthProperty().getValue().intValue();

        if (toolbarMenuButtons.isEmpty()) {
            toolbarMenuButtons.add(new ToolBarMenuButton("Менеджер сборок", FontAwesomeIcon.ARCHIVE, null, "#24988A", "#45CBBB", VENTO.GUI.ManageModpacks, width).build());
            toolbarMenuButtons.add(new ToolBarMenuButton("Скриншоты", FontAwesomeIcon.PICTURE_ALT, null, "#0BB571", "#51FFB7", VENTO.GUI.Screenshots, width).build());
            toolbarMenuButtons.add(new ToolBarMenuButton("Открыть папку", FontAwesomeIcon.FOLDER_OPEN, null, "#2672FF", "#88C5FF", VENTO.GUI.Folder, width).build());
            toolbarMenuButtons.add(new ToolBarMenuButton("Настройки", FontAwesomeIcon.GEARS, null, "#F0BE26", "#FFE69C", VENTO.GUI.Settings, width).build());
        }

        VENTO.launcherManager.toolbarbox.getChildren().addAll(toolbarMenuButtons);
    }

    private static Pane faqButton = null;

    public static void showFaqButton() {
        Platform.runLater(LauncherManager::initFaqButton);
    }

    private static void initFaqButton() {
        if (faqButton == null) {
            faqButton = new Pane();
            faqButton.setPrefWidth(140);
            faqButton.setPrefHeight(25);
            faqButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 3px;");
            faqButton.setLayoutY(15);
            faqButton.setLayoutX(15);
            faqButton.setCursor(Cursor.HAND);
            faqButton.setOpacity(0.75);
            faqButton.setOnMouseClicked(e -> DesktopUtils.openUrl("https://r-launcher.su/faq"));

            List<String> textLabel = new ArrayList<>();
            textLabel.add("Открыть FAQ");
            textLabel.add("Получить помощь");
            textLabel.add("FAQ по лаунчеру");
            textLabel.add("Нужна помощь?");

            Label text = new Label();
            text.setText(textLabel.get(new Random().nextInt(textLabel.size())));
            text.setFont(Font.font("System", FontWeight.BOLD, 13));
            text.setTextFill(Paint.valueOf("#ffffff"));
            text.setPrefWidth(faqButton.getPrefWidth());
            text.setPrefHeight(faqButton.getPrefHeight());
            text.setAlignment(Pos.CENTER);
            //faqButton.setOnMouseEntered(e -> faqButton.setOpacity(1.0));
            //faqButton.setOnMouseExited(e -> faqButton.setOpacity(0.75));
            faqButton.setOnMouseEntered(e -> AnimationUtils.applyEnterAnimation(faqButton));
            faqButton.setOnMouseExited(e -> AnimationUtils.applyExitAnimation(faqButton));
            faqButton.getChildren().setAll(text);
        }

        VENTO.launcherManager.mainArea.getChildren().add(faqButton);
    }
}
