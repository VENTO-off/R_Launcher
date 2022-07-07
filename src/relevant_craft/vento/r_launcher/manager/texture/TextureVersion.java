package relevant_craft.vento.r_launcher.manager.texture;

public enum TextureVersion {
    LATEST("Новые версии", "Minecraft 1.6 и выше", "resourcepacks"),
    LEGACY("Старые версии", "Minecraft 1.5.2 и ниже", "texturepacks"),
    ;

    private String displayName;
    private String description;
    private String folder;

    TextureVersion(String displayName, String description, String folder) {
        this.displayName = displayName;
        this.description = description;
        this.folder = folder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getFolder() {
        return folder;
    }
}
