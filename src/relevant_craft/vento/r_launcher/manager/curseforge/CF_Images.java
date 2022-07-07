package relevant_craft.vento.r_launcher.manager.curseforge;

public class CF_Images {
    private String thumbnail;
    //private String thumbnail_size;
    private String image;
    //private String image_size;

    public CF_Images(String thumbnail, String thumbnail_size, String image, String image_size) {
        this.thumbnail = thumbnail;
        //this.thumbnail_size = thumbnail_size;
        this.image = image;
        //this.image_size = image_size;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    //public String getThumbnail_size() {
    //    return thumbnail_size;
    //}

    public String getImage() {
        return image;
    }

    //public String getImage_size() {
    //    return image_size;
    //}
}
