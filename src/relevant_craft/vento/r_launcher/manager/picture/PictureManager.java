package relevant_craft.vento.r_launcher.manager.picture;

import javafx.scene.image.Image;

public class PictureManager {

    public static Image loadImage(String name) {
        return new Image(PictureManager.class.getResourceAsStream("pictures/" + name));
    }

}
