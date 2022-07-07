package relevant_craft.vento.r_launcher.minecraft.starter;

import javafx.application.Platform;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.console.Console;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.analyzer.AnalyzerManager;
import relevant_craft.vento.r_launcher.manager.assets.AssetsManager;
import relevant_craft.vento.r_launcher.manager.console.ConsoleManager;
import relevant_craft.vento.r_launcher.manager.json.JsonManager;
import relevant_craft.vento.r_launcher.manager.library.LibraryManager;
import relevant_craft.vento.r_launcher.manager.server.Server;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.minecraft.RunMinecraft;

import java.io.File;
import java.io.IOException;

public class MinecraftStarter implements JavaProcessRunnable {

    @Override
    public void onJavaProcessEnded(JavaProcess paramJavaProcess) {
        Platform.runLater(() -> {
            VENTO.window.show();
            RunMinecraft.onMinecraftStopped();

            if (SettingsManager.useDeveloperConsole) {
                ConsoleManager.onKillProcess();
                if (SettingsManager.closeConsole) {
                    if (VENTO.console != null) {
                        if (paramJavaProcess.getExitCode() == 0) {
                            VENTO.console.close();
                            VENTO.console = null;
                        }
                    }
                }
            }

            if (SettingsManager.enableAnalyzer) {
                if (paramJavaProcess.getExitCode() != 0 && !paramJavaProcess.isKilledByUser()) {
                    NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                    notify.setTitle("Ошибка в Minecraft");
                    notify.setMessage("Minecraft был закрыт с ошибкой.\nОткрыть «Анализатор ошибок Minecraft» для выявления проблем и их решения?");
                    notify.setYesOrNo(true);
                    notify.showNotify();
                    if (notify.getAnswer()) {
                        AnalyzerManager.setErrorLogs(paramJavaProcess.getStartupCommands(), paramJavaProcess.getSysOutLines().getItems(), paramJavaProcess.getExitCode());
                        VENTO.startGUI(VENTO.GUI.Analyzer);
                    } else {
                        AnalyzerManager.resetErrorLogs();
                    }
                }
            }
        });
    }

    public void launchGame(String username, String accessToken, String UUID, AssetsManager assetsManager, JsonManager json, LibraryManager libraryManager, String server) throws Exception {
        final JavaProcessLauncher processLauncher = new JavaProcessLauncher(null, new String[0]);
        processLauncher.directory(new File(SettingsManager.minecraftDirectory));

        File game_directory = new File(SettingsManager.minecraftDirectory);
        if (json.getModpackName() != null) {
            game_directory = new File(game_directory + File.separator + "versions" + File.separator + json.getModpackName());
        }

        File assetsDirectory = new File(SettingsManager.minecraftDirectory + File.separator + "assets");
        if (json.getAssetIndex().equalsIgnoreCase("legacy")) {
            assetsDirectory = new File(assetsDirectory, File.separator + "virtual" + File.separator + "legacy");
        }
        File nativesDirectory = new File((SettingsManager.minecraftDirectory + File.separator + "versions" + File.separator + json.version + File.separator + "natives"));

        String[] mc_args = json.getMinecraftArgs()
                .replace("${auth_player_name}", username)
                .replace("${version_name}", "\"" + json.getId() + "\"")
                .replace("${game_directory}", "\"" + game_directory.getAbsolutePath() + "\"")
                .replace("${assets_root}", "\"" + assetsDirectory.getAbsolutePath() + "\"")
                .replace("${game_assets}", "\"" + assetsDirectory.getAbsolutePath() + "\"")
                .replace("${assets_index_name}", json.getAssetIndex())
                .replace("${auth_uuid}", UUID)
                .replace("${auth_access_token}", accessToken)
                .replace("${user_type}", "legacy")
                .replace("${version_type}", json.getType())
                .replace("${user_properties}", "{}")
                .split(" ");
        processLauncher.addCommands(new String[]{"-Xms" + "512" + "M"});
        if (json.getModpackRam() == 0) {
            processLauncher.addCommands(new String[]{"-Xmx" + SettingsManager.RAM + "M"});
        } else {
            processLauncher.addCommands(new String[]{"-Xmx" + json.getModpackRam() + "M"});
        }
        if (SettingsManager.JVM_args != null) {
            processLauncher.addCommands(SettingsManager.JVM_args.split(" "));
        }
        if (json.getModpackName() != null) {
            processLauncher.addCommands(new String[] { "-Dr_launcher_modpack_title=" + json.getModpackName() + " [" + json.getId() + "]" });
        }
        processLauncher.addCommands(new String[] { "-Dfml.ignoreInvalidMinecraftCertificates=true" });
        processLauncher.addCommands(new String[] { "-Dfml.ignorePatchDiscrepancies=true" });
        //processLauncher.addCommands(new String[] { "-Dfile.encoding=UTF-8" });
        processLauncher.addCommands(new String[] { "-Djava.library.path=" + nativesDirectory.getAbsolutePath() });
        processLauncher.addCommands(new String[] { "-cp", "\"" + libraryManager.getLibsForLaunch() + "\"" });
        processLauncher.addCommands(new String[] { json.getMainClass() });
        processLauncher.addCommands(mc_args);
        if (SettingsManager.Minecraft_args != null) {
            processLauncher.addCommands(SettingsManager.Minecraft_args.split(" "));
        }
        if (SettingsManager.useFullScreen) {
            processLauncher.addCommands(new String[] { "--fullscreen" });
        }
        if (server != null) {
            String[] address = Server.splitAddress(server);
            processLauncher.addCommands(new String[] { "--server", address[0], "--port", address[1] });
        }
        processLauncher.addCommands(new String[] { "--width", String.valueOf(SettingsManager.clientWidth) });
        processLauncher.addCommands(new String[] { "--height", String.valueOf(SettingsManager.clientHeight) });

        if (SettingsManager.useDeveloperConsole) {
            Console.showConsole();
        }

        final JavaProcess process = processLauncher.start();
        process.safeSetExitRunnable(this);

        Platform.setImplicitExit(false);
        VENTO.window.hide();
    }
}
