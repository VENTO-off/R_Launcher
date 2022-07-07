package relevant_craft.vento.r_launcher.manager.version;

public enum VersionType {
    RELEASE("release", "Релиз"),
    SNAPSHOT("snapshot", "Снапшот"),
    BETA("old_beta", "Бета"),
    ALPHA("old_alpha", "Альфа"),
    MODIFIED("modified", "Модифицированная"),
    UNKNOWN("", ""),
    ;

    public String type;
    public String displayName;

    VersionType(String _type, String _displayName) {
        type = _type;
        displayName = _displayName;
    }
}
