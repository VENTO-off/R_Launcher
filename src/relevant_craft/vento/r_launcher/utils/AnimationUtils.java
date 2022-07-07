package relevant_craft.vento.r_launcher.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {

    public static void applyEnterAnimation(Node node) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(75));
        transition.setNode(node);
        transition.setFromValue(0.75);
        transition.setToValue(1.0);
        transition.play();
    }

    public static void applyExitAnimation(Node node) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(75));
        transition.setNode(node);
        transition.setFromValue(1.0);
        transition.setToValue(0.75);
        transition.play();
    }
}
