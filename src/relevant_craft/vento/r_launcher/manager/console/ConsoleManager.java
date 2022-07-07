package relevant_craft.vento.r_launcher.manager.console;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TextArea;
import relevant_craft.vento.r_launcher.minecraft.starter.JavaProcess;
import relevant_craft.vento.r_launcher.utils.ColorUtils;

import java.util.List;

public class ConsoleManager {

    private static TextArea logsArea;
    private static JFXButton killMinecraft;
    private static JavaProcess process;

    public ConsoleManager(TextArea _logsArea, JFXButton _killMinecraft) {
        logsArea = _logsArea;
        killMinecraft = _killMinecraft;
    }

    public static void setProcess(JavaProcess _process) {
        process = _process;
        killMinecraft.setDisable(false);
    }

    public static void killProcess() {
        process.stop();
        onKillProcess();
    }

    public static void onKillProcess() {
        killMinecraft.setDisable(true);
    }

    public static void log(String line) {
        if (logsArea != null) {
            logsArea.appendText((line.contains("[CHAT]") ? ColorUtils.removeColorCodes(line) : line) + "\n");
        }
    }

    public static void log(List<String> commands) {
        StringBuilder commands_line = new StringBuilder();
        for (String cmd : commands) {
            commands_line.append(cmd).append(" ");
        }
        log(commands_line.toString().trim());
    }
}
