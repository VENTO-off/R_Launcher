package relevant_craft.vento.r_launcher.minecraft.starter;

import javafx.application.Platform;
import relevant_craft.vento.r_launcher.manager.console.ConsoleManager;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessMonitorThread extends Thread {
    private final JavaProcess process;

    public ProcessMonitorThread(final JavaProcess process) {
        this.process = process;
    }

    @Override
    public void run() {
        final InputStreamReader reader = new InputStreamReader(process.getRawProcess().getInputStream());
        final BufferedReader buf = new BufferedReader(reader);

        try {
            while (process.isAlive()) {
                final String line = buf.readLine();

                if (line != null) {
                    if (SettingsManager.useDeveloperConsole) {
                        Platform.runLater(() -> ConsoleManager.log(line));
                    }
                    process.getSysOutLines().add(line);
                }
            }
            Platform.runLater(() -> ConsoleManager.log("\n" + "Process finished with exit code " + process.getExitCode()));
        } catch (IOException ignored) {
        } finally {
            try {
                buf.close();
            } catch (final IOException ignored) {}
        }

        final JavaProcessRunnable onExit = process.getExitRunnable();

        if (onExit != null)
            onExit.onJavaProcessEnded(process);
    }
}
