package relevant_craft.vento.r_launcher.manager.curseforge;

public enum CF_Projects {
    Mods("https://curseforge.com/minecraft/mc-mods", "curseforge/Mods/", "Моды"),
    Textures("https://curseforge.com/minecraft/texture-packs", "curseforge/Textures/", "Текстуры"),
    Worlds("https://curseforge.com/minecraft/worlds", "curseforge/Worlds/", "Карты"),
    ModPacks("https://curseforge.com/minecraft/modpacks", "curseforge/ModPacks/", "Модпаки"),
    ;

    private final String project_url;
    private final String r_launcher_url;
    private final String name;

    CF_Projects(String project_url, String r_launcher_url, String name) {
        this.project_url = project_url;
        this.r_launcher_url = r_launcher_url;
        this.name = name;
    }

    public String getProject_url() {
        return project_url;
    }

    public String getRLauncher_url() {
        return r_launcher_url;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
