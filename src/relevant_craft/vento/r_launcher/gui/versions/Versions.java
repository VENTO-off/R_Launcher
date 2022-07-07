package relevant_craft.vento.r_launcher.gui.versions;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;
import relevant_craft.vento.r_launcher.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

public class Versions implements Initializable {

    public static AnchorPane loadVersionsWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane versions = VENTO.loadGUI(VENTO.GUI.Versions.path);
        versions.setLayoutX(width.getValue() / 2 - versions.getPrefWidth() / 2);
        versions.setLayoutY(height.getValue() / 2 - versions.getPrefHeight() / 2);

        return versions;
    }

    public void onClose_Versions(MouseEvent mouseEvent) {
        VENTO.closeGUI();
    }

    @FXML
    private JFXListView<Version> versionList;
    @FXML
    private Label nameText;
    @FXML
    private Label nameField;
    @FXML
    private Label releaseText;
    @FXML
    private Label releaseField;
    @FXML
    private Label typeText;
    @FXML
    private Label typeField;
    @FXML
    private Label installText;
    @FXML
    private Label installField;
    @FXML
    private JFXButton folderVersion;
    @FXML
    private JFXButton actionButton;
    @FXML
    private JFXSpinner loading;
    @FXML
    private JFXCheckBox onlyInstalledCheckbox;
    @FXML
    private AnchorPane tooltipArea;
    @FXML
    private AnchorPane arrowArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        versionList.setCellFactory(new Callback<ListView<Version>, ListCell<Version>>() {
            @Override
            public JFXListCell<Version> call(ListView<Version> param) {
                return new JFXListCell<Version>() {
                    @Override
                    protected void updateItem(Version version, boolean empty) {
                        super.updateItem(version, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
                            icon.setFill(Paint.valueOf("#939699"));
                            if (!version.isLocal) { icon.setOpacity(0); }
                            setGraphic(icon);
                            setText(version.displayName);
                            setFont(Font.font(15));
                        }
                    }
                };
            }
        });

        setVersions(onlyInstalledCheckbox.isSelected());
        hideElements();
    }

    public void onVersionChoose(MouseEvent mouseEvent) {
        Version version = versionList.getSelectionModel().getSelectedItem();
        if (version == null) {
            hideElements();
            return;
        }
        String pathToJson = SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + version.name + File.separator + version.name + ".json";

        nameField.setText(version.displayName);
        releaseField.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(VersionManager.getReleaseDate(version))));
        typeField.setText(version.type.displayName);
        installField.setText(FileUtils.getCreationTime(pathToJson) == 0 ? "-" : new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(FileUtils.getCreationTime(pathToJson)));

        folderVersion.setDisable(!version.isLocal);
        actionButton.setDisable(false);

        if (version.isLocal) {
            actionButton.setText("Удалить");
            actionButton.setStyle("-fx-background-color: #F26B50");
            actionButton.setRipplerFill(Paint.valueOf("#ff9b8b"));
        } else {
            actionButton.setText("Установить");
            actionButton.setStyle("-fx-background-color: #7FCF3D");
            actionButton.setRipplerFill(Paint.valueOf("#beff85"));
        }

        showElements();
    }

    public void onFolder_Action(ActionEvent actionEvent) {
        Version version = versionList.getSelectionModel().getSelectedItem();
        String path = SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + version.name;
        DesktopUtils.openFolder(path, false);
    }

    public void onVersion_Action(ActionEvent actionEvent) {
        Version version = versionList.getSelectionModel().getSelectedItem();
        String path = SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + version.name;

        if (actionButton.getText().equals("Установить")) {
            folderVersion.setDisable(true);
            actionButton.setDisable(true);
            versionList.setDisable(true);
            onlyInstalledCheckbox.setDisable(true);
            loading.setVisible(true);

            actionButton.setText("Установка...");
            actionButton.setStyle("-fx-background-color: #F7C50E");
            actionButton.setRipplerFill(Paint.valueOf("#ffe071"));

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        DownloadManager.downloadFile(VENTO.WEB + version.url, path + File.separator + version.name + ".json", false);
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка скачивания");
                            notify.setMessage("Не удалось скачать .json файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                            notify.showNotify();
                            actionButton.setText("Установить");
                            actionButton.setStyle("-fx-background-color: #7FCF3D");
                            actionButton.setRipplerFill(Paint.valueOf("#beff85"));
                            folderVersion.setDisable(false);
                            actionButton.setDisable(false);
                            versionList.setDisable(false);
                            onlyInstalledCheckbox.setDisable(false);
                            loading.setVisible(false);
                            setVersions(onlyInstalledCheckbox.isSelected());
                            versionList.getSelectionModel().select(version);
                        });
                        return null;
                    }

                    VersionManager.setVersionLocal(version);

                    Platform.runLater(() -> {
                        VersionManager.setVersions();
                        actionButton.setText("Удалить");
                        actionButton.setStyle("-fx-background-color: #F26B50");
                        actionButton.setRipplerFill(Paint.valueOf("#ff9b8b"));
                        folderVersion.setDisable(false);
                        actionButton.setDisable(false);
                        versionList.setDisable(false);
                        onlyInstalledCheckbox.setDisable(false);
                        loading.setVisible(false);
                        installField.setText(FileUtils.getCreationTime(path) == 0 ? "-" : new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(FileUtils.getCreationTime(path)));
                        setVersions(onlyInstalledCheckbox.isSelected());
                        versionList.getSelectionModel().select(version);
                    });
                    return null;
                }
            };
            new Thread(task).start();
        } else if (actionButton.getText().equals("Удалить")) {
            NotifyWindow notify = new NotifyWindow(NotifyType.QUESTION);
            notify.setTitle("Подтвердите действие");
            notify.setMessage("Вы действительно хотите удалить версию «" + version.name + "» и все её файлы?\nОтменить данное действие нельзя.");
            notify.setYesOrNo(true);
            notify.showNotify();
            if (notify.getAnswer()) {
                folderVersion.setDisable(true);
                actionButton.setDisable(true);
                versionList.setDisable(true);
                onlyInstalledCheckbox.setDisable(true);
                loading.setVisible(true);

                hideElements();

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() {
                        FileUtils.removeDirectory(new File(path));

                        Platform.runLater(() -> {
                            VersionManager.removeLocalVersion(version.name);
                            VersionManager.setVersions();
                            versionList.setDisable(false);
                            onlyInstalledCheckbox.setDisable(false);
                            loading.setVisible(false);
                            setVersions(onlyInstalledCheckbox.isSelected());
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    }

    public void onOnlyInstalled_Check(ActionEvent actionEvent) {
        setVersions(onlyInstalledCheckbox.isSelected());
        hideElements();
    }

    private void setVersions(boolean onlyLocal) {
        Collection<Version> versions = VersionManager.getVersions(false);
        versions.removeIf(version -> !version.isLocal && onlyLocal);
        versionList.getItems().setAll(versions);
    }

    private void hideElements() {
        nameText.setVisible(false);
        nameField.setVisible(false);
        releaseText.setVisible(false);
        releaseField.setVisible(false);
        typeText.setVisible(false);
        typeField.setVisible(false);
        installText.setVisible(false);
        installField.setVisible(false);
        folderVersion.setVisible(false);
        actionButton.setVisible(false);

        tooltipArea.setVisible(true);
        tooltipArea.getChildren().setAll(getToolTip());
        arrowArea.setVisible(true);
        arrowArea.getChildren().setAll(new ImageView(PictureManager.loadImage("arrow2.png")));
    }

    private void showElements() {
        nameText.setVisible(true);
        nameField.setVisible(true);
        releaseText.setVisible(true);
        releaseField.setVisible(true);
        typeText.setVisible(true);
        typeField.setVisible(true);
        installText.setVisible(true);
        installField.setVisible(true);
        folderVersion.setVisible(true);
        actionButton.setVisible(true);

        tooltipArea.setVisible(false);
        arrowArea.setVisible(false);
    }

    private Label getToolTip() {
        Label toolTip = new Label();
        toolTip.setText("Пожалуйста, выберите версию, чтобы управлять ею");
        toolTip.setWrapText(true);
        toolTip.setTextFill(Paint.valueOf("#727272"));
        toolTip.setFont(Font.font(15));
        toolTip.setTextAlignment(TextAlignment.CENTER);
        toolTip.setAlignment(Pos.CENTER);
        toolTip.setPrefWidth(tooltipArea.getPrefWidth());
        toolTip.setPrefHeight(tooltipArea.getPrefHeight());

        return toolTip;
    }

    public void onHelp_Versions(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_11");
    }
}
