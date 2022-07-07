package relevant_craft.vento.r_launcher.manager.tutorial;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import relevant_craft.vento.r_launcher.VENTO;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager {

    private static List<TutorialNotify> tutorialNotifies;

    public static void showTutorial() {
        tutorialNotifies = new ArrayList<>();

        TutorialNotify accounts = new TutorialNotify();
        accounts.setCoords(VENTO.window.getWidth() - accounts.getWidth() - 100, (accounts.getHeight() + 5) * -1);
        accounts.setText("1. Создайте аккаунт");
        accounts.setIcon(FontAwesomeIcon.USER_CIRCLE_ALT);
        accounts.renderBottomArrow(0);
        accounts.renderNotify(1500);
        tutorialNotifies.add(accounts);

        TutorialNotify version = new TutorialNotify();
        version.setCoords(60, VENTO.window.getHeight() + 5);
        version.setText("2. Выберите версию");
        version.setIcon(FontAwesomeIcon.INDENT);
        version.renderTopArrow(50);
        version.renderNotify(1600);
        tutorialNotifies.add(version);

        TutorialNotify play = new TutorialNotify();
        play.setCoords(300, VENTO.window.getHeight() + 5);
        play.setText("3. Запустите игру");
        play.setIcon(FontAwesomeIcon.GAMEPAD);
        play.renderTopArrow(-50);
        play.renderNotify(1700);
        tutorialNotifies.add(play);
    }

    public static void updateTutorialNotifies() {
        if (tutorialNotifies == null) {
            return;
        }

        for (TutorialNotify notify : tutorialNotifies) {
            notify.updateCoords();
        }
    }

    public static void closeNotifies() {
        if (tutorialNotifies == null) {
            return;
        }

        for (TutorialNotify notify : tutorialNotifies) {
            notify.close();
        }
    }
}
