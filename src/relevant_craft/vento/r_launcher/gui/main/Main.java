package relevant_craft.vento.r_launcher.gui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.icons525.Icons525;
import de.jensd.fx.glyphs.icons525.Icons525View;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.account.AccountManager;
import relevant_craft.vento.r_launcher.manager.account.AccountType;
import relevant_craft.vento.r_launcher.manager.advertisement.AdvertisementManager;
import relevant_craft.vento.r_launcher.manager.background.BackgroundManager;
import relevant_craft.vento.r_launcher.manager.hardware.HardwareManager;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.launcher.NewYearStyle;
import relevant_craft.vento.r_launcher.manager.notify.NotifyManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.tutorial.TutorialManager;
import relevant_craft.vento.r_launcher.manager.updater.UpdaterManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.minecraft.RunMinecraft;
import relevant_craft.vento.r_launcher.utils.AnimationUtils;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Main implements Initializable {

    public static void show() {
        VENTO.window.initStyle(StageStyle.UNDECORATED);
        VENTO.window.setResizable(false);
        VENTO.window.setTitle("R-Launcher");
        VENTO.window.setScene(new Scene(VENTO.root, 1000, 600));
        VENTO.root.getScene().setFill(Color.valueOf("#282828"));
        VENTO.window.show();
    }

    /**********************************************
     *                  BACKGROUND                *
     **********************************************/
    @FXML
    private Pane background;
    @FXML
    private AnchorPane mainArea;
    @FXML
    private JFXComboBox<Version> versionList;
    @FXML
    private JFXButton MinecraftButton;
    @FXML
    private JFXButton MinecraftOptionsButton;
    @FXML
    private Pane toolBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        VENTO.launcherManager = new LauncherManager(mainArea,
                background,
                versionList,
                currentAccount,
                mainFrontArea,
                darkLayer,
                toolBar,
                bottomToolbar,
                toolbarbox,
                MinecraftButton,
                MinecraftOptionsButton,
                accountList,
                optionsList,
                webList,
                notifyList,
                notifyButton);
        title.setText(title.getText() + " v" + VENTO.VERSION + (VENTO.ONLINE_MODE ? "" : " [нет подключения]") + (VENTO.DEVELOPER ? " [DevBuild]" : ""));

        BackgroundManager.initBackground();

        AccountManager.initLastAccount();

        UpdaterManager.checkRUpdater();
        UpdaterManager.checkUpdate();

        HardwareManager.sendHardwareStats();

        LauncherManager.showToolBarMenu();

        LauncherManager.showSideMenu();

        LauncherManager.showFaqButton();

        VersionManager.loadVersions();

        AdvertisementManager.loadAdvertisements();

        NotifyManager.loadNotifies();

        NewYearStyle.startSnow();
    }

    public void selectVersion(ActionEvent actionEvent) {
        LauncherManager.checkModalWindow();

        LauncherManager.lastVersion = versionList.getValue().name;
        LauncherManager.saveData();
    }

    /**********************************************
     *                   BUTTONS                  *
     **********************************************/
    @FXML
    private AnchorPane mainFrontArea;
    @FXML
    private Pane darkLayer;
    @FXML
    private AnchorPane bottomToolbar;
    @FXML
    private HBox toolbarbox;

    public void onStart_Minecraft(ActionEvent actionEvent) {
        LauncherManager.checkModalWindow();

        if (AccountManager.selectedAccount == null) {
            NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
            notify.setTitle("Ошибка");
            notify.setMessage("У Вас `не выбран аккаунт`! Пожалуйста, создайте новый аккаунт.\n«Менеджер аккаунтов» находится в верхнем правом углу.");
            notify.showNotify();
            return;
        }
        if (versionList.getValue() == null) {
            NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
            notify.setTitle("Ошибка");
            notify.setMessage("Версия Minecraft не выбрана! Пожалуйста, выберите версию Minecraft для запуска.");
            notify.showNotify();
            return;
        }

        AdvertisementManager.addForceServers();

        RunMinecraft.runMinecraft(null);
    }

    @FXML
    private VBox optionsList;
    private double layoutY_optionsMinecraft = 0;

    public static boolean FORCE_DOWNLOAD = false;

    public void onOptions_Minecraft(ActionEvent actionEvent) {
        if (layoutY_optionsMinecraft == 0) {
            layoutY_optionsMinecraft = optionsList.getLayoutY();
        }

        if (optionsList.isVisible()) {
            optionsList.getChildren().clear();
            optionsList.setVisible(false);
        } else {
            LauncherManager.checkModalWindow();

            JFXButton folder = new JFXButton();
            folder.setPrefWidth(optionsList.getPrefWidth());
            folder.setAlignment(Pos.CENTER_LEFT);
            folder.setText("Менеджер версий");
            folder.setFont(Font.font(15));
            folder.setCursor(Cursor.HAND);
            folder.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SLACK));
            folder.setOnAction(event -> {
                VENTO.startGUI(VENTO.GUI.Versions);
                optionsList.getChildren().clear();
                optionsList.setVisible(false);
            });
            optionsList.getChildren().add(folder);

            JFXButton versions = new JFXButton();
            versions.setPrefWidth(optionsList.getPrefWidth());
            versions.setAlignment(Pos.CENTER_LEFT);
            versions.setText("Обновить версии");
            versions.setFont(Font.font(15));
            versions.setCursor(Cursor.HAND);
            versions.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REPEAT));
            versions.setOnAction(event -> {
                VersionManager.loadVersions();
                optionsList.getChildren().clear();
                optionsList.setVisible(false);
            });
            optionsList.getChildren().add(versions);

            JFXButton download = new JFXButton();
            download.setPrefWidth(optionsList.getPrefWidth());
            download.setAlignment(Pos.CENTER_LEFT);
            download.setText("Перекачать клиент");
            download.setFont(Font.font(15));
            download.setCursor(Cursor.HAND);
            if (FORCE_DOWNLOAD) {
                download.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE_ALT));
            } else {
                download.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT));
            }
            download.setOnAction(event -> {
                FORCE_DOWNLOAD = !FORCE_DOWNLOAD;
                if (FORCE_DOWNLOAD) {
                    download.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE_ALT));
                } else {
                    download.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT));
                }
            });
            optionsList.getChildren().add(download);

            JFXButton debug = new JFXButton();
            debug.setPrefWidth(optionsList.getPrefWidth());
            debug.setAlignment(Pos.CENTER_LEFT);
            debug.setText("Режим отладки");
            debug.setFont(Font.font(15));
            debug.setCursor(Cursor.HAND);
            if (SettingsManager.useDeveloperConsole) {
                debug.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE_ALT));
            } else {
                debug.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT));
            }
            debug.setOnAction(event -> {
                SettingsManager.useDeveloperConsole = !SettingsManager.useDeveloperConsole;
                SettingsManager.saveSettings();
                if (SettingsManager.useDeveloperConsole) {
                    debug.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE_ALT));
                } else {
                    debug.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT));
                }
            });
            optionsList.getChildren().add(debug);

            JFXButton assets = new JFXButton();
            assets.setPrefWidth(optionsList.getPrefWidth());
            assets.setAlignment(Pos.CENTER_LEFT);
            assets.setText("Проверять Assets");
            assets.setFont(Font.font(15));
            assets.setCursor(Cursor.HAND);
            if (SettingsManager.checkAssets) {
                assets.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE_ALT));
            } else {
                assets.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT));
            }
            assets.setOnAction(event -> {
                SettingsManager.checkAssets = !SettingsManager.checkAssets;
                SettingsManager.saveSettings();
                if (SettingsManager.checkAssets) {
                    assets.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE_ALT));
                } else {
                    assets.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT));
                }
            });
            optionsList.getChildren().add(assets);

            optionsList.setLayoutY(layoutY_optionsMinecraft - optionsList.getChildren().size() * 34);
            optionsList.setVisible(true);
        }
    }

    /**********************************************
     *                   TITLE                    *
     **********************************************/
    @FXML
    private FontAwesomeIconView title;
    @FXML
    private VBox webList;

    public void onTitle_Click(MouseEvent mouseEvent) {
        if (webList.isVisible()) {
            webList.setVisible(false);
            webList.getChildren().clear();
        } else {
            LauncherManager.checkModalWindow();

            JFXButton site = new JFXButton();
            site.setPrefWidth(optionsList.getPrefWidth());
            site.setAlignment(Pos.CENTER_LEFT);
            site.setText("Наш сайт");
            site.setFont(Font.font(15));
            site.setCursor(Cursor.HAND);
            site.setGraphic(new Icons525View(Icons525.SELECT));
            site.setOnAction(event -> {
                DesktopUtils.openUrl(VENTO.SITE);
                webList.getChildren().clear();
                webList.setVisible(false);
            });
            webList.getChildren().add(site);

            JFXButton vk = new JFXButton();
            vk.setPrefWidth(optionsList.getPrefWidth());
            vk.setAlignment(Pos.CENTER_LEFT);
            vk.setText("Наша группа");
            vk.setFont(Font.font(15));
            vk.setCursor(Cursor.HAND);
            vk.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.VK));
            vk.setOnAction(event -> {
                DesktopUtils.openUrl(VENTO.VK);
                webList.getChildren().clear();
                webList.setVisible(false);
            });
            webList.getChildren().add(vk);

            webList.setVisible(true);
        }
    }

    public void onTitle_Entered(MouseEvent mouseEvent) {
        //title.setOpacity(1.0);
        AnimationUtils.applyEnterAnimation(title);
    }

    public void onTitle_Exited(MouseEvent mouseEvent) {
        //title.setOpacity(0.75);
        AnimationUtils.applyExitAnimation(title);
    }

    /**********************************************
     *                CLOSE BUTTON                *
     **********************************************/
    @FXML
    private FontAwesomeIconView closeButton;

    public void onClose_Click(MouseEvent mouseEvent) {
        Animation animation = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(VENTO.root.opacityProperty(), 0)));
        animation.play();
        animation.setOnFinished(e -> Platform.exit());
    }

    public void onClose_Entered(MouseEvent mouseEvent) {
        //closeButton.setOpacity(1.0);
        AnimationUtils.applyEnterAnimation(closeButton);
    }

    public void onClose_Exited(MouseEvent mouseEvent) {
        //closeButton.setOpacity(0.75);
        AnimationUtils.applyExitAnimation(closeButton);
    }

    /**********************************************
     *               MINIMIZE BUTTON              *
     **********************************************/
    @FXML
    private FontAwesomeIconView minimizeButton;

    public void onMinimize_Click(MouseEvent mouseEvent) {
        VENTO.window.setIconified(true);
    }

    public void onMinimize_Entered(MouseEvent mouseEvent) {
        //minimizeButton.setOpacity(1.0);
        AnimationUtils.applyEnterAnimation(minimizeButton);
    }

    public void onMinimize_Exited(MouseEvent mouseEvent) {
        //minimizeButton.setOpacity(0.75);
       AnimationUtils.applyExitAnimation(minimizeButton);
    }

    /**********************************************
     *                   NOTIFY                   *
     **********************************************/
    @FXML
    private MaterialDesignIconView notifyButton;
    @FXML
    private VBox notifyList;

    public void onNotify_Click(MouseEvent mouseEvent) {
        if (notifyList.isVisible()) {
            notifyList.setVisible(false);
            notifyList.getChildren().clear();
        } else {
            LauncherManager.checkModalWindow();

            if (NotifyManager.getNotifiesAmount() > 0) {
                ScrollPane scroll = new ScrollPane();
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setVmax(280);
                scroll.setMaxHeight(scroll.getVmax());
                scroll.setPrefWidth(accountList.getPrefWidth());
                scroll.setFitToWidth(true);
                scroll.setStyle("-fx-focus-color: transparent; -fx-background-radius: 4;");

                VBox notifies_list = new VBox();
                notifies_list.setPrefWidth(scroll.getPrefWidth());
                notifies_list.setStyle("-fx-background-color: white;");
                notifyList.getChildren().add(scroll);

                notifies_list.getChildren().setAll(NotifyManager.rendered_notifies);

                scroll.setContent(notifies_list);
            } else {
                notifyList.getChildren().add(NotifyManager.createEmpty());
            }

            notifyList.setVisible(true);
        }

        NotifyManager.animateNotifyButton();
    }

    public void onNotify_Entered(MouseEvent mouseEvent) {
        AnimationUtils.applyEnterAnimation(notifyButton);
    }

    public void onNotify_Exited(MouseEvent mouseEvent) {
        AnimationUtils.applyExitAnimation(notifyButton);
    }

    /**********************************************
     *                  ACCOUNTS                  *
     **********************************************/
    @FXML
    private Label currentAccount;
    @FXML
    private VBox accountList;

    public void onAccount_Click(MouseEvent mouseEvent) {
        if (accountList.isVisible()) {
            accountList.setVisible(false);
            accountList.getChildren().clear();
        } else {
            LauncherManager.checkModalWindow();

            ScrollPane scroll = new ScrollPane();
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setVmax(445);
            scroll.setMaxHeight(scroll.getVmax());
            scroll.setPrefWidth(accountList.getPrefWidth());
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-focus-color: transparent; -fx-background-radius: 4;");

            VBox accounts_list = new VBox();
            accounts_list.setPrefWidth(scroll.getPrefWidth());
            accounts_list.setStyle("-fx-background-color: white;");
            accountList.getChildren().add(scroll);

            if (VENTO.accountManager.getAmount() > 0) {
                for (int i = 0; i < VENTO.accountManager.getAmount(); i++) {
                    JFXButton acc = AccountManager.convertToButton(i, accountList.widthProperty().getValue());
                    accounts_list.getChildren().add(acc);

                    acc.setOnMouseClicked(e -> {
                        AccountManager.selectedAccount = AccountManager.getByName(acc.getText(), AccountType.valueOf(acc.getAccessibleRoleDescription()));
                        AccountManager.saveLastAccount();
                        currentAccount.setText(AccountManager.selectedAccount.name);
                        currentAccount.setGraphic(AccountManager.convertToImageType(AccountManager.selectedAccount));
                        accountList.setVisible(false);
                        accountList.getChildren().clear();
                    });
                }
            } else {
                accounts_list.getChildren().add(AccountManager.createEmpty());
            }

            JFXButton accManager = new JFXButton();
            accManager.setButtonType(JFXButton.ButtonType.FLAT);
            accManager.setText("Менеджер Аккаунтов");
            accManager.setCursor(Cursor.HAND);
            accManager.setFont(Font.font(15));
            accManager.setCursor(Cursor.HAND);
            accManager.setPrefWidth(accountList.widthProperty().getValue());
            accManager.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.USERS));
            accManager.setAlignment(Pos.BASELINE_LEFT);
            accountList.getChildren().add(accManager);
            accManager.setOnMouseClicked(event -> {
                VENTO.startGUI(VENTO.GUI.Accounts);

                accountList.setVisible(false);
                accountList.getChildren().clear();
            });

            scroll.setContent(accounts_list);
            accountList.setVisible(true);
        }
    }

    public void onAccount_Entered(MouseEvent mouseEvent) {
        //currentAccount.setOpacity(1.0);
        AnimationUtils.applyEnterAnimation(currentAccount);
    }

    public void onAccount_Exited(MouseEvent mouseEvent) {
        //currentAccount.setOpacity(0.75);
        AnimationUtils.applyExitAnimation(currentAccount);
    }


    /**********************************************
     *                 DRAG WINDOW                *
     **********************************************/
    private static double xOffset = 0;
    private static double yOffset = 0;

    public void onMouseDragged(MouseEvent mouseEvent) {
        VENTO.window.setX(mouseEvent.getScreenX() + xOffset);
        VENTO.window.setY(mouseEvent.getScreenY() + yOffset);

        TutorialManager.updateTutorialNotifies();
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        xOffset = VENTO.window.getX() - mouseEvent.getScreenX();
        yOffset = VENTO.window.getY() - mouseEvent.getScreenY();
    }
}
