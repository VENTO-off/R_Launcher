package relevant_craft.vento.r_launcher.utils;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;

public class JsonUtils {

    public static JsonObject readJson(String json) throws Exception {
        FileReader fileReader = new FileReader(json);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(fileReader);
        fileReader.close();
        return element.getAsJsonObject();
    }

    public static void saveJson(String json, JsonObject jsonObject) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter fileWriter = new FileWriter(json);
        fileWriter.write(gson.toJson(jsonObject));
        fileWriter.close();
    }
}
