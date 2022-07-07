package relevant_craft.vento.r_launcher.manager.curseforge;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CF_Project {
    private String avatar;
    private String title;
    private String downloads;
    private String date;
    private String description;
    private String description_ru;
    private String href;
    private List<String> categories;
    private List<CF_Images> images;
    private List<CF_Dependence> dependencies;
    private List<CF_Downloads> files;

    public CF_Project(String avatar, String title, String downloads, String date, String description, String description_ru, String href, List<String> categories, List<CF_Images> images, List<CF_Dependence> dependencies, List<CF_Downloads> files) {
        this.avatar = avatar;
        this.title = title;
        this.downloads = downloads;
        this.date = date;
        this.description = description;
        this.description_ru = description_ru;
        this.href = href;
        this.categories = categories;
        this.images = images;
        this.dependencies = dependencies;
        this.files = files;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getTitle() {
        return title;
    }

    public String getDownloads() {
        return downloads;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getDescription_RU() {
        return description_ru;
    }

    public String getHref() {
        return href;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<CF_Images> getImages() { return images; }

    public List<CF_Dependence> getDependencies() {
        return dependencies;
    }

    public List<CF_Downloads> getFiles() {
        return files;
    }
}