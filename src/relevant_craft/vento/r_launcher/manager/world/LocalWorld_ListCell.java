package relevant_craft.vento.r_launcher.manager.world;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.utils.FileUtils;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.io.File;

public class LocalWorld_ListCell extends ListCell<LocalWorld> {
    private Pane layout = new Pane();
    private Label title = new Label();
    private Label version = new Label();
    private JFXButton delete = new JFXButton();

    public LocalWorld_ListCell() {
        super();
        layout.setPrefWidth(221);
        layout.setPrefHeight(35);

        title.setFont(Font.font(15));
        title.setPrefWidth(190);
        title.setLayoutX(0);
        title.setLayoutY(0);

        version.setFont(Font.font(11));
        version.setTextFill(Paint.valueOf("#727272"));
        version.setMaxWidth(190);
        version.setLayoutX(0);
        version.setLayoutY(18);

        delete.setFont(Font.font(14));
        delete.setRipplerFill(Paint.valueOf("#d3d3d3"));
        delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        delete.setAlignment(Pos.CENTER);
        delete.setCursor(Cursor.HAND);
        delete.setPrefHeight(31);
        delete.setLayoutX(188);
        delete.setLayoutY(2);
        FontAwesomeIconView close_icon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
        close_icon.setFill(Paint.valueOf("#ff0000"));
        delete.setGraphic(close_icon);

        layout.getChildren().setAll(title, version, delete);
    }

    @Override
    protected void updateItem(LocalWorld localWorld, boolean empty) {
        super.updateItem(localWorld, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            title.setText(localWorld.getName());

            version.setText(localWorld.getVersion());
            TooltipUtils.addTooltip(version, "Версия мира");

            delete.setOnAction(event -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.QUESTION);
                notify.setTitle("Подтвердите действие");
                notify.setMessage("Вы действительно хотите удалить «" + localWorld.getName() + "»?\nОтменить данное действие нельзя.");
                notify.setYesOrNo(true);
                notify.showNotify();
                if (notify.getAnswer()) {
                    FileUtils.removeDirectory(new File(localWorld.getPath()));
                    WorldManager.deleteLocalWorld(localWorld);
                    WorldManager.setInstalledWorldsList();
                }
            });
            TooltipUtils.addTooltip(delete, "Удалить");

            setGraphic(layout);
        }
    }
}

