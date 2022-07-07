package relevant_craft.vento.r_launcher.manager.curseforge;

public class CF_Downloads {
    private String status;
    private String name;
    private String size;
    private String uploaded;
    private String link;

    CF_Downloads(String status, String name, String size, String uploaded, String link) {
        this.status = status;
        this.name = name;
        this.size = size;
        this.uploaded = uploaded;
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getUploaded() {
        return uploaded;
    }

    public String getLink() {
        return link;
    }
}
