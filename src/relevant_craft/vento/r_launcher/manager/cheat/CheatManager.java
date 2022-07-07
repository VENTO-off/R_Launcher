package relevant_craft.vento.r_launcher.manager.cheat;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CheatManager {

    private static JFXListView<Cheat> cheatsList;
    private static JFXComboBox<CheatVersion> versionList;
    private static AnchorPane imageArea;
    private static JFXSpinner imageLoading;
    private static Label cheatLoadingText;
    private static Label descriptionArea;
    private static JFXButton actionButton;
    private static JFXProgressBar cheatProgressbar;
    private static Label progressLabel;
    private static Separator separator;
    private static Separator separator2;
    private static Separator separator3;
    private static FontAwesomeIconView versionList_Text;
    private static AnchorPane arrowArea;
    private static JFXSpinner loading;
    private static FontAwesomeIconView loadingText;
    private static FontAwesomeIconView cheatsFor;
    private static FontAwesomeIconView searchIcon;
    private static JFXTextField search;
    private static Pane cheatNameLayout;
    private static Label cheatName;

    public CheatManager(JFXListView<Cheat> _cheatsList, JFXComboBox _versionList, AnchorPane _imageArea, JFXSpinner _imageLoading, Label _cheatLoadingText, Label _descriptionArea, JFXButton _actionButton, JFXProgressBar _cheatProgressbar, Label _progressLabel, Separator _separator, Separator _separator2, Separator _separator3, FontAwesomeIconView _versionList_Text, JFXSpinner _loading, FontAwesomeIconView _loadingText, AnchorPane _arrowArea, FontAwesomeIconView _cheatsFor, FontAwesomeIconView _searchIcon, JFXTextField _search, Pane _cheatNameLayout, Label _cheatName) {
        cheatsList = _cheatsList;
        versionList = _versionList;
        imageArea = _imageArea;
        imageLoading = _imageLoading;
        cheatLoadingText = _cheatLoadingText;
        descriptionArea = _descriptionArea;
        actionButton = _actionButton;
        cheatProgressbar = _cheatProgressbar;
        progressLabel = _progressLabel;
        separator = _separator;
        separator2 = _separator2;
        separator3 = _separator3;
        versionList_Text = _versionList_Text;
        arrowArea = _arrowArea;
        loading = _loading;
        loadingText = _loadingText;
        cheatsFor = _cheatsFor;
        searchIcon = _searchIcon;
        search = _search;
        cheatNameLayout = _cheatNameLayout;
        cheatName = _cheatName;
    }

    private static List<Cheat> cheats = new ArrayList<>();

    public static void loadCheats() {
        hideButtons();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getCheats();

                Platform.runLater(() -> {
                    setCheatVersions();
                    showButtons();
                });

                return null;
            }

        };
        new Thread(task).start();
    }

    private static void hideButtons() {
        cheatsList.setVisible(false);
        versionList.setVisible(false);
        imageArea.setVisible(false);
        descriptionArea.setVisible(false);
        actionButton.setVisible(false);
        cheatProgressbar.setVisible(false);
        progressLabel.setVisible(false);
        separator.setVisible(false);
        separator2.setVisible(false);
        separator3.setVisible(false);
        versionList_Text.setVisible(false);
        arrowArea.setVisible(false);
        cheatsFor.setVisible(false);
        searchIcon.setVisible(false);
        search.setVisible(false);
        cheatNameLayout.setVisible(false);

        loading.setVisible(true);
        loadingText.setVisible(true);
        loadingText.setText("Загрузка списка читов...");
    }

    private static void showButtons() {
        cheatsList.setVisible(true);
        versionList.setVisible(true);
        imageArea.setVisible(true);
        imageArea.getChildren().setAll(getToolTip());
        descriptionArea.setVisible(true);
        separator2.setVisible(true);
        separator3.setVisible(true);
        versionList_Text.setVisible(true);
        arrowArea.setVisible(true);
        arrowArea.getChildren().setAll(new ImageView(PictureManager.loadImage("arrow.png")));
        cheatsFor.setVisible(true);
        searchIcon.setVisible(true);
        search.setVisible(true);

        loading.setVisible(false);
        loadingText.setVisible(false);
    }

    private static void getCheats() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (!cheats.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + "cheats.php");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка подключения");
            notify.setMessage("При попытке получить список читов произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
            notify.showNotify();
            return;
        }

        String[] cheats_list = response.split("<::>");
        for (int i = 0; i < cheats_list.length; i++) {
            String current = cheats_list[i];

            String name = current.substring(current.indexOf('/') + 1).replace('/', ' ');
            String url = current;
            String path = SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + name;
            String version = current.substring(current.lastIndexOf('/') + 1);
            boolean isLocal = isCheatLocal(name, path);
            String jar = VENTO.REPOSITORY + url + '/' + name + ".jar";
            String json = VENTO.REPOSITORY + url + '/' + name + ".json";
            String extra = VENTO.REPOSITORY + url + '/' + "extra.zip";
            String description = VENTO.REPOSITORY + current.substring(0, current.lastIndexOf('/')) + '/' + "description.txt";
            String picture = VENTO.REPOSITORY + current.substring(0, current.lastIndexOf('/')) + '/' + "picture.png";

            cheats.add(new Cheat(name, url, path, version, isLocal, jar, json, extra, description, picture));
        }
    }

    private static boolean isCheatLocal(String name, String path) {
        File jar = new File(path + File.separator + name + ".jar");
        File json = new File(path + File.separator + name + ".json");
        if (jar.exists() && json.exists()) {
            return true;
        }

        return false;
    }

    public static void editCheatLocal(Cheat cheat) {
        for (Cheat ch : cheats) {
            if (cheat == ch) {
                ch.setLocal(!ch.isLocal());
            }
        }
    }

    private static void setCheatVersions() {
        List<CheatVersion> list = new ArrayList<>();
        for (Cheat cheat : cheats) {
            if (!containsCheatVersion(list, cheat.getVersion())) {
                list.add(new CheatVersion(cheat.getVersion(), getCheatsByVersion(cheat.getVersion()).size()));
            }
        }

        versionList.getItems().setAll(CheatSorter.ShellSort(list, 2));
        versionList.getItems().setAll(CheatSorter.ShellSort(list, 1));
        versionList.getItems().setAll(CheatSorter.ShellSort(list, 0));
        versionList.getItems().add(0, new CheatVersion("<все версии>", cheats.size()));
        versionList.setValue(new CheatVersion("<не выбрано>", 0));
    }

    private static boolean containsCheatVersion(List<CheatVersion> list, String version) {
        for (CheatVersion cv : list) {
            if (cv.getVersion().equals(version)) {
                return true;
            }
        }

        return false;
    }

    public static List<Cheat> getCheatsByVersion(String version) {
        if (version.equalsIgnoreCase("<не выбрано>")) {
            return new ArrayList<>();
        }

        if (version.equalsIgnoreCase("<все версии>")) {
            return cheats;
        }

        List<Cheat> list = new ArrayList<>();
        for (Cheat cheat : cheats) {
            if (cheat.getVersion().equals(version)) {
                list.add(cheat);
            }
        }

        return list;
    }

    public static void findCheatsByString(String find) {
        if (find == null) {
            return;
        }

        List<Cheat> filtered = new ArrayList<>();
        for (Cheat cheat : cheats) {
            if (cheat.getName().toLowerCase().contains(find.toLowerCase())) {
                filtered.add(cheat);
            }
        }

        setCheatsList(filtered, true);
        versionList.getSelectionModel().selectFirst();
        search.setText(find);
    }

    public static void setCheatsList(List<Cheat> newCheats, boolean doScroll) {
        cheatsList.getItems().setAll(newCheats);
        cheatsFor.setText("Найдено " + cheatsList.getItems().size() + " читов:");
        if (doScroll) {
            cheatsList.scrollTo(0);
        }
    }

    private static String loadCheatDescription(Cheat cheat) {
        String response;
        try {
            URL url = new URL(cheat.getDescription());
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            return "#error";
        }

        return response;
    }

    public static void loadCheatInfo(Cheat cheat) {
        descriptionArea.setText("");
        imageArea.getChildren().clear();
        arrowArea.setVisible(false);
        imageLoading.setVisible(true);
        cheatLoadingText.setVisible(true);
        cheatLoadingText.setText("Загрузка информации о чите...");
        descriptionArea.setVisible(false);
        actionButton.setVisible(false);
        separator.setVisible(false);
        cheatNameLayout.setVisible(false);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                Image image = new Image(cheat.getPicture());
                ImageView imageView = new ImageView();
                imageView.setImage(image);
                imageView.setFitWidth(350);
                imageView.setFitHeight(220);

                String description = loadCheatDescription(cheat);

                Platform.runLater(() -> {
                    imageLoading.setVisible(false);
                    cheatLoadingText.setVisible(false);
                    imageArea.getChildren().setAll(imageView);
                    descriptionArea.setVisible(true);
                    descriptionArea.setText(description.replace("\\n", System.lineSeparator()));
                    if (cheat.isLocal()) {
                        actionButton.setText("Удалить");
                        actionButton.setStyle("-fx-background-color: #F26B50");
                        actionButton.setRipplerFill(Paint.valueOf("#ff9b8b"));
                    } else {
                        actionButton.setText("Установить");
                        actionButton.setStyle("-fx-background-color: #7FCF3D");
                        actionButton.setRipplerFill(Paint.valueOf("#beff85"));
                    }
                    actionButton.setVisible(true);
                    separator.setVisible(true);
                    cheatNameLayout.setVisible(true);
                    cheatName.setText(cheat.getName());
                });

                return null;
            }

        };
        new Thread(task).start();
    }

    public static Label getToolTip() {
        Label toolTip = new Label();
        toolTip.setText("Пожалуйста, выберите версию и чит, чтобы начать установку");
        toolTip.setWrapText(true);
        toolTip.setTextFill(Paint.valueOf("#727272"));
        toolTip.setFont(Font.font(15));
        toolTip.setTextAlignment(TextAlignment.CENTER);
        toolTip.setAlignment(Pos.CENTER);
        toolTip.setPrefWidth(imageArea.getPrefWidth());
        toolTip.setPrefHeight(imageArea.getPrefHeight());

        return toolTip;
    }
}
