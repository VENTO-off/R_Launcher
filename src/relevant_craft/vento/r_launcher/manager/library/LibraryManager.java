package relevant_craft.vento.r_launcher.manager.library;

import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    public List<Library> libs;

    public LibraryManager() {
        libs = new ArrayList<>();
    }

    public void add(Library lib) {
        libs.add(lib);
    }

    public void addAll(List<Library> libs) { this.libs.addAll(libs); }

    public String getLibsForLaunch() {
        StringBuilder result = new StringBuilder();
        for (Library lib : libs) {
            if (lib.id.contains("relevant_craft.vento.r_launcher.modpack")) {
                continue;
            }
            if (!lib.isNatives) {
                result.append(lib.path.getAbsolutePath()).append("\\").append(lib.name.replace(".pack.xz", "")).append(";");
            }
        }
        result = new StringBuilder(result.substring(0, result.length() - 1));

        return result.toString();
    }

    public Library getLibById(String id, boolean isNatives) {
        for (Library lib : libs) {
            if (lib.id.equals(id) && lib.isNatives == isNatives) {
                return lib;
            }
        }

        return null;
    }

    public Library findLibById(String id, boolean isNatives) {
        for (Library lib : libs) {
            if (lib.id.contains(id) && lib.isNatives == isNatives) {
                return lib;
            }
        }

        return null;
    }

    public int countAllSize() {
        int size = 0;
        for (Library lib : libs) {
            size += lib.size;
        }
        return size;
    }

    public static String correctPath(String _path, boolean _isMainJar, boolean _isNatives, String _version, boolean fullPath) {
        String path = "";
        if (fullPath) {
            path = SettingsManager.minecraftDirectory + File.separator + (_isMainJar || _isNatives ? "versions" : "libraries") + File.separator;
        }

        if (_isMainJar) {
            return path + _version;
        } else if (_isNatives) {
            return path + _version + File.separator + "natives";
        }

        boolean colon = false;
        for (int i = 0; i < _path.length(); i++) {
            if (_path.charAt(i) == ':') {
                colon = true;
                path += "\\";
                continue;
            }

            if (_path.charAt(i) == '.') {
                if (!colon) {
                    path += "\\";
                    continue;
                }
            }

            path += _path.charAt(i);
        }

        return path;
    }

    public static String nameFromUrl(String _path, String url, boolean _isMainJar, String version) {
        if (_isMainJar) {
            return version + ".jar";
        }
        if (url == null) {
            String correctPath = correctPath(_path, false, false, version, false);
            StringBuilder temp = new StringBuilder(correctPath);
            temp.setCharAt(temp.lastIndexOf("\\"), '-');
            return temp.substring(temp.lastIndexOf("\\") + 1, temp.length()) + ".jar";
        }
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static URL correctURL(String _path, boolean _isNatives, String native_os) throws MalformedURLException {
        String url = "https://libraries.minecraft.net/" + correctPath(_path, false, false, null, false).replace("\\", "/");
        StringBuilder file = new StringBuilder(url);
        file.setCharAt(file.lastIndexOf("/"), '-');
        url += file.substring(file.lastIndexOf("/"), file.length());
        if (_isNatives) {
            url += "-" + native_os;
        }
        url += ".jar";

        return new URL(url);
    }

    public static String urlForRepository(String repository, String path, boolean isPacked) {
        String correctPath = correctPath(path, false, false, null, false);
        StringBuilder temp = new StringBuilder(correctPath);
        temp.setCharAt(temp.lastIndexOf("\\"), '-');

        String jar = temp.substring(temp.lastIndexOf("\\") + 1, temp.length()) + ".jar";
        if (isPacked) {
            jar += ".pack.xz";
        }

        return repository + correctPath.replace("\\", "/") + "/" + jar;
    }
}
