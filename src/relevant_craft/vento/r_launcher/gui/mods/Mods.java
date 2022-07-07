package relevant_craft.vento.r_launcher.gui.mods;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.icons525.Icons525View;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.curseforge.*;
import relevant_craft.vento.r_launcher.manager.modpack.Modpack;
import relevant_craft.vento.r_launcher.manager.modpack.ManageModpackManager;
import relevant_craft.vento.r_launcher.manager.mod.LocalMod;
import relevant_craft.vento.r_launcher.manager.mod.LocalMod_ListCell;
import relevant_craft.vento.r_launcher.manager.mod.ModsManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Mods implements Initializable {

    public static AnchorPane loadModsWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane mods = VENTO.loadGUI(VENTO.GUI.Mods.path);
        mods.setLayoutX(width.getValue() / 2 - mods.getPrefWidth() / 2);
        mods.setLayoutY(height.getValue() / 2 - mods.getPrefHeight() / 2);

        return mods;
    }

    public void onClose_Mods(MouseEvent mouseEvent) {
        VENTO.closeGUI();

        ModsManager.clearCache();
    }

    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXListView installedMods;
    @FXML
    private FontAwesomeIconView modsFor;
    @FXML
    private JFXTextField search;
    @FXML
    private JFXListView webMods;
    @FXML
    private JFXComboBox<CF_Version> versionList;
    @FXML
    private JFXComboBox<CF_Category> categoryList;
    @FXML
    private FontAwesomeIconView searchIcon;
    @FXML
    private FontAwesomeIconView versionText;
    @FXML
    private FontAwesomeIconView categoryText;
    @FXML
    private JFXSpinner loading;
    @FXML
    private Label loadingText;
    @FXML
    private Separator separator;
    @FXML
    private Pane current_cf_project;

    @FXML
    private JFXComboBox<Modpack> modpackList;
    @FXML
    private JFXButton next;

    @FXML
    private Icons525View modpackTooltip;

    public void initialize(URL location, ResourceBundle resources) {
        VENTO.modsManager = new ModsManager(installedMods, modsFor, search, webMods, versionList, categoryList, searchIcon, versionText, categoryText, loading, loadingText, separator, current_cf_project);

        CF_Installer.current_cf_project = CF_Projects.Mods;

        versionList.setButtonCell(new ListCell<CF_Version>() {
            @Override
            protected void updateItem(CF_Version cf_version, boolean empty) {
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    setText(cf_version.getVersionName());
                }
            }
        });
        versionList.setCellFactory(param -> new JFXListCell<CF_Version>() {
            @Override
            protected void updateItem(CF_Version cf_version, boolean empty) {
                super.updateItem(cf_version, empty);
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else  {
                    setText(cf_version.getVersionName());
                }
            }
        });

        categoryList.setButtonCell(new ListCell<CF_Category>() {
            @Override
            protected void updateItem(CF_Category cf_category, boolean empty) {
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    setText(cf_category.getDisplayName().trim());
                }
            }
        });
        categoryList.setCellFactory(param -> new JFXListCell<CF_Category>() {
            @Override
            protected void updateItem(CF_Category cf_category, boolean empty) {
                super.updateItem(cf_category, empty);
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    setText(cf_category.getDisplayName());
                }
            }
        });

        search.textProperty().addListener((observable, oldValue, newValue) -> ModsManager.findModsByString(search.getText()));

        webMods.setCellFactory((Callback<ListView<CF_Project>, ListCell<CF_Project>>) param -> new CF_ListCell(search));
        webMods.setOnMouseClicked(event -> webMods.getSelectionModel().clearSelection());
        webMods.setFocusTraversable(false);

        installedMods.setCellFactory((Callback<ListView<LocalMod>, ListCell<LocalMod>>) param -> new LocalMod_ListCell());
        installedMods.setOnMouseClicked(event -> installedMods.getSelectionModel().clearSelection());
        installedMods.setFocusTraversable(false);

        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() == 1) {
                CF_Installer.current_modpack = null;
                if (current_cf_project.isVisible()) {
                    current_cf_project.setVisible(false);
                    current_cf_project.getChildren().clear();
                    CF_CurrentProject.killScreenshotLoader();
                    ModsManager.showElements();
                }
            }
        });
        tabPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
                event.consume();
            }
        });

        modpackList.getItems().setAll(ManageModpackManager.getModpacks());
        modpackList.getItems().add(0, new Modpack(SettingsManager.minecraftDirectory.substring(SettingsManager.minecraftDirectory.lastIndexOf('\\') + 1), null, SettingsManager.minecraftDirectory, 0, 0));
        modpackList.getItems().add(new Modpack("<Создать новую сборку>", null, null, 0, 0));
        modpackList.getSelectionModel().selectFirst();

        modpackList.setOnAction(e -> {
            if (modpackList.getSelectionModel().getSelectedIndex() == modpackList.getItems().size() - 1) {
                VENTO.startGUI(VENTO.GUI.ManageModpacks);
            }
        });

        modpackList.setButtonCell(new ListCell<Modpack>() {
            @Override
            protected void updateItem(Modpack modpack, boolean empty) {
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    if (modpack.getPath() == null || modpack.getPath().equals(SettingsManager.minecraftDirectory)) {
                        setText(modpack.getName());
                    } else {
                        setText("Сборка: " + modpack.getName() + " [" + modpack.getVersion() + "]");
                    }
                }
            }
        });
        modpackList.setCellFactory(param -> new JFXListCell<Modpack>() {
            @Override
            protected void updateItem(Modpack modpack, boolean empty) {
                super.updateItem(modpack, empty);
                setFont(Font.font(15));
                if (empty) {
                    setText(null);
                } else {
                    if (modpack.getPath() == null) {
                        setText(modpack.getName());
                        setAlignment(Pos.CENTER);
                        setFont(Font.font("System", FontPosture.ITALIC, getFont().getSize()));
                    } else if (modpack.getPath().equals(SettingsManager.minecraftDirectory)) {
                        setText(modpack.getName());
                    } else {
                        setText("Сборка: " + modpack.getName() + " [" + modpack.getVersion() + "]");
                    }
                }
            }
        });

        next.setOnAction(event -> {
            CF_Installer.current_modpack = modpackList.getValue();
            ModsManager.loadMods();
            ModsManager.loadLocalMods();
            tabPane.getSelectionModel().select(1);
        });

        TooltipUtils.addTooltip(modpackTooltip, "Сборка, в которую будут устанавливаться\nвсе моды. Создать свою сборку можно,\nнажав на кнопку «Менеджер сборок» в главном меню.", "НЕ рекомендуется использовать .minecraft");
    }

    public void onVersion_Choose(ActionEvent actionEvent) {
        search.setText(null);
        if (!categoryList.getItems().isEmpty()) {
            categoryList.setValue(categoryList.getItems().get(0));
        }
        ModsManager.loadModsForVersion(versionList.getValue().getVersionName());
    }

    public void onCategory_Choose(ActionEvent actionEvent) {
        search.setText(null);
        ModsManager.findModsByCategory(categoryList.getValue());
    }

    public void onHelp_Mods(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_19");
    }
}
