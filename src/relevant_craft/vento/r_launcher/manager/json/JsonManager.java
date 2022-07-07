package relevant_craft.vento.r_launcher.manager.json;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.curseforge.CF_ModpackMods;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.library.Library;
import relevant_craft.vento.r_launcher.manager.library.LibraryManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.utils.OperatingSystem;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonManager {
    public String version;
    private boolean isExternal;
    private JsonObject jsonObject = new JsonObject();
    private JsonArray libraries = new JsonArray();
    private JsonManager jsonInherited;

    public JsonManager(String version, boolean isExternal) {
        this.version = version;
        this.isExternal = isExternal;
    }

    public boolean read() {
        JsonParser parser = new JsonParser();
        JsonElement element;
        if (isExternal) {
            try {
                //String jsonText = readUrl("http://s3.amazonaws.com/Minecraft.Download/versions/" + version + "/" + version + ".json");
                String jsonText = readUrl(VENTO.WEB + "versions" + '/' + version + ".json");
                element = parser.parse(jsonText);
                jsonObject = element.getAsJsonObject();
            } catch (Exception e) {
                return false;
            }
        } else {
            try {
                FileReader fileReader = new FileReader(SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + version + File.separator + version + ".json");
                element = parser.parse(fileReader);
                jsonObject = element.getAsJsonObject();
                fileReader.close();
            } catch (Exception e) {
                return false;
            }
        }

        try {
            if (jsonObject.has("libraries")) {
                libraries = jsonObject.getAsJsonArray("libraries");
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static String readUrl(String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public void setJsonInherited(JsonManager jsonInherited) {
        this.jsonInherited = jsonInherited;
    }

    public String getId() throws JsonException {
        if (jsonObject.has("id")) {
            return jsonObject.get("id").getAsString();
        }

        if (jsonInherited != null) {
            return jsonInherited.getId();
        }

        throw new JsonException("id");
    }

    public String getType() throws JsonException {
        if (jsonObject.has("type")) {
            return jsonObject.get("type").getAsString();
        }

        if (jsonInherited != null) {
            return jsonInherited.getType();
        }

        throw new JsonException("type");
    }

    public String getMinecraftArgs() {
        if (jsonObject.has("minecraftArguments")) {
            String args = jsonObject.get("minecraftArguments").getAsString();
            if (jsonInherited != null) {
                args += " " + jsonInherited.getMinecraftArgs();
            }

            return args;
        } else if (jsonObject.has("arguments")) {
            StringBuilder args = new StringBuilder();
            JsonObject arguments = jsonObject.getAsJsonObject("arguments");
            JsonArray game = arguments.getAsJsonArray("game");
            for (JsonElement element : game) {
                if (!element.isJsonObject()) {
                    args.append(element.getAsString()).append(" ");
                }
            }

            if (jsonInherited != null) {
                args.append(" ").append(jsonInherited.getMinecraftArgs());
            }

            return args.toString();
        }

        if (jsonInherited != null) {
            return jsonInherited.getMinecraftArgs();
        }

        return null;
    }

    public String getMainClass() throws JsonException {
        if (jsonObject.has("mainClass")) {
            return jsonObject.get("mainClass").getAsString();
        }

        if (jsonInherited != null) {
            return jsonInherited.getMainClass();
        }

        throw new JsonException("mainClass");
    }

    public String getAssetIndex() throws JsonException {
        if (jsonObject.has("assets")) {
            return jsonObject.get("assets").getAsString();
        }

        if (jsonInherited != null) {
            return jsonInherited.getAssetIndex();
        }

        throw new JsonException("assets");
    }

    public Date getReleaseTime() throws ParseException { return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(jsonObject.get("releaseTime").getAsString()); }

    public String getAssetsURL() throws JsonException {
        if (jsonInherited == null) {
            if (jsonObject.has("assetIndex")) {
                return jsonObject.get("assetIndex").getAsJsonObject().get("url").getAsString();
            } else {
                return "http://s3.amazonaws.com/Minecraft.Download/indexes/" + getAssetIndex() + ".json";
            }
        } else {
            return jsonInherited.getAssetsURL();
        }
    }

    public boolean isInherits() {
        return jsonObject.has("inheritsFrom");
    }

    public String getInheritsFrom() throws JsonException {
        if (jsonObject.has("inheritsFrom")) {
            return jsonObject.get("inheritsFrom").getAsString();
        }

        throw new JsonException("inheritsFrom");
    }

    public boolean isModpackHidden() {
        if (jsonObject.has("modpack_hide")) {
            return jsonObject.get("modpack_hide").getAsBoolean();
        }

        return false;
    }

    public String getModpackName() {
        if (jsonObject.has("modpack_name")) {
            return jsonObject.get("modpack_name").getAsString();
        }

        return null;
    }

    public int getModpackRam() {
        if (jsonObject.has("modpack_ram")) {
            return jsonObject.get("modpack_ram").getAsInt();
        }

        return 0;
    }

    public List<Library> getModpackMods() {
        List<Library> list = new ArrayList<>();
        if (jsonObject.has("modpack_mods")) {
            JsonArray mods = jsonObject.getAsJsonArray("modpack_mods");
            for (JsonElement element : mods) {
                JsonObject mod = element.getAsJsonObject();
                if (mod.has("name") && mod.has("exact_url")) {
                    list.add(new Library(mod.get("name").getAsString(), SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + getModpackName() + File.separator + "mods", false, mod.get("exact_url").getAsString(), 0, false, false, false, null, version));
                }
            }
        }

        return list;
    }

    public void replaceAuthlib(LibraryManager libraryManager) {
        Library authlib = null;

        String id = getAuthlib(libraryManager);
        if (id != null) {
            id = id.replace("authlib", "elyby_authlib");
            authlib = new Library(id, id, LibraryManager.urlForRepository(VENTO.REPOSITORY + "libraries/", id, false), 0, false, false, false, null, version);
        } else {
            id = "com.mojang:elyby_authlib:mc" + version;
            authlib = new Library(id, id, LibraryManager.urlForRepository(VENTO.REPOSITORY + "libraries/", id, false), 0, false, false, false, null, version);
        }

        File jar = new File(authlib.path + File.separator + authlib.name);
        if (jar.exists() || DownloadManager.existsUrlFile(authlib.url.toString())) {
            deleteAuthlib(libraryManager);
            libraryManager.libs.add(0, authlib);
        }
    }

    private String getAuthlib(LibraryManager libraryManager) {
        for (Library library : libraryManager.libs) {
            if (library.id.contains("com.mojang:authlib")) {
                return library.id;
            }
        }

        return null;
    }

    private void deleteAuthlib(LibraryManager libraryManager) {
        for (Library library : libraryManager.libs) {
            if (library.id.contains("com.mojang:authlib")) {
                libraryManager.libs.remove(library);
                return;
            }
        }
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public LibraryManager getAllLibraries() {
        LibraryManager libraryManager = new LibraryManager();

        for (JsonElement element : libraries) {
            boolean isCompatible = true;

            JsonObject library = element.getAsJsonObject();
            if (library.has("rules")) {
                JsonArray rules = library.getAsJsonArray("rules");
                for (JsonElement element1 : rules) {
                    JsonObject rule = element1.getAsJsonObject();
                    String action = rule.get("action").getAsString();
                    if (rule.has("os")) {
                        JsonObject os = rule.getAsJsonObject("os");
                        String name = os.get("name").getAsString();
                        if (action.equals("disallow")) {
                            if (OperatingSystem.getCurrentPlatform().getName().equals(name)) {
                                isCompatible = false;
                            }
                        } else if (action.equals("allow")) {
                            if (!OperatingSystem.getCurrentPlatform().getName().equals(name)) {
                                isCompatible = false;
                            }
                        }
                    }
                }
            }

            if (!isCompatible) {
                continue;
            }

            if (library.has("natives")) {
                JsonObject natives = library.getAsJsonObject("natives");
                if (!library.has("downloads")) {
                    libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), null, -1, false, false, false, null, version));
                    if (natives.has(OperatingSystem.getCurrentPlatform().getName())) {
                        String native_os = natives.get(OperatingSystem.getCurrentPlatform().getName()).getAsString().replace("${arch}", (System.getProperty("os.arch").contains("64") ? "64" : "32" ));
                        libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), null, -1, true, false, false, native_os, version));
                    }
                    continue;
                }
                JsonObject downloads = library.getAsJsonObject("downloads");
                if (downloads.has("artifact")) {
                    JsonObject artifact = downloads.getAsJsonObject("artifact");
                    libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), artifact.get("url").getAsString(), artifact.get("size").getAsLong(), false, false, false, null, version));
                }

                JsonObject classifiers = downloads.getAsJsonObject("classifiers");
                if (natives.has(OperatingSystem.getCurrentPlatform().getName())) {
                    String native_os = natives.get(OperatingSystem.getCurrentPlatform().getName()).getAsString().replace("${arch}", (System.getProperty("os.arch").contains("64") ? "64" : "32" ));
                    JsonObject current_native_os = classifiers.getAsJsonObject(native_os);
                    libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), current_native_os.get("url").getAsString(), current_native_os.get("size").getAsLong(), true, false, false, native_os, version));
                }
                continue;
            }

            if (library.has("downloads")) {
                JsonObject downloads = library.getAsJsonObject("downloads");
                if (downloads.has("artifact")) {
                    JsonObject artifact = downloads.getAsJsonObject("artifact");
                    libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), artifact.get("url").getAsString(), artifact.get("size").getAsLong(), false, false, false, null, version));
                }
            } else if (library.has("exact_url")) {
                libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), library.get("exact_url").getAsString(), 0, false, false, false, null, version));
            } else if (library.has("url")) {
                if (library.get("url").getAsString().endsWith("/")) {
                    libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), LibraryManager.urlForRepository(library.get("url").getAsString(), library.get("name").getAsString(), library.has("packed")), 0, false, false, library.has("packed"), null, version));
                }
            } else if (library.has("name")) {
                libraryManager.add(new Library(library.get("name").getAsString(), library.get("name").getAsString(), null, -1, false, false, false, null, version));
            }
        }

        if (jsonObject.has("downloads")) {
            JsonObject downloads = jsonObject.getAsJsonObject("downloads");
            if (downloads.size() > 0 && downloads.has("client")) {
                JsonObject client = downloads.getAsJsonObject("client");
                libraryManager.add(new Library(version, version, client.get("url").getAsString(), client.get("size").getAsLong(), false, true, false, null, version));
            } else {
                libraryManager.add(new Library(version, version, null, -1, false, true, false, null, version));
            }
        } else {
            libraryManager.add(new Library(version, version, null, -1, false, true, false, null, version));
        }

        if (getModpackName() != null) {
            libraryManager.addAll(getModpackMods());
        }

        if (jsonInherited != null) {
            List<Library> inheritedLibs = jsonInherited.getAllLibraries().libs;
            for (Library lib : inheritedLibs) {
                if (lib.isNatives || lib.isMainJar) {
                    lib.path = new File(lib.path.getAbsolutePath().replace(lib.version, version));
                    lib.name = lib.name.replace(lib.version, version);
                }
            }

            libraryManager.addAll(inheritedLibs);
        }

        return libraryManager;
    }

    public List<Library> getExtraLibraries() {
        List<Library> list = new ArrayList<>();
        if (jsonObject.has("extraLibraries")) {
            JsonArray extraLibraries = jsonObject.getAsJsonArray("extraLibraries");
            for (JsonElement element : extraLibraries) {
                JsonObject lib = element.getAsJsonObject();
                if (lib.has("name") && lib.has("size") && lib.has("exact_url")) {
                    list.add(new Library(lib.get("name").getAsString(), lib.get("name").getAsString(), true, lib.get("exact_url").getAsString(), lib.get("size").getAsLong(), false, false, false, null, version));
                }
            }
        }

        return list;
    }
}
