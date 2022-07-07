package relevant_craft.vento.r_launcher.manager.curseforge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import relevant_craft.vento.r_launcher.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CF_ModpackManifest {

    private String destination;
    private JsonObject jsonObject = new JsonObject();

    public CF_ModpackManifest(String destination) {
        this.destination = destination;
    }

    public boolean read() {
        JsonParser parser = new JsonParser();
        try {
            FileReader fileReader = new FileReader(destination + File.separator + "manifest.json");
            JsonElement jsonElement = parser.parse(fileReader);
            jsonObject = jsonElement.getAsJsonObject();
            fileReader.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getModpackName() {
        return StringUtils.fixStringForFile(jsonObject.get("name").getAsString().trim());
    }

    public String getOverrides() {
        if (jsonObject.has("overrides")) {
            return jsonObject.get("overrides").getAsString();
        }

        return null;
    }

    public String getModpackMinecraftVersion() {
        JsonObject minecraft = jsonObject.getAsJsonObject("minecraft");
        return minecraft.get("version").getAsString();
    }

    private List<String> getModpackModloaders() {
        List<String> modloaders = new ArrayList<>();

        JsonObject minecraft = jsonObject.getAsJsonObject("minecraft");
        JsonArray modLoaders = minecraft.getAsJsonArray("modLoaders");
        for (JsonElement element : modLoaders) {
            JsonObject modloader = element.getAsJsonObject();
            if (modloader.has("id") && modloader.has("primary")) {
                if (modloader.get("primary").getAsBoolean()) {
                    modloaders.add(modloader.get("id").getAsString());
                }
            }
        }

        return modloaders;
    }

    public boolean hasModloader(String modloader) {
        for (String ml : getModpackModloaders()) {
            if (ml.toLowerCase().contains(modloader.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public HashMap<String, String> getModpackMods() {
        HashMap<String, String> mods = new HashMap<>();

        JsonArray files = jsonObject.getAsJsonArray("files");
        for (JsonElement element : files) {
            JsonObject file = element.getAsJsonObject();
            if (file.has("projectID") && file.has("fileID") && file.has("required")) {
                String projectID = file.get("projectID").getAsString();
                String fileID = file.get("fileID").getAsString();
                boolean required = file.get("required").getAsBoolean();
                if (!required) {
                    continue;
                }
                mods.put(CF_UrlBuilder(projectID, fileID), getModName(projectID));
            }
        }

        return mods;
    }

    //TODO unsafe
    private String CF_UrlBuilder(String projectID, String fileID) {
        return "https://www.curseforge.com/minecraft/mc-mods/" + projectID + "/download/" + fileID + "/file";
    }

    private String getModName(String projectID) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(destination + File.separator + "modlist.html"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(projectID)) {
                    line = line.substring(line.indexOf("\">") + 2, line.indexOf("</a>"));
                    line = line.substring(0, line.lastIndexOf(" (by "));
                    line = StringUtils.fixStringForFile(line);
                    reader.close();
                    return line;
                }
            }
            reader.close();
        } catch (Exception ignored) {}

        return projectID;
    }

    public void deleteManifest() {
        File manifest = new File(destination + File.separator + "manifest.json");
        if (manifest.exists()) { manifest.delete(); }
    }

    public void deleteModlist() {
        File modlist = new File(destination + File.separator + "modlist.html");
        if (modlist.exists()) { modlist.delete(); }
    }
}
