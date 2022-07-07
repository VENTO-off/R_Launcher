package relevant_craft.vento.r_launcher.manager.cheat;

public class Cheat {

    private String name;
    private String url;
    private String path;
    private String version;
    private boolean isLocal;
    private String jar;
    private String json;
    private String extra;
    private String description;
    private String picture;

    public Cheat(String _name, String _url, String _path, String _version, boolean _isLocal, String _jar, String _json, String _extra, String _description, String _picture) {
        name = _name;
        url = _url;
        path = _path;
        version = _version;
        isLocal = _isLocal;
        jar = _jar;
        json = _json;
        extra = _extra;
        description = _description;
        picture = _picture;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() { return path; }

    public String getVersion() {
        return version;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getJar() {
        return jar;
    }

    public String getJson() {
        return json;
    }

    public String getExtra() {
        return extra;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }
}
