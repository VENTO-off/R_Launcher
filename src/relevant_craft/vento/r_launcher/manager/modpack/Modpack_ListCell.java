package relevant_craft.vento.r_launcher.manager.modpack;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class Modpack_ListCell extends ListCell<Modpack> {
    private Pane layout = new Pane();
    private Label name = new Label();
    private Label description = new Label();

    public Modpack_ListCell() {
        super();
        //layout.setPrefWidth(280);
        layout.setPrefHeight(35);

        name.setFont(Font.font(15));
        name.setLayoutX(2);
        name.setLayoutY(0);

        description.setFont(Font.font(11));
        description.setTextFill(Paint.valueOf("#727272"));
        description.setLayoutX(2);
        description.setLayoutY(19);

        layout.getChildren().addAll(name, description);
    }

    @Override
    protected void updateItem(Modpack modpack, boolean empty) {
        super.updateItem(modpack, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            name.setText(modpack.getName());
            description.setText("Minecraft " + modpack.getVersion() + "  |  " + "Установлено модов: " + modpack.getMods());

            setGraphic(layout);
        }
    }
}
