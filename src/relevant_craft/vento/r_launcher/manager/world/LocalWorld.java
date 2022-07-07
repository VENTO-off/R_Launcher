package relevant_craft.vento.r_launcher.manager.world;

import relevant_craft.vento.r_launcher.utils.StringUtils;

public class LocalWorld {
    private String name;
    private String path;
    private String version;

    LocalWorld(String name, String path, String version) {
        this.name = StringUtils.fixString(name);
        this.path = path;
        this.version = "Minecraft" + ' ' + version;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
