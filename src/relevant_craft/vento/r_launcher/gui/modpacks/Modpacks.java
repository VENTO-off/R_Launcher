package relevant_craft.vento.r_launcher.gui.modpacks;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.curseforge.*;
import relevant_craft.vento.r_launcher.manager.modpack.Modpack;
import relevant_craft.vento.r_launcher.manager.modpack.ModpackManager;
import relevant_craft.vento.r_launcher.manager.modpack.Modpack_ListCell;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Modpacks implements Initializable {

    public static AnchorPane loadModpacksWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane modpacks = VENTO.loadGUI(VENTO.GUI.Modpacks.path);
        modpacks.setLayoutX(width.getValue() / 2 - modpacks.getPrefWidth() / 2);
        modpacks.setLayoutY(height.getValue() / 2 - modpacks.getPrefHeight() / 2);

        return modpacks;
    }

    public void onClose_Modpacks(MouseEvent mouseEvent) {
        VENTO.closeGUI();

        ModpackManager.clearCache();
    }

    @FXML
    private JFXListView installedModpacks;
    @FXML
    private FontAwesomeIconView modpacksFor;
    @FXML
    private JFXTextField search;
    @FXML
    private JFXListView webModpacks;
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

    public void initialize(URL location, ResourceBundle resources) {
        VENTO.modpackManager = new ModpackManager(installedModpacks, modpacksFor, search, webModpacks, versionList, categoryList, searchIcon, versionText, categoryText, loading, loadingText, separator, current_cf_project);

        CF_Installer.current_cf_project = CF_Projects.ModPacks;

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

        search.textProperty().addListener((observable, oldValue, newValue) -> ModpackManager.findModpacksByString(search.getText()));

        webModpacks.setCellFactory((Callback<ListView<CF_Project>, ListCell<CF_Project>>) param -> new CF_ListCell(search));
        webModpacks.setOnMouseClicked(event -> webModpacks.getSelectionModel().clearSelection());
        webModpacks.setFocusTraversable(false);

        installedModpacks.setCellFactory((Callback<ListView<Modpack>, ListCell<Modpack>>) param -> new Modpack_ListCell());
        installedModpacks.setOnMouseClicked(event -> installedModpacks.getSelectionModel().clearSelection());
        installedModpacks.setFocusTraversable(false);

        CF_Installer.current_modpack = null;
        ModpackManager.loadModpacks();
        ModpackManager.loadLocalModpacks();
        ModpackManager.setInstalledModpacksList();
    }

    public void onVersion_Choose(ActionEvent actionEvent) {
        search.setText(null);
        if (!categoryList.getItems().isEmpty()) {
            categoryList.setValue(categoryList.getItems().get(0));
        }
        ModpackManager.loadModpacksForVersion(versionList.getValue().getVersionName());
    }

    public void onCategory_Choose(ActionEvent actionEvent) {
        search.setText(null);
        ModpackManager.findModpacksByCategory(categoryList.getValue());
    }

    public void onHelp_Modpacks(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_31");
    }
}
