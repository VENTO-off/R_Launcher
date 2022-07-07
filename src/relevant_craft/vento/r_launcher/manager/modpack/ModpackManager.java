package relevant_craft.vento.r_launcher.manager.modpack;

import com.google.gson.*;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.curseforge.*;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.extract.ExtractManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModpackManager {

    private static JFXListView installedModpacks;
    private static FontAwesomeIconView modpacksFor;
    private static JFXTextField search;
    private static JFXListView webModpacks;
    private static JFXComboBox<CF_Version> versionList;
    private static JFXComboBox<CF_Category> categoryList;
    private static FontAwesomeIconView searchIcon;
    private static FontAwesomeIconView versionText;
    private static FontAwesomeIconView categoryText;
    private static JFXSpinner loading;
    private static Label loadingText;
    private static Separator separator;
    public static Pane current_cf_project;

    public ModpackManager(JFXListView _installedModpacks, FontAwesomeIconView _modpacksFor, JFXTextField _search, JFXListView _webModpacks, JFXComboBox _versionList, JFXComboBox _categoryList, FontAwesomeIconView _searchIcon, FontAwesomeIconView _versionText, FontAwesomeIconView _categoryText, JFXSpinner _loading, Label _loadingText, Separator _separator, Pane _current_cf_project) {
        installedModpacks = _installedModpacks;
        modpacksFor = _modpacksFor;
        search = _search;
        webModpacks = _webModpacks;
        versionList = _versionList;
        categoryList = _categoryList;
        searchIcon = _searchIcon;
        versionText = _versionText;
        categoryText = _categoryText;
        loading = _loading;
        loadingText = _loadingText;
        separator = _separator;
        current_cf_project = _current_cf_project;
    }

    private static List<CF_Project> all_modpacks = new ArrayList<>();
    private static List<CF_Project> modpacks = new ArrayList<>();
    private static List<CF_Version> modpacks_version = new ArrayList<>();
    private static List<CF_Category> modpacks_category = new ArrayList<>();
    private static List<Modpack> local_modpacks = new ArrayList<>();

    private static String current_version;

    public static void hideElements(String msg) {
        modpacksFor.setVisible(false);
        searchIcon.setVisible(false);
        search.setVisible(false);
        webModpacks.setVisible(false);
        versionText.setVisible(false);
        versionList.setVisible(false);
        categoryText.setVisible(false);
        categoryList.setVisible(false);
        separator.setVisible(false);

        if (msg != null) {
            loading.setVisible(true);
            loadingText.setVisible(true);
            loadingText.setText(msg);
        }
    }

    public static void showElements() {
        modpacksFor.setVisible(true);
        searchIcon.setVisible(true);
        search.setVisible(true);
        webModpacks.setVisible(true);
        versionText.setVisible(true);
        versionList.setVisible(true);
        categoryText.setVisible(true);
        categoryList.setVisible(true);
        separator.setVisible(true);

        loading.setVisible(false);
        loadingText.setVisible(false);
        loadingText.setText(null);
    }

    public static void loadModpacks() {
        ModpackManager.hideElements("Загрузка списка модпаков...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getVersions();
                getCategories();

                Platform.runLater(() -> {
                    versionList.getItems().setAll(modpacks_version);
                    versionList.setValue(new CF_Version("<не выбрано>"));
                    categoryList.getItems().setAll(modpacks_category);
                    categoryList.setValue(categoryList.getItems().get(0));
                    ModpackManager.showElements();
                });

                return null;
            }
        };
        new Thread(task).start();
    }

    private static void getVersions() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (!modpacks_version.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.ModPacks.getRLauncher_url() + "versions.txt");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка подключения");
            notify.setMessage("При попытке получить список версий произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
            notify.showNotify();
            return;
        }

        ManageModpackManager.getModpackSources();

        String[] modpacks_version_list = response.split("<::>");
        for (String version : modpacks_version_list) {
            if (VersionManager.getVersionByName(version) != null) {
                //if (!ManageModpackManager.getModificationsForVersion(VersionManager.getVersionByName(version)).isEmpty()) {
                    if (ManageModpackManager.getModLoaderForVersion(VersionManager.getVersionByName(version)) != null) {
                        modpacks_version.add(new CF_Version(version));
                    }
                //}
            }
        }
    }

    private static void getCategories() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (!modpacks_category.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.ModPacks.getRLauncher_url() + "categories.txt");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка подключения");
            notify.setMessage("При попытке получить список категорий произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
            notify.showNotify();
            return;
        }

        String[] modpacks_categories_list = response.split("<::>");
        modpacks_category.add(new CF_Category("Все категории", "all"));
        for (int i = 0; i < modpacks_categories_list.length; i++) {
            String[] current = modpacks_categories_list[i].split("=");
            modpacks_category.add(new CF_Category(current[1], current[0]));
        }
    }

    private static JsonObject jsonObject = new JsonObject();

    public static void loadModpacksForVersion(String versionName) {
        ModpackManager.hideElements("Загрузка модпаков для " + versionName + "...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                ModpackManager.getModpacks(versionList.getValue().getVersionName());
                Platform.runLater(ModpackManager::showElements);
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void getModpacks(String versionName) {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (versionName.equals("<не выбрано>")) {
            return;
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement element;

        String url = VENTO.WEB + CF_Projects.ModPacks.getRLauncher_url() + versionName;
        File cache = new File(VENTO.LAUNCHER_DIR + File.separator + "cf-cache");
        if (!cache.exists()) { cache.mkdir(); }

        File archive = new File(cache + File.separator + versionName + ".tar.gz");
        File file = new File(cache + File.separator + versionName + ".json");
        try {
            DownloadManager.downloadFile(url + ".tar.gz", archive.getAbsolutePath(), true);
            ExtractManager.extractTarGZ(archive, file);

            FileReader fileReader = new FileReader(file);
            element = parser.parse(fileReader);

            fileReader.close();
            file.delete();
            archive.delete();
        } catch (Exception e) {
            String response;
            try {
                if (!DownloadManager.existsUrlFile(url + ".json")) {
                    all_modpacks.clear();
                    Platform.runLater(ModpackManager::setModpacksList);
                    return;
                }
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new URL(url + ".json").openStream()));
                response = bufferedreader.readLine();
                bufferedreader.close();
            } catch (IOException e2) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка подключения");
                    notify.setMessage("При попытке получить список модпаков произошла ошибка.\nПожалуйста, проверьте подключение к сети\nили обратитесь в тех.поддержку лаунчера.");
                    notify.showNotify();
                });
                return;
            }
            element = parser.parse(response);
        }

        jsonObject = element.getAsJsonObject();

        all_modpacks.clear();

        JsonArray cf_projects = jsonObject.getAsJsonArray("cf-projects");
        for (JsonElement current_element : cf_projects) {
            JsonObject cf_project = current_element.getAsJsonObject();
            all_modpacks.add(gson.fromJson(cf_project.toString(), CF_Project.class));
        }

        modpacks = all_modpacks;

        Platform.runLater(ModpackManager::setModpacksList);

        current_version = versionName;
    }

    public static void findModpacksByString(String find) {
        if (find == null) {
            return;
        }

        findModpacksByCategory(categoryList.getValue());

        List<CF_Project> filtered = new ArrayList<>();

        for (CF_Project cf_project : modpacks) {
            if (cf_project.getTitle().toLowerCase().contains(find.toLowerCase())) {
                filtered.add(cf_project);
            }
        }

        setModpacksList(filtered);
    }

    public static void findModpacksByCategory(CF_Category category) {
        if (category.getId().equals("all")) {
            modpacks = all_modpacks;
            setModpacksList();
            return;
        }

        modpacks = new ArrayList<>();

        for (CF_Project cf_project : all_modpacks) {
            if (cf_project.getCategories().contains(category.getId())) {
                modpacks.add(cf_project);
            }
        }

        setModpacksList();
    }

    private static void setModpacksList() {
        webModpacks.getItems().setAll(modpacks);
        modpacksFor.setText("Найдено " + modpacks.size() + " модпаков:");
        webModpacks.scrollTo(0);
    }

    private static void setModpacksList(List<CF_Project> newModpacks) {
        webModpacks.getItems().setAll(newModpacks);
        modpacksFor.setText("Найдено " + newModpacks.size() + " модпаков:");
        webModpacks.scrollTo(0);
    }

    public static void clearCache() {
        CF_ImageCache.clearCache();

        modpacks.clear();
        all_modpacks.clear();
        local_modpacks.clear();

        CF_Installer.current_cf_project = null;
        CF_Installer.current_modpack = null;
    }

    public static void loadLocalModpacks() {
        local_modpacks.clear();

        local_modpacks = ManageModpackManager.getModpacks();
    }

    public static void setInstalledModpacksList() {
        installedModpacks.getItems().setAll(local_modpacks);
    }

    public static String translateCategory(String category) {
        for (CF_Category cf_category : modpacks_category) {
            if (cf_category.getId().equals(category)) {
                return cf_category.getDisplayName();
            }
        }

        return category;
    }
}
