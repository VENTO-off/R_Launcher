package relevant_craft.vento.r_launcher.manager.modpack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ManageModpackSource {

    public JsonObject jsonObject;

    public ManageModpackSource(String json) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            this.jsonObject = element.getAsJsonObject();
        } catch (Exception e) {
            jsonObject = null;
        }
    }

    public String getMinecraftArguments() {
        if (!jsonObject.has("minecraftArguments")) {
            return null;
        }

        return jsonObject.get("minecraftArguments").getAsString();
    }

    public String getMinecraftArgumentsModLoader() {
        if (!jsonObject.has("minecraftArgumentsModLoader")) {
            return null;
        }

        return jsonObject.get("minecraftArgumentsModLoader").getAsString();
    }

    public String getMainClass() {
        if (!jsonObject.has("mainClass")) {
            return null;
        }

        return jsonObject.get("mainClass").getAsString();
    }

    public List<ManageModpackSourceLibrary> getLibraries() {
        if (!jsonObject.has("libraries")) {
            return null;
        }

        List<ManageModpackSourceLibrary> libraries = new ArrayList<>();

        JsonArray jsonLibraries = new JsonArray();
        jsonLibraries = jsonObject.getAsJsonArray("libraries");

        for (JsonElement element : jsonLibraries) {
            JsonObject library = element.getAsJsonObject();

            String name = library.has("name") ? library.get("name").getAsString() : null;
            String url = library.has("url") ? library.get("url").getAsString() : null;
            String exact_url = library.has("exact_url") ? library.get("exact_url").getAsString() : null;
            boolean packed = library.has("packed");
            boolean replace = library.has("override");

            libraries.add(new ManageModpackSourceLibrary(name, url, exact_url, packed, replace));
        }

        return libraries;
    }

    public JsonArray getExtraLibraries() {
        if (!jsonObject.has("extraLibraries")) {
            return null;
        }

        return jsonObject.get("extraLibraries").getAsJsonArray();
    }

    public JsonArray getModsModLoader() {
        if (!jsonObject.has("modsModLoader")) {
            return null;
        }

        return jsonObject.get("modsModLoader").getAsJsonArray();
    }

    public List<ManageModpackSourceMod> getModsModLoader_list() {
        if (!jsonObject.has("modsModLoader")) {
            return null;
        }

        List<ManageModpackSourceMod> modpackMods = new ArrayList<>();

        for (JsonElement element : getModsModLoader()) {
            JsonObject mod = element.getAsJsonObject();
            if (mod.has("name") && mod.has("exact_url")) {
                modpackMods.add(new ManageModpackSourceMod(mod.get("name").getAsString(), mod.get("exact_url").getAsString()));
            }
        }

        return modpackMods;
    }

}
