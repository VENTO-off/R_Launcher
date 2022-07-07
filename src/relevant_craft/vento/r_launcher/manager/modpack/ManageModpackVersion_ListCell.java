package relevant_craft.vento.r_launcher.manager.modpack;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import relevant_craft.vento.r_launcher.manager.version.Version;

public class ManageModpackVersion_ListCell extends ListCell<Version> {
    private Pane layout = new Pane();
    private Label version = new Label();
    private Label modifications = new Label();

    public ManageModpackVersion_ListCell() {
        super();
        layout.setPrefWidth(275);
        layout.setPrefHeight(25);

        version.setFont(Font.font(15.0));
        version.setPrefHeight(layout.getPrefHeight());
        version.setLayoutX(5);

        modifications.setFont(Font.font(12.0));
        modifications.setDisable(true);
        modifications.setPrefHeight(layout.getPrefHeight());
        modifications.setPrefWidth(175);
        modifications.setLayoutX(layout.getPrefWidth() - modifications.getPrefWidth() - 5 + 25);
        modifications.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(version, modifications);
    }


    @Override
    protected void updateItem(Version ver, boolean empty) {
        super.updateItem(ver, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            version.setText(ver.displayName);
            modifications.setText(ManageModpackManager.getModificationsForVersion(ver));

            setGraphic(layout);
        }
    }
}
