package relevant_craft.vento.r_launcher.manager.socket.commands;

import com.google.gson.JsonObject;

public class curseforge_download {

    public static JsonObject createRequest(String link) {
        JsonObject json = new JsonObject();
        json.addProperty("command", "curseforge_download");
        json.addProperty("link", link);

        return json;
    }
}
