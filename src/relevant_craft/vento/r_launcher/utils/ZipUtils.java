package relevant_craft.vento.r_launcher.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    public static Image getImageFromZip(String path, String name) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(new File(path));
            return new Image(zip.getInputStream(zip.getEntry(name)));
        } catch (Exception e) {
            return null;
        } finally {
            try {
                zip.close();
            } catch (Exception e) {}
        }
    }

    public static JsonObject getJsonFromZip(String path, String name) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(new File(path));
            BufferedReader br = new BufferedReader(new InputStreamReader(zip.getInputStream(zip.getEntry(name))));
            StringBuffer json = new StringBuffer();

            String line = null;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            json.deleteCharAt(0);
            json.deleteCharAt(json.length() - 1);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json.toString());
            return element.getAsJsonObject();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                zip.close();
            } catch (Exception e) {}
        }
    }

    public static boolean hasFile(String path, String name) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(new File(path));
            Enumeration entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry file = (ZipEntry) entries.nextElement();
                if (file.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                zip.close();
            } catch (Exception e) {}
        }
    }
}
