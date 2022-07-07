package relevant_craft.vento.r_launcher.gui.modpacks;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.modpack.*;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.manager.version.VersionType;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageModpacks implements Initializable {

    public static AnchorPane loadManageModpacksWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane modpacks = VENTO.loadGUI(VENTO.GUI.ManageModpacks.path);
        modpacks.setLayoutX(width.getValue() / 2 - modpacks.getPrefWidth() / 2);
        modpacks.setLayoutY(height.getValue() / 2 - modpacks.getPrefHeight() / 2);

        return modpacks;
    }

    public void onClose_ManageModpacks(MouseEvent mouseEvent) {
        VENTO.closeGUI();
    }

    @FXML
    private JFXSpinner loading;
    @FXML
    private FontAwesomeIconView loadingText;
    @FXML
    private JFXListView<Modpack> modpackList;
    @FXML
    private FontAwesomeIconView nameText;
    @FXML
    private JFXTextField nameField;
    @FXML
    private FontAwesomeIconView versionText;
    @FXML
    private JFXComboBox<Version> versionCombobox;
    @FXML
    private FontAwesomeIconView ramText;
    @FXML
    private JFXComboBox<String> ramCombobox;
    @FXML
    private Label ramWarning;
    @FXML
    private JFXSpinner loadingCreate;
    @FXML
    private JFXCheckBox installModloader;
    @FXML
    private JFXCheckBox installLiteLoader;
    @FXML
    private JFXCheckBox installOptiFine;
    @FXML
    private Separator separator;
    @FXML
    private AnchorPane tooltipArea;
    @FXML
    private AnchorPane arrowArea;
    @FXML
    private JFXButton backButton;
    @FXML
    private JFXButton addModpack;
    @FXML
    private JFXButton createModpack;
    @FXML
    private JFXButton folderModpack;
    @FXML
    private JFXButton deleteModpack;

    private Modpack selectedModpack = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ManageModpackManager.loadModpackSources(loading, loadingText, modpackList, separator, tooltipArea, arrowArea, createModpack);
        versionCombobox.getItems().setAll(VersionManager.getAllExternalVersions(false, false, false, false ,true));

        versionCombobox.setButtonCell(new ListCell<Version>() {
            @Override
            protected void updateItem(Version version, boolean empty) {
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    setText(version.name);
                }
            }
        });
        versionCombobox.setCellFactory(param -> new ManageModpackVersion_ListCell());

        modpackList.getItems().setAll(ManageModpackManager.getModpacks());
        modpackList.setCellFactory(param -> new Modpack_ListCell());

        ramCombobox.getItems().add("Использовать настройки лаунчера");
        ramCombobox.getItems().add("<Установить своё значение>");
        ramCombobox.setEditable(true);
        ramCombobox.getEditor().setFont(Font.font(15));

        ramCombobox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    setText(value);
                }
            }
        });

        ramCombobox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (ramCombobox.getSelectionModel().getSelectedIndex() == 0) {
                Platform.runLater(() -> {
                    ramCombobox.setValue(ramCombobox.getItems().get(0));
                });
            } else {
                Platform.runLater(() -> {
                    String suffix = " МБ";
                    if (newValue.equals(suffix)) {
                        return;
                    }
                    int caret = ramCombobox.getEditor().getCaretPosition();
                    ramCombobox.setValue(newValue.replaceAll("[^\\d]", "") + suffix);
                    ramCombobox.getEditor().positionCaret(caret);
                });
            }
        });

        ramCombobox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(0)) {
                Platform.runLater(() -> {
                    ramCombobox.getEditor().setEditable(false);
                    ramCombobox.getEditor().setCursor(Cursor.HAND);
                });
            } else {
                Platform.runLater(() -> {
                    ramCombobox.setValue(null);
                    ramCombobox.getEditor().setEditable(true);
                    ramCombobox.getEditor().setCursor(Cursor.TEXT);
                });
            }
        });

        ramCombobox.getEditor().caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            if (ramCombobox.getSelectionModel().getSelectedIndex() == 0) {
                ramCombobox.show();
            }
        });

        ramCombobox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                int ram;
                try {
                    ram = Integer.valueOf(ramCombobox.getValue().split(" ")[0]);
                } catch (Exception e) {
                    ramWarning.setGraphic(null);
                    return;
                }

                if (ram < 512) {
                    Text reason = new Text("Слишком мало памяти!");
                    reason.setFill(Paint.valueOf("#ff0000"));
                    reason.setFont(Font.font("System", FontWeight.BOLD, 11));
                    Text description = new Text("(Игра может работать неправильно)");
                    description.setFill(Paint.valueOf("#a3a3a3"));
                    description.setFont(Font.font("System", FontPosture.ITALIC, 11));
                    ramWarning.setGraphic(new TextFlow(reason, new Text(" "), description));
                } else if (ram > ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1000000) {
                    Text reason = new Text("Слишком много памяти!");
                    reason.setFill(Paint.valueOf("#ff0000"));
                    reason.setFont(Font.font("System", FontWeight.BOLD, 11));
                    Text description = new Text("(Игра может работать неправильно)");
                    description.setFill(Paint.valueOf("#a3a3a3"));
                    description.setFont(Font.font("System", FontPosture.ITALIC, 11));
                    ramWarning.setGraphic(new TextFlow(reason, new Text(" "), description));
                } else {
                    ramWarning.setGraphic(null);
                }
            }
        });

        ramCombobox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new JFXListCell<String>() {
                    @Override
                    protected void updateItem(String value, boolean empty) {
                        super.updateItem(value, empty);
                        setFont(Font.font(15));
                        if (empty) {
                            setText(null);
                        } else {
                            setText(value);
                            if (value.equals(ramCombobox.getItems().get(ramCombobox.getItems().size() - 1))) {
                                setAlignment(Pos.CENTER);
                                setFont(Font.font("System", FontPosture.ITALIC, getFont().getSize()));
                            }
                        }
                    }
                };
            }
        });

        ramCombobox.getSelectionModel().selectFirst();

        lockCheckBoxes();
        displayEditor(false);
        displayWelcome(true);
    }

    public void onModpack_PreCreate(ActionEvent actionEvent) {
        selectedModpack = null;
        modpackList.getSelectionModel().clearSelection();
        displayWelcome(false);
        displayEditor(true);
        applyEditor(null);
    }

    public void onModpack_Create(ActionEvent actionEvent) {
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка");
            notify.setMessage("Поле с названием не должно быть пустым.");
            notify.showNotify();
            return;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\s\\.\\_]+$");
        Matcher matcher = pattern.matcher(nameField.getText());
        if (!matcher.matches()) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка");
            notify.setMessage("Название сборки может содержать только:\n`латинские буквы`, `цифры`, `пробелы`,\n`нижнее подчеркивание` и `точки`.");
            notify.showNotify();
            return;
        }

        if (nameField.getText().endsWith(".")) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка");
            notify.setMessage("Название не должно заканчиваться точкой.");
            notify.showNotify();
            return;
        }

        if (versionCombobox.getValue() == null || versionCombobox.getValue().name.equals("<не выбрано>")) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка");
            notify.setMessage("Не выбрана версия Minecraft для сборки.");
            notify.showNotify();
            return;
        }

        if (selectedModpack == null) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Platform.runLater(() -> {
                        loadingCreate.setVisible(true);
                        lockEditor(true);
                    });

                    int ram = 0;
                    if (ramCombobox.getSelectionModel().getSelectedIndex() != 0) {
                        try {
                            ram = Integer.parseInt(ramCombobox.getValue().split(" ")[0]);
                        } catch (Exception ignored) {}
                    }

                    ManageModpackManager.createModpack(nameField.getText(),versionCombobox.getValue(), ram, installModloader.isSelected(), installLiteLoader.isSelected(), installOptiFine.isSelected());

                    Platform.runLater(() -> {
                        loadingCreate.setVisible(false);
                        nameField.setText(null);
                        modpackList.getItems().setAll(ManageModpackManager.getModpacks());

                        modpackList.getSelectionModel().clearSelection();
                        displayEditor(false);
                        lockEditor(false);
                        displayWelcome(true);
                    });
                    return null;
                }
            };
            new Thread(task).start();
        } else {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Platform.runLater(() -> {
                        loadingCreate.setVisible(true);
                        lockEditor(true);
                    });

                    int ram = 0;
                    if (ramCombobox.getSelectionModel().getSelectedIndex() != 0) {
                        try {
                            ram = Integer.valueOf(ramCombobox.getValue().split(" ")[0]);
                        } catch (Exception ignored) {}
                    }

                    ManageModpackManager.editModpack(selectedModpack, nameField.getText(), versionCombobox.getValue(), ram, installModloader.isSelected(), installLiteLoader.isSelected(), installOptiFine.isSelected());

                    Platform.runLater(() -> {
                        loadingCreate.setVisible(false);
                        nameField.setText(null);
                        modpackList.getItems().setAll(ManageModpackManager.getModpacks());

                        modpackList.getSelectionModel().clearSelection();
                        displayEditor(false);
                        lockEditor(false);
                        displayWelcome(true);
                    });
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    public void onModpack_Back(ActionEvent actionEvent) {
        selectedModpack = null;
        modpackList.getSelectionModel().clearSelection();
        displayEditor(false);
        applyEditor(null);
        displayWelcome(true);
    }

    public void onVersion_Choose(ActionEvent actionEvent) {
        Version version = versionCombobox.getValue();
        ManageModpackSources sources = ManageModpackManager.getModpackSources(version);

        applyCheckboxes(sources);
    }

    public void onModpack_Choose(MouseEvent mouseEvent) {
        if (modpackList.getSelectionModel() == null) {
            return;
        }
        if (modpackList.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        selectedModpack = modpackList.getSelectionModel().getSelectedItem();

        displayWelcome(false);
        displayEditor(true);
        applyEditor(selectedModpack);
    }

    public void onModpack_Folder(ActionEvent actionEvent) {
        if (selectedModpack != null) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(selectedModpack.getPath()));
                } catch (IOException e) {}
            }
        }
    }

    public void onModpack_Delete(ActionEvent actionEvent) {
        if (selectedModpack != null) {
            NotifyWindow notify = new NotifyWindow(NotifyType.QUESTION);
            notify.setTitle("Подтвердите действие");
            notify.setMessage("Вы действительно хотите удалить сборку «" + selectedModpack.getName() + "» и все её файлы?\nОтменить данное действие нельзя.");
            notify.setYesOrNo(true);
            notify.showNotify();
            if (notify.getAnswer()) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() {
                        Platform.runLater(() -> {
                            loadingCreate.setVisible(true);
                            lockEditor(true);
                        });

                        ManageModpackManager.deleteModpack(selectedModpack);
                        selectedModpack = null;

                        Platform.runLater(() -> {
                            loadingCreate.setVisible(false);
                            modpackList.getSelectionModel().clearSelection();
                            modpackList.getItems().setAll(ManageModpackManager.getModpacks());
                            lockEditor(false);
                            displayEditor(false);
                            displayWelcome(true);
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    }

    private void lockCheckBoxes() {
        installModloader.setSelected(false);
        installModloader.setDisable(true);
        installLiteLoader.setSelected(false);
        installLiteLoader.setDisable(true);
        installOptiFine.setSelected(false);
        installOptiFine.setDisable(true);
    }

    private void displayEditor(boolean isVisible) {
        nameText.setVisible(isVisible);
        nameField.setVisible(isVisible);
        versionText.setVisible(isVisible);
        versionCombobox.setVisible(isVisible);
        ramText.setVisible(isVisible);
        ramCombobox.setVisible(isVisible);
        installModloader.setVisible(isVisible);
        installLiteLoader.setVisible(isVisible);
        installOptiFine.setVisible(isVisible);
        backButton.setVisible(isVisible);
        addModpack.setVisible(isVisible);
        deleteModpack.setVisible(isVisible);
        folderModpack.setVisible(isVisible);
    }

    private void lockEditor(boolean isLocked) {
        nameText.setDisable(isLocked);
        nameField.setDisable(isLocked);
        versionText.setDisable(isLocked);
        versionCombobox.setDisable(isLocked);
        ramText.setDisable(isLocked);
        ramCombobox.setDisable(isLocked);
        installModloader.setDisable(isLocked);
        installLiteLoader.setDisable(isLocked);
        installOptiFine.setDisable(isLocked);
        backButton.setDisable(isLocked);
        addModpack.setDisable(isLocked);
        deleteModpack.setDisable(isLocked);
        folderModpack.setDisable(isLocked);
        if (versionCombobox.isShowing()) {
            versionCombobox.hide();
        }
        if (ramCombobox.isShowing()) {
            ramCombobox.hide();
        }
    }

    private void applyEditor(Modpack modpack) {
        if (modpack == null) {
            nameField.setText(null);
            //versionCombobox.setValue(null);
            versionCombobox.setValue(new Version("<не выбрано>", null, false, VersionType.UNKNOWN));
            versionCombobox.setDisable(false);
            ramCombobox.getSelectionModel().selectFirst();
            ramWarning.setGraphic(null);
            deleteModpack.setDisable(true);
            folderModpack.setDisable(true);
            addModpack.setText("Создать");
            lockCheckBoxes();
            return;
        }

        nameField.setText(modpack.getName());
        versionCombobox.getItems().forEach(version -> {
            if (version.name.equals(modpack.getVersion())) {
                versionCombobox.setValue(version);
            }
        });
        versionCombobox.setDisable(true);
        deleteModpack.setDisable(false);
        folderModpack.setDisable(false);
        addModpack.setText("Сохранить");

        ManageModpackSources sources = ManageModpackManager.getModpackSources(versionCombobox.getValue());
        applyCheckboxes(sources);

        installModloader.setSelected(ManageModpackManager.hasModLoader(modpack, versionCombobox.getValue()));
        installLiteLoader.setSelected(ManageModpackManager.hasLiteLoader(modpack, versionCombobox.getValue()));
        installOptiFine.setSelected(ManageModpackManager.hasOptiFine(modpack, versionCombobox.getValue()));

        ramWarning.setGraphic(null);
        if (modpack.getRam() == 0) {
            ramCombobox.getSelectionModel().selectFirst();
        } else {
            ramCombobox.getSelectionModel().selectLast();
            ramCombobox.setValue(modpack.getRam() + " МБ");
        }
    }

    private void applyCheckboxes(ManageModpackSources sources) {
        lockCheckBoxes();

        if (sources == null) {
            return;
        }

        if (sources.ModLoader != null) {
            installModloader.setDisable(false);
            installModloader.setText("Установить " + sources.ModLoaderName);
        }

        if (sources.LiteLoader != null) {
            installLiteLoader.setDisable(false);
        }

        if (sources.OptiFine != null) {
            installOptiFine.setDisable(false);
        }
    }

    private void displayWelcome(boolean isVisible) {
        if (!isVisible) {
            tooltipArea.setVisible(false);
            arrowArea.setVisible(false);
            createModpack.setVisible(false);
        } else {
            tooltipArea.setVisible(true);
            tooltipArea.getChildren().setAll(getToolTip());
            arrowArea.setVisible(true);
            arrowArea.getChildren().setAll(new ImageView(PictureManager.loadImage("arrow2.png")));
            createModpack.setVisible(true);
        }
    }

    private Label getToolTip() {
        Label toolTip = new Label();
        toolTip.setText("Пожалуйста, выберите сборку, чтобы управлять ею или создайте новую");
        toolTip.setWrapText(true);
        toolTip.setTextFill(Paint.valueOf("#727272"));
        toolTip.setFont(Font.font(15));
        toolTip.setTextAlignment(TextAlignment.CENTER);
        toolTip.setAlignment(Pos.CENTER);
        toolTip.setPrefWidth(tooltipArea.getPrefWidth());
        toolTip.setPrefHeight(tooltipArea.getPrefHeight());

        return toolTip;
    }

    public void onHelp_ManageModpacks(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_18");
    }
}
