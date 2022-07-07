package relevant_craft.vento.r_launcher.manager.download;

import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.pdfsam.ui.RingProgressIndicator;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.utils.FileUtils;
import relevant_craft.vento.r_launcher.utils.FormatUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {

    public static void downloadMinecraftFile(String _url, String _file, FontAwesomeIconView downloadedStatus, FontAwesomeIconView speedStatus, FontAwesomeIconView progressStatus, FontAwesomeIconView eta, RingProgressIndicator rpi, long to_downloadTotal, long downloadedTotal, long fileSize, long startDownload, boolean countSize) throws IOException {
        long downloaded = 0;
        long lastSpeedUpdate = System.currentTimeMillis();
        try {
            URL url = new URL(_url);
            File file = new File(_file + ".download");
            if (file.exists()) { file.delete(); }

            Platform.runLater(() -> {
                if (rpi.isIndeterminate()) {
                    rpi.setProgress(0);
                    progressStatus.setText(0 + "%");
                }
                downloadedStatus.setText("Файл: " + FormatUtils.formatSize(fileSize, true) + "/" + FormatUtils.formatSize(fileSize, true));
            });

            long startTime = System.nanoTime();     //speed

            InputStream input = url.openStream();
            URLConnection urlconnection = url.openConnection();
            urlconnection.setDefaultUseCaches(false);
            final long size = urlconnection.getContentLength();

            FileUtils.checkFile(file, false);

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            while (true) {
                int read = input.read(buffer);
                if (read == -1) {
                    break;
                }

                downloaded += read;
                if (countSize) {
                    downloadedTotal += read;
                }
                output.write(buffer, 0, read);

                long finalDownloadedTotal = downloadedTotal;
                long finalDownloaded = downloaded;

                Platform.runLater(() -> {
                    int progress = (int) (finalDownloadedTotal * 100 / to_downloadTotal);
                    if (rpi.getProgress() != progress) {
                        rpi.setProgress(progress);
                        progressStatus.setText(progress + "%");
                    }
                    downloadedStatus.setText("Файл: " + FormatUtils.formatSize(finalDownloaded, true) + "/" + FormatUtils.formatSize(size, true));
                });

                if (System.currentTimeMillis() - lastSpeedUpdate >= 250 || downloaded == size) {
                    lastSpeedUpdate = System.currentTimeMillis();
                    double speedInBytes;
                    try {
                        long timeTook = System.nanoTime() - startTime;
                        speedInBytes = (downloaded / (timeTook / 1000000000D));
                    } catch (ArithmeticException e) {
                        return;
                    }

                    Platform.runLater(() -> {
                        speedStatus.setText("Скорость: " + FormatUtils.formatSize((long) speedInBytes, true) + "/cек");
                    });
                }
            }

            input.close();
            output.close();

            if (new File(_file).exists()) { new File(_file).delete(); }
            file.renameTo(new File(_file));
            if (new File(_file).length() == 0) {
                new File(_file).delete();
                throw new IOException();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void downloadCheatOrСFFile(String _url, String _file, JFXProgressBar progressBar) throws IOException {
        downloadCheatOrСFFile(_url, _file, progressBar, null);
    }

    public static void downloadCheatOrСFFile(String _url, String _file, JFXProgressBar progressBar, Label progressLabel) throws IOException {
        long progress = 0;
        long downloaded = 0;
        try {
            URL url = new URL(_url);
            File file = new File(_file + ".download");
            if (file.exists()) { file.delete(); }

            Platform.runLater(() -> {
                //progressBar.setProgress(0);
            });

            InputStream input = url.openStream();
            URLConnection urlconnection = url.openConnection();
            urlconnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            urlconnection.setDefaultUseCaches(false);
            final int size = urlconnection.getContentLength();

            FileUtils.checkFile(file, false);

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                downloaded += n;
                progress = downloaded * 100L / size;

                output.write(buffer, 0, n);

                double finalProgress = (double) progress / 100;
                Platform.runLater(() -> {
                    progressBar.setProgress(finalProgress);
                    if (progressLabel != null) {
                        progressLabel.setText("Скачивание (" + ((int)(finalProgress * 100)) + "%)...");
                    }
                });
            }

            input.close();
            output.close();

            if (new File(_file).exists()) { new File(_file).delete(); }
            file.renameTo(new File(_file));
            if (new File(_file).length() == 0) {
                new File(_file).delete();
                throw new IOException();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String _url, String _file, boolean isSilent) throws IOException {
        try {
            URL url = new URL(_url);
            File file = new File(_file + ".download");
            if (file.exists()) { file.delete(); }

            InputStream input = url.openStream();
            URLConnection urlconnection = url.openConnection();
            urlconnection.setDefaultUseCaches(false);
            final int size = urlconnection.getContentLength();

            FileUtils.checkFile(file, isSilent);

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }

            input.close();
            output.close();

            if (new File(_file).exists()) { new File(_file).delete(); }
            file.renameTo(new File(_file));
            if (new File(_file).length() == 0) {
                new File(_file).delete();
                throw new IOException();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsUrlFile(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getUrlFileSize(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDefaultUseCaches(false);
            connection.setRequestMethod("HEAD");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                return connection.getContentLength();
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            return 0;
        }
    }
}
