package relevant_craft.vento.r_launcher.gui.console;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.console.ConsoleManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Console implements Initializable {

    public static void showConsole() {
        if (VENTO.console != null /*&& !SettingsManager.closeConsole*/) {
            return;
        }

        VENTO.console = new Stage();
        VENTO.console.getIcons().setAll(PictureManager.loadImage("console.png"));

        AnchorPane console = VENTO.loadGUI(VENTO.GUI.Console.path);
        VENTO.console.setTitle("Консоль разработчика");
        VENTO.console.setScene(new Scene(console));
        VENTO.console.setX(20);
        VENTO.console.setY(20);
        VENTO.console.show();

        VENTO.console.setOnCloseRequest(event -> {
            VENTO.console = null;
        });
    }

    @FXML
    private TextArea logsArea;
    @FXML
    private JFXButton killMinecraft;
    @FXML
    private JFXCheckBox closeConsole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VENTO.consoleManager = new ConsoleManager(logsArea, killMinecraft);
        closeConsole.setSelected(SettingsManager.closeConsole);
        logsArea.setContextMenu(getContextMenu());
        //logsArea.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            //new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(logsArea.scrollTopProperty(), Double.MAX_VALUE))).play();
            //logsArea.setScrollLeft(0);
        //});
    }

    public void onMinecraft_Kill(ActionEvent actionEvent) {
        ConsoleManager.killProcess();
    }

    public void onCloseConsole_Click(ActionEvent actionEvent) {
        SettingsManager.closeConsole = closeConsole.isSelected();

        SettingsManager.saveSettings();
    }

    public ContextMenu getContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem copy = new MenuItem("Копировать");
        copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copy.setOnAction(event -> {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection strSel = new StringSelection(logsArea.getSelectedText());
            clipboard.setContents(strSel, null);
        });
        menu.getItems().add(copy);

        menu.getItems().add(new SeparatorMenuItem());

        MenuItem copy_all = new MenuItem("Копировать всё");
        //copy_all.setAccelerator(KeyCombination.keyCombination("Ctrl+A+C"));
        copy_all.setOnAction(event -> {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection strSel = new StringSelection(logsArea.getText());
            clipboard.setContents(strSel, null);
        });
        menu.getItems().add(copy_all);

        MenuItem select_all = new MenuItem("Выделить всё");
        select_all.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        select_all.setOnAction(event -> {
            logsArea.selectAll();
        });
        menu.getItems().add(select_all);

        menu.getItems().add(new SeparatorMenuItem());

        MenuItem save = new MenuItem("Сохранить в файл");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        save.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Сохранить логи");
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            fc.setInitialFileName("logs.txt");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT", "*.txt");
            fc.getExtensionFilters().add(extFilter);

            File file = fc.showSaveDialog(VENTO.console);
            if (file != null) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(logsArea.getText());
                    fileWriter.close();
                } catch (IOException ex) {
                }
            }
        });
        menu.getItems().add(save);

        menu.getItems().add(new SeparatorMenuItem());

        MenuItem clear = new MenuItem("Очистить");
        clear.setOnAction(event -> {
            logsArea.clear();
        });
        menu.getItems().add(clear);

        return menu;
    }
}
