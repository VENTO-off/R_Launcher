package relevant_craft.vento.r_launcher.manager.modpack;

public class ManageModpackSourceLibrary {

    public String name = null;
    public String url = null;
    public String exact_url = null;
    public boolean packed = false;
    public boolean override = false;

    public ManageModpackSourceLibrary(String name, String url, String exact_url, boolean packed, boolean override) {
        this.name = name;
        this.url = url;
        this.exact_url = exact_url;
        this.packed = packed;
        this.override = override;
    }
}
