package relevant_craft.vento.r_launcher.gui.worlds;

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
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.world.LocalWorld;
import relevant_craft.vento.r_launcher.manager.world.LocalWorld_ListCell;
import relevant_craft.vento.r_launcher.manager.world.WorldManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Worlds implements Initializable {

    public static AnchorPane loadWorldsWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane worlds = VENTO.loadGUI(VENTO.GUI.Worlds.path);
        worlds.setLayoutX(width.getValue() / 2 - worlds.getPrefWidth() / 2);
        worlds.setLayoutY(height.getValue() / 2 - worlds.getPrefHeight() / 2);

        return worlds;
    }

    public void onClose_Worlds(MouseEvent mouseEvent) {
        VENTO.closeGUI();

        WorldManager.clearCache();
    }

    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXListView installedWorlds;
    @FXML
    private FontAwesomeIconView worldsFor;
    @FXML
    private JFXTextField search;
    @FXML
    private JFXListView webWorlds;
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
        VENTO.worldManager = new WorldManager(installedWorlds, worldsFor, search, webWorlds, versionList, categoryList, searchIcon, versionText, categoryText, loading, loadingText, separator, current_cf_project);

        CF_Installer.current_cf_project = CF_Projects.Worlds;

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

        search.textProperty().addListener((observable, oldValue, newValue) -> WorldManager.findWorldsByString(search.getText()));

        webWorlds.setCellFactory((Callback<ListView<CF_Project>, ListCell<CF_Project>>) param -> new CF_ListCell(search));
        webWorlds.setOnMouseClicked(event -> webWorlds.getSelectionModel().clearSelection());
        webWorlds.setFocusTraversable(false);

        installedWorlds.setCellFactory((Callback<ListView<LocalWorld>, ListCell<LocalWorld>>) param -> new LocalWorld_ListCell());
        installedWorlds.setOnMouseClicked(event -> installedWorlds.getSelectionModel().clearSelection());
        installedWorlds.setFocusTraversable(false);

        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() == 1) {
                CF_Installer.current_modpack = null;
                if (current_cf_project.isVisible()) {
                    current_cf_project.setVisible(false);
                    current_cf_project.getChildren().clear();
                    CF_CurrentProject.killScreenshotLoader();
                    WorldManager.showElements();
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
            WorldManager.loadWorlds();
            WorldManager.loadLocalWorlds();
            tabPane.getSelectionModel().select(1);
        });

        TooltipUtils.addTooltip(modpackTooltip, "Сборка, в которую будут устанавливаться\nвсе карты. Создать свою сборку можно,\nнажав на кнопку «Менеджер сборок» в главном меню.");
    }

    public void onVersion_Choose(ActionEvent actionEvent) {
        search.setText(null);
        if (!categoryList.getItems().isEmpty()) {
            categoryList.setValue(categoryList.getItems().get(0));
        }
        WorldManager.loadWorldsForVersion(versionList.getValue().getVersionName());
    }

    public void onCategory_Choose(ActionEvent actionEvent) {
        search.setText(null);
        WorldManager.findWorldsByCategory(categoryList.getValue());
    }

    public void onHelp_Worlds(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_21");
    }
}
