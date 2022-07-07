package relevant_craft.vento.r_launcher.gui.settings;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.icons525.Icons525View;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.background.BackgroundManager;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.updater.UpdaterManager;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;
import relevant_craft.vento.r_launcher.utils.OperatingSystem;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class Settings implements Initializable {

    public static AnchorPane loadSettingsWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane settings = VENTO.loadGUI(VENTO.GUI.Settings.path);
        settings.setLayoutX(width.getValue() / 2 - settings.getPrefWidth() / 2);
        settings.setLayoutY(height.getValue() / 2 - settings.getPrefHeight() / 2);

        return settings;
    }

    /*      General     */
    @FXML
    private JFXToggleButton useAutoBackground;
    @FXML
    private Icons525View autobackgroundTooltip;
    @FXML
    private JFXToggleButton useCustomBackground;
    @FXML
    private Icons525View custombackgroundTooltip;
    @FXML
    private JFXTextField customBackground;
    @FXML
    private JFXButton browseBackground;
    @FXML
    private JFXToggleButton useDeveloperConsole;
    @FXML
    private Icons525View consoleTooltip;
    @FXML
    private JFXToggleButton checkAssets;
    @FXML
    private Icons525View assetsTooltip;
    @FXML
    private JFXToggleButton enableAnalyzer;
    @FXML
    private Icons525View analyzerTooltip;
    @FXML
    private JFXComboBox language;

    /*      Minecraft       */
    @FXML
    private JFXTextField minecraftDirectory;
    @FXML
    private Icons525View minecraftdirectoryTooltip;
    @FXML
    private JFXButton browseDirectory;
    @FXML
    private JFXTextField clientWidth;
    @FXML
    private JFXTextField clientHeight;
    @FXML
    private Icons525View resolutionTooltip;
    @FXML
    private JFXToggleButton useFullScreen;
    @FXML
    private Icons525View fullscreenTooltip;
    @FXML
    private JFXCheckBox showSnapshots;
    @FXML
    private JFXCheckBox showBeta;
    @FXML
    private JFXCheckBox showAlpha;
    @FXML
    private JFXCheckBox showModifications;
    @FXML
    private JFXCheckBox showLegacy;
    @FXML
    private Icons525View versionsTooltip;
    @FXML
    private JFXTextField JVM_args;
    @FXML
    private Icons525View jvmargsTooltip;
    @FXML
    private JFXTextField Minecraft_args;
    @FXML
    private Icons525View minecraftargsTooltip;
    @FXML
    private JFXSlider sliderRAM;
    @FXML
    private JFXTextField RAM;
    @FXML
    private Icons525View ramTooltip;
    @FXML
    private Label ramWarning;

    /*      Additional      */
    @FXML
    private Icons525View resetsettingsTooltip;
    @FXML
    private JFXToggleButton optimizeSettings;
    @FXML
    private Icons525View optimizesettingsTooltip;
    @FXML
    private JFXToggleButton enableCheats;
    @FXML
    private Icons525View enableCheatsTooltip;
    @FXML
    private Separator separatorCheats;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        useSettings();
        initRamListener();
        checkRAM();

        TooltipUtils.addTooltip(autobackgroundTooltip, "При каждом запуске лаунчера,\nбудет загружаться случайная\nкартинка для фона.");
        TooltipUtils.addTooltip(custombackgroundTooltip, "Установить свой фон в лаунчер.", "Сначала нужно отключить\n«Автоматический фон»");
        TooltipUtils.addTooltip(consoleTooltip, "Включить консоль для Minecraft.");
        TooltipUtils.addTooltip(assetsTooltip, "Включить проверку и скачивание\nзвуков, языков Minecraft\nперед каждым запуском игры.", "Рекомендуется включить!");
        TooltipUtils.addTooltip(analyzerTooltip, "Включить анализатор ошибок,\nкоторый после завершения Minecraft с ошибками\nбудет предлагать их решение.", "Рекомендуется включить!");
        TooltipUtils.addTooltip(minecraftdirectoryTooltip, "Директория, в которой будет\nзапускаться Minecraft. Здесь\nхранятся .jar файлы, миры,\nресурс-паки, звуки.");
        TooltipUtils.addTooltip(resolutionTooltip, "Minecraft будет запускаться\nс указанной шириной и высотой.");
        TooltipUtils.addTooltip(fullscreenTooltip, "Minecraft будет запускаться\nв полноэкранном режиме.");
        TooltipUtils.addTooltip(versionsTooltip, "Версии Minecraft, которые будут\nотображены в выпадающем списке\nв главном меню лаунчера.");
        TooltipUtils.addTooltip(jvmargsTooltip, "Аргументы виртуальной машины Java (JVM)\nиспользуются, чтобы сконфигурировать и\nскорректировать то, как JVM будет работать в игре.", "Использовать с осторожностью!");
        TooltipUtils.addTooltip(minecraftargsTooltip, "Аргументы, которые будут\nизменять параметры Minecraft.");
        TooltipUtils.addTooltip(ramTooltip, "Количество выделяемой оперативной памяти для Minecraft.", "Использовать с осторожностью!");
        TooltipUtils.addTooltip(resetsettingsTooltip, "Все настройки лаунчера будут\nвозвращены в первоначальное состояние.");
        TooltipUtils.addTooltip(optimizesettingsTooltip, "В зависимости от устройства,\nможет повысить производительность\nигры или, наоборот, уменьшить.");
        TooltipUtils.addTooltip(enableCheatsTooltip, "В главном меню лаунчера будет\nдобавлена кнопка «Читы»,\nгде можно установить любые читы.");
    }

    public void onClose_Settings(MouseEvent mouseEvent) {
        applySettings();
        SettingsManager.checkMinecraftDirectory();
        SettingsManager.saveSettings();
        VersionManager.setVersions();
        VENTO.closeGUI();
    }

    private void applySettings() {
        SettingsManager.useAutoBackground = useAutoBackground.isSelected();
        SettingsManager.useCustomBackground = useCustomBackground.isSelected();
        SettingsManager.customBackground = customBackground.getText();
        SettingsManager.useDeveloperConsole = useDeveloperConsole.isSelected();
        SettingsManager.checkAssets = checkAssets.isSelected();
        SettingsManager.enableAnalyzer = enableAnalyzer.isSelected();
        //SettingsManager.language = language.getPromptText();
        SettingsManager.minecraftDirectory = minecraftDirectory.getText();
        SettingsManager.clientWidth = Integer.valueOf(clientWidth.getText());
        SettingsManager.clientHeight = Integer.valueOf(clientHeight.getText());
        SettingsManager.useFullScreen = useFullScreen.isSelected();
        SettingsManager.showSnapshots = showSnapshots.isSelected();
        SettingsManager.showBeta = showBeta.isSelected();
        SettingsManager.showAlpha = showAlpha.isSelected();
        SettingsManager.showModifications = showModifications.isSelected();
        SettingsManager.showLegacy = showLegacy.isSelected();
        SettingsManager.JVM_args = JVM_args.getText();
        SettingsManager.Minecraft_args = Minecraft_args.getText();
        SettingsManager.RAM = Integer.valueOf(RAM.getText());
        SettingsManager.optimizeSettings = optimizeSettings.isSelected();
        SettingsManager.enableCheats = enableCheats.isSelected();
    }

    private void useSettings() {
        useAutoBackground.setSelected(SettingsManager.useAutoBackground);
        useCustomBackground.setSelected(SettingsManager.useCustomBackground);
        customBackground.setText(SettingsManager.customBackground);
        useDeveloperConsole.setSelected(SettingsManager.useDeveloperConsole);
        checkAssets.setSelected(SettingsManager.checkAssets);
        enableAnalyzer.setSelected(SettingsManager.enableAnalyzer);
        //language.setPromptText(SettingsManager.language);

        //Rules
        useAutoBackground.setDisable(useCustomBackground.isSelected());
        useCustomBackground.setDisable(useAutoBackground.isSelected());
        customBackground.setDisable(!useCustomBackground.isSelected());
        browseBackground.setDisable(!useCustomBackground.isSelected());

        optimizeSettings.setSelected(SettingsManager.optimizeSettings);

        minecraftDirectory.setText(SettingsManager.minecraftDirectory);
        clientWidth.setText(String.valueOf(SettingsManager.clientWidth));
        clientHeight.setText(String.valueOf(SettingsManager.clientHeight));
        useFullScreen.setSelected(SettingsManager.useFullScreen);
        showSnapshots.setSelected(SettingsManager.showSnapshots);
        showBeta.setSelected(SettingsManager.showBeta);
        showAlpha.setSelected(SettingsManager.showAlpha);
        showModifications.setSelected(SettingsManager.showModifications);
        showLegacy.setSelected(SettingsManager.showLegacy);
        JVM_args.setText(SettingsManager.JVM_args);
        Minecraft_args.setText(SettingsManager.Minecraft_args);
        sliderRAM.setValue(SettingsManager.RAM);
        RAM.setText(String.valueOf(SettingsManager.RAM));

        enableCheats.setSelected(SettingsManager.enableCheats);
        if (!UpdaterManager.SHOW_CHEATS) {
            enableCheats.setVisible(false);
            enableCheatsTooltip.setVisible(false);
            separatorCheats.setVisible(false);
        }
    }

    /**********************************************
     *                 BACKGROUND                 *
     **********************************************/
    public void onChooseBackground(MouseEvent mouseEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите картинку для фона");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pictures", "*.png", "*.jpg", "*.jpeg"));

        File file = fc.showOpenDialog(VENTO.window);
        if (file != null) {
            customBackground.setText(file.getAbsolutePath());
            SettingsManager.customBackground = file.getAbsolutePath();
            BackgroundManager.setCustomBackground();
        }
    }

    public void onClick_useAutoBackground(ActionEvent actionEvent) {
        useCustomBackground.setDisable(useAutoBackground.isSelected());
        if (useAutoBackground.isSelected()) {
            BackgroundManager.setAutoBackground();
        } else {
            BackgroundManager.setDefaultBackground();
        }
    }

    public void onClick_useCustomBackground(ActionEvent actionEvent) {
        useAutoBackground.setDisable(useCustomBackground.isSelected());
        customBackground.setDisable(!useCustomBackground.isSelected());
        browseBackground.setDisable(!useCustomBackground.isSelected());
        if (useCustomBackground.isSelected()) {
            if (!SettingsManager.customBackground.isEmpty()) {
                BackgroundManager.setCustomBackground();
            }
        } else {
            BackgroundManager.setDefaultBackground();
        }
    }

    /**********************************************
     *            MINECRAFT DIRECTORY             *
     **********************************************/
    public void onChooseDirectory(MouseEvent mouseEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Выберите папку для Minecraft");
        dc.setInitialDirectory(new File(OperatingSystem.getCurrentPlatform().getAppdataDir()));

        File dir = dc.showDialog(VENTO.window);
        if (dir != null) {
            minecraftDirectory.setText(dir.getAbsolutePath());
            SettingsManager.minecraftDirectory = dir.getAbsolutePath();
            VersionManager.loadVersions();
        }
    }

    /**********************************************
     *                 RAM SLIDER                 *
     **********************************************/
    public void onSliderScroll(MouseEvent mouseEvent) {
        final int tick = (int) sliderRAM.getMajorTickUnit();
        int current = (int) sliderRAM.getValue();

        if (current % tick != 0) {
            int minTick = getNearestTick(current, tick, false);
            int maxTick = getNearestTick(current, tick, true);
            if ((maxTick - current) < (current - minTick)) {
                sliderRAM.setValue(maxTick);
            } else {
                sliderRAM.setValue(minTick);
            }
        }

        RAM.setText(String.valueOf((int) sliderRAM.getValue()));
        checkRAM();
    }

    private static int getNearestTick(int current, int tick, boolean isMax) {
        while (true) {
            if (isMax) {
                current++;
            } else {
                current--;
            }
            if (current % tick == 0) {
                return current;
            }
        }
    }

    private void initRamListener() {
        RAM.textProperty().addListener((observable, oldValue, newValue) -> {
            int ram;
            try {
                ram = Integer.valueOf(RAM.getText());
            } catch (Exception e) {
                return;
            }

            checkRAM();
            sliderRAM.setValue(ram);
        });
    }

    private void checkRAM() {
        int ram;
        try {
            ram = Integer.valueOf(RAM.getText());
        } catch (Exception e) {
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

    /**********************************************
     *               RESET SETTINGS               *
     **********************************************/
    public void onClick_DefaultSettings(ActionEvent actionEvent) {
        NotifyWindow notify = new NotifyWindow(NotifyType.QUESTION);
        notify.setTitle("Подтвердите действие");
        notify.setMessage("Вы действительно хотите `сбросить все настройки` лаунчера?\nОтменить данное действие нельзя.");
        notify.setYesOrNo(true);
        notify.showNotify();
        if (notify.getAnswer()) {
            SettingsManager.deleteSettings();
            SettingsManager.loadSettings();
            useSettings();
        }
    }

    /**********************************************
     *              OPTIMIZE SETTINGS             *
     **********************************************/
    public void onClick_optimizeSettings(ActionEvent actionEvent) {
        SettingsManager.optimizeSettings = optimizeSettings.isSelected();
        if (optimizeSettings.isSelected()) {
            SettingsManager.optimizeSettings();
        } else {
            SettingsManager.removeOptimizedSettings();
            SettingsManager.loadSettings();
        }
        useSettings();
    }

    //Input Checker
    public void checkInput(KeyEvent keyEvent) {
        try {
            Integer.valueOf(keyEvent.getCharacter());
            JFXTextField target = (JFXTextField) keyEvent.getTarget();
            if (target.getText().length() >= 4) {
                if (!target.getSelectedText().equals(target.getText())) {
                    keyEvent.consume();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            keyEvent.consume();
        }
    }

    /**********************************************
     *                ENABLE CHEATS               *
     **********************************************/
    public void onClick_enableCheats(ActionEvent actionEvent) {
        SettingsManager.enableCheats = enableCheats.isSelected();
        LauncherManager.reinitSideMenu();
    }

    public void onHelp_Settings(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_8");
    }
}
