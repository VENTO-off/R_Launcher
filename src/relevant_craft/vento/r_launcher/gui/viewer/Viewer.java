package relevant_craft.vento.r_launcher.gui.viewer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;

import java.awt.*;
import java.util.LinkedHashMap;

public class Viewer {

    private static Pane layout = null;
    private static LinkedHashMap<ImageView, String> screenshots = null;
    private static Thread imageLoader = null;

    public static void openViewer(LinkedHashMap<ImageView, String> screenshots, ImageView thumbnail) {
        if (VENTO.viewer != null) {
            return;
        }

        Viewer.layout = new Pane();
        Viewer.screenshots = screenshots;

        VENTO.viewer = new Stage();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        VENTO.viewer.setTitle("R-Launcher ImageViewer");
        VENTO.viewer.setWidth(screenSize.getWidth());
        VENTO.viewer.setHeight(screenSize.getHeight());
        VENTO.viewer.initStyle(StageStyle.TRANSPARENT);
        VENTO.viewer.setAlwaysOnTop(true);
        VENTO.viewer.setResizable(false);
        VENTO.viewer.setMaximized(true);

        layout.setStyle("-fx-background-color: rgba(32, 32, 32, 0.9);");
        layout.setPrefWidth(VENTO.viewer.getWidth());
        layout.setPrefHeight(VENTO.viewer.getHeight());

        Scene scene = new Scene(layout);
        scene.setFill(null);
        VENTO.viewer.setScene(scene);
        VENTO.viewer.show();

        renderThumbnail(thumbnail);
    }

    private static void renderElements(boolean isFirst, boolean isLast, ImageView thumbnail) {
        //Close Button
        JFXButton close = new JFXButton();
        close.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        close.setCursor(Cursor.HAND);
        close.setStyle("-fx-background-radius: 0;");
        close.setRipplerFill(Paint.valueOf("rgba(0, 0, 0, 0.5);"));
        close.setFocusTraversable(false);
        close.setPrefWidth(100);
        close.setPrefHeight(100);
        close.setLayoutX(layout.getPrefWidth() - close.getPrefWidth());
        close.setLayoutY(0);
        close.setOpacity(0.5);

        FontAwesomeIconView close_icon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
        close_icon.setGlyphSize(30);
        close_icon.setFill(Paint.valueOf("#ffffff"));
        close.setGraphic(close_icon);

        close.setOnMouseEntered(e -> {
            close.setStyle("-fx-background-color: rgba(0, 0, 0, 0.25);");
            close.setOpacity(1);
        });

        close.setOnMouseExited(e -> {
            close.setStyle("-fx-background-color: transparent;");
            close.setOpacity(0.5);
        });

        close.setOnAction(e -> {
            VENTO.viewer.close();
            VENTO.viewer = null;
            Viewer.imageLoader.stop();
            Viewer.imageLoader = null;
        });

        layout.getChildren().add(close);


        //Next Picture
        if (!isLast) {
            JFXButton next = new JFXButton();
            next.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            next.setCursor(Cursor.HAND);
            next.setStyle("-fx-background-radius: 0;");
            next.setRipplerFill(Paint.valueOf("rgba(0, 0, 0, 0.5);"));
            next.setFocusTraversable(false);
            next.setPrefWidth(100);
            next.setPrefHeight(layout.getPrefHeight() - 2 * close.getPrefHeight());
            next.setLayoutX(layout.getPrefWidth() - next.getPrefWidth());
            next.setLayoutY(close.getPrefHeight());
            next.setOpacity(0.5);

            FontAwesomeIconView next_icon = new FontAwesomeIconView(FontAwesomeIcon.CHEVRON_RIGHT);
            next_icon.setGlyphSize(30);
            next_icon.setFill(Paint.valueOf("#ffffff"));
            next.setGraphic(next_icon);

            next.setOnMouseEntered(e -> {
                next.setStyle("-fx-background-color: rgba(0, 0, 0, 0.25);");
                next.setOpacity(1);
            });

            next.setOnMouseExited(e -> {
                next.setStyle("-fx-background-color: transparent;");
                next.setOpacity(0.5);
            });

            next.setOnAction(e -> {
                renderThumbnail(getNext(thumbnail));
            });

            layout.getChildren().add(next);
        }

        //Previous Picture
        if (!isFirst) {
            JFXButton previous = new JFXButton();
            previous.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            previous.setCursor(Cursor.HAND);
            previous.setStyle("-fx-background-radius: 0;");
            previous.setRipplerFill(Paint.valueOf("rgba(0, 0, 0, 0.5);"));
            previous.setFocusTraversable(false);
            previous.setPrefWidth(100);
            previous.setPrefHeight(layout.getPrefHeight() - 2 * close.getPrefHeight());
            previous.setLayoutX(0);
            previous.setLayoutY(close.getPrefHeight());
            previous.setOpacity(0.5);

            FontAwesomeIconView previous_icon = new FontAwesomeIconView(FontAwesomeIcon.CHEVRON_LEFT);
            previous_icon.setGlyphSize(30);
            previous_icon.setFill(Paint.valueOf("#ffffff"));
            previous.setGraphic(previous_icon);

            previous.setOnMouseEntered(e -> {
                previous.setStyle("-fx-background-color: rgba(0, 0, 0, 0.25);");
                previous.setOpacity(1);
            });

            previous.setOnMouseExited(e -> {
                previous.setStyle("-fx-background-color: transparent;");
                previous.setOpacity(0.5);
            });

            previous.setOnAction(e -> {
                renderThumbnail(getPrevious(thumbnail));
            });

            layout.getChildren().add(previous);
        }

        layout.setOnScroll(e -> {
            if (e.getDeltaY() > 0) {
                if (!isFirst) {
                    renderThumbnail(getPrevious(thumbnail));
                }
            } else {
                if (!isLast) {
                    renderThumbnail(getNext(thumbnail));
                }
            }
        });

        VENTO.viewer.getScene().setOnKeyReleased(e -> {
            KeyCode key = e.getCode();
            if (key == KeyCode.ESCAPE) {
                VENTO.viewer.close();
                VENTO.viewer = null;
                Viewer.imageLoader.stop();
                Viewer.imageLoader = null;
            } else if (key == KeyCode.LEFT) {
                if (!isFirst) {
                    renderThumbnail(getPrevious(thumbnail));
                }
            } else if (key == KeyCode.RIGHT) {
                if (!isLast) {
                    renderThumbnail(getNext(thumbnail));
                }
            }
        });
    }

    private static void renderThumbnail(ImageView thumbnail) {
        if (imageLoader != null && imageLoader.isAlive()) {
            imageLoader.stop();
            imageLoader = null;
        }

        layout.getChildren().clear();

        ImageView imageArea = new ImageView(thumbnail.getImage());
        imageArea.setPreserveRatio(true);
        imageArea.setLayoutX(layout.getPrefWidth() / 2 - imageArea.getBoundsInParent().getWidth() / 2);
        imageArea.setLayoutY(layout.getPrefHeight() / 2 - imageArea.getBoundsInParent().getHeight() / 2);
        layout.getChildren().add(imageArea);

        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(3);
        imageArea.setEffect(blur);

        JFXSpinner loading = new JFXSpinner();
        loading.setPrefWidth(75);
        loading.setPrefHeight(75);
        loading.setLayoutX(layout.getPrefWidth() / 2 - loading.getPrefWidth() / 2);
        loading.setLayoutY(layout.getPrefHeight() / 2 - loading.getPrefHeight() / 2);
        layout.getChildren().add(loading);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(javafx.scene.paint.Color.BLACK);

        Label title = new Label();
        title.setText(VENTO.viewer.getTitle());
        title.setTextFill(Paint.valueOf("#ffffff"));
        title.setFont(Font.font("System", FontWeight.BOLD, 25));
        title.setAlignment(Pos.CENTER);
        title.setPrefWidth(400);
        title.setLayoutX(layout.getPrefWidth() / 2 - title.getPrefWidth() / 2);
        title.setLayoutY(0);
        title.setEffect(shadow);
        title.setOpacity(0.5);
        layout.getChildren().add(title);

        Label status = new Label();
        status.setText("Загрузка...");
        status.setTextFill(Paint.valueOf("#ffffff"));
        status.setFont(Font.font("System", FontWeight.BOLD, 25));
        status.setAlignment(Pos.CENTER);
        status.setPrefWidth(400);
        status.setLayoutX(layout.getPrefWidth() / 2 - status.getPrefWidth() / 2);
        status.setLayoutY(layout.getPrefHeight() - 35);
        status.setEffect(shadow);
        status.setOpacity(0.5);
        layout.getChildren().add(status);

        renderElements(isFirst(thumbnail), isLast(thumbnail), thumbnail);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                Image image = new Image(screenshots.get(thumbnail), true);
                double progress = 0;
                while (image.getProgress() < 1.0) {
                    if (progress != image.getProgress()) {
                        progress = image.getProgress();
                        Platform.runLater(() -> status.setText("Загрузка (" + ((int) (image.getProgress() * 100)) + "%)..."));
                    }
                }

                Platform.runLater(() -> {
                    loading.setVisible(false);
                    status.setText("Картинка " + getSequenceNumber(thumbnail) + " из " + screenshots.size());
                    renderImage(imageArea, image);
                });
                return null;
            }
        };
        imageLoader = new Thread(task);
        imageLoader.start();
    }

    private static void renderImage(ImageView imageArea, Image image) {
        if (image == null || image.isError()) {
            Label error = new Label();
            error.setText("Картинка недоступна");
            error.setTextFill(Paint.valueOf("#ffffff"));
            error.setFont(Font.font("System", FontWeight.BOLD, 25));
            error.setLayoutX(layout.getPrefWidth() / 2 - LauncherManager.fontLoader.computeStringWidth(error.getText(),  error.getFont()) / 2);
            error.setLayoutY(layout.getPrefHeight() / 2 - error.getFont().getSize() / 2);
            layout.getChildren().add(error);
            imageArea.setVisible(false);
            return;
        }

        imageArea.setImage(image);
        imageArea.setPreserveRatio(true);
        imageArea.setEffect(null);
        if (image.getWidth() > layout.getPrefWidth()) {
            imageArea.setFitWidth(layout.getPrefWidth());
        }
        if (image.getHeight() > layout.getPrefHeight()) {
            imageArea.setFitHeight(layout.getPrefHeight());
        }
        imageArea.setLayoutX(layout.getPrefWidth() / 2 - imageArea.getBoundsInParent().getWidth() / 2);
        imageArea.setLayoutY(layout.getPrefHeight() / 2 - imageArea.getBoundsInParent().getHeight() / 2);
    }

    private static boolean isFirst(ImageView img) {
        for (ImageView imageView : screenshots.keySet()) {
            if (imageView == img) {
                return true;
            }
            return false;
        }

        return false;
    }

    private static boolean isLast(ImageView img) {
        ImageView last = null;
        for (ImageView imageView : screenshots.keySet()) {
            last = imageView;
        }

        if (last == img) {
            return true;
        }

        return false;
    }

    private static ImageView getPrevious(ImageView img) {
        ImageView previous = null;
        for (ImageView imageView : screenshots.keySet()) {
            if (imageView == img) {
                return previous;
            }
            previous = imageView;
        }

        return null;
    }

    private static ImageView getNext(ImageView img) {
        ImageView previous = null;
        for (ImageView imageView : screenshots.keySet()) {
            if (previous == img) {
                return imageView;
            }
            previous = imageView;
        }

        return null;
    }

    private static int getSequenceNumber(ImageView img) {
        int counter = 0;
        for (ImageView imageView : screenshots.keySet()) {
            counter++;
            if (imageView == img) {
                return counter;
            }
        }

        return 0;
    }
}