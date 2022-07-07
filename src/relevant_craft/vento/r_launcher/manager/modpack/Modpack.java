package relevant_craft.vento.r_launcher.manager.modpack;

public class Modpack {
    private String name;
    private String version;
    private String path;
    private int ram;
    private int mods;

    public Modpack(String name, String version, String path, int ram, int mods) {
        this.name = name;
        this.version = version;
        this.path = path;
        this.ram = ram;
        this.mods = mods;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public int getRam() {
        return ram;
    }

    public int getMods() {
        return mods;
    }
}
