package relevant_craft.vento.r_launcher.utils;

import java.io.File;

public enum OperatingSystem {
    LINUX("linux", new String[]{"linux", "unix"}), WINDOWS("windows", new String[]{"win"}), OSX("osx", new String[]{"mac"}), UNKNOWN("unknown", new String[0]);

    public static OperatingSystem getCurrentPlatform() {
        final String osName = System.getProperty("os.name").toLowerCase();

        for (final OperatingSystem os : values())
            for (final String alias : os.getAliases())
                if (osName.contains(alias))
                    return os;

        return UNKNOWN;
    }

    private final String name;

    private final String[] aliases;

    private OperatingSystem(final String name, final String[] aliases) {
        this.name = name;
        this.aliases = aliases == null ? new String[0] : aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getJavaDir() {
        final String separator = System.getProperty("file.separator");
        final String path = System.getProperty("java.home") + separator + "bin" + separator;

        if (getCurrentPlatform() == WINDOWS && new File(path + "javaw.exe").isFile())
            return path + "javaw.exe";

        return path + "java";
    }

    public String getJavaDir(String customPath) {
        System.setProperty("java.home", customPath);

        final String separator = System.getProperty("file.separator");
        final String path = System.getProperty("java.home") + separator + "bin" + separator;

        if (getCurrentPlatform() == WINDOWS && new File(path + "javaw.exe").isFile())
            return path + "javaw.exe";

        return path + "java";
    }

    public String getAppdataDir() {
        if (getCurrentPlatform() == WINDOWS) {
            return System.getenv("APPDATA");
        } else if (getCurrentPlatform() == LINUX) {
            return System.getProperty("user.home");
        } else if (getCurrentPlatform() == OSX) {
            final String separator = System.getProperty("file.separator");
            return System.getProperty("user.home") + separator + "Library" + separator + "Application Support";
        } else {
            return null;
        }
    }

    public String getDefaultMinecraftDir() {
        final String separator = System.getProperty("file.separator");
        if (getCurrentPlatform() == OSX) {
            return getAppdataDir() + separator + "minecraft";
        } else if (getCurrentPlatform() == WINDOWS || getCurrentPlatform() == LINUX) {
            return getAppdataDir() + separator + ".minecraft";
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isSupported() {
        return this != UNKNOWN;
    }

    public boolean canSupport(OperatingSystem currentOS, String name) {
        if (name.contains(getCurrentPlatform().name)) {
            return true;
        }
        return false;
    }
}