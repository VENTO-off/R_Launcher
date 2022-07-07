package relevant_craft.vento.r_launcher.gui.notify;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.notify.NotifyManager;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class Notify implements Initializable {

    public static AnchorPane loadNotifyWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane notify = VENTO.loadGUI(VENTO.GUI.Notify.path);
        notify.setLayoutX(width.getValue() / 2 - notify.getPrefWidth() / 2);
        notify.setLayoutY(height.getValue() / 2 - notify.getPrefHeight() / 2);

        return notify;
    }

    public void onClose_Notify(MouseEvent mouseEvent) {
        NotifyManager.deleteNotify(NotifyManager.currentNotify);
        NotifyManager.currentNotify = null;
        VENTO.closeGUI();
    }

    @FXML
    private FontAwesomeIconView notifyTitle;
    @FXML
    private ScrollPane fullText;
    @FXML
    private JFXButton openUrl;
    @FXML
    private Label dateText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notifyTitle.setText(NotifyManager.currentNotify.getTitle());

        NotifyText text = new NotifyText(fullText);
        String[] lines = NotifyManager.currentNotify.getFull_text().replace("\\n", System.lineSeparator()).split(System.lineSeparator());
        for (String line : lines) {
            text.addMessage(line);
        }

        Date date = new Date(NotifyManager.currentNotify.getDate());
        dateText.setText(new SimpleDateFormat("dd MMMMM yyyy", new Locale("ru")).format(date));

        String url = NotifyManager.currentNotify.getUrl();
        if (url.equals("none")) {
            openUrl.setVisible(false);
        } else {
            String link;
            if (url.contains("#")) {
                String[] data = url.split("#");
                openUrl.setText(data[0]);
                link = data[1];
            } else {
                link = url;
            }

            openUrl.setOnAction(e -> {
                DesktopUtils.openUrl(link);
            });
        }
    }
}
