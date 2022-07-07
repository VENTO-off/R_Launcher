package relevant_craft.vento.r_launcher.manager.texture;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.io.File;

public class LocalTexture_ListCell extends ListCell<LocalTexture> {
    private Pane layout = new Pane();
    private Pane image = new Pane();
    private Label title = new Label();
    private Label version = new Label();
    private JFXButton delete = new JFXButton();

    public LocalTexture_ListCell() {
        super();
        layout.setPrefWidth(221);
        layout.setPrefHeight(35);

        image.setPrefWidth(35);
        image.setPrefHeight(35);
        image.setLayoutX(0);
        image.setLayoutY(0);

        title.setFont(Font.font(15));
        title.setPrefWidth(145);
        title.setLayoutX(40);
        title.setLayoutY(0);

        version.setFont(Font.font(11));
        version.setTextFill(Paint.valueOf("#727272"));
        version.setMaxWidth(145);
        version.setLayoutX(40);
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

        layout.getChildren().setAll(image, title, version, delete);
    }

    @Override
    protected void updateItem(LocalTexture localTexture, boolean empty) {
        super.updateItem(localTexture, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            ImageView imageView = new ImageView(localTexture.getAvatar());
            imageView.setFitHeight(image.getPrefHeight());
            imageView.setFitWidth(image.getPrefWidth());
            image.getChildren().setAll(imageView);
            title.setText(localTexture.getName());

            version.setText(localTexture.getVersion().getDisplayName());
            TooltipUtils.addTooltip(version, localTexture.getVersion().getDescription());

            delete.setOnAction(event -> {
                NotifyWindow notify = new NotifyWindow(NotifyType.QUESTION);
                notify.setTitle("Подтвердите действие");
                notify.setMessage("Вы действительно хотите удалить «" + localTexture.getName() + "»?\nОтменить данное действие нельзя.");
                notify.setYesOrNo(true);
                notify.showNotify();
                if (notify.getAnswer()) {
                    new File(localTexture.getPath()).delete();
                    TextureManager.deleteLocalTexture(localTexture);
                    TextureManager.setInstalledTexturesList();
                }
            });
            TooltipUtils.addTooltip(delete, "Удалить");

            setGraphic(layout);
        }
    }
}
