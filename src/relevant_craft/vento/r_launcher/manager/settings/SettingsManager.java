package relevant_craft.vento.r_launcher.manager.settings;

import relevant_craft.vento.r_launcher.utils.OperatingSystem;

import java.io.File;
import java.util.prefs.Preferences;

public class SettingsManager {

    private static Preferences data = Preferences.userRoot().node("rlauncher/settings");

    /*      General     */
    public static boolean useAutoBackground;
    public static boolean useCustomBackground;
    public static String customBackground;
    public static boolean useDeveloperConsole;
    public static String language;

    /*      Minecraft       */
    public static String minecraftDirectory;
    public static int clientWidth;
    public static int clientHeight;
    public static boolean useFullScreen;
    public static boolean showSnapshots;
    public static boolean showBeta;
    public static boolean showAlpha;
    public static boolean showModifications;
    public static boolean showLegacy;
    public static String JVM_args;
    public static String Minecraft_args;
    public static int RAM;

    /*      Additional  */
    public static boolean optimizeSettings;
    public static boolean enableCheats;
    public static boolean showNewYear;

    /*      Console     */
    public static boolean closeConsole;

    /*      Assets      */
    public static boolean checkAssets;

    /*      Analyzer    */
    public static boolean enableAnalyzer;

    public static void loadSettings() {
        useAutoBackground = data.getBoolean("useAutoBackground", true);
        useCustomBackground = data.getBoolean("useCustomBackground", false);
        customBackground = data.get("customBackground", "");
        useDeveloperConsole = data.getBoolean("useDeveloperConsole", false);
        language = data.get("language", "ru");

        minecraftDirectory = data.get("minecraftDirectory", OperatingSystem.getCurrentPlatform().getDefaultMinecraftDir());
        clientWidth = data.getInt("clientWidth", 925);
        clientHeight = data.getInt("clientHeight", 530);
        useFullScreen = data.getBoolean("useFullScreen", false);
        showSnapshots = data.getBoolean("showSnapshots", false);
        showBeta = data.getBoolean("showBeta", false);
        showAlpha = data.getBoolean("showAlpha", false);
        showModifications = data.getBoolean("showModifications", true);
        showLegacy = data.getBoolean("showLegacy", false);
        JVM_args = data.get("JVM_args", "-XX:+UseG1GC");
        Minecraft_args = data.get("Minecraft_args", "");
        RAM = data.getInt("RAM", (System.getProperty("os.arch").contains("64") ? 1024 : 768));

        optimizeSettings = data.getBoolean("optimizeSettings", false);
        enableCheats = data.getBoolean("enableCheats", false);
        showNewYear = data.getBoolean("showNewYear", false);

        closeConsole = data.getBoolean("closeConsole", true);

        checkAssets = data.getBoolean("checkAssets", true);

        enableAnalyzer = data.getBoolean("enableAnalyzer", true);
    }

    public static void saveSettings() {
        data.putBoolean("useAutoBackground", useAutoBackground);
        data.putBoolean("useCustomBackground", useCustomBackground);
        data.put("customBackground", customBackground);

        data.putBoolean("useDeveloperConsole", useDeveloperConsole);
        data.put("language", language);

        data.put("minecraftDirectory", minecraftDirectory);
        data.putInt("clientWidth", clientWidth);
        data.putInt("clientHeight", clientHeight);
        data.putBoolean("useFullScreen", useFullScreen);
        data.putBoolean("showSnapshots" ,showSnapshots);
        data.putBoolean("showBeta", showBeta);
        data.putBoolean("showAlpha", showAlpha);
        data.putBoolean("showModifications", showModifications);
        data.putBoolean("showLegacy", showLegacy);
        data.put("JVM_args", JVM_args);
        data.put("Minecraft_args", Minecraft_args);
        data.putInt("RAM", RAM);

        data.putBoolean("optimizeSettings", optimizeSettings);
        data.putBoolean("enableCheats", enableCheats);
        data.putBoolean("showNewYear", showNewYear);

        data.putBoolean("closeConsole", closeConsole);

        data.putBoolean("checkAssets", checkAssets);

        data.putBoolean("enableAnalyzer", enableAnalyzer);
    }

    public static void deleteSettings() {
        data.remove("useAutoBackground");
        data.remove("useCustomBackground");
        data.remove("customBackground");
        data.remove("useDeveloperConsole");
        data.remove("language");

        data.remove("minecraftDirectory");
        data.remove("clientWidth");
        data.remove("clientHeight");
        data.remove("useFullScreen");
        data.remove("showSnapshots");
        data.remove("showBeta");
        data.remove("showAlpha");
        data.remove("showModifications");
        data.remove("showLegacy");
        data.remove("JVM_args");
        data.remove("Minecraft_args");
        data.remove("RAM");

        data.remove("optimizeSettings");
        data.remove("enableCheats");
        data.remove("showNewYear");

        data.remove("closeConsole");

        data.remove("checkAssets");

        data.remove("enableAnalyzer");
    }

    public static void checkMinecraftDirectory() {
        File mc_dir = new File(SettingsManager.minecraftDirectory);
        if (!mc_dir.exists()) {
            mc_dir.mkdirs();
        }
    }

    public static void optimizeSettings() {
        int memory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        if (memory < 512) {
            memory = 512;
        }
        RAM = memory;

        StringBuilder args = new StringBuilder();
        args.append("-XX:+UseG1GC").append(" ");
        args.append("-XX:+DisableExplicitGC").append(" ");
        args.append("-XX:MaxGCPauseMillis=10").append(" ");
        args.append("-XX:SoftRefLRUPolicyMSPerMB=10000").append(" ");

        final int threads = Runtime.getRuntime().availableProcessors() - 1;
        if (threads > 1) {
            args.append("-XX:ParallelGCThreads=" + threads).append(" ");
        }

        if (memory > 512) {
            args.append("-Xmn128M");
        }

        JVM_args = args.toString();
    }

    public static void removeOptimizedSettings() {
        data.remove("JVM_args");
        data.remove("RAM");
        data.putBoolean("optimizeSettings", false);
    }
}
