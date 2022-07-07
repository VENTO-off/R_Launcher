package relevant_craft.vento.r_launcher.manager.curseforge;

public class CF_Category {
    private String displayName;
    private String id;

    public CF_Category(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }
}
