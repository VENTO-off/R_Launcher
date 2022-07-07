package relevant_craft.vento.r_launcher.manager.assets;

import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.File;

public class Asset {
    private String name;
    private String hash;
    private long size;
    private boolean isLegacy;
    private String url;
    private File file;

    public Asset(String name, String hash, long size, boolean isLegacy) {
        this.name = name;
        this.hash = hash;
        this.size = size;
        this.isLegacy = isLegacy;
        this.url = "http://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;
        if (isLegacy) {
            this.file = new File(SettingsManager.minecraftDirectory + File.separator + "assets" + File.separator + "virtual" + File.separator + "legacy" + File.separator + name);
        } else {
            this.file = new File(SettingsManager.minecraftDirectory + File.separator + "assets" + File.separator + "objects" + File.separator + hash.substring(0, 2) + File.separator + hash);
        }
    }

    public long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public File getFile() {
        return file;
    }
}
