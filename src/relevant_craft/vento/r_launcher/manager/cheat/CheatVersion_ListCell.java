package relevant_craft.vento.r_launcher.manager.cheat;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class CheatVersion_ListCell extends ListCell<CheatVersion> {
    private Pane layout = new Pane();
    private Label version = new Label();
    private Label amount = new Label();

    public CheatVersion_ListCell() {
        super();
        layout.setPrefWidth(300);
        layout.setPrefHeight(25);

        version.setFont(Font.font(15.0));
        version.setPrefHeight(layout.getPrefHeight());
        version.setLayoutX(5);

        amount.setFont(Font.font(12.0));
        amount.setDisable(true);
        amount.setPrefHeight(layout.getPrefHeight());
        amount.setPrefWidth(100);
        amount.setLayoutX(layout.getPrefWidth() - amount.getPrefWidth() - 5 + 25);
        amount.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(version, amount);
    }

    @Override
    protected void updateItem(CheatVersion cheatVersion, boolean empty) {
        super.updateItem(cheatVersion, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            version.setText(cheatVersion.getVersion());
            amount.setText(cheatVersion.getAmount() + " чит(ов)");

            setGraphic(layout);
        }
    }
}
