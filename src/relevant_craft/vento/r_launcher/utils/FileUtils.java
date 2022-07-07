package relevant_craft.vento.r_launcher.utils;

import javafx.application.Platform;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    public static boolean removeDirectory(File directory) {
        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;

        String[] list = directory.list();

        if (list != null) {
            for (String file : list) {
                File entry = new File(directory, file);

                if (entry.isDirectory()) {
                    if (!removeDirectory(entry))
                        return false;
                } else {
                    if (!entry.delete())
                        return false;
                }
            }
        }

        return directory.delete();
    }

    public static void moveDirectory(File sourceFile, File destFile) {
        try {
            Files.walk(sourceFile.toPath()).forEach(source -> {
                try {
                    Path dest = destFile.toPath().resolve(sourceFile.toPath().relativize(source));
                    if (Files.isDirectory(source)) {
                        if (!Files.exists(dest)) {
                            Files.createDirectory(dest);
                        }
                        return;
                    }
                    Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ignored) {}
            });
            removeDirectory(sourceFile);
        } catch (Exception ignored) {}
    }

    public static void copyDirectory(File sourceFile, File destFile) {
        try {
            Files.walk(sourceFile.toPath()).forEach(source -> {
                try {
                    Path dest = destFile.toPath().resolve(sourceFile.toPath().relativize(source));
                    if (Files.isDirectory(source)) {
                        if (!Files.exists(dest)) {
                            Files.createDirectory(dest);
                        }
                        return;
                    }
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}
    }

    public static void checkFile(File file, boolean isSilent) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                if (!isSilent) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка файла");
                        notify.setMessage("Файл " + file.getAbsolutePath() + " является папкой.");
                        notify.showNotify();
                    });
                }
                throw new IOException("File '" + file + "' is a directory");
            }
            if (!file.canWrite()) {
                if (!isSilent) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка файла");
                        notify.setMessage("Файл " + file.getAbsolutePath() + " невозможно записать.");
                        notify.showNotify();
                    });
                }
                throw new IOException("File '" + file + "' cannot be written");
            }
        } else {
            File parent = file.getParentFile();
            if ((parent != null) && (!parent.exists()) && (!parent.mkdirs())) {
                if (!isSilent) {
                    Platform.runLater(() -> {
                        NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                        notify.setTitle("Ошибка файла");
                        notify.setMessage("Файл " + file.getAbsolutePath() + " невозможно создать.");
                        notify.showNotify();
                    });
                }
                throw new IOException("File '" + file + "' could not be created");
            }
        }
    }

    public static long getCreationTime(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }

        try {
            Path p = Paths.get(file.getAbsolutePath());
            BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
            return view.creationTime().toMillis();
        } catch (Exception e) {
            return 0;
        }
    }

}
