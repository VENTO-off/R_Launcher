package relevant_craft.vento.r_launcher.gui.analyzer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.analyzer.AnalyzerManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Analyzer implements Initializable {

    public static AnchorPane loadAnalyzerWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane analyzer = VENTO.loadGUI(VENTO.GUI.Analyzer.path);
        analyzer.setLayoutX(width.getValue() / 2 - analyzer.getPrefWidth() / 2);
        analyzer.setLayoutY(height.getValue() / 2 - analyzer.getPrefHeight() / 2);

        return analyzer;
    }

    @FXML
    private JFXSpinner loading;
    @FXML
    private FontAwesomeIconView loadingText;
    @FXML
    private Label errorLogName;
    @FXML
    private TextArea errorLogText;
    @FXML
    private Label descriptionName;
    @FXML
    private ScrollPane descriptionText;
    @FXML
    private JFXButton openUrl;

    public void onClose_Analyzer(MouseEvent mouseEvent) {
        AnalyzerManager.resetErrorLogs();
        VENTO.analyzerManager = null;
        VENTO.closeGUI();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VENTO.analyzerManager = new AnalyzerManager(loading, loadingText, errorLogName, errorLogText, descriptionName, descriptionText, openUrl);
        errorLogText.setContextMenu(getContextMenu());

        AnalyzerManager.analyzeErrorLog();
    }

    public ContextMenu getContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem copy = new MenuItem("Копировать");
        copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copy.setOnAction(event -> {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection strSel = new StringSelection(errorLogText.getSelectedText());
            clipboard.setContents(strSel, null);
        });
        menu.getItems().add(copy);

        menu.getItems().add(new SeparatorMenuItem());

        MenuItem copy_all = new MenuItem("Копировать всё");
        //copy_all.setAccelerator(KeyCombination.keyCombination("Ctrl+A+C"));
        copy_all.setOnAction(event -> {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection strSel = new StringSelection(errorLogText.getText());
            clipboard.setContents(strSel, null);
        });
        menu.getItems().add(copy_all);

        MenuItem select_all = new MenuItem("Выделить всё");
        select_all.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        select_all.setOnAction(event -> {
            errorLogText.selectAll();
        });
        menu.getItems().add(select_all);

        menu.getItems().add(new SeparatorMenuItem());

        MenuItem save = new MenuItem("Сохранить в файл");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        save.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Сохранить логи");
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            fc.setInitialFileName("error_log.txt");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT", "*.txt");
            fc.getExtensionFilters().add(extFilter);

            File file = fc.showSaveDialog(VENTO.console);
            if (file != null) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(errorLogText.getText());
                    fileWriter.close();
                } catch (IOException ex) {
                }
            }
        });
        menu.getItems().add(save);

        return menu;
    }
}
