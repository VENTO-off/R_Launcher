package relevant_craft.vento.r_launcher.manager.version;

public class Version {

    public String name;
    public String url;
    public boolean isLocal;
    public VersionType type;
    public String displayName;
    public String modpackName;
    public boolean modpackHidden;

    public Version(String _name, String _url, boolean _isLocal, VersionType _type) {
        name = _name;
        url = _url;
        isLocal = _isLocal;
        type = _type;
        displayName = name;
        modpackName = null;
        modpackHidden = false;

        if (type == VersionType.SNAPSHOT) {
            displayName += " (" + type.displayName + ")";
        } else if (type == VersionType.BETA) {
            displayName += " (" + type.displayName + ")";
        } else if (type == VersionType.ALPHA) {
            displayName += " (" + type.displayName + ")";
        }
    }

    public Version(String _name, String _url, boolean _isLocal, VersionType _type, String _modpackName, boolean _modpackHidden) {
        name = _name;
        url = _url;
        isLocal = _isLocal;
        type = _type;
        displayName = name;
        modpackName = _modpackName;
        modpackHidden = _modpackHidden;

        if (type == VersionType.SNAPSHOT) {
            displayName += " (" + type.displayName + ")";
        } else if (type == VersionType.BETA) {
            displayName += " (" + type.displayName + ")";
        } else if (type == VersionType.ALPHA) {
            displayName += " (" + type.displayName + ")";
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
