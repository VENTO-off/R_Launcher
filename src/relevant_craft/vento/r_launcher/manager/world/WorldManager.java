package relevant_craft.vento.r_launcher.manager.world;

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
import relevant_craft.vento.r_launcher.minecraft.servers.CompressedStreamTools;
import relevant_craft.vento.r_launcher.minecraft.servers.nbt.NBTTagCompound;
import relevant_craft.vento.r_launcher.utils.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    private static JFXListView installedWorlds;
    private static FontAwesomeIconView worldsFor;
    private static JFXTextField search;
    private static JFXListView webWorlds;
    private static JFXComboBox<CF_Version> versionList;
    private static JFXComboBox<CF_Category> categoryList;
    private static FontAwesomeIconView searchIcon;
    private static FontAwesomeIconView versionText;
    private static FontAwesomeIconView categoryText;
    private static JFXSpinner loading;
    private static Label loadingText;
    private static Separator separator;
    public static Pane current_cf_project;

    public WorldManager(JFXListView _installedWorlds, FontAwesomeIconView _worldsFor, JFXTextField _search, JFXListView _webWorlds, JFXComboBox _versionList, JFXComboBox _categoryList, FontAwesomeIconView _searchIcon, FontAwesomeIconView _versionText, FontAwesomeIconView _categoryText, JFXSpinner _loading, Label _loadingText, Separator _separator, Pane _current_cf_project) {
        installedWorlds = _installedWorlds;
        worldsFor = _worldsFor;
        search = _search;
        webWorlds = _webWorlds;
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

    private static List<CF_Project> all_worlds = new ArrayList<>();
    private static List<CF_Project> worlds = new ArrayList<>();
    private static List<CF_Version> worlds_version = new ArrayList<>();
    private static List<CF_Category> worlds_category = new ArrayList<>();
    private static List<LocalWorld> local_worlds = new ArrayList<>();

    private static String current_version;

    public static void hideElements(String msg) {
        worldsFor.setVisible(false);
        searchIcon.setVisible(false);
        search.setVisible(false);
        webWorlds.setVisible(false);
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
        worldsFor.setVisible(true);
        searchIcon.setVisible(true);
        search.setVisible(true);
        webWorlds.setVisible(true);
        versionText.setVisible(true);
        versionList.setVisible(true);
        categoryText.setVisible(true);
        categoryList.setVisible(true);
        separator.setVisible(true);

        loading.setVisible(false);
        loadingText.setVisible(false);
        loadingText.setText(null);
    }

    public static void loadWorlds() {
        WorldManager.hideElements("Загрузка списка карт...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getVersions();
                getCategories();

                Platform.runLater(() -> {
                    versionList.getItems().setAll(worlds_version);
                    versionList.setValue(new CF_Version("<не выбрано>"));
                    categoryList.getItems().setAll(worlds_category);
                    categoryList.setValue(categoryList.getItems().get(0));
                    WorldManager.showElements();
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

        if (!worlds_version.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.Worlds.getRLauncher_url() + "versions.txt");
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

        String[] worlds_version_list = response.split("<::>");
        for (int i = 0; i < worlds_version_list.length; i++) {
            worlds_version.add(new CF_Version(worlds_version_list[i]));
        }
    }

    private static void getCategories() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (!worlds_category.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.Worlds.getRLauncher_url() + "categories.txt");
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

        String[] worlds_categories_list = response.split("<::>");
        worlds_category.add(new CF_Category("Все категории", "all"));
        for (int i = 0; i < worlds_categories_list.length; i++) {
            String[] current = worlds_categories_list[i].split("=");
            worlds_category.add(new CF_Category(current[1], current[0]));
        }
    }

    private static JsonObject jsonObject = new JsonObject();

    public static void loadWorldsForVersion(String versionName) {
        WorldManager.hideElements("Загрузка карт для " + versionName + "...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                WorldManager.getWorlds(versionList.getValue().getVersionName());
                Platform.runLater(WorldManager::showElements);
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void getWorlds(String versionName) {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (versionName.equals("<не выбрано>")) {
            return;
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement element;

        String url = VENTO.WEB + CF_Projects.Worlds.getRLauncher_url() + versionName;
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
                    all_worlds.clear();
                    Platform.runLater(WorldManager::setWorldsList);
                    return;
                }
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new URL(url + ".json").openStream()));
                response = bufferedreader.readLine();
                bufferedreader.close();
            } catch (IOException e2) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка подключения");
                    notify.setMessage("При попытке получить список карт произошла ошибка.\nПожалуйста, проверьте подключение к сети\nили обратитесь в тех.поддержку лаунчера.");
                    notify.showNotify();
                });
                return;
            }
            element = parser.parse(response);
        }

        jsonObject = element.getAsJsonObject();

        all_worlds.clear();

        JsonArray cf_projects = jsonObject.getAsJsonArray("cf-projects");
        for (JsonElement current_element : cf_projects) {
            JsonObject cf_project = current_element.getAsJsonObject();
            all_worlds.add(gson.fromJson(cf_project.toString(), CF_Project.class));
        }

        worlds = all_worlds;

        Platform.runLater(WorldManager::setWorldsList);

        current_version = versionName;
    }

    public static void findWorldsByString(String find) {
        if (find == null) {
            return;
        }

        findWorldsByCategory(categoryList.getValue());

        List<CF_Project> filtered = new ArrayList<>();

        for (CF_Project cf_project : worlds) {
            if (cf_project.getTitle().toLowerCase().contains(find.toLowerCase())) {
                filtered.add(cf_project);
            }
        }

        setWorldsList(filtered);
    }

    public static void findWorldsByCategory(CF_Category category) {
        if (category.getId().equals("all")) {
            worlds = all_worlds;
            setWorldsList();
            return;
        }

        worlds = new ArrayList<>();

        for (CF_Project cf_project : all_worlds) {
            if (cf_project.getCategories().contains(category.getId())) {
                worlds.add(cf_project);
            }
        }

        setWorldsList();
    }

    private static void setWorldsList() {
        webWorlds.getItems().setAll(worlds);
        worldsFor.setText("Найдено " + worlds.size() + " карт:");
        webWorlds.scrollTo(0);
    }

    private static void setWorldsList(List<CF_Project> newWorlds) {
        webWorlds.getItems().setAll(newWorlds);
        worldsFor.setText("Найдено " + newWorlds.size() + " карт:");
        webWorlds.scrollTo(0);
    }

    public static void clearCache() {
        CF_ImageCache.clearCache();

        worlds.clear();
        all_worlds.clear();
        local_worlds.clear();

        CF_Installer.current_cf_project = null;
        CF_Installer.current_modpack = null;
    }

    public static void loadLocalWorlds() {
        local_worlds.clear();

        File folder = new File(CF_Installer.current_modpack.getPath() + File.separator + "saves");
        if (folder.isDirectory() && folder.exists()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                File level_dat = new File(file.getAbsolutePath() + File.separator + "level.dat");
                if (!level_dat.exists() || !level_dat.isFile()) {
                    continue;
                }
                addLocalWorld(level_dat);
            }
        }

        setInstalledWorldsList();
    }

    public static void setInstalledWorldsList() {
        installedWorlds.getItems().setAll(local_worlds);
    }

    public static void addLocalWorld(File level_dat) {
        try {
            NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(level_dat));
            NBTTagCompound nbt_data = compound.getCompoundTag("Data");

            String name = level_dat.getName();
            if (nbt_data.hasKey("LevelName")) {
                name = nbt_data.getString("LevelName");
            }

            String version = "<не определено>";
            if (nbt_data.hasKey("Version")) {
                NBTTagCompound nbt_version = nbt_data.getCompoundTag("Version");
                if (nbt_version.hasKey("Name")) {
                    version = nbt_version.getString("Name");
                }
            }

            local_worlds.add(new LocalWorld(name, level_dat.getParent(), version));
        } catch (Exception e) {
            local_worlds.add(new LocalWorld(level_dat.getParentFile().getName(), level_dat.getParent(), "<не определено>"));
        }
    }

    public static void deleteLocalWorld(LocalWorld localWorld) {
        if (local_worlds.contains(localWorld)) {
            local_worlds.remove(localWorld);
        }
    }

    public static String translateCategory(String category) {
        for (CF_Category cf_category : worlds_category) {
            if (cf_category.getId().equals(category)) {
                return cf_category.getDisplayName();
            }
        }

        return category;
    }

    public static void clearSavesDirectory(String dir) {
        File[] files = new File(dir).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File level_dat = new File(file + File.separator + "level.dat");
                if (!level_dat.exists()) {
                    FileUtils.removeDirectory(file);
                }
            }
        }
    }
}
