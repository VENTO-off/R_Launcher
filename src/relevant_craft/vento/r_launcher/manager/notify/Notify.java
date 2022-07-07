package relevant_craft.vento.r_launcher.manager.notify;

public class Notify {

    private String id;
    private long date;
    private String title;
    private String short_text;
    private String full_text;
    private String url;

    public Notify(String id, long date, String title, String short_text, String full_text, String url) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.short_text = short_text;
        this.full_text = full_text;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getShort_text() {
        return short_text;
    }

    public String getFull_text() {
        return full_text;
    }

    public String getUrl() {
        return url;
    }
}
