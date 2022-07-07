package relevant_craft.vento.r_launcher.manager.curseforge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import relevant_craft.vento.r_launcher.utils.JsonUtils;

import java.io.File;
import java.util.HashMap;

public class CF_ModpackMods {

    private String json;

    public CF_ModpackMods(File version_dir) {
        this.json = version_dir.getAbsolutePath() + File.separator + "mods" + File.separator + "mods.json";
    }

    public boolean hasJson() {
        return new File(json).exists();
    }

    public HashMap<String, String> getMods() {
        HashMap<String, String> list = new HashMap<>();
        try {
            JsonObject jsonMods = JsonUtils.readJson(json);
            JsonArray modsList = jsonMods.getAsJsonArray("modpack_mods");
            for (JsonElement element : modsList) {
                JsonObject mod = element.getAsJsonObject();
                if (mod.has("name") && mod.has("url")) {
                    list.put(mod.get("name").getAsString() + ".jar", mod.get("url").getAsString());
                }

            }
        } catch (Exception ignored) {}

        return list;
    }

    public void delete() {
        new File(json).delete();
    }
}
