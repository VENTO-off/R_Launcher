package relevant_craft.vento.r_launcher.gui.cheats;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.cheat.Cheat;
import relevant_craft.vento.r_launcher.manager.cheat.CheatManager;
import relevant_craft.vento.r_launcher.manager.cheat.CheatVersion;
import relevant_craft.vento.r_launcher.manager.cheat.CheatVersion_ListCell;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.extract.ExtractManager;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;
import relevant_craft.vento.r_launcher.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Cheats implements Initializable {

    public static AnchorPane loadCheatsWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane cheats = VENTO.loadGUI(VENTO.GUI.Cheats.path);
        cheats.setLayoutX(width.getValue() / 2 - cheats.getPrefWidth() / 2);
        cheats.setLayoutY(height.getValue() / 2 - cheats.getPrefHeight() / 2);

        return cheats;
    }

    public void onClose_Cheats(MouseEvent mouseEvent) {
        VENTO.cheatManager = null;
        VersionManager.setVersions();
        VENTO.closeGUI();
    }

    @FXML
    private JFXSpinner imageLoading;
    @FXML
    private Label cheatLoadingText;
    @FXML
    private AnchorPane imageArea;
    @FXML
    private JFXButton actionButton;
    @FXML
    private JFXProgressBar cheatProgressbar;
    @FXML
    private Label progressLabel;
    @FXML
    private JFXListView<Cheat> cheatsList;
    @FXML
    private JFXComboBox<CheatVersion> versionList;
    @FXML
    private Label descriptionArea;
    @FXML
    private Separator separator;
    @FXML
    private Separator separator2;
    @FXML
    private Separator separator3;
    @FXML
    private FontAwesomeIconView versionList_Text;
    @FXML
    private AnchorPane arrowArea;
    @FXML
    private JFXSpinner loading;
    @FXML
    private FontAwesomeIconView loadingText;
    @FXML
    private FontAwesomeIconView cheatsFor;
    @FXML
    private FontAwesomeIconView searchIcon;
    @FXML
    private JFXTextField search;
    @FXML
    private Pane cheatNameLayout;
    @FXML
    private Label cheatName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VENTO.cheatManager = new CheatManager(cheatsList, versionList, imageArea, imageLoading, cheatLoadingText, descriptionArea, actionButton, cheatProgressbar, progressLabel, separator, separator2, separator3, versionList_Text, loading, loadingText, arrowArea, cheatsFor, searchIcon, search, cheatNameLayout, cheatName);

        CheatManager.loadCheats();

        //versionList.setPromptText("<версия не выбрана>");
        versionList.setButtonCell(new ListCell<CheatVersion>() {
            @Override
            protected void updateItem(CheatVersion cheatVersion, boolean empty) {
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    setText(cheatVersion.getVersion());
                }
            }
        });
        versionList.setCellFactory(param -> new CheatVersion_ListCell());

        cheatsList.setCellFactory(new Callback<ListView<Cheat>, ListCell<Cheat>>() {
            @Override
            public JFXListCell<Cheat> call(ListView<Cheat> param) {
                return new JFXListCell<Cheat>() {
                    @Override
                    protected void updateItem(Cheat cheat, boolean empty) {
                        super.updateItem(cheat, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
                            icon.setFill(Paint.valueOf("#939699"));
                            if (!cheat.isLocal()) { icon.setOpacity(0); }
                            setGraphic(icon);
                            setText(cheat.getName());
                            setFont(Font.font(15));
                        }
                    }
                };
            }
        });

        search.textProperty().addListener((observable, oldValue, newValue) -> CheatManager.findCheatsByString(search.getText()));
    }

    public void onVersion_Choose(ActionEvent actionEvent) {
        CheatManager.setCheatsList(CheatManager.getCheatsByVersion(versionList.getValue().getVersion()), true);

        descriptionArea.setText("");
        imageArea.getChildren().setAll(CheatManager.getToolTip());
        arrowArea.setVisible(true);
        imageLoading.setVisible(false);
        descriptionArea.setVisible(false);
        actionButton.setVisible(false);
        separator.setVisible(false);
        search.setText(null);
        cheatNameLayout.setVisible(false);
    }

    public void onCheat_Choose(MouseEvent mouseEvent) {
        if (cheatsList.getSelectionModel() == null) {
            return;
        }
        if (cheatsList.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Cheat cheat = cheatsList.getSelectionModel().getSelectedItem();

        CheatManager.loadCheatInfo(cheat);
    }

    public void onCheat_Action(ActionEvent actionEvent) {
        Cheat cheat = cheatsList.getSelectionModel().getSelectedItem();

        if (actionButton.getText().equals("Установить")) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    File jar = new File(cheat.getPath() + File.separator + cheat.getName() + ".jar");
                    File json = new File(cheat.getPath() + File.separator + cheat.getName() + ".json");
                    File extra = new File(cheat.getPath() + File.separator + "extra" + ".zip");

                    Platform.runLater(() -> {
                        versionList.setDisable(true);
                        cheatsList.setDisable(true);
                        search.setDisable(true);
                        cheatProgressbar.setVisible(true);
                        cheatProgressbar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                        progressLabel.setVisible(true);
                        progressLabel.setText("Подготовка...");
                        actionButton.setText("Установка...");
                        actionButton.setStyle("-fx-background-color: #F7C50E");
                        actionButton.setRipplerFill(Paint.valueOf("#ffe071"));
                    });

                    try {
                        DownloadManager.downloadFile(cheat.getJson(), json.getAbsolutePath(), false);
                        if (DownloadManager.existsUrlFile(cheat.getExtra())) {
                            DownloadManager.downloadFile(cheat.getExtra(), extra.getAbsolutePath(), false);
                            new ExtractManager().unpackZIP(extra.getAbsolutePath(), extra.getParentFile().getAbsolutePath());
                            extra.delete();
                        }
                        DownloadManager.downloadCheatOrСFFile(cheat.getJar(), jar.getAbsolutePath(), cheatProgressbar, progressLabel);
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            versionList.setDisable(false);
                            cheatsList.setDisable(false);
                            search.setDisable(false);
                            cheatProgressbar.setVisible(false);
                            progressLabel.setVisible(false);
                            actionButton.setText("Установить");
                            actionButton.setStyle("-fx-background-color: #7FCF3D");
                            actionButton.setRipplerFill(Paint.valueOf("#beff85"));
                        });
                        return null;
                    }

                    CheatManager.editCheatLocal(cheat);
                    VersionManager.addLocalVersion(cheat.getName(), false);

                    Platform.runLater(() -> {
                        VersionManager.setVersionByName(cheat.getName());
                        if (search.getText() == null || search.getText().isEmpty()) {
                            CheatManager.setCheatsList(CheatManager.getCheatsByVersion(versionList.getValue().getVersion()), false);
                        } else {
                            CheatManager.findCheatsByString(search.getText());
                        }
                        cheatsList.getSelectionModel().select(cheat);
                        versionList.setDisable(false);
                        cheatsList.setDisable(false);
                        search.setDisable(false);
                        cheatProgressbar.setVisible(false);
                        progressLabel.setVisible(false);
                        actionButton.setText("Удалить");
                        actionButton.setStyle("-fx-background-color: #F26B50");
                        actionButton.setRipplerFill(Paint.valueOf("#ff9b8b"));
                    });
                    return null;
                }
            };
            new Thread(task).start();
        } else if (actionButton.getText().equals("Удалить")) {
            if (!FileUtils.removeDirectory(new File(cheat.getPath()))) {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка удаления");
                notify.setMessage("При удалении возникла ошибка. Попробуйте, удалить вручную.");
                notify.showNotify();
                return;
            }

            actionButton.setText("Установить");
            actionButton.setStyle("-fx-background-color: #7FCF3D");
            actionButton.setRipplerFill(Paint.valueOf("#beff85"));

            CheatManager.editCheatLocal(cheat);
            VersionManager.removeLocalVersion(cheat.getName());

            if (search.getText() == null || search.getText().isEmpty()) {
                CheatManager.setCheatsList(CheatManager.getCheatsByVersion(versionList.getValue().getVersion()), false);
            } else {
                CheatManager.findCheatsByString(search.getText());
            }
            cheatsList.getSelectionModel().select(cheat);
        }
    }

    public void onHelp_Cheats(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_23");
    }
}
