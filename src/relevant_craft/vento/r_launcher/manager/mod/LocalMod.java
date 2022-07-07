package relevant_craft.vento.r_launcher.manager.mod;

public class LocalMod {
    private String name;
    private String path;
    private String version;

    LocalMod(String name, String path, String version) {
        this.name = name;
        this.path = path;
        this.version = version;
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
