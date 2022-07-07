package relevant_craft.vento.r_launcher.manager.assets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssetsManager {
    private JsonObject jsonObject = new JsonObject();

    public List<Asset> objects = new ArrayList<>();
    public boolean isLegacy;
    public File assetsDirectory;

    public AssetsManager(String version, boolean read) {
        JsonParser parser = new JsonParser();
        JsonElement element;
        try {
            FileReader fileReader = new FileReader(SettingsManager.minecraftDirectory + File.separator + "assets" + File.separator + "indexes" + File.separator + version + ".json");
            element = parser.parse(fileReader);
            jsonObject = element.getAsJsonObject();

            if (jsonObject.has("virtual")) {
                isLegacy = jsonObject.get("virtual").getAsBoolean();
            } else {
                isLegacy = false;
            }

            assetsDirectory = new File(SettingsManager.minecraftDirectory + File.separator + "assets");
            if (isLegacy) {
                assetsDirectory = new File(assetsDirectory, File.separator + "virtual" + File.separator + "legacy");
            }
        } catch (FileNotFoundException e) {
            return;
        }

        if (read) {
            getAssetsObjects();
        }
    }

    private void getAssetsObjects() {
        JsonObject current_objects = jsonObject.getAsJsonObject("objects");
        Set<Map.Entry<String, JsonElement>> entries = current_objects.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            JsonObject current_object = current_objects.getAsJsonObject(entry.getKey());
            objects.add(new Asset(entry.getKey(), current_object.get("hash").getAsString(), current_object.get("size").getAsLong(), isLegacy));
        }
    }


}
