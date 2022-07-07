package relevant_craft.vento.r_launcher.manager.texture;

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
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.curseforge.*;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.extract.ExtractManager;
import relevant_craft.vento.r_launcher.utils.ZipUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TextureManager {

    private static JFXListView installedTextures;
    private static FontAwesomeIconView texturesFor;
    private static JFXTextField search;
    private static JFXListView webTextures;
    private static JFXComboBox<CF_Version> versionList;
    private static JFXComboBox<CF_Category> categoryList;
    private static FontAwesomeIconView searchIcon;
    private static FontAwesomeIconView versionText;
    private static FontAwesomeIconView categoryText;
    private static JFXSpinner loading;
    private static Label loadingText;
    private static Separator separator;
    public static Pane current_cf_project;

    public TextureManager(JFXListView _installedTextures, FontAwesomeIconView _texturesFor, JFXTextField _search, JFXListView _webTextures, JFXComboBox _versionList, JFXComboBox _categoryList, FontAwesomeIconView _searchIcon, FontAwesomeIconView _versionText, FontAwesomeIconView _categoryText, JFXSpinner _loading, Label _loadingText, Separator _separator, Pane _current_cf_project) {
        installedTextures = _installedTextures;
        texturesFor = _texturesFor;
        search = _search;
        webTextures = _webTextures;
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

    private static List<CF_Project> all_textures = new ArrayList<>();
    private static List<CF_Project> textures = new ArrayList<>();
    private static List<CF_Version> textures_version = new ArrayList<>();
    private static List<CF_Category> textures_category = new ArrayList<>();
    private static List<LocalTexture> local_textures = new ArrayList<>();

    private static String current_version;

    public static void hideElements(String msg) {
        texturesFor.setVisible(false);
        searchIcon.setVisible(false);
        search.setVisible(false);
        webTextures.setVisible(false);
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
        texturesFor.setVisible(true);
        searchIcon.setVisible(true);
        search.setVisible(true);
        webTextures.setVisible(true);
        versionText.setVisible(true);
        versionList.setVisible(true);
        categoryText.setVisible(true);
        categoryList.setVisible(true);
        separator.setVisible(true);

        loading.setVisible(false);
        loadingText.setVisible(false);
        loadingText.setText(null);
    }

    public static void loadTextures() {
        TextureManager.hideElements("Загрузка списка текстур...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                getVersions();
                getCategories();

                Platform.runLater(() -> {
                    versionList.getItems().setAll(textures_version);
                    versionList.setValue(new CF_Version("<не выбрано>"));
                    categoryList.getItems().setAll(textures_category);
                    categoryList.setValue(categoryList.getItems().get(0));
                    TextureManager.showElements();
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

        if (!textures_version.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.Textures.getRLauncher_url() + "versions.txt");
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

        String[] textures_version_list = response.split("<::>");
        for (int i = 0; i < textures_version_list.length; i++) {
            textures_version.add(new CF_Version(textures_version_list[i]));
        }
    }

    private static void getCategories() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (!textures_category.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + CF_Projects.Textures.getRLauncher_url() + "categories.txt");
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

        String[] textures_categories_list = response.split("<::>");
        textures_category.add(new CF_Category("Все категории", "all"));
        for (int i = 0; i < textures_categories_list.length; i++) {
            String[] current = textures_categories_list[i].split("=");
            textures_category.add(new CF_Category(current[1], current[0]));
        }
    }

    private static JsonObject jsonObject = new JsonObject();

    public static void loadTexturesForVersion(String versionName) {
        TextureManager.hideElements("Загрузка текстур для " + versionName + "...");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                TextureManager.getTextures(versionList.getValue().getVersionName());
                Platform.runLater(() -> {
                    TextureManager.showElements();
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void getTextures(String versionName) {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (versionName.equals("<не выбрано>")) {
            return;
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement element;

        String url = VENTO.WEB + CF_Projects.Textures.getRLauncher_url() + versionName;
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
                    all_textures.clear();
                    Platform.runLater(TextureManager::setTexturesList);
                    return;
                }
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new URL(url + ".json").openStream()));
                response = bufferedreader.readLine();
                bufferedreader.close();
            } catch (IOException e2) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка подключения");
                    notify.setMessage("При попытке получить список текстур произошла ошибка.\nПожалуйста, проверьте подключение к сети\nили обратитесь в тех.поддержку лаунчера.");
                    notify.showNotify();
                });
                return;
            }
            element = parser.parse(response);
        }

        jsonObject = element.getAsJsonObject();

        all_textures.clear();

        JsonArray cf_projects = jsonObject.getAsJsonArray("cf-projects");
        for (JsonElement current_element : cf_projects) {
            JsonObject cf_project = current_element.getAsJsonObject();
            all_textures.add(gson.fromJson(cf_project.toString(), CF_Project.class));
        }

        textures = all_textures;

        Platform.runLater(TextureManager::setTexturesList);

        current_version = versionName;
    }

    public static void findTexturesByString(String find) {
        if (find == null) {
            return;
        }

        findTexturesByCategory(categoryList.getValue());

        List<CF_Project> filtered = new ArrayList<>();

        for (CF_Project cf_project : textures) {
            if (cf_project.getTitle().toLowerCase().contains(find.toLowerCase())) {
                filtered.add(cf_project);
            }
        }

        setTexturesList(filtered);
    }

    public static void findTexturesByCategory(CF_Category category) {
        if (category.getId().equals("all")) {
            textures = all_textures;
            setTexturesList();
            return;
        }

        textures = new ArrayList<>();

        for (CF_Project cf_project : all_textures) {
            if (cf_project.getCategories().contains(category.getId())) {
                textures.add(cf_project);
            }
        }

        setTexturesList();
    }

    private static void setTexturesList() {
        webTextures.getItems().setAll(textures);
        texturesFor.setText("Найдено " + textures.size() + " текстур:");
        webTextures.scrollTo(0);
    }

    private static void setTexturesList(List<CF_Project> newTextures) {
        webTextures.getItems().setAll(newTextures);
        texturesFor.setText("Найдено " + newTextures.size() + " текстур:");
        webTextures.scrollTo(0);
    }

    public static void clearCache() {
        CF_ImageCache.clearCache();

        textures.clear();
        all_textures.clear();
        local_textures.clear();

        CF_Installer.current_cf_project = null;
        CF_Installer.current_modpack = null;
    }

    public static void loadLocalTextures() {
        local_textures.clear();

        for (TextureVersion textureVersion : TextureVersion.values()) {
            File folder = new File(CF_Installer.current_modpack.getPath() + File.separator + textureVersion.getFolder());
            if (folder.isDirectory() && folder.exists()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    if (!file.getName().endsWith(".zip")) {
                        continue;
                    }
                    addLocalTexture(file, textureVersion);
                }
            }
        }

        setInstalledTexturesList();
    }

    public static void setInstalledTexturesList() {
        installedTextures.getItems().setAll(local_textures);
    }

    public static void addLocalTexture(File file, TextureVersion textureVersion) {
        Image avatar = ZipUtils.getImageFromZip(file.getAbsolutePath(), "pack.png");
        if (avatar != null) {
            local_textures.add(new LocalTexture(file.getName().substring(0, file.getName().lastIndexOf('.')), file.getAbsolutePath(), avatar, textureVersion));
        }
    }

    public static void deleteLocalTexture(LocalTexture localTexture) {
        if (local_textures.contains(localTexture)) {
            local_textures.remove(localTexture);
        }
    }

    public static TextureVersion getTextureVersion() {
        String[] data = TextureManager.current_version.split("\\.");
        if (Integer.valueOf(data[0]) == 1 && Integer.valueOf((data[1].contains("-") ? data[1].substring(0, data[1].indexOf('-')) : data[1])) <= 5) {
            return TextureVersion.LEGACY;
        } else {
            return TextureVersion.LATEST;
        }
    }

    public static String translateCategory(String category) {
        for (CF_Category cf_category : textures_category) {
            if (cf_category.getId().equals(category)) {
                return cf_category.getDisplayName();
            }
        }

        return category;
    }

    public static CF_Project getTextureByHref(String href) {
        for (CF_Project cf_project : all_textures) {
            if (cf_project.getHref().equals(href)) {
                return cf_project;
            }
        }

        return null;
    }

    public static boolean hasRealDependencies(CF_Project cf_project) {
        for (CF_Dependence cf_dependence : cf_project.getDependencies()) {
            if (TextureManager.getTextureByHref(cf_dependence.getHref()) != null) {
                return true;
            }
        }

        return false;
    }
}
