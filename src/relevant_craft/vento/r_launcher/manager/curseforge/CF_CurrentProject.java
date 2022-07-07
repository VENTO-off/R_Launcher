package relevant_craft.vento.r_launcher.manager.curseforge;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.icons525.Icons525;
import de.jensd.fx.glyphs.icons525.Icons525View;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.gui.viewer.Viewer;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.mod.ModsManager;
import relevant_craft.vento.r_launcher.manager.modpack.ModpackManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.texture.TextureManager;
import relevant_craft.vento.r_launcher.manager.world.WorldManager;
import relevant_craft.vento.r_launcher.utils.DateUtils;
import relevant_craft.vento.r_launcher.utils.TooltipUtils;

import java.util.LinkedHashMap;

public class CF_CurrentProject {

    public static void setCurrentProject(Pane layout, CF_Project cf_project) {
        if (!layout.getChildren().isEmpty()) {
            layout.getChildren().clear();
        }

        Pane image = new Pane();
        image.setPrefWidth(60);
        image.setPrefHeight(60);
        image.setLayoutX(5);
        image.setLayoutY(5);

        JFXSpinner loading = new JFXSpinner();
        loading.setPrefWidth(30);
        loading.setPrefHeight(30);
        loading.setLayoutX(20);
        loading.setLayoutY(20);
        layout.getChildren().add(loading);

        CF_ImageCache.renderAvatar(cf_project, image, loading);
        layout.getChildren().add(image);

        Label title = new Label(cf_project.getTitle());
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setLayoutX(75);
        title.setLayoutY(7);
        layout.getChildren().add(title);

        Label type = new Label(CF_Installer.current_cf_project.getName());
        type.setFont(Font.font(14));
        type.setTextFill(Paint.valueOf("#b7b7b7"));
        type.setLayoutX(75);
        type.setLayoutY(35);
        layout.getChildren().add(type);

        FontAwesomeIconView close = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
        close.setGlyphSize(20);
        close.setLayoutX(529);
        close.setLayoutY(20);
        close.setCursor(Cursor.HAND);
        close.setOnMouseClicked(event -> {
            Animation animation = new Timeline(new KeyFrame(Duration.millis(75), new KeyValue(layout.opacityProperty(), 0)));
            animation.play();
            animation.setOnFinished(e -> {
                layout.setVisible(false);
                layout.getChildren().clear();
                if (CF_Installer.current_cf_project == CF_Projects.Textures) {
                    TextureManager.showElements();
                } else if (CF_Installer.current_cf_project == CF_Projects.Mods) {
                    ModsManager.showElements();
                } else if (CF_Installer.current_cf_project == CF_Projects.Worlds) {
                    WorldManager.showElements();
                } else if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {
                    ModpackManager.showElements();
                }
            });
            killScreenshotLoader();
        });
        layout.getChildren().add(close);

        JFXTabPane tabs = new JFXTabPane();
        tabs.setLayoutX(0);
        tabs.setLayoutY(70);
        tabs.setPrefWidth(layout.getPrefWidth());
        tabs.setPrefHeight(layout.getPrefHeight() - tabs.getLayoutY());
        layout.getChildren().add(tabs);

        /*  Вкладка 'Обзор'   */
        Tab overview = new Tab();
        overview.setText("Обзор");

        ScrollPane overview_scroll = new ScrollPane();
        overview_scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        overview_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        overview_scroll.setPrefWidth(layout.getPrefWidth());
        overview_scroll.setStyle("-fx-background: #ffffff;");

        overview.setContent(overview_scroll);
        tabs.getTabs().add(overview);

        Pane overview_layout = new Pane();
        overview_layout.setPrefWidth(overview_scroll.getPrefWidth());
        overview_scroll.setContent(overview_layout);

        renderOverview(overview_layout, cf_project);


        /*  Вкладка 'Установка'  */
        if (!cf_project.getFiles().isEmpty()) {
            Tab install = new Tab();
            install.setText("Установка");

            ScrollPane install_scroll = new ScrollPane();
            install_scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            install_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            install_scroll.setPrefWidth(layout.getPrefWidth());
            install_scroll.setStyle("-fx-background: #ffffff;");

            install.setContent(install_scroll);
            tabs.getTabs().add(install);

            Pane install_layout = new Pane();
            install_layout.setPrefWidth(install_scroll.getPrefWidth());
            install_scroll.setContent(install_layout);

            renderDownloadFiles(install_layout, cf_project);
        }

        /*  Вкладка 'Скриншоты'  */
        if (!cf_project.getImages().isEmpty()) {
            Tab screenshots = new Tab();
            screenshots.setText("Скриншоты");

            ScrollPane screenshots_scroll = new ScrollPane();
            screenshots_scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            screenshots_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            screenshots_scroll.setPrefWidth(layout.getPrefWidth());
            screenshots_scroll.setStyle("-fx-background: #ffffff;");

            screenshots.setContent(screenshots_scroll);
            tabs.getTabs().add(screenshots);

            Pane screenshots_layout = new Pane();
            screenshots_layout.setPrefWidth(screenshots_scroll.getPrefWidth());
            screenshots_scroll.setContent(screenshots_layout);

            renderScreenshots(screenshots_layout, cf_project);
        }


        /*  Вкладка 'Зависимости'   */
        if (!cf_project.getDependencies().isEmpty()) {
            if ((CF_Installer.current_cf_project == CF_Projects.Textures && TextureManager.hasRealDependencies(cf_project)) || (CF_Installer.current_cf_project == CF_Projects.Mods && ModsManager.hasRealDependencies(cf_project))) {
                Tab dependencies = new Tab();
                dependencies.setText("Зависимости");
                Icons525View dependenciesTooltip = new Icons525View(Icons525.INFO_CIRCLE2);
                dependenciesTooltip.setFill(Paint.valueOf("#ffffff"));
                dependenciesTooltip.setGlyphSize(16);
                TooltipUtils.addTooltip(dependenciesTooltip, "Список модов, которые требуются\nдля работы " + cf_project.getTitle() + ".", "Установить обязательно!");
                dependencies.setGraphic(dependenciesTooltip);

                JFXListView<CF_Project> dependencies_list = new JFXListView<>();
                dependencies_list.setLayoutX(0);
                dependencies_list.setLayoutY(0);
                dependencies_list.setPrefWidth(layout.getPrefWidth());
                dependencies_list.setPrefHeight(layout.getPrefHeight());
                dependencies_list.setCellFactory(param -> new CF_ListCell(null));
                dependencies_list.setOnMouseClicked(event -> dependencies_list.getSelectionModel().clearSelection());
                dependencies_list.setFocusTraversable(false);

                dependencies.setContent(dependencies_list);
                tabs.getTabs().add(dependencies);

                renderDependencies(dependencies_list, cf_project);
            }
        }


        /*  Вкладка 'Установленные моды'  */
        if (!cf_project.getDependencies().isEmpty()) {
            if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {
                Tab installedMods = new Tab();
                installedMods.setText("Установленные моды" + " (" + cf_project.getDependencies().size() + ")");

                JFXListView<String> mods_list = new JFXListView<>();
                mods_list.setLayoutX(0);
                mods_list.setLayoutY(0);
                mods_list.setPrefWidth(layout.getPrefWidth());
                mods_list.setPrefHeight(layout.getPrefHeight());
                mods_list.setOnMouseClicked(event -> mods_list.getSelectionModel().clearSelection());
                mods_list.setFocusTraversable(false);
                mods_list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        return new JFXListCell<String>() {
                            @Override
                            protected void updateItem(String mod, boolean empty) {
                                super.updateItem(mod, empty);
                                if (empty) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    setText(mod);
                                    setFont(Font.font(15));
                                }
                            }
                        };
                    }
                });

                installedMods.setContent(mods_list);
                tabs.getTabs().add(installedMods);

                renderInstalledMods(mods_list, cf_project);
            }
        }

        if (CF_Installer.current_cf_project == CF_Projects.Textures) {
            TextureManager.hideElements(null);
        } else if (CF_Installer.current_cf_project == CF_Projects.Mods) {
            ModsManager.hideElements(null);
        } else if (CF_Installer.current_cf_project == CF_Projects.Worlds) {
            WorldManager.hideElements(null);
        } else if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {
            ModpackManager.hideElements(null);
        }
        layout.setVisible(true);
        layout.setOpacity(0);

        Animation animation = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(layout.opacityProperty(), 1)));
        animation.play();
    }

    private static void renderOverview(Pane overview_layout, CF_Project cf_project) {
        Label date = new Label();
        date.setText("Последнее обновление:");
        date.setFont(Font.font("System", FontWeight.BOLD, 16));
        date.setLayoutX(10);
        date.setLayoutY(10);
        overview_layout.getChildren().add(date);

        Label date_value = new Label();
        date_value.setText(DateUtils.translateDateToRussian(cf_project.getDate()));
        date_value.setFont(Font.font(16));
        date_value.setLayoutX(date.getLayoutX() + LauncherManager.fontLoader.computeStringWidth(date.getText(), date.getFont()) + 10);
        date_value.setLayoutY(date.getLayoutY());
        overview_layout.getChildren().add(date_value);

        Label downloads = new Label();
        downloads.setText("Всего скачиваний:");
        downloads.setFont(Font.font("System", FontWeight.BOLD, 16));
        downloads.setLayoutX(10);
        downloads.setLayoutY(30);
        overview_layout.getChildren().add(downloads);

        Label downloads_value = new Label();
        downloads_value.setText(cf_project.getDownloads());
        downloads_value.setFont(Font.font(16));
        downloads_value.setLayoutX(downloads.getLayoutX() + LauncherManager.fontLoader.computeStringWidth(downloads.getText(), downloads.getFont()) + 10);
        downloads_value.setLayoutY(downloads.getLayoutY());
        overview_layout.getChildren().add(downloads_value);

        Label categories = new Label();
        categories.setText("Категории:");
        categories.setFont(Font.font("System", FontWeight.BOLD, 16));
        categories.setLayoutX(10);
        categories.setLayoutY(50);
        overview_layout.getChildren().add(categories);

        int Y_POSITION = 70;

        for (String category : cf_project.getCategories()) {
            if (CF_Installer.current_cf_project == CF_Projects.Textures) {
                category = TextureManager.translateCategory(category);
            } else if (CF_Installer.current_cf_project == CF_Projects.Mods) {
                category = ModsManager.translateCategory(category);
            } else if (CF_Installer.current_cf_project == CF_Projects.Worlds) {
                category = WorldManager.translateCategory(category);
            } else if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {
                category = ModpackManager.translateCategory(category);
            }

            Label current_category = new Label();
            current_category.setText("— " + category.trim());
            current_category.setFont(Font.font(16));
            current_category.setLayoutX(10);
            current_category.setLayoutY(Y_POSITION);
            overview_layout.getChildren().add(current_category);

            Y_POSITION += 20;
        }

        Label description = new Label();
        description.setText("Описание:");
        description.setFont(Font.font("System", FontWeight.BOLD, 16));
        description.setLayoutX(10);
        description.setLayoutY(Y_POSITION + 20);
        overview_layout.getChildren().add(description);

        Label description_value = new Label();
        description_value.setText(cf_project.getDescription());
        description_value.setFont(Font.font(16));
        description_value.setPrefWidth(overview_layout.getPrefWidth() - 20);
        description_value.setLayoutX(10);
        description_value.setLayoutY(description.getLayoutY() + 20);
        description_value.setWrapText(true);
        description_value.setAlignment(Pos.TOP_LEFT);
        //description_value.setStyle("-fx-line-spacing: -3;");
        description_value.setTextOverrun(OverrunStyle.CLIP);
        description_value.setPrefHeight(Math.ceil(LauncherManager.fontLoader.computeStringWidth(description_value.getText(), description_value.getFont()) / (overview_layout.getPrefWidth() - 20)) * 25);
        overview_layout.getChildren().add(description_value);
    }

    private static void renderDownloadFiles(Pane install_layout, CF_Project cf_project) {
        int Y_POSITION = 10;

        for (CF_Downloads cf_downloads : cf_project.getFiles()) {
            Pane status = new Pane();
            status.setPrefWidth(30);
            status.setPrefHeight(30);
            status.setLayoutX(10);
            status.setLayoutY(Y_POSITION);
            ImageView imageView = new ImageView(PictureManager.loadImage(cf_downloads.getStatus() + ".png"));
            imageView.setFitHeight(30);
            imageView.setFitHeight(30);
            status.getChildren().setAll(imageView);

            if (cf_downloads.getStatus().equals("Release")) {
                TooltipUtils.addTooltip(status, "Релиз");
            } else if (cf_downloads.getStatus().equals("Beta")) {
                TooltipUtils.addTooltip(status, "Бета");
            } else if (cf_downloads.getStatus().equals("Alpha")) {
                TooltipUtils.addTooltip(status, "Альфа");
            }

            Label name = new Label(cf_downloads.getName());
            name.setFont(Font.font(15));
            name.setPrefWidth(355);
            name.setLayoutX(50);
            name.setLayoutY(Y_POSITION - 4);

            Label info = new Label("Размер: " + cf_downloads.getSize() + "  |  " + "Загружено: " + DateUtils.translateDateToRussian(cf_downloads.getUploaded()));
            info.setFont(Font.font(11));
            info.setTextFill(Paint.valueOf("#727272"));
            info.setPrefWidth(355);
            info.setLayoutX(50);
            info.setLayoutY(Y_POSITION + 17);

            Label progress = new Label();
            progress.setFont(Font.font(11));
            progress.setTextFill(info.getTextFill());
            progress.setPrefWidth(info.getPrefWidth() / 2);
            progress.setLayoutX(info.getLayoutX() + info.getPrefWidth() / 2);
            progress.setLayoutY(info.getLayoutY() - 10);
            progress.setAlignment(Pos.CENTER_RIGHT);
            progress.setOpacity(0);

            JFXButton install = new JFXButton();
            install.setButtonType(JFXButton.ButtonType.RAISED);
            install.setFont(Font.font("System", FontWeight.BOLD, 14.0));
            install.setTextFill(Paint.valueOf("#ffffff"));
            install.setRipplerFill(Paint.valueOf("#beff85"));
            install.setText("Установить");
            install.setStyle("-fx-background-color: #7FCF3D;");
            install.setCursor(Cursor.HAND);
            install.setPrefWidth(115);
            install.setLayoutX(415);
            install.setLayoutY(Y_POSITION);
            install.setOnAction(event -> {
                if (CF_Installer.current_cf_project == CF_Projects.Textures) {
                    CF_Installer.install_CF_Project(install_layout, name, info, progress, install, cf_downloads.getLink(), CF_Installer.current_modpack.getPath() + '/' + TextureManager.getTextureVersion().getFolder() + '/' + cf_downloads.getName() + (cf_downloads.getName().endsWith(".zip") ? "" : ".zip"));
                } else if (CF_Installer.current_cf_project == CF_Projects.Mods) {
                    CF_Installer.install_CF_Project(install_layout, name, info, progress, install, cf_downloads.getLink(), CF_Installer.current_modpack.getPath() + '/' + "mods" + '/' + cf_downloads.getName() + (cf_downloads.getName().endsWith(".jar") ? "" : ".jar"));
                } else if (CF_Installer.current_cf_project == CF_Projects.Worlds) {
                    CF_Installer.install_CF_Project(install_layout, name, info, progress, install, cf_downloads.getLink(), CF_Installer.current_modpack.getPath() + '/' + "saves" + '/' + cf_downloads.getName() + (cf_downloads.getName().endsWith(".zip") ? "" : ".zip"));
                } else if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {
                    CF_Installer.install_CF_Project(install_layout, name, info, progress, install, cf_downloads.getLink(), SettingsManager.minecraftDirectory + '/' + "versions" + '/' + cf_downloads.getName() + (cf_downloads.getName().endsWith(".zip") ? "" : ".zip"));
                }
            });

            install_layout.getChildren().addAll(status, name, info, progress, install);

            Y_POSITION += 40;
        }
    }

    private static Thread screenshots_loader = null;

    private static void renderScreenshots(Pane screenshots_layout, CF_Project cf_project) {
        JFXSpinner loading = new JFXSpinner();
        loading.setPrefWidth(38);
        loading.setPrefHeight(38);
        loading.setLayoutX(screenshots_layout.getPrefWidth() / 2 - loading.getPrefWidth() / 2);
        loading.setLayoutY(180 / 2 - loading.getPrefHeight() / 2);
        screenshots_layout.getChildren().add(loading);

        Label loading_text = new Label();
        loading_text.setText("Загрузка картинок (" + "0" + "%)...");
        loading_text.setFont(Font.font(16));
        loading_text.setAlignment(Pos.CENTER);
        loading_text.setPrefWidth(200);
        loading_text.setLayoutX(screenshots_layout.getPrefWidth() / 2 - loading_text.getPrefWidth() / 2);
        loading_text.setLayoutY(loading.getLayoutY() + loading.getPrefHeight() + 10);
        screenshots_layout.getChildren().add(loading_text);

        FontAwesomeIconView zoom = new FontAwesomeIconView(FontAwesomeIcon.SEARCH_PLUS);
        zoom.setGlyphSize(75);
        zoom.setFill(Paint.valueOf("#ffffff"));
        zoom.setOpacity(0);
        zoom.setCursor(Cursor.HAND);
        zoom.setPickOnBounds(false);
        screenshots_layout.getChildren().add(zoom);

        FontAwesomeIconView zoom_text = new FontAwesomeIconView();
        zoom_text.setText("Нажмите, чтобы увеличить");
        zoom_text.setGlyphSize(14);
        zoom_text.setFill(Paint.valueOf("#ffffff"));
        zoom_text.setOpacity(0);
        zoom_text.setCursor(Cursor.HAND);
        zoom_text.setPickOnBounds(false);
        screenshots_layout.getChildren().add(zoom_text);

        LinkedHashMap<ImageView, String> screenshots = new LinkedHashMap<>();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                for (CF_Images cf_images : cf_project.getImages()) {
                    Image thumbnail = new Image(cf_images.getThumbnail());
                    Platform.runLater(() -> {
                        ImageView imageView = new ImageView(thumbnail);

                        if (thumbnail.getWidth() > screenshots_layout.getPrefWidth()) {
                            imageView.setFitWidth(screenshots_layout.getPrefWidth() - 50);
                            imageView.setFitHeight((screenshots_layout.getPrefWidth() - 50) / (thumbnail.getWidth() / thumbnail.getHeight()));
                        } else {
                            imageView.setFitWidth(thumbnail.getWidth());
                            imageView.setFitHeight(thumbnail.getHeight());
                        }

                        imageView.setLayoutX(screenshots_layout.getPrefWidth() / 2 - imageView.getFitWidth() / 2);

                        if (screenshots.isEmpty()) {
                            imageView.setLayoutY(10);
                        } else {
                            ImageView last = null;
                            for (ImageView img : screenshots.keySet()) {
                                last = img;
                            }
                            imageView.setLayoutY(last.getLayoutY() + last.getFitHeight() + 10);
                        }

                        imageView.setCursor(Cursor.HAND);

                        imageView.setOnMouseEntered(e -> {
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setBrightness(-0.75);
                            imageView.setEffect(colorAdjust);

                            zoom.setLayoutX(imageView.getLayoutX() + imageView.getFitWidth() / 2 - zoom.getGlyphSize().intValue() / 2);
                            zoom.setLayoutY(imageView.getLayoutY() + imageView.getFitHeight() / 2 + zoom.getGlyphSize().intValue() /4);
                            zoom.toFront();
                            zoom.setOpacity(1);

                            zoom_text.setLayoutX(imageView.getLayoutX() + imageView.getFitWidth() / 2 - LauncherManager.fontLoader.computeStringWidth(zoom_text.getText(), zoom_text.getFont()) / 2);
                            zoom_text.setLayoutY(imageView.getLayoutY() + imageView.getFitHeight() - zoom_text.getGlyphSize().intValue() / 2);
                            zoom_text.toFront();
                            zoom_text.setOpacity(1);
                        });

                        imageView.setOnMouseExited(e -> {
                            if (e.getX() > 0 && e.getX() < imageView.getFitWidth() && e.getY() > 0 && e.getY() < imageView.getFitHeight()) {
                                return;
                            }
                            imageView.setEffect(null);
                            zoom.setOpacity(0);
                            zoom_text.setOpacity(0);
                        });

                        imageView.setOnMouseClicked(e -> {
                            Viewer.openViewer(screenshots, imageView);
                        });

                        screenshots.put(imageView, cf_images.getImage());
                        loading_text.setText("Загрузка картинок (" + (screenshots.size() * 100 / cf_project.getImages().size()) + "%)...");
                    });
                }
                Platform.runLater(() -> {
                    screenshots_layout.getChildren().remove(loading);
                    screenshots_layout.getChildren().remove(loading_text);
                    for (ImageView imageView : screenshots.keySet()) {
                        screenshots_layout.getChildren().add(imageView);
                    }
                });
                return null;
            }
        };
        screenshots_loader = new Thread(task);
        screenshots_loader.start();
    }

    private static void renderDependencies(JFXListView dependencies_layout, CF_Project cf_project) {
        for (CF_Dependence cf_dependence : cf_project.getDependencies()) {
            if (ModsManager.getModByHref(cf_dependence.getHref()) != null) {
                dependencies_layout.getItems().add(ModsManager.getModByHref(cf_dependence.getHref()));
            }
        }
    }

    private static void renderInstalledMods(JFXListView mods_layout, CF_Project cf_project) {
        for (CF_Dependence cf_dependence : cf_project.getDependencies()) {
            mods_layout.getItems().add(cf_dependence.getTitle());
        }
    }

    public static void killScreenshotLoader() {
        if (screenshots_loader != null && screenshots_loader.isAlive()) {
            screenshots_loader.stop();
            screenshots_loader = null;
        }
    }
}
