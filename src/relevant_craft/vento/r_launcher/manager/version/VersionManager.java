package relevant_craft.vento.r_launcher.manager.version;

import com.jfoenix.controls.JFXListCell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.json.JsonManager;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VersionManager {

    private static File versions_dir = null;

    private static HashMap<Version, Long> versions_list = new HashMap<>();
    private static HashMap<Version, Long> external_version_list = new HashMap<>();

    public static void loadVersions() {
        VENTO.launcherManager.versionList.setButtonCell(new ListCell<Version>(){
            @Override
            protected void updateItem(Version version, boolean empty) {
                if (empty) {
                    setText(null);
                } else {
                    setText(version.displayName);
                    setFont(Font.font(15));
                }
            }
        });

        VENTO.launcherManager.versionList.setCellFactory(new Callback<ListView<Version>, ListCell<Version>>() {
            @Override
            public JFXListCell<Version> call(ListView<Version> param) {
                return new JFXListCell<Version>() {
                    @Override
                    protected void updateItem(Version version, boolean empty) {
                        super.updateItem(version, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
                            icon.setFill(Paint.valueOf("#939699"));
                            if (!version.isLocal) { icon.setOpacity(0); }
                            setGraphic(icon);
                            setText(version.displayName);
                            setFont(Font.font(15));
                            setPrefWidth(VENTO.launcherManager.versionList.getPrefWidth() - 30);
                        }
                    }
                };
            }
        });

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                versions_list.clear();
                versions_dir = new File(SettingsManager.minecraftDirectory + File.separator + "versions");

                Platform.runLater(() -> {
                    LauncherManager.lockButtons(true);
                });

                VersionManager.getLocalVersions();
                VersionManager.getExternalVersions();

                Platform.runLater(() -> {
                    setVersions();

                    if (LauncherManager.lastVersion == null) {
                        VENTO.launcherManager.versionList.getSelectionModel().selectFirst();
                    } else {
                        Version lastVersion = VersionManager.findLastVersion(LauncherManager.lastVersion);
                        if (lastVersion == null) {
                            VENTO.launcherManager.versionList.getSelectionModel().selectFirst();
                        } else {
                            VENTO.launcherManager.versionList.getSelectionModel().select(lastVersion);
                        }
                    }

                    LauncherManager.unlockButtons(true);
                });

                return null;
            }

        };

        new Thread(task).start();
    }

    public static void setVersionLocal(Version version) {
        for (Version ver : versions_list.keySet()) {
            if (ver == version) {
                ver.isLocal = true;
            }
        }
    }

    public static void setVersions() {
        VENTO.launcherManager.versionList.getItems().setAll(getVersions());
    }

    public static void addLocalVersion(String name, boolean isModpack) {
        JsonManager json = new JsonManager(name, false);
        if (!json.read()) {
            return;
        }
        try {
            versions_list.put(new Version(name, null, true, translateToVersionType(json.getType()), (isModpack ? name : null), false), json.getReleaseTime().getTime());
        } catch (Exception ignored) {}
    }

    public static void removeLocalVersion(String name) {
        for (Version version : versions_list.keySet()) {
            if (version.name.equals(name)) {
                if (VENTO.launcherManager.versionList.getValue().name.equals(version.name)) {
                    VENTO.launcherManager.versionList.getSelectionModel().selectFirst();
                    LauncherManager.lastVersion = VENTO.launcherManager.versionList.getSelectionModel().toString();
                }


                if (containsExternalVersions(version.name)) {
                    version.isLocal = false;
                } else {
                    versions_list.remove(version);
                }
                return;
            }
        }
    }

    public static void setVersionByName(String name) {
        for (Version version : versions_list.keySet()) {
            if (version.name.equals(name)) {
                VENTO.launcherManager.versionList.getSelectionModel().select(version);
                LauncherManager.lastVersion = VENTO.launcherManager.versionList.getSelectionModel().toString();
                return;
            }
        }
    }

    public static void getLocalVersions() {
        if (!versions_dir.exists()) {
            return;
        }

        String[] versions = versions_dir.list((current, name) -> new File(current, name).isDirectory());

        for (int i = 0; i < versions.length; i++) {
            JsonManager json = new JsonManager(versions[i], false);
            if (!json.read()) {
                continue;
            }
            try {
                versions_list.put(new Version(versions[i], null, true, translateToVersionType(json.getType()), json.getModpackName(), json.isModpackHidden()), json.getReleaseTime().getTime());
            } catch (Exception ignored) {}
        }
    }

    public static void getExternalVersions() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        String response;
        try {
            URL url = new URL(VENTO.WEB + "versions.php");
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
        for (int i = 0; i < versions.length; i++) {
            String[] version = versions[i].split(";");

            try {
                if (!containsVersions(version[1])) {
                    versions_list.put(new Version(version[1], version[0], false, translateToVersionType(version[3])), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(version[2]).getTime());
                }
                external_version_list.put(new Version(version[1], version[0], false, translateToVersionType(version[3])), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(version[2]).getTime());
            } catch (ParseException e) {}
        }
    }

    public static Collection<Version> getVersions() {
        return getVersions(true);
    }

    public static Collection<Version> getVersions(boolean isFiltered) {
        Set<Map.Entry<Version, Long>> set = versions_list.entrySet();
        List<Map.Entry<Version, Long>> list = new ArrayList<Map.Entry<Version, Long>>(set);

        Collections.sort(list, (v1, v2) -> (v2.getValue()).compareTo(v1.getValue()));

        Collection<Version> sorted_version_list = new ArrayList<>();
        for (Map.Entry<Version, Long> entry : list) {
            if (entry.getKey().isLocal) {
                sorted_version_list.add(entry.getKey());
                continue;
            }
            if (!SettingsManager.showSnapshots && entry.getKey().type == VersionType.SNAPSHOT && isFiltered) {
                continue;
            } else if (!SettingsManager.showBeta && entry.getKey().type == VersionType.BETA && isFiltered) {
                continue;
            } else if (!SettingsManager.showAlpha && entry.getKey().type == VersionType.ALPHA && isFiltered) {
                continue;
            } else if (!SettingsManager.showModifications && entry.getKey().type == VersionType.MODIFIED && isFiltered) {
                continue;
            } else if (!SettingsManager.showLegacy && versions_list.get(entry.getKey()) <= 1400088563000L && isFiltered) {            //1.7.10
                continue;
            }
            sorted_version_list.add(entry.getKey());
        }

        return sorted_version_list;
    }

    public static Collection<Version> getAllExternalVersions(boolean showSnapshots, boolean showBeta, boolean showAlpha, boolean showModifications, boolean showLegacy) {
        Set<Map.Entry<Version, Long>> set = external_version_list.entrySet();
        List<Map.Entry<Version, Long>> list = new ArrayList<Map.Entry<Version, Long>>(set);

        Collections.sort(list, new Comparator<Map.Entry<Version, Long>>() {
            public int compare(Map.Entry<Version, Long> o1, Map.Entry<Version, Long> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Collection<Version> sorted_version_list = new ArrayList<>();
        for (Map.Entry<Version, Long> entry : list) {
            if (!showSnapshots && entry.getKey().type == VersionType.SNAPSHOT ) {
                continue;
            } else if (!showBeta && entry.getKey().type == VersionType.BETA) {
                continue;
            } else if (!showAlpha && entry.getKey().type == VersionType.ALPHA) {
                continue;
            } else if (!showModifications && entry.getKey().type == VersionType.MODIFIED) {
                continue;
            } else if (!showLegacy && versions_list.get(entry.getKey()) <= 1400088563000L) {            //1.7.10
                continue;
            }
            sorted_version_list.add(entry.getKey());
        }

        return sorted_version_list;
    }

    public static Version findLastVersion(String ver) {
        for (Version version : versions_list.keySet()) {
            if (version.name.equals(ver)) {
                return version;
            }
        }

        return null;
    }

    public static boolean containsVersions(String ver) {
        for (Version version : versions_list.keySet()) {
            if (version.name.equals(ver)) {
                return true;
            }
        }

        return false;
    }

    private static boolean containsExternalVersions(String ver) {
        for (Version version : external_version_list.keySet()) {
            if (version.name.equals(ver)) {
                return true;
            }
        }

        return false;
    }

    private static VersionType translateToVersionType(String s) {
        for (VersionType vt : VersionType.values()) {
            if (vt.type.equals(s)) {
                return vt;
            }
        }

        return VersionType.UNKNOWN;
    }

    public static HashMap<Version, Long> getForgeVersions() {
        HashMap<Version, Long> forge_versions = new HashMap<>();
        for (Version version : external_version_list.keySet()) {
            if (version.name.startsWith("Forge ")) {
                forge_versions.put(version, external_version_list.get(version));
            }
        }

        return forge_versions;
    }

    public static List<Version> getModpackVersions() {
        List<Version> modpack_versions = new ArrayList<>();
        for (Version version : versions_list.keySet()) {
            if (version.modpackName != null && !version.modpackHidden) {
                modpack_versions.add(version);
            }
        }

        return modpack_versions;
    }

    public static long getReleaseDate(Version version) {
        if (versions_list.containsKey(version)) {
            return versions_list.get(version);
        }

        return 0;
    }

    public static Version getVersionByName(String version) {
        for (Version ver : external_version_list.keySet()) {
            if (ver.displayName.equals(version)) {
                return ver;
            }
        }

        return null;
    }
}
