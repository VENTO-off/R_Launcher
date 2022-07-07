package relevant_craft.vento.r_launcher.manager.advertisement;

public class AdvertisementServer {
    private String address;
    private String name;
    private String description;
    private String avatar;

    AdvertisementServer() {}

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
