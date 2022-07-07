package relevant_craft.vento.r_launcher.manager.curseforge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.extract.ExtractManager;
import relevant_craft.vento.r_launcher.manager.modpack.ManageModpackManager;
import relevant_craft.vento.r_launcher.manager.modpack.Modpack;
import relevant_craft.vento.r_launcher.manager.mod.ModsManager;
import relevant_craft.vento.r_launcher.manager.modpack.ModpackManager;
import relevant_craft.vento.r_launcher.manager.socket.SocketAnswer;
import relevant_craft.vento.r_launcher.manager.socket.SocketManager;
import relevant_craft.vento.r_launcher.manager.socket.commands.curseforge_download;
import relevant_craft.vento.r_launcher.manager.texture.TextureManager;
import relevant_craft.vento.r_launcher.manager.version.Version;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.manager.world.WorldManager;
import relevant_craft.vento.r_launcher.utils.FileUtils;
import relevant_craft.vento.r_launcher.utils.JsonUtils;
import relevant_craft.vento.r_launcher.utils.StringUtils;
import relevant_craft.vento.r_launcher.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class CF_Installer {

    private static Thread downloader = null;

    public static CF_Projects current_cf_project = null;
    public static Modpack current_modpack = null;

    public static void install_CF_Project(Pane layout, Label name, Label info, Label progress, JFXButton install, String url, String destination) {
        final int elements = layout.getChildren().size();

        if (downloader != null && downloader.isAlive()) {
            NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
            notify.setTitle("Идёт установка");
            notify.setMessage("Сейчас идет установка другого файла...\nПожалуйста, дождитесь окончания, чтобы скачать другой файл.");
            notify.showNotify();
            return;
        }

        String fixedDestination = destination.substring(0, destination.lastIndexOf("/") + 1) + StringUtils.fixStringForFile(destination.substring(destination.lastIndexOf("/")));

        File file = new File(fixedDestination);
        if (current_cf_project == CF_Projects.ModPacks) {
            file = new File(destination.substring(0, destination.lastIndexOf('.')));
        }
        if (file.exists()) {
            NotifyWindow notify = new NotifyWindow(NotifyType.QUESTION);
            notify.setTitle("Файл скачан");
            notify.setMessage("Файл «" + destination.substring(destination.lastIndexOf('/') + 1) + "» уже существует.\nПерекачать его заново?");
            notify.setYesOrNo(true);
            notify.showNotify();
            if (!notify.getAnswer()) {
                return;
            }
        }

        Animation animationName = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(name.prefWidthProperty(), name.getPrefWidth() / 2)));
        animationName.setDelay(Duration.millis(50));
        animationName.play();

        JFXProgressBar progressBar = new JFXProgressBar();
        progressBar.setPrefHeight(5);
        progressBar.setPrefWidth(info.getPrefWidth());
        progressBar.setLayoutX(info.getLayoutX());
        progressBar.setLayoutY(info.getLayoutY() + 7);
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Animation animationInfo = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(info.opacityProperty(), 0)));
        animationInfo.setDelay(Duration.millis(50));
        animationInfo.play();
        animationInfo.setOnFinished(e -> {
            info.setVisible(false);
            layout.getChildren().add(progressBar);
        });

        Animation animationProgress = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(progress.opacityProperty(), 1)));
        animationProgress.setDelay(Duration.millis(50));
        animationProgress.play();

        install.setVisible(false);

        JFXButton cancel = new JFXButton();
        cancel.setButtonType(JFXButton.ButtonType.RAISED);
        cancel.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        cancel.setTextFill(Paint.valueOf("#ffffff"));
        cancel.setRipplerFill(Paint.valueOf("#ff9b8b"));
        cancel.setText("Отменить");
        cancel.setStyle("-fx-background-color: #F26B50;");
        cancel.setCursor(Cursor.HAND);
        cancel.setPrefWidth(install.getPrefWidth());
        cancel.setLayoutX(install.getLayoutX());
        cancel.setLayoutY(install.getLayoutY());
        cancel.setOnAction(event -> {
            finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
        });
        layout.getChildren().add(cancel);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Platform.runLater(() -> progress.setText("Подготовка..."));
                    SocketAnswer socketAnswer = SocketManager.connectSocket(curseforge_download.createRequest(url));
                    if (socketAnswer == null) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка скачивания");
                            notify.setMessage("Не удалось скачать файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                            notify.showNotify();
                        });
                        finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
                        return null;
                    } else if (socketAnswer.isError()) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка скачивания");
                            notify.setMessage(socketAnswer.getResponse());
                            notify.showNotify();
                        });
                        finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
                        return null;
                    }

                    DownloadManager.downloadCheatOrСFFile(socketAnswer.getResponse(), fixedDestination, progressBar, progress);
                    if (CF_Installer.current_cf_project == CF_Projects.Textures) {                                      //install texture
                        Platform.runLater(() -> {
                            TextureManager.addLocalTexture(new File(fixedDestination), TextureManager.getTextureVersion());
                            TextureManager.setInstalledTexturesList();
                        });
                    } else if (CF_Installer.current_cf_project == CF_Projects.Mods) {                                   //install mod
                        Platform.runLater(() -> {
                            ModsManager.addLocalMod(new File(fixedDestination));
                            ModsManager.setInstalledModsList();
                        });
                    } else if (CF_Installer.current_cf_project == CF_Projects.Worlds) {                                 //install world
                        Platform.runLater(() -> {
                            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                            progress.setText("Установка...");
                        });
                        if (ZipUtils.hasFile(fixedDestination, "level.dat")) {
                            String unpacked = fixedDestination.substring(0, fixedDestination.lastIndexOf("."));
                            ExtractManager.unpackZIP(fixedDestination, unpacked);
                            Platform.runLater(() -> {
                                WorldManager.addLocalWorld(new File(unpacked + File.separator + "level.dat"));
                                WorldManager.setInstalledWorldsList();
                            });
                        } else {
                            String unpacked = fixedDestination.substring(0, fixedDestination.lastIndexOf("/"));
                            File[] old_directories = new File(unpacked).listFiles();

                            ExtractManager.unpackZIP(fixedDestination, unpacked);

                            File[] directories = new File(unpacked).listFiles();

                            files_loop:for (File dir : directories) {
                                if (dir.isFile()) { continue files_loop; }
                                for (File f : old_directories) { if (f.getName().equals(dir.getName())) { continue files_loop; } }
                                    String folder = unpacked + '/' + dir.getName();
                                    Platform.runLater(() -> {
                                        WorldManager.addLocalWorld(new File(folder + File.separator + "level.dat"));
                                        WorldManager.setInstalledWorldsList();
                                    });
                            }
                            new File(fixedDestination).delete();
                            WorldManager.clearSavesDirectory(unpacked);
                        }
                    } else if (CF_Installer.current_cf_project == CF_Projects.ModPacks) {                               //install modpack
                        Platform.runLater(() -> {
                            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                            progress.setText("Установка...");
                        });

                        File modpackFolder = new File(fixedDestination.substring(0, fixedDestination.lastIndexOf('.')));
                        modpackFolder.mkdir();

                        ExtractManager.unpackZIP(fixedDestination, modpackFolder.getAbsolutePath());
                        new File(fixedDestination).delete();

                        CF_ModpackManifest manifest = new CF_ModpackManifest(modpackFolder.getAbsolutePath());
                        if (!manifest.read()) {
                            FileUtils.removeDirectory(modpackFolder);
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка установки");
                                notify.setMessage("Не удалось установить модпак.\nПожалуйста, попробуйте установить другую версию этого модпака.");
                                notify.showNotify();
                            });
                            finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
                            return null;
                        }

                        if (manifest.getOverrides() != null) {
                            File overrides = new File(modpackFolder.getAbsolutePath() + File.separator + manifest.getOverrides());
                            if (overrides.listFiles() != null) {
                                FileUtils.moveDirectory(overrides, modpackFolder);
                            }
                        }

                        File world = new File(modpackFolder.getAbsolutePath() + File.separator + "template");
                        if (world.exists()) {
                            File new_world = new File(modpackFolder.getAbsolutePath() + File.separator + manifest.getModpackName());
                            world.renameTo(new_world);

                            File saves = new File(modpackFolder.getAbsolutePath() + File.separator + "saves");
                            FileUtils.moveDirectory(new_world, saves);
                        }

                        File maps = new File(modpackFolder.getAbsolutePath() + File.separator + "maps");
                        if (maps.exists()) {
                            maps.renameTo(new File(modpackFolder.getAbsolutePath() + File.separator + "saves"));
                        }

                        File resources = new File((modpackFolder.getAbsolutePath() + File.separator + "resources"));
                        if (resources.exists()) {
                            if (new File(resources.getAbsolutePath() + File.separator + "pack.mcmeta").exists()) {
                                File resourcepacks = new File(modpackFolder.getAbsolutePath() + File.separator + "resourcepacks");
                                if (!resourcepacks.exists()) { resourcepacks.mkdir(); }
                                File resourcepack = new File(resourcepacks.getAbsolutePath() + File.separator + "resourcepack");
                                if (!resourcepack.exists()) { resourcepack.mkdir(); }
                                File assets = new File(resourcepack.getAbsolutePath() + File.separator + "assets");
                                if (!assets.exists()) { assets.mkdir(); }

                                FileUtils.copyDirectory(resources, assets);
                                File pack_mcmeta = new File(assets.getAbsolutePath() + File.separator + "pack.mcmeta");
                                if (pack_mcmeta.exists()) {
                                    try {
                                        Files.move(pack_mcmeta.toPath(), new File(resourcepack.getAbsolutePath() + File.separator + pack_mcmeta.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    } catch (Exception ignored) {}
                                }
                                try {
                                    ExtractManager.packZIP(resourcepack.getAbsolutePath(), resourcepacks + File.separator + manifest.getModpackName() + ".zip");
                                } catch (Exception ignored) {}
                                FileUtils.removeDirectory(resourcepack);
                            }
                        }

                        File newModpackFolder = new File(modpackFolder.getParentFile() + File.separator + manifest.getModpackName());
                        if (newModpackFolder.exists()) {
                            FileUtils.removeDirectory(modpackFolder);
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка установки");
                                notify.setMessage("Не удалось установить модпак.\nСборка с названием «" + manifest.getModpackName() + "» уже у Вас установлена.");
                                notify.showNotify();
                            });
                            finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
                            return null;
                        }
                        modpackFolder.renameTo(newModpackFolder);
                        modpackFolder = newModpackFolder;
                        manifest.setDestination(modpackFolder.getAbsolutePath());

                        ManageModpackManager.getModpackSources();
                        Version modpackVersion = VersionManager.getVersionByName(manifest.getModpackMinecraftVersion());
                        if (modpackVersion == null || ManageModpackManager.getModLoaderForVersion(modpackVersion) == null || !manifest.hasModloader(ManageModpackManager.getModLoaderForVersion(modpackVersion))) {
                            FileUtils.removeDirectory(modpackFolder);
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка установки");
                                notify.setMessage("Не удалось установить модпак.\nДанная версия Minecraft с поддержкой модов еще не добавлена в лаунчер.\nПожалуйста, попробуйте через некоторое время.");
                                notify.showNotify();
                            });
                            finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
                            return null;
                        }

                        ManageModpackManager.createModpack(manifest.getModpackName(), modpackVersion, 0, true, false, false);

                        HashMap<String, String> modpackMods = manifest.getModpackMods();
                        JsonObject jsonMods = new JsonObject();
                        JsonArray jsonModpackMods = new JsonArray();
                        int modsInstalled = 0;
                        File modsFolder = new File(modpackFolder.getAbsolutePath() + File.separator + "mods");
                        if (!modsFolder.exists()) { modsFolder.mkdir(); }
                        for (String url : modpackMods.keySet()) {
                            int finalModsInstalled = modsInstalled;
                            Platform.runLater(() -> {
                                progress.setText("Установка модов (" + finalModsInstalled + "/" + modpackMods.size() + ")...");
                                progressBar.setProgress(((double)(finalModsInstalled * 100 / modpackMods.size()) / 100));
                            });

                            try {
                                File emptyMod = new File(modsFolder.getAbsolutePath() + File.separator + modpackMods.get(url) + ".jar");
                                if (!emptyMod.exists()) {
                                    emptyMod.createNewFile();
                                }
                            } catch (IOException ignored) {}
                            try { Thread.sleep(7); } catch (InterruptedException ignored) {}

                            JsonObject jsonMod = new JsonObject();
                            jsonMod.addProperty("name", modpackMods.get(url));
                            jsonMod.addProperty("url", url);
                            jsonModpackMods.add(jsonMod);

                            modsInstalled++;
                        }

                        jsonMods.addProperty("_comment", "Mods list for R-Launcher.SU");
                        jsonMods.add("modpack_mods", jsonModpackMods);
                        try {
                            JsonUtils.saveJson(modsFolder.getAbsolutePath() + File.separator + "mods.json", jsonMods);
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка установки");
                                notify.setMessage("Не удалось установить моды.\nПожалуйста, попробуйте еще раз.");
                                notify.showNotify();
                            });
                        }

                        ModpackManager.loadLocalModpacks();
                        Platform.runLater(ModpackManager::setInstalledModpacksList);

                        manifest.deleteManifest();
                        manifest.deleteModlist();

                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() {
                                try { Thread.sleep(700); } catch (InterruptedException ignored) {}
                                if (manifest.getModpackMods().size() >= 100) {
                                    Platform.runLater(() -> {
                                        NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                                        notify.setTitle("Важная рекомендация");
                                        notify.setMessage("Для сборок с большим количеством модов рекомендуется выделять `больше 2048 МБ` памяти.\nДля этого нужно перейти в раздел «Менеджер сборок», выбрать текущую сборку и\nустановить нужное количество памяти в пункте «Выделение памяти».\nВ противном случае сборка может лагать или даже не запустится.");
                                        notify.showNotify();
                                    });
                                }
                                return null;
                            }
                        };
                        new Thread(task).start();
                    }
                    finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка скачивания");
                        notify.setMessage("Не удалось скачать файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                        notify.showNotify();
                    });
                    finishInstall(layout, name, info, progress, install, elements, progressBar, cancel, false);
                }
                return null;
            }
        };
        downloader = new Thread(task);
        downloader.start();
    }

    private static void finishInstall(Pane layout, Label name, Label info, Label progress, JFXButton install, int elements, JFXProgressBar progressBar, JFXButton cancel, boolean isSuccessful) {
        Platform.runLater(() -> {
            if (isSuccessful) {
                FontAwesomeIconView check = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
                check.setFill(Paint.valueOf("#ffffff"));
                install.setGraphic(check);
                install.setText("Готово");
                progress.setText("Установка завершена!");
            } else {
                progress.setText("Установка отменена!");
            }

            Animation animationProgress = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(progress.opacityProperty(), 0)));
            animationProgress.setDelay(Duration.millis(500));
            animationProgress.play();
            animationProgress.setOnFinished(e -> progress.setText(null));

            info.setVisible(true);
            install.setVisible(true);

            Animation animationName = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(name.prefWidthProperty(), name.getPrefWidth() * 2)));
            animationName.setDelay(animationProgress.getDelay().add(Duration.millis(200)));
            animationName.play();

            Animation animationInfo = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(info.opacityProperty(), 1)));
            animationInfo.setDelay(animationName.getDelay());
            animationInfo.play();

            progressBar.setVisible(false);
            //cancel.setVisible(false);

            layout.getChildren().remove(elements, layout.getChildren().size());
        });

        downloader.stop();
        downloader = null;
    }
}
