package relevant_craft.vento.r_launcher.manager.curseforge;

public class CF_Version {
    private String versionName;

    public CF_Version (String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return versionName;
    }

    @Override
    public String toString() {
        return versionName;
    }
}
