package relevant_craft.vento.r_launcher.minecraft;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.text.TextAlignment;
import org.pdfsam.ui.RingProgressIndicator;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.main.Main;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.account.Account;
import relevant_craft.vento.r_launcher.manager.account.AccountManager;
import relevant_craft.vento.r_launcher.manager.account.AccountType;
import relevant_craft.vento.r_launcher.manager.assets.Asset;
import relevant_craft.vento.r_launcher.manager.assets.AssetsManager;
import relevant_craft.vento.r_launcher.manager.curseforge.CF_ModpackMods;
import relevant_craft.vento.r_launcher.manager.download.DownloadManager;
import relevant_craft.vento.r_launcher.manager.extract.ExtractManager;
import relevant_craft.vento.r_launcher.manager.json.JsonException;
import relevant_craft.vento.r_launcher.manager.json.JsonManager;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.library.Library;
import relevant_craft.vento.r_launcher.manager.library.LibraryManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.manager.socket.SocketAnswer;
import relevant_craft.vento.r_launcher.manager.socket.SocketManager;
import relevant_craft.vento.r_launcher.manager.socket.commands.curseforge_download;
import relevant_craft.vento.r_launcher.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.AuthenticationUnavailableException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.InvalidCredentialsException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.RequestException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.UserMigratedException;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.AuthenticationResponse;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.LicenceNotBoughtException;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.RefreshResponse;
import relevant_craft.vento.r_launcher.minecraft.starter.MinecraftStarter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunMinecraft {

    private static Thread minecraft = null;

    public static void runMinecraft(String server) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    RunMinecraft.prepareAndRunMinecraft(server);
                } catch (JsonException e) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка чтения");
                        notify.setMessage("Ошибка чтения .json файла.\nПопробуйте, `перекачать клиент` или обратитесь в тех.поддержку лаунчера.\nНе найдено: \"" + e.getMessage() + "\".");
                        notify.showNotify();
                        onMinecraftStopped();
                    });
                }
                return null;
            }

        };

        if (minecraft == null) {
            LauncherManager.lockButtons(false);
            minecraft = new Thread(task);
            minecraft.start();
        } else {
            onMinecraftStopped();
        }
    }

    public static void onMinecraftStopped() {
        LauncherManager.unlockButtons(false);
        minecraft.stop();
        minecraft = null;

        VersionManager.setVersions();
        LauncherManager.showMainMenu();
    }

    private static void prepareAndRunMinecraft(String server) throws JsonException {
        RingProgressIndicator rpi = new RingProgressIndicator();
        rpi.makeIndeterminate();
        rpi.setLayoutX(VENTO.launcherManager.mainArea.getWidth() / 2 - 475 / 2);
        rpi.setLayoutY(VENTO.launcherManager.mainArea.getHeight() / 2 - 475 / 2);

        FontAwesomeIconView progress = new FontAwesomeIconView();
        progress.setText(null);
        progress.setTextAlignment(TextAlignment.CENTER);
        progress.setGlyphSize(75);
        progress.setStyle("-fx-font-weight: bold;");
        progress.setWrappingWidth(300);
        progress.setLayoutX(VENTO.launcherManager.mainArea.getWidth() / 2 - progress.getWrappingWidth() / 2);
        progress.setLayoutY(VENTO.launcherManager.mainArea.getHeight() / 2 - 75);

        FontAwesomeIconView status = new FontAwesomeIconView();
        status.setText(null);
        status.setTextAlignment(TextAlignment.CENTER);
        status.setGlyphSize(25);
        status.setStyle("-fx-font-weight: bold;");
        status.setWrappingWidth(400);
        status.setLayoutX(VENTO.launcherManager.mainArea.getWidth() / 2 - status.getWrappingWidth() / 2);
        status.setLayoutY(VENTO.launcherManager.mainArea.getHeight() / 2);

        FontAwesomeIconView downloadedStatus = new FontAwesomeIconView();
        downloadedStatus.setText(null);
        downloadedStatus.setTextAlignment(TextAlignment.CENTER);
        downloadedStatus.setGlyphSize(19);
        downloadedStatus.setStyle("-fx-font-weight: bold;");
        downloadedStatus.setWrappingWidth(400);
        downloadedStatus.setLayoutX(VENTO.launcherManager.mainArea.getWidth() / 2 - downloadedStatus.getWrappingWidth() / 2);
        downloadedStatus.setLayoutY(status.getLayoutY() + 50);

        FontAwesomeIconView speed = new FontAwesomeIconView();
        speed.setText(null);
        speed.setTextAlignment(TextAlignment.CENTER);
        speed.setGlyphSize(19);
        speed.setStyle("-fx-font-weight: bold;");
        speed.setWrappingWidth(400);
        speed.setLayoutX(VENTO.launcherManager.mainArea.getWidth() / 2 - speed.getWrappingWidth() / 2);
        speed.setLayoutY(downloadedStatus.getLayoutY() + 30);

        FontAwesomeIconView eta = new FontAwesomeIconView();
        eta.setText(null);
        eta.setTextAlignment(TextAlignment.CENTER);
        eta.setGlyphSize(19);
        eta.setStyle("-fx-font-weight: bold;");
        eta.setWrappingWidth(400);
        eta.setLayoutX(VENTO.launcherManager.mainArea.getWidth() / 2 - eta.getWrappingWidth() / 2);
        eta.setLayoutY(speed.getLayoutY() + 30);

        Platform.runLater(() -> {
            VENTO.launcherManager.mainArea.getChildren().setAll(rpi, progress, status, downloadedStatus, speed, eta);
        });

        Account account = AccountManager.selectedAccount;
        JsonManager json;
        AssetsManager assetsManager;
        LibraryManager libraryManager;
        String username = null;
        String accessToken = null;
        String UUID = null;

        if (VENTO.ONLINE_MODE) {
            //Authenticate
            status.setText("Авторизация...");

            if (account.type == AccountType.PIRATE) {
                username = account.name;
                accessToken = java.util.UUID.randomUUID().toString();
                UUID = java.util.UUID.randomUUID().toString();
            } else if (account.type == AccountType.MOJANG) {
                RefreshResponse refreshResponse = AccountManager.refreshMojangAccount(account);
                if (refreshResponse != null) {
                    username = refreshResponse.getSelectedProfile().getName();
                    accessToken = refreshResponse.getAccessToken();
                    UUID = refreshResponse.getSelectedProfile().getUUID().toString();
                } else {
                    AuthenticationResponse authResponse;
                    try {
                        authResponse = AccountManager.authMojangAccount(account);
                    } catch (AuthenticationUnavailableException e) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка авторизации");
                            notify.setMessage("Серверы Mojang сейчас недоступны. Попробуйте позже.");
                            notify.showNotify();
                            RunMinecraft.onMinecraftStopped();
                        });
                        return;
                    } catch (RequestException e) {
                        if (e instanceof LicenceNotBoughtException) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка аккаунта");
                                notify.setMessage("На Вашем Mojang аккаунте `не куплена лицензия` Minecraft.");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        } else if (e instanceof UserMigratedException) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка авторизации");
                                notify.setMessage("Для авторизации в аккаунт `нужно использовать E-mail` вместо никнейма.");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        } else if (e instanceof InvalidCredentialsException) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка авторизации");
                                notify.setMessage("Невверный логин/пароль от Вашего аккаунта.\nПопробуйте, заново авторизоваться.");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        } else {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка");
                                notify.setMessage("Во время авторизации в Mojang аккаунт произошла неизвестная ошибка.");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        }
                    }

                    username = authResponse.getSelectedProfile().getName();
                    accessToken = authResponse.getAccessToken();
                    UUID = authResponse.getSelectedProfile().getUUID().toString();
                }
            } else if (account.type == AccountType.ELYBY) {
                RefreshResponse refreshResponse = AccountManager.refreshElybyAccount(account);
                if (refreshResponse != null) {
                    username = refreshResponse.getSelectedProfile().getName();
                    accessToken = refreshResponse.getAccessToken();
                    UUID = refreshResponse.getSelectedProfile().getUUID().toString();
                } else {
                    AuthenticationResponse authResponse = AccountManager.authElybyAccount(account);
                    if (authResponse == null) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка");
                            notify.setMessage("Невверный логин/пароль от Вашего аккаунта.\nПопробуйте, заново авторизоваться.");
                            notify.showNotify();
                            RunMinecraft.onMinecraftStopped();
                        });
                        return;
                    }
                }
            }

            //Download json
            File version_dir = new File(SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + VENTO.launcherManager.versionList.getValue().name);
            if (!version_dir.exists() || Main.FORCE_DOWNLOAD) {
                File json_file = new File(version_dir + File.separator + VENTO.launcherManager.versionList.getValue().name + ".json");
                if (!json_file.exists() || Main.FORCE_DOWNLOAD) {
                    try {
                        String url = VENTO.WEB + VENTO.launcherManager.versionList.getValue().url;
                        if (VENTO.launcherManager.versionList.getValue().url == null) {
                            url = VENTO.WEB + "versions" + '/' + VENTO.launcherManager.versionList.getValue().name + ".json";
                        }
                        if (!json_file.exists() || DownloadManager.existsUrlFile(url)) {
                            DownloadManager.downloadFile(url, json_file.getAbsolutePath(), false);
                            VersionManager.setVersionLocal(VENTO.launcherManager.versionList.getValue());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка скачивания");
                            notify.setMessage("Не удалось скачать .json файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                            notify.showNotify();
                            RunMinecraft.onMinecraftStopped();
                        });
                        return;
                    }
                }
            }

            //Read json
            status.setText("Чтение JSON...");
            json = new JsonManager(VENTO.launcherManager.versionList.getValue().name, false);
            if (!json.read()) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка чтения");
                    notify.setMessage("Ошибка чтения .json файла.\nПопробуйте, `перекачать клиент` или обратитесь в тех.поддержку лаунчера.\n\nДля этого нажмите на 'три точки' (возле кнопки 'Играть') и\nустановите галочку `Перекачать клиент`.");
                    notify.showNotify();
                    RunMinecraft.onMinecraftStopped();
                });
                return;
            }

            //Check inheritance
            if (json.isInherits()) {
                JsonManager jsonInherited = new JsonManager(json.getInheritsFrom(), true);
                if (!jsonInherited.read()) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка чтения");
                        notify.setMessage("Ошибка чтения .json файла.\nПопробуйте, `перекачать клиент` или обратитесь в тех.поддержку лаунчера.\n\nДля этого нажмите на 'три точки' (возле кнопки 'Играть') и\nустановите галочку `Перекачать клиент`.");
                        notify.showNotify();
                        RunMinecraft.onMinecraftStopped();
                    });
                }
                json.setJsonInherited(jsonInherited);
            }

            //Download assetIndex
            File assetIndex = new File(SettingsManager.minecraftDirectory + File.separator + "assets" + File.separator + "indexes" + File.separator + json.getAssetIndex() + ".json");
            if (!assetIndex.exists() || Main.FORCE_DOWNLOAD) {
                try {
                    DownloadManager.downloadFile(json.getAssetsURL(), assetIndex.getAbsolutePath(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка скачивания");
                        notify.setMessage("Не удалось скачать assetIndex файл.\nПожалуйста, проверьте подключение к сети и попробуйте снова.");
                        notify.showNotify();
                        RunMinecraft.onMinecraftStopped();
                    });
                    return;
                }
            }

            //Check assets
            assetsManager = new AssetsManager(json.getAssetIndex(), true);
            List<Asset> assets_download = new ArrayList<>();
            if (SettingsManager.checkAssets) {
                status.setText("Проверка assets...");
                for (Asset asset : assetsManager.objects) {
                    if (!asset.getFile().exists() || (asset.getFile().exists() && asset.getFile().length() != asset.getSize())) {
                        assets_download.add(asset);
                    }
                }
            }

            //Download assets
            if (assets_download.size() != 0) {
                final String download_text_assets = "Скачивание assets";
                status.setText(download_text_assets);
                long startDownload = System.nanoTime();
                long to_downloadTotal = 0;
                for (Asset asset : assets_download) {
                    to_downloadTotal += asset.getSize();
                }
                long downloadedTotal = 0;
                for (Asset asset : assets_download) {
                    status.setText(download_text_assets + ": " + assets_download.indexOf(asset) + "/" + assets_download.size());

                    try {
                        DownloadManager.downloadMinecraftFile(asset.getUrl(), asset.getFile().toString(), downloadedStatus, speed, progress, eta, rpi, to_downloadTotal, downloadedTotal, asset.getSize(), startDownload, true);
                    } catch (IOException ignored) {}
                    downloadedTotal += asset.getSize();
                }
            }

            try { Thread.sleep(50); } catch (InterruptedException e) {}
            progress.setText(null);
            status.setText(null);
            downloadedStatus.setText(null);
            speed.setText(null);
            eta.setText(null);
            rpi.makeIndeterminate();

            //Check libraries
            status.setText("Проверка библиотек...");
            List<Library> libs_download = new ArrayList<>();
            libraryManager = json.getAllLibraries();
            if (account.type == AccountType.ELYBY) {
                json.replaceAuthlib(libraryManager);
            }
            for (Library library : libraryManager.libs) {
                File current = new File(library.path + File.separator + library.name);
                if ((!current.exists()) || Main.FORCE_DOWNLOAD /*|| (current.exists() && current.length() != library.size)*/) {
                    if (library.size == -1) {
                        try {
                            URL generated_url = LibraryManager.correctURL(library.id, library.isNatives, library.natives_os);
                            if (DownloadManager.existsUrlFile(generated_url.toString())) {
                                library.url = generated_url;
                                library.size = DownloadManager.getUrlFileSize(generated_url.toString());
                                if ((library.isNatives && library.path.exists() && !Main.FORCE_DOWNLOAD)) {
                                    continue;
                                }
                                if (library.isPacked && new File(library.path + File.separator + library.name.substring(0, library.name.length() - 8)).exists() && !Main.FORCE_DOWNLOAD) {
                                    continue;
                                }
                                libs_download.add(library);
                            }
                        } catch (MalformedURLException e) {
                            continue;
                        }
                        continue;
                    }
                    if (library.size == 0) {
                        library.size = DownloadManager.getUrlFileSize(library.url.toString());

                        //ignore .pack.xz
                        if (library.isPacked && library.size == 0) {
                            try {
                                library.url = new URL(library.url.toString().replace(".pack.xz", ""));
                                library.size = DownloadManager.getUrlFileSize(library.url.toString());
                                library.isPacked = false;
                            } catch (Exception ignored) {}
                        }
                    }
                    if ((library.isNatives && library.path.exists() && !Main.FORCE_DOWNLOAD)) {
                        continue;
                    }
                    if (library.isPacked && new File(library.path + File.separator + library.name.substring(0, library.name.length() - 8)).exists() && !Main.FORCE_DOWNLOAD) {
                        continue;
                    }
                    libs_download.add(library);
                }
            }

            List<Library> extraLibraries = json.getExtraLibraries();
            for (Library extraLibrary : extraLibraries) {
                if (!new File(extraLibrary.path.getAbsolutePath() + File.separator + extraLibrary.name).exists()) {
                    libs_download.add(extraLibrary);
                }
            }


            Main.FORCE_DOWNLOAD = false;

            //Download libraries
            if (libs_download.size() != 0) {
                final String download_text_libs = "Скачивание библиотек";
                status.setText(download_text_libs);
                long startDownload = System.nanoTime();
                long to_downloadTotal = 0;
                for (Library library : libs_download) {
                    to_downloadTotal += library.size;
                }
                long downloadedTotal = 0;
                for (Library library : libs_download) {
                    status.setText(download_text_libs + ": " + libs_download.indexOf(library) + "/" + libs_download.size());

                    try {
                        DownloadManager.downloadMinecraftFile(library.url.toString(), library.path.getAbsolutePath() + File.separator + library.name, downloadedStatus, speed, progress, eta, rpi, to_downloadTotal, downloadedTotal, library.size, startDownload, true);
                    } catch (IOException e) {
                        //Download from r-launcher repository
                        try {
                            library.url = new URL(library.url.toString().replace("https://libraries.minecraft.net/", VENTO.REPOSITORY + "libraries" + '/'));
                            DownloadManager.downloadMinecraftFile(library.url.toString(), library.path.getAbsolutePath() + File.separator + library.name, downloadedStatus, speed, progress, eta, rpi, to_downloadTotal, downloadedTotal, library.size, startDownload, true);
                        } catch (Exception e2) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка скачивания");
                                notify.setMessage("Не удалось скачать файлы библиотек.\nПожалуйста, проверьте подключение к сети и попробуйте снова.\nФайл: \"" + library.name + "\".");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        }
                    }
                    downloadedTotal += library.size;
                    if (library.isNatives) {
                        try {
                            File zip = new File(library.path + File.separator + library.name);
                            ExtractManager.unpackZIP(zip.getAbsolutePath(), library.path.getAbsolutePath());
                            zip.delete();
                        } catch (IOException e) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка распаковки");
                                notify.setMessage("Ошибка распаковки архива.\nПопробуйте ещё раз или обратитесь в тех.поддержку лаунчера.");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        }
                    } else if (library.isPacked) {
                        try {
                            File xz = new File(library.path + File.separator + library.name);
                            File pack = new File(library.path + File.separator + library.name.substring(0, library.name.length() - 3));
                            File jar = new File(library.path + File.separator + library.name.substring(0, library.name.length() - 8));

                            ExtractManager.unpackXZ(xz.getAbsolutePath(), pack.getAbsolutePath());
                            ExtractManager.unpackLZMA(pack.getAbsolutePath(), jar.getAbsolutePath());

                            xz.delete();
                            pack.delete();
                        } catch (IOException e) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка распаковки");
                                notify.setMessage("Ошибка распаковки архива.\nПопробуйте ещё раз или обратитесь в тех.поддержку лаунчера.");
                                notify.showNotify();
                                RunMinecraft.onMinecraftStopped();
                            });
                            return;
                        }
                    }
                }
            }

            try { Thread.sleep(50); } catch (InterruptedException e) {}
            progress.setText(null);
            status.setText(null);
            downloadedStatus.setText(null);
            speed.setText(null);
            eta.setText(null);
            rpi.makeIndeterminate();

            //Download mods
            if (json.getModpackName() != null) {
                CF_ModpackMods cf_mods = new CF_ModpackMods(version_dir);
                if (cf_mods.hasJson()) {
                    HashMap<String, String> mods = cf_mods.getMods();

                    final String download_text_mods = "Скачивание модов";
                    status.setText(download_text_mods);
                    long startDownload = System.nanoTime();
                    long to_downloadTotal = mods.size();
                    long downloadedTotal = 0;


                    for (String name : mods.keySet()) {
                        status.setText(download_text_mods + ": " + downloadedTotal + "/" + mods.size());
                        String url = mods.get(name);

                        if (url.contains("curseforge.com")) {
                            SocketAnswer socketAnswer = SocketManager.connectSocket(curseforge_download.createRequest(url));
                            if (socketAnswer == null || socketAnswer.isError()) {
                                Platform.runLater(() -> {
                                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                    notify.setTitle("Ошибка скачивания");
                                    notify.setMessage("Не удалось скачать мод «" + name.replace(".jar", "") + "».\nМод будет пропущен в данной сборке.\nПопробуйте его установить вручную в разделе «Моды».");
                                    notify.showNotify();
                                });
                                continue;
                            }
                            url = socketAnswer.getResponse();
                        }

                        try {
                            DownloadManager.downloadMinecraftFile(url, version_dir.getAbsolutePath() + File.separator + "mods" + File.separator + name, downloadedStatus, speed, progress, eta, rpi, to_downloadTotal, downloadedTotal, 0, startDownload, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка скачивания");
                                notify.setMessage("Не удалось скачать мод «" + name.replace(".jar", "") + "».\nМод будет пропущен в данной сборке.\nПопробуйте его установить вручную в разделе «Моды».");
                                notify.showNotify();
                                //RunMinecraft.onMinecraftStopped();
                            });
                        }
                        downloadedTotal++;
                        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    }
                    cf_mods.delete();
                }

                Library r_launcher_modpack = libraryManager.findLibById("relevant_craft.vento.r_launcher.modpack", false);
                if (r_launcher_modpack != null) {
                    File downloadedRLauncherLib = new File(r_launcher_modpack.path.getAbsolutePath() + File.separator + r_launcher_modpack.name);
                    if (downloadedRLauncherLib.exists() && downloadedRLauncherLib.length() != 0) {
                        File modRLauncher = new File(version_dir.getAbsolutePath() + File.separator + "mods" + File.separator + r_launcher_modpack.name);
                        if (!modRLauncher.exists() || modRLauncher.length() == 0) {
                            try {
                                Files.copy(downloadedRLauncherLib.toPath(), modRLauncher.toPath());
                                modRLauncher.setReadOnly();
                            } catch (IOException ignored) {}
                        }
                    }
                }
            }

            VersionManager.setVersionLocal(VENTO.launcherManager.versionList.getValue());
        } else {
            //Authenticate
            username = account.name;
            accessToken = java.util.UUID.randomUUID().toString();
            UUID = java.util.UUID.randomUUID().toString();

            //Read json
            status.setText("Чтение JSON...");
            json = new JsonManager(VENTO.launcherManager.versionList.getValue().name, false);
            if (!json.read()) {
                Platform.runLater(() -> {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка чтения");
                    notify.setMessage("Ошибка чтения .json файла.\nПопробуйте, `перекачать клиент` или обратитесь в тех.поддержку лаунчера.\n\nДля этого нажмите на 'три точки' (возле кнопки 'Играть') и\nустановите галочку `Перекачать клиент`.");
                    notify.showNotify();
                    RunMinecraft.onMinecraftStopped();
                });
                return;
            }

            //Read Assets
            assetsManager = new AssetsManager(json.getAssetIndex(), false);

            //Read libraries
            libraryManager = json.getAllLibraries();
        }

        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        progress.setVisible(false);
        status.setText("Запуск Minecraft...");
        downloadedStatus.setVisible(false);
        speed.setVisible(false);
        eta.setVisible(false);
        rpi.makeIndeterminate();

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        //Launch minecraft
        AssetsManager finalAssets = assetsManager;
        JsonManager finalJson = json;
        LibraryManager finalLibraryManager = libraryManager;
        String finalUsername = username;
        String finalAccessToken = accessToken;
        String finalUUID = UUID;
        Platform.runLater(() -> {
            MinecraftStarter minecraft = new MinecraftStarter();
            try {
                minecraft.launchGame(finalUsername, finalAccessToken, finalUUID, finalAssets, finalJson, finalLibraryManager, server);
            } catch (Exception e) {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка запуска");
                notify.setMessage("Ошибка запуска Minecraft!\nПопробуйте, `перекачать клиент` или обратитесь в тех.поддержку лаунчера.\n\nДля этого нажмите на 'три точки' (возле кнопки 'Играть') и\nустановите галочку `Перекачать клиент`.");
                notify.showNotify();
                RunMinecraft.onMinecraftStopped();
            }
        });
    }
}
