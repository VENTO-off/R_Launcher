package relevant_craft.vento.r_launcher.manager.mod;

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
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.utils.ZipUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModsManager {

    private static JFXListView installedMods;
    private static FontAwesomeIconView modsFor;
    private static JFXTextField search;
    private static JFXListView webMods;
    private static JFXComboBox<CF_Version> versionList;
    private static JFXComboBox<CF_Category> categoryList;
    private static FontAwesomeIconView searchIcon;
    private static FontAwesomeIconView versionText;
    private static FontAwesomeIconView categoryText;
    private static JFXSpinner loading;
    private static Label loadingText;
    private static Separator separator;
    public static Pane current_cf_project;

    public ModsManager(JFXListView _installedMods, FontAwesomeIconView _modsFor, JFXTextField _search, JFXListView _webMods, JFXComboBox _versionList, JFXComboBox _categoryList, FontAwesomeIconView _searchIcon, FontAwesomeIconView _versionText, FontAwesomeIconView _categoryText, JFXSpinner _loading, Label _loadingText, Separator _separator, Pane _current_cf_project) {
        installedMods = _installedMods;
        modsFor = _modsFor;
        search = _search;
        webMods = _webMods;
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

    private static List<CF_Project> all_mods = new ArrayList<>();
    private static List<CF_Project> mods = new ArrayList<>();
    private static List<CF_Version> mods_version = new ArrayList<>();
    private static List<CF_Category> mods_category = new ArrayList<>();
    private static List<LocalMod> local_mods = new ArrayList<>();

    private static String current_version;

    public static void hideElements(String msg) {
        modsFor.setVisible(false);
        searchIcon.setVisible(false);
        search.setVisible(false);
        webMods.setVisible(false);
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
        modsFor.setVisible(true);
        searchIcon.setVisible(true);
        search.setVisible(true);
        webMods.setVisible(true);
        versionText.setVisible(true);
        versionList.setVisible(true);
        categoryText.setVisible(true);
        categoryList.setVisible(true);
        separator.setVisible(true);

        loading.setVisible(false);
        loadingText.setVisible(false);
        loadingText.setText(null);
    }

    public static void loadMods() {
        ModsManager.hideElements("Загрузка списка модов...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getVersions();
                getCategories();

                Platform.runLater(() -> {
                    versionList.getItems().setAll(mods_version);
                    versionList.setValue(new CF_Version("<не выбрано>"));
                    categoryList.getItems().setAll(mods_category);
                    categoryList.setValue(categoryList.getItems().get(0));
                    ModsManager.showElements();
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

        if (!mods_version.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.Mods.getRLauncher_url() + "versions.txt");
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

        String[] mods_version_list = response.split("<::>");
        for (int i = 0; i < mods_version_list.length; i++) {
            mods_version.add(new CF_Version(mods_version_list[i]));
        }
    }

    private static void getCategories() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (!mods_category.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.Mods.getRLauncher_url() + "categories.txt");
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

        String[] mods_categories_list = response.split("<::>");
        mods_category.add(new CF_Category("Все категории", "all"));
        for (int i = 0; i < mods_categories_list.length; i++) {
            String[] current = mods_categories_list[i].split("=");
            mods_category.add(new CF_Category(current[1], current[0]));
        }
    }

    private static JsonObject jsonObject = new JsonObject();

    public static void loadModsForVersion(String versionName) {
        ModsManager.hideElements("Загрузка модов для " + versionName + "...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                ModsManager.getMods(versionList.getValue().getVersionName());
                Platform.runLater(ModsManager::showElements);
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void getMods(String versionName) {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (versionName.equals("<не выбрано>")) {
            return;
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement element;

        String url = VENTO.WEB + CF_Projects.Mods.getRLauncher_url() + versionName;
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
                    all_mods.clear();
                    Platform.runLater(ModsManager::setModsList);
                    return;
                }
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new URL(url + ".json").openStream()));
                response = bufferedreader.readLine();
                bufferedreader.close();
            } catch (IOException e2) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка подключения");
                    notify.setMessage("При попытке получить список модов произошла ошибка.\nПожалуйста, проверьте подключение к сети\nили обратитесь в тех.поддержку лаунчера.");
                    notify.showNotify();
                });
                return;
            }
            element = parser.parse(response);
        }

        jsonObject = element.getAsJsonObject();

        all_mods.clear();

        JsonArray cf_projects = jsonObject.getAsJsonArray("cf-projects");
        for (JsonElement current_element : cf_projects) {
            JsonObject cf_project = current_element.getAsJsonObject();
            all_mods.add(gson.fromJson(cf_project.toString(), CF_Project.class));
        }

        mods = all_mods;

        Platform.runLater(ModsManager::setModsList);

        current_version = versionName;
    }

    public static void findModsByString(String find) {
        if (find == null) {
            return;
        }

        findModsByCategory(categoryList.getValue());

        List<CF_Project> filtered = new ArrayList<>();

        for (CF_Project cf_project : mods) {
            if (cf_project.getTitle().toLowerCase().contains(find.toLowerCase())) {
                filtered.add(cf_project);
            }
        }

        setModsList(filtered);
    }

    public static void findModsByCategory(CF_Category category) {
        if (category.getId().equals("all")) {
            mods = all_mods;
            setModsList();
            return;
        }

        mods = new ArrayList<>();

        for (CF_Project cf_project : all_mods) {
            if (cf_project.getCategories().contains(category.getId())) {
                mods.add(cf_project);
            }
        }

        setModsList();
    }

    private static void setModsList() {
        webMods.getItems().setAll(mods);
        modsFor.setText("Найдено " + mods.size() + " модов:");
        webMods.scrollTo(0);
    }

    private static void setModsList(List<CF_Project> newMods) {
        webMods.getItems().setAll(newMods);
        modsFor.setText("Найдено " + newMods.size() + " модов:");
        webMods.scrollTo(0);
    }

    public static void clearCache() {
        CF_ImageCache.clearCache();

        mods.clear();
        all_mods.clear();
        local_mods.clear();

        CF_Installer.current_cf_project = null;
        CF_Installer.current_modpack = null;
    }

    public static void loadLocalMods() {
        local_mods.clear();

        File folder = new File(CF_Installer.current_modpack.getPath() + File.separator + "mods");
        if (folder.isDirectory() && folder.exists()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (!file.getName().endsWith(".jar")) {
                    continue;
                }
                if (file.getName().toLowerCase().contains("r-launcher") && file.getName().toLowerCase().contains("modpack")) {
                    continue;
                }
                addLocalMod(file);
            }
        }

        setInstalledModsList();
    }

    public static void setInstalledModsList() {
        installedMods.getItems().setAll(local_mods);
    }

    public static void addLocalMod(File file) {
        JsonObject jsonObject = ZipUtils.getJsonFromZip(file.getAbsolutePath(), "mcmod.info");
        String name;
        if (jsonObject != null && jsonObject.has("name")) {
            name = jsonObject.get("name").getAsString();
        } else {
            name = file.getName().substring(0, file.getName().lastIndexOf("."));
        }
        String version = "<не определено>";
        if (jsonObject != null && jsonObject.has("version")) {
            version = jsonObject.get("version").getAsString();
        }
        local_mods.add(new LocalMod(name, file.getAbsolutePath(), version));
    }

    public static void deleteLocalMod(LocalMod localMod) {
        if (local_mods.contains(localMod)) {
            local_mods.remove(localMod);
        }
    }

    public static String translateCategory(String category) {
        for (CF_Category cf_category : mods_category) {
            if (cf_category.getId().equals(category)) {
                return cf_category.getDisplayName();
            }
        }

        return category;
    }

    public static CF_Project getModByHref(String href) {
        for (CF_Project cf_project : all_mods) {
            if (cf_project.getHref().equals(href)) {
                return cf_project;
            }
        }

        return null;
    }

    public static boolean hasRealDependencies(CF_Project cf_project) {
        for (CF_Dependence cf_dependence : cf_project.getDependencies()) {
            if (ModsManager.getModByHref(cf_dependence.getHref()) != null) {
                return true;
            }
        }

        return false;
    }
}
