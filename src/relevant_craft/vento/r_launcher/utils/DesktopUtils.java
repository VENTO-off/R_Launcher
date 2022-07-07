package relevant_craft.vento.r_launcher.utils;

import relevant_craft.vento.r_launcher.VENTO;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopUtils {

    public static void openFolder(String path, boolean doCreate) {
        File folder = new File(path);
        if (!folder.exists() && doCreate) {
            folder.mkdir();
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(folder);
            } catch (Exception e) {}
        }
    }

    public static void openUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {}
        }
    }
}