package relevant_craft.vento.r_launcher.manager.analyzer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.utils.Base64Utils;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AnalyzerManager {

    private static JFXSpinner loading;
    private static FontAwesomeIconView loadingText;
    private static Label errorLogName;
    private static TextArea errorLogText;
    private static Label descriptionName;
    private static ScrollPane descriptionText;
    private static JFXButton openUrl;

    private static StringBuilder currentErrorLogs;
    private static String errorLogResponse;
    private static String descriptionResponse;
    private static String urlResponse;

    public AnalyzerManager(JFXSpinner _loading, FontAwesomeIconView _loadingText, Label _errorLogName, TextArea _errorLogText, Label _descriptionName, ScrollPane _descriptionText, JFXButton _openUrl) {
        loading = _loading;
        loadingText = _loadingText;
        errorLogName = _errorLogName;
        errorLogText = _errorLogText;
        descriptionName = _descriptionName;
        descriptionText = _descriptionText;
        openUrl = _openUrl;
    }

    private static void hideElements() {
        errorLogName.setVisible(false);
        errorLogText.setVisible(false);
        descriptionName.setVisible(false);
        descriptionText.setVisible(false);
        openUrl.setVisible(false);

        loading.setVisible(true);
        loadingText.setVisible(true);
        loadingText.setText("Анализ ошибок...");
    }

    private static void showElements() {
        errorLogName.setVisible(true);
        errorLogText.setVisible(true);
        descriptionName.setVisible(true);
        descriptionText.setVisible(true);

        loading.setVisible(false);
        loadingText.setVisible(false);
    }

    public static void setErrorLogs(List<String> startupCommands, String[] logs, int exitCode) {
        currentErrorLogs = new StringBuilder();

        //Version info
        currentErrorLogs.append(VENTO.launcherManager.versionList.getValue().name).append(";");
        currentErrorLogs.append(VENTO.launcherManager.versionList.getValue().type).append(";");
        currentErrorLogs.append(VENTO.launcherManager.versionList.getValue().modpackName != null).append(";");
        currentErrorLogs.append("<::>");

        //Startup command
        for (String cmd : startupCommands) {
            currentErrorLogs.append(cmd).append(" ");
        }
        currentErrorLogs.append("\n").append("...").append("\n");

        //Logs
        for (String log : logs) {
            currentErrorLogs.append(log).append("\n");
        }

        //Exit code
        currentErrorLogs.append("\n").append("Process finished with exit code ").append(exitCode);
    }

    public static void resetErrorLogs() {
        currentErrorLogs = null;
        errorLogResponse = null;
        descriptionResponse = null;
        urlResponse = null;
    }

    public static void analyzeErrorLog() {
        hideElements();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                requestErrorLog();

                Platform.runLater(() -> {
                    setAnswer();
                    showElements();
                });

                return null;
            }

        };
        new Thread(task).start();
    }

    private static void requestErrorLog() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        if (currentErrorLogs == null) {
            return;
        }

        String response;
        try {
            String postData = "log=" + Base64Utils.encode(currentErrorLogs.toString()).replace("+", "_");
            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection)  new URL(VENTO.WEB + "analyzer.php").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
            notify.setTitle("Ошибка подключения");
            notify.setMessage("При попытке проанализировать проблемы в Minecraft произошла ошибка.\nПожалуйста, проверьте подключение к сети.");
            notify.showNotify();
            return;
        }

        if (response == null) {
            return;
        }

        if (response.isEmpty()) {
            return;
        }

        if (!response.contains("<::>")) {
            return;
        }

        String[] data = response.split("<::>");
        errorLogResponse = Base64Utils.decode(data[0]);
        descriptionResponse = Base64Utils.decode(data[1]);
        urlResponse = data[2];
    }

    private static void setAnswer() {
        String[] errorLines = errorLogResponse.replace("\\n", System.lineSeparator()).split(System.lineSeparator());
        for (String line : errorLines) {
            errorLogText.appendText(line + "\n");
        }

        AnalyzerText descriptionMsg = new AnalyzerText(descriptionText);
        String[] descriptionLines = descriptionResponse.replace("\\n", System.lineSeparator()).split(System.lineSeparator());
        for (String line : descriptionLines) {
            descriptionMsg.addMessage(line);
        }

        if (urlResponse.equalsIgnoreCase("none")) {
            openUrl.setVisible(false);
        } else {
            openUrl.setVisible(true);
            openUrl.setOnAction(event -> DesktopUtils.openUrl(urlResponse));
        }
    }

}
