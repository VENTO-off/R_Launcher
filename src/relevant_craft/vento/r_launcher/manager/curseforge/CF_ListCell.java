package relevant_craft.vento.r_launcher.manager.curseforge;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import relevant_craft.vento.r_launcher.manager.mod.ModsManager;
import relevant_craft.vento.r_launcher.manager.modpack.ModpackManager;
import relevant_craft.vento.r_launcher.manager.texture.TextureManager;
import relevant_craft.vento.r_launcher.manager.world.WorldManager;
import relevant_craft.vento.r_launcher.utils.DateUtils;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CF_ListCell extends ListCell<CF_Project> {
    private Pane layout = new Pane();
    private Pane image = new Pane();
    private JFXSpinner loader = new JFXSpinner();
    private Label title = new Label();
    private Label description = new Label();
    private Label date = new Label();
    private FontAwesomeIconView clock_icon = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT);
    private Label downloads = new Label();
    private FontAwesomeIconView download_icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
    private JFXButton download = new JFXButton();
    private JFXTextField search;

    public CF_ListCell(JFXTextField search) {
        super();
        layout.setPrefWidth(521);
        layout.setPrefHeight(100);

        image.setPrefWidth(80);
        image.setPrefHeight(80);
        image.setLayoutX(5);
        image.setLayoutY(10);

        loader.setPrefWidth(30);
        loader.setPrefHeight(30);
        loader.setLayoutX(29);
        loader.setLayoutY(34);

        title.setFont(Font.font("System", FontWeight.BOLD, 15.0));
        title.setPrefWidth(320);
        title.setLayoutX(90);
        title.setLayoutY(5);

        description.setFont(Font.font(14.0));
        description.setWrapText(true);
        description.setAlignment(Pos.TOP_LEFT);
        description.setStyle("-fx-line-spacing: -4;");
        description.setPrefWidth(320);
        description.setPrefHeight(80);
        description.setLayoutX(90);
        description.setLayoutY(24);

        date.setFont(Font.font(14.0));
        date.setAlignment(Pos.CENTER_RIGHT);
        date.setPrefWidth(96);
        date.setLayoutX(405);
        date.setLayoutY(16);
        TooltipUtils.addTooltip(date, "Последнее обновление");

        clock_icon.setGlyphSize(14);
        clock_icon.setLayoutX(505);
        clock_icon.setLayoutY(31);
        TooltipUtils.addTooltip(clock_icon, "Последнее обновление");

        downloads.setFont(Font.font(14.0));
        downloads.setAlignment(Pos.CENTER_RIGHT);
        downloads.setPrefWidth(96);
        downloads.setLayoutX(405);
        downloads.setLayoutY(36);
        TooltipUtils.addTooltip(downloads, "Количество скачиваний");

        download_icon.setGlyphSize(14);
        download_icon.setLayoutX(505);
        download_icon.setLayoutY(53);
        TooltipUtils.addTooltip(download_icon, "Количество скачиваний");

        download.setButtonType(JFXButton.ButtonType.RAISED);
        download.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        download.setTextFill(Paint.valueOf("#ffffff"));
        download.setText("Подробнее");
        download.setRipplerFill(Paint.valueOf("#ffe071"));
        download.setStyle("-fx-background-color: #F7C50E;");
        download.setCursor(Cursor.HAND);
        download.setPrefWidth(115);
        download.setLayoutX(403);
        download.setLayoutY(60);

        this.search = search;

        layout.getChildren().addAll(image, loader, title, description, date, clock_icon, downloads, download_icon, download);
    }

    @Override
    protected void updateItem(CF_Project cf_project, boolean empty) {
        super.updateItem(cf_project, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            CF_ImageCache.renderAvatar(cf_project, image, loader);
            if (search == null || search.getText() == null || search.getText().isEmpty()) {
                title.setGraphic(null);
                title.setText(cf_project.getTitle());
            } else {
                try {
                    TextFlow textFlow = new TextFlow();
                    String[] letters = cf_project.getTitle().replaceAll("(?i)" + Pattern.quote(search.getText()), "%").split("");
                    List<String> searched = searchOccurrences(cf_project.getTitle(), search.getText().toLowerCase());
                    int index = 0;
                    for (String letter : letters) {
                        if (letter.equals("%")) {
                            Text text = new Text(searched.get(index));
                            text.setFont(Font.font("System", FontWeight.BOLD, 15.0));
                            text.setFill(Paint.valueOf("#1cb521"));
                            textFlow.getChildren().add(text);
                            index++;
                        } else {
                            Text text = new Text(letter);
                            text.setFont(Font.font("System", FontWeight.BOLD, 15.0));
                            text.setFill(Paint.valueOf("#000000"));
                            textFlow.getChildren().add(text);
                        }
                    }
                    title.setGraphic(textFlow);
                    title.setText(null);
                } catch (Exception e) {
                    title.setGraphic(null);
                    title.setText(cf_project.getTitle());
                }
            }
            description.setText(cf_project.getDescription_RU());
            date.setText(DateUtils.translateDateToRussian(cf_project.getDate()));
            downloads.setText(cf_project.getDownloads());
            download.setOnAction(event -> {
                if (CF_Installer.current_cf_project == CF_Projects.Textures) {
                    CF_CurrentProject.setCurrentProject(TextureManager.current_cf_project, cf_project);
                } else if (CF_Installer.current_cf_project == CF_Projects.Mods) {
                    CF_CurrentProject.setCurrentProject(ModsManager.current_cf_project, cf_project);
                } else if (CF_Installer.current_cf_project == CF_Projects.Worlds) {
                    CF_CurrentProject.setCurrentProject(WorldManager.current_cf_project, cf_project);
                } else if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {
                    CF_CurrentProject.setCurrentProject(ModpackManager.current_cf_project, cf_project);
                }
            });

            setGraphic(layout);
        }
    }

    private List<String> searchOccurrences(String title, String search) throws Exception {
        try {
            List<String> searched = new ArrayList<>();
            String[] letters = title.split("");
            int search_index = 0;
            for (String letter : letters) {
                if (letter.equalsIgnoreCase(String.valueOf(search.toLowerCase().charAt(search_index)))) {
                    if (search_index == 0) {
                        searched.add(letter);
                    } else {
                        searched.set(searched.size() - 1, searched.get(searched.size() - 1) + letter);
                    }
                    search_index++;
                    if (search_index >= search.length()) {
                        search_index = 0;
                    }
                } else {
                    if (search_index > 0 && search_index < search.length()) {
                        if (!searched.isEmpty()) {
                            searched.remove(searched.size() - 1);
                        }
                    }
                    search_index = 0;
                }
            }

            return searched;
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
