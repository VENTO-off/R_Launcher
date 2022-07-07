package relevant_craft.vento.r_launcher.manager.hardware;

import javafx.concurrent.Task;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.utils.AntivirusUtils;
import relevant_craft.vento.r_launcher.utils.Base64Utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HardwareManager {

    public static void sendHardwareStats() {
        if (!VENTO.ONLINE_MODE) {
            return;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    URL url = new URL(VENTO.WEB + "hardware_stats.php?r_hardware=" + Base64Utils.encode(getHardwareInfo()).replace("+", "_"));
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
                    bufferedreader.close();
                } catch (Exception ignored) {}
                return null;
            }
        };
        new Thread(task).start();
    }

    private static String getHardwareInfo() {
        StringBuilder hardware = new StringBuilder();

        hardware.append(LauncherManager.launcher_id.toString()).append("<::>");

        hardware.append(System.getProperty("os.name")).append(" (").append(System.getProperty("os.arch")).append(")").append("<::>");

        hardware.append(((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1024 / 1024).append(" Mb").append("<::>");

        hardware.append(diskDriveSize(getInfoFromCMD("wmic DISKDRIVE get size")) / 1024 / 1024 / 1024).append(" Gb").append("<::>");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        hardware.append((int) screenSize.getWidth()).append("x").append((int) screenSize.getHeight()).append("<::>");

        hardware.append(getInfoFromCMD("wmic CPU get name")).append("<::>");

        String[] gpu = getInfoFromCMD("wmic path win32_VideoController get name").split("\\r?\\n");
        hardware.append((gpu.length == 1 ? gpu[0] + "<::>" : gpu[0] + "<::>" + gpu[1])).append("<::>");

        hardware.append(translateType(getInfoFromCMD("wmic SystemEnclosure get chassistypes"))).append("<::>");

        hardware.append(AntivirusUtils.getAntivirus()).append("<::>");

        return hardware.toString();
    }

    private static String getInfoFromCMD(String command) {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c " + command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));

            String result = new String();

            boolean isFirst = true;
            String line;
            while((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                if (!line.isEmpty()) {
                    result += line.trim() + "\n";
                }
            }

            return result.trim();
        } catch (Exception e) {
            return "<unknown>";
        }
    }

    private static long diskDriveSize(String string) {
        long size = 0;

        try {
            String[] data = string.split("\\r?\\n");
            for (String s : data) {
                size += Long.valueOf(s.trim());
            }
        } catch (Exception ignored) {}

        return size;
    }

    private static String translateType(String type) {
        try {
            int number_type = Integer.valueOf(type.replace("{", "").replace("}", ""));
            switch (number_type) {
                case 1:
                    return "Other";
                case 2:
                    return "Unknown";
                case 3:
                    return "Desktop";
                case 4:
                    return "Low Profile Desktop";
                case 5:
                    return "Pizza Box";
                case 6:
                    return "Mini Tower";
                case 7:
                    return "Tower";
                case 8:
                    return "Portable";
                case 9:
                    return "Laptop";
                case 10:
                    return "Notebook";
                case 11:
                    return "Handheld";
                case 12:
                    return "Docking Station";
                case 13:
                    return "All-in-One";
                case 14:
                    return "Sub-Notebook";
                case 15:
                    return "Space Saving";
                case 16:
                    return "Lunch Box";
                case 17:
                    return "Main System Chassis";
                case 18:
                    return "Expansion Chassis";
                case 19:
                    return "Sub-Chassis";
                case 20:
                    return "Bus Expansion Chassis";
                case 21:
                    return "Peripheral Chassis";
                case 22:
                    return "Storage Chassis";
                case 23:
                    return "Rack Mount Chassis";
                case 24:
                    return "Sealed-case PC";
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            return "<unknown>";
        }
    }
}
