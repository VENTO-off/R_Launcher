package relevant_craft.vento.r_launcher.manager.texture;

import javafx.scene.image.Image;
import relevant_craft.vento.r_launcher.utils.StringUtils;

public class LocalTexture {
    private String name;
    private String path;
    private Image avatar;
    private TextureVersion version;

    public LocalTexture(String name, String path, Image avatar, TextureVersion version) {
        this.name = StringUtils.fixString(name);;
        this.path = path;
        this.avatar = avatar;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Image getAvatar() {
        return avatar;
    }

    public TextureVersion getVersion() {
        return version;
    }
}
