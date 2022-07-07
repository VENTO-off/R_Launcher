package relevant_craft.vento.r_launcher.manager.curseforge;

public class CF_Dependence {
    private String title;
    private String href;

    public CF_Dependence(String name, String href) {
        this.title = name;
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }
}
