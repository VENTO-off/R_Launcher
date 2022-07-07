package relevant_craft.vento.r_launcher.manager.modpack;

import com.google.gson.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.json.JsonManager;
import relevant_craft.vento.r_launcher.manager.library.LibraryManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.utils.FileUtils;
import relevant_craft.vento.r_launcher.utils.JsonUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ManageModpackManager {

    public static List<Modpack> getModpacks() {
        List<Version> modpack_versions = VersionManager.getModpackVersions();
        List<Modpack> modpacks = new ArrayList<>();
        for (Version version : modpack_versions) {
            try {
                JsonObject jsonObject = JsonUtils.readJson(getPathToJson(version.name).getAbsolutePath());
                String versionName = jsonObject.get("id").getAsString();
                //TODO REMOVE
                if (versionName.startsWith("Forge") && versionName.contains(" ")) {
                    try {
                        versionName = versionName.split(" ")[1];
                    } catch (Exception e) {}
                }
                //END OF TODO
                int ram = 0;
                if (jsonObject.has("modpack_ram")) {
                    ram = jsonObject.get("modpack_ram").getAsInt();
                }
                String path = getPathToJson(version.name).getParent();
                int mods = getModsAmount(path);

                modpacks.add(new Modpack(version.name, versionName, path, ram, mods));
            } catch (Exception e) {}
        }

        return modpacks;
    }

    public static void createModpack(String modpackName, Version version, int ram, boolean installModLoader, boolean installLiteLoader, boolean installOptiFine) {
        File dir = getPathToDir(modpackName);
        File json = getPathToJson(modpackName);

        if (VersionManager.containsVersions(modpackName)) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка");
                notify.setMessage("Такое название уже существует в списке версий.");
                notify.showNotify();
            });
            return;
        }

        if (dir.exists() && json.exists()) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка");
                notify.setMessage("Такая сборка уже добавлена.");
                notify.showNotify();
            });
            return;
        }

        try {
            DownloadManager.downloadFile(VENTO.WEB + version.url, json.getAbsolutePath(), false);
        } catch (IOException e) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка скачивания");
                notify.setMessage("Не удалось скачать .json файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                notify.showNotify();
            });
            return;
        }

        JsonObject jsonObject;
        try {
            jsonObject = JsonUtils.readJson(json.getAbsolutePath());
        } catch (Exception e) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка чтения");
                notify.setMessage("Не удалось прочитать .json файл.\nПопробуйте создать новую сборку.");
                notify.showNotify();
            });
            return;
        }

        setModpackName(jsonObject, modpackName);

        setModpackRam(jsonObject, ram);

        addRLauncherLibrary(jsonObject, version);

        new File(dir + File.separator + "saves").mkdir();

        if (installModLoader) {
            installModLoader(jsonObject, version);

            new File(dir + File.separator + "mods").mkdir();
            new File(dir + File.separator + "config").mkdir();
        }
        if (installLiteLoader) {
            installLiteLoader(jsonObject, version);
        }
        if (installOptiFine) {
            installOptiFine(jsonObject, version, installModLoader);
        }

        try {
            JsonUtils.saveJson(json.getAbsolutePath(), jsonObject);
        } catch (Exception e) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка сохранения");
                notify.setMessage("Не удалось сохранить .json файл.\nПопробуйте создать новую сборку.");
                notify.showNotify();
            });
            return;
        }

        VersionManager.addLocalVersion(modpackName, true);
        Platform.runLater(() -> {
            VersionManager.setVersionByName(modpackName);
            VersionManager.setVersions();
        });
    }

    public static void editModpack(Modpack modpack, String newModpackName, Version version, int ram, boolean installModLoader, boolean installLiteLoader, boolean installOptiFine) {
        File dir = getPathToDir(modpack.getName());
        File json = getPathToJson(modpack.getName());
        File jar = getPathToJar(modpack.getName());

        JsonObject jsonObject;
        try {
            jsonObject = JsonUtils.readJson(json.getAbsolutePath());
        } catch (Exception e) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка чтения");
                notify.setMessage("Не удалось прочитать .json файл.\nПопробуйте создать новую сборку.");
                notify.showNotify();
            });
            return;
        }

        if (!modpack.getName().equals(newModpackName)) {
            if (VersionManager.containsVersions(newModpackName)) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                    notify.setTitle("Ошибка");
                    notify.setMessage("Такое название уже существует в списке версий.");
                    notify.showNotify();
                });
                return;
            }

            if (getPathToDir(newModpackName).exists() && getPathToJson(newModpackName).exists()) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                    notify.setTitle("Ошибка");
                    notify.setMessage("Такая сборка уже добавлена.");
                    notify.showNotify();
                });
                return;
            }

            setModpackName(jsonObject, newModpackName);
        }

        JsonManager jsonVersion = new JsonManager(version.name, true);
        if (!jsonVersion.read()) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка скачивания");
                notify.setMessage("Не удалось скачать .json файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                notify.showNotify();
            });
            return;
        }

        //TODO REMOVE
        if (!jsonObject.get("id").getAsString().equals(version.name)) {
            jsonObject.remove("id");
            jsonObject.addProperty("id", version.name);
        }
        //END OF TODO

        setModpackRam(jsonObject, ram);

        try {
            setMainClass(jsonObject, jsonVersion.getMainClass());
        } catch (Exception ignored) {}

        setMinecraftArguments(jsonObject, jsonVersion.getJsonObject());

        setLibraries(jsonObject, jsonVersion.getJsonObject());

        addRLauncherLibrary(jsonObject, version);

        removeExtraLibraries(jsonObject);

        removeModpackMods(jsonObject);

        if (installModLoader) {
            installModLoader(jsonObject, version);
        }
        if (installLiteLoader) {
            installLiteLoader(jsonObject, version);
        }
        if (installOptiFine) {
            installOptiFine(jsonObject, version, installModLoader);
        } else {
            deleteOptiFine(version, dir);
        }

        try {
            JsonUtils.saveJson(json.getAbsolutePath(), jsonObject);
        } catch (Exception e) {
            Platform.runLater(() -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка сохранения");
                notify.setMessage("Не удалось сохранить .json файл.\nПопробуйте создать новую сборку.");
                notify.showNotify();
            });
            return;
        }

        if (!modpack.getName().equals(newModpackName)) {
            Platform.runLater(() -> {
                VersionManager.removeLocalVersion(modpack.getName());
            });

            json.renameTo(new File(dir + File.separator + newModpackName + ".json"));
            jar.renameTo(new File(dir + File.separator + newModpackName + ".jar"));
            dir.renameTo(getPathToDir(newModpackName));

            Platform.runLater(() -> {
                VersionManager.addLocalVersion(newModpackName, true);
                VersionManager.setVersionByName(newModpackName);
                VersionManager.setVersions();
            });
        }
    }

    public static void deleteModpack(Modpack modpack) {
        FileUtils.removeDirectory(new File(modpack.getPath()));
        Platform.runLater(() -> VersionManager.removeLocalVersion(modpack.getName()));
    }

    private static File getPathToDir(String name) {
        return new File(SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + name);
    }

    private static File getPathToJson(String name) {
        return new File(getPathToDir(name) + File.separator + name + ".json");
    }

    private static File getPathToJar(String name) {
        return new File(getPathToDir(name) + File.separator + name + ".jar");
    }

    private static void addRLauncherLibrary(JsonObject jsonObject, Version version) {
        String versionName = (version.name.length() - version.name.replace(".", "").length() > 1 ? (version.name.substring(0, version.name.lastIndexOf("."))) : version.name);
        String r_launcher_modpack_url = VENTO.REPOSITORY + "libraries/" + "r_launcher_modpack/" + versionName +  "/" + "R-Launcher_Modpack.jar";
        if (DownloadManager.existsUrlFile(r_launcher_modpack_url)) {
            JsonArray libraries = jsonObject.getAsJsonArray("libraries");
            JsonObject r_launcher_modpack = new JsonObject();
            r_launcher_modpack.addProperty("name", "relevant_craft.vento.r_launcher.modpack" + ":" + versionName);
            r_launcher_modpack.addProperty("exact_url", r_launcher_modpack_url);
            libraries.add(r_launcher_modpack);
        }
    }

    private static void setMainClass(JsonObject jsonObject, String mainClass) {
        jsonObject.remove("mainClass");
        jsonObject.addProperty("mainClass", mainClass);
    }

    private static void setModpackName(JsonObject jsonObject, String modpackName) {
        if (jsonObject.has("modpack_name")) {
            jsonObject.remove("modpack_name");
        }
        jsonObject.addProperty("modpack_name", modpackName);
    }

    private static void setModpackRam(JsonObject jsonObject, int ram) {
        if (jsonObject.has("modpack_ram")) {
            jsonObject.remove("modpack_ram");
        }
        jsonObject.addProperty("modpack_ram", ram);
    }

    private static void setMinecraftArguments(JsonObject jsonObject, JsonObject from) {
        if (jsonObject.has("minecraftArguments")) {
            jsonObject.remove("minecraftArguments");
            jsonObject.addProperty("minecraftArguments", from.get("minecraftArguments").getAsString());
        } else {
            JsonObject arguments = jsonObject.getAsJsonObject("arguments");
            arguments.remove("game");
            arguments.add("game", from.getAsJsonObject("arguments").getAsJsonArray("game"));
        }
    }

    private static void setLibraries(JsonObject jsonObject, JsonObject from) {
        jsonObject.remove("libraries");
        jsonObject.add("libraries", from.getAsJsonArray("libraries"));
    }

    private static void addMinecraftArguments(JsonObject jsonObject, String args) {
        if (jsonObject.has("minecraftArguments")) {
            String arguments = jsonObject.get("minecraftArguments").getAsString();
            jsonObject.remove(arguments);
            jsonObject.addProperty("minecraftArguments", arguments + " " + args);
        } else {
            JsonObject arguments = jsonObject.getAsJsonObject("arguments");
            JsonArray game_arguments = arguments.getAsJsonArray("game");
            game_arguments.add(args);
        }
    }

    private static void addLibraries(JsonObject jsonObject, List<ManageModpackSourceLibrary> libs) {
        for (ManageModpackSourceLibrary lib : libs) {
            if (!hasLibrary(jsonObject, lib.name)) {
                JsonObject library = new JsonObject();
                if (lib.name != null) {
                    library.addProperty("name", lib.name);
                }
                if (lib.url != null) {
                    library.addProperty("url", lib.url);
                }
                if (lib.exact_url != null) {
                    library.addProperty("exact_url", lib.exact_url);
                }
                if (lib.packed) {
                    library.addProperty("packed", "forge");
                }
                if (lib.override) {
                    addToTopLibrary(jsonObject, library);
                } else {
                    addToEndLibrary(jsonObject, library);
                }
            }
        }
    }

    private static void addExtraLibraries(JsonObject jsonObject, JsonArray extraLibraries) {
        jsonObject.add("extraLibraries", extraLibraries);
    }

    private static void removeExtraLibraries(JsonObject jsonObject) {
        if (jsonObject.has("extraLibraries")) {
            jsonObject.remove("extraLibraries");
        }
    }

    private static void addModpackMods(JsonObject jsonObject, JsonArray modsModLoader) {
        jsonObject.add("modpack_mods", modsModLoader);
    }

    private static void removeModpackMods(JsonObject jsonObject) {
        if (jsonObject.has("modpack_mods")) {
            jsonObject.remove("modpack_mods");
        }
    }

    private static void addToTopLibrary(JsonObject jsonObject, JsonObject library) {
        JsonArray libraries = jsonObject.getAsJsonArray("libraries");
        JsonArray new_libraries = new JsonArray();

        new_libraries.add(library);

        for (JsonElement element : libraries) {
            JsonObject lib = element.getAsJsonObject();
            new_libraries.add(lib);
        }

        jsonObject.remove("libraries");
        jsonObject.add("libraries", new_libraries);
    }

    private static void addToEndLibrary(JsonObject jsonObject, JsonObject library) {
        JsonArray libraries = jsonObject.getAsJsonArray("libraries");
        libraries.add(library);
    }

    private static boolean hasLibrary(JsonObject jsonObject, String name) {
        JsonArray libraries = jsonObject.getAsJsonArray("libraries");
        for (JsonElement element : libraries) {
            JsonObject library = element.getAsJsonObject();
            if (library.get("name").getAsString().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasModpackMod(JsonObject jsonObject, String name) {
        if (!jsonObject.has("modpack_mods")) {
            return false;
        }

        JsonArray modpackMods = jsonObject.getAsJsonArray("modpack_mods");
        for (JsonElement element : modpackMods) {
            JsonObject mod = element.getAsJsonObject();
            if (mod.get("name").getAsString().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private static int getModsAmount(String path) {
        File mods_dir = new File(path + File.separator + "mods");
        if (!mods_dir.exists()) {
            return 0;
        }

        int mods = 0;
        String[] files = mods_dir.list();
        for (String file : files) {
            if (file.endsWith(".jar")) {
                mods++;
            }
        }

        return mods;
    }

    private static HashMap<String, ManageModpackSources> modpackSources = new HashMap<>();

    public static void loadModpackSources(JFXSpinner loading, FontAwesomeIconView loadingText, JFXListView<Modpack> modpackList, Separator separator, AnchorPane tooltipArea, AnchorPane arrowArea, JFXButton createModpack) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    loading.setVisible(true);
                    loadingText.setVisible(true);
                    loadingText.setText("Загрузка списка версий...");
                    modpackList.setVisible(false);
                    separator.setVisible(false);
                    tooltipArea.setVisible(false);
                    arrowArea.setVisible(false);
                    createModpack.setVisible(false);
                });

                getModpackSources();

                Platform.runLater(() -> {
                    loading.setVisible(false);
                    loadingText.setVisible(false);
                    modpackList.setVisible(true);
                    separator.setVisible(true);
                    tooltipArea.setVisible(true);
                    arrowArea.setVisible(true);
                    createModpack.setVisible(true);
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    public static void getModpackSources() {
        if (!modpackSources.isEmpty()) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + "modpack.php");
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

        String[] versions = response.split("<::>");
        for (String version : versions) {
            String[] elements = version.split("#");
            String[] jsons = elements[1].split(";");

            ManageModpackSources sources = new ManageModpackSources();

            for (String json : jsons) {
                String[] data = json.split("->");
                if (data[0].startsWith("LiteLoader")) {
                    sources.LiteLoader = new ManageModpackSource(data[1]);
                } else if (data[0].startsWith("OptiFine")) {
                    sources.OptiFine = new ManageModpackSource(data[1]);
                } else {
                    sources.ModLoader = new ManageModpackSource(data[1]);
                    sources.ModLoaderName = data[0].replace(".json", "");
                }
            }

            modpackSources.put(elements[0].substring(elements[0].indexOf("/") + 1), sources);
        }
    }

    protected static String getModificationsForVersion(Version version) {
        if (!modpackSources.containsKey(version.displayName)) {
            return "";
        }

        String modifications = "";
        ManageModpackSources sources = modpackSources.get(version.displayName);
        if (sources.ModLoader != null) {
            modifications += sources.ModLoaderName + " | ";
        }
        if (sources.LiteLoader != null) {
            modifications += "LiteLoader" + " | ";
        }
        if (sources.OptiFine != null) {
            modifications += "OptiFine" + " | ";
        }

        return modifications.substring(0, modifications.length() - 3);
    }

    public static String getModLoaderForVersion(Version version) {
        if (!modpackSources.containsKey(version.displayName)) {
            return null;
        }

        ManageModpackSources sources = modpackSources.get(version.displayName);
        return sources.ModLoader == null ? null : sources.ModLoaderName;
    }

    public static ManageModpackSources getModpackSources(Version version) {
        if (version == null) {
            return null;
        }

        if (!modpackSources.containsKey(version.displayName)) {
            return null;
        }

        return modpackSources.get(version.displayName);
    }

    public static boolean hasModLoader(Modpack modpack, Version version) {
        try {
            if (getModpackSources(version) == null) {
                return false;
            }
            ManageModpackSource modloader = getModpackSources(version).ModLoader;
            if (modloader == null) {
                return false;
            }

            JsonObject jsonObject = JsonUtils.readJson(getPathToJson(modpack.getName()).getAbsolutePath());

            for (ManageModpackSourceLibrary library : modloader.getLibraries()) {
                if (!hasLibrary(jsonObject, library.name)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasLiteLoader(Modpack modpack, Version version) {
        try {
            if (getModpackSources(version) == null) {
                return false;
            }
            ManageModpackSource liteLoader = getModpackSources(version).LiteLoader;
            if (liteLoader == null) {
                return false;
            }

            JsonObject jsonObject = JsonUtils.readJson(getPathToJson(modpack.getName()).getAbsolutePath());

            for (ManageModpackSourceLibrary library : liteLoader.getLibraries()) {
                if (!hasLibrary(jsonObject, library.name)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasOptiFine(Modpack modpack, Version version) {
        try {
            if (getModpackSources(version) == null) {
                return false;
            }
            ManageModpackSource optiFine = getModpackSources(version).OptiFine;
            if (optiFine == null) {
                return false;
            }

            JsonObject jsonObject = JsonUtils.readJson(getPathToJson(modpack.getName()).getAbsolutePath());
            boolean hasOptiFine = true;

            for (ManageModpackSourceLibrary library : optiFine.getLibraries()) {
                if (!hasLibrary(jsonObject, library.name)) {
                    hasOptiFine = false;
                    break;
                }
            }

            if (hasOptiFine) {
                return true;
            }

            if (optiFine.getModsModLoader() != null) {
                hasOptiFine = true;
                for (ManageModpackSourceMod mod : optiFine.getModsModLoader_list()) {
                    if (!hasModpackMod(jsonObject, mod.name)) {
                        hasOptiFine = false;
                        break;
                    }
                }
            }

            return hasOptiFine;
        } catch (Exception e) {
            return false;
        }
    }

    public static void installModLoader(JsonObject jsonObject, Version version) {
        if (getModpackSources(version) == null) {
            return;
        }
        ManageModpackSource modloader = getModpackSources(version).ModLoader;
        if (modloader == null) {
            return;
        }

        if (modloader.getMainClass() != null) {
            setMainClass(jsonObject, modloader.getMainClass());
        }
        if (modloader.getMinecraftArguments() != null) {
            addMinecraftArguments(jsonObject, modloader.getMinecraftArguments());
        }
        if (modloader.getLibraries() != null) {
            addLibraries(jsonObject, modloader.getLibraries());
        }
        if (modloader.getExtraLibraries() != null) {
            addExtraLibraries(jsonObject, modloader.getExtraLibraries());
        }
    }

    public static void installLiteLoader(JsonObject jsonObject, Version version) {
        if (getModpackSources(version) == null) {
            return;
        }
        ManageModpackSource liteloader = getModpackSources(version).LiteLoader;
        if (liteloader == null) {
            return;
        }

        if (liteloader.getMainClass() != null) {
            setMainClass(jsonObject, liteloader.getMainClass());
        }
        if (liteloader.getMinecraftArguments() != null) {
            addMinecraftArguments(jsonObject, liteloader.getMinecraftArguments());
        }
        if (liteloader.getLibraries() != null) {
            addLibraries(jsonObject, liteloader.getLibraries());
        }
    }

    public static void installOptiFine(JsonObject jsonObject, Version version, boolean installModLoader) {
        if (getModpackSources(version) == null) {
            return;
        }
        ManageModpackSource optifine = getModpackSources(version).OptiFine;
        if (optifine == null) {
            return;
        }

        if (optifine.getModsModLoader() != null && installModLoader) {
            addModpackMods(jsonObject, optifine.getModsModLoader());
            return;
        }

        if (optifine.getMainClass() != null) {
            setMainClass(jsonObject, optifine.getMainClass());
        }
        if (optifine.getMinecraftArguments() != null) {
            if (installModLoader) {
                if (optifine.getMinecraftArgumentsModLoader() != null) {
                    addMinecraftArguments(jsonObject, optifine.getMinecraftArgumentsModLoader());
                }
            } else {
                addMinecraftArguments(jsonObject, optifine.getMinecraftArguments());
            }
        }
        if (optifine.getLibraries() != null) {
            addLibraries(jsonObject, optifine.getLibraries());
        }
    }

    public static void deleteOptiFine(Version version, File dir) {
        if (getModpackSources(version) == null) {
            return;
        }
        ManageModpackSource optifine = getModpackSources(version).OptiFine;
        if (optifine == null) {
            return;
        }

        if (optifine.getModsModLoader() == null) {
            return;
        }

        File mods_dir = new File(dir + File.separator + "mods");
        if (!mods_dir.exists()) {
            return;
        }


        for (ManageModpackSourceMod mod : optifine.getModsModLoader_list()) {
            File mod_file = new File(mods_dir + File.separator + LibraryManager.nameFromUrl(null, mod.exact_url, false, null));
            if (mod_file.exists() && mod_file.isFile()) {
                mod_file.delete();
            }
        }

    }
}
