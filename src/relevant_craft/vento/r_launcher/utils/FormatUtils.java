package relevant_craft.vento.r_launcher.utils;

public class FormatUtils {

    private static final String[] SI_UNITS = { "б", "Кб", "Мб", "Гб", "Тб", "Пб", "Еб" };
    private static final String[] BINARY_UNITS = { "Б", "Кбит", "Мбит", "Гбит", "Тбит", "Пбит", "Ебит" };

    public static String formatSize(long bytes, boolean useSI) {
        final String[] units = useSI ? SI_UNITS : BINARY_UNITS;
        final int base = useSI ? 1000 : 1024;

        if (bytes < base) {
            return bytes + " " + units[0];
        }

        final int exponent = (int) (Math.log(bytes) / Math.log(base));
        final String unit = units[exponent];
        return String.format("%.1f %s", bytes / Math.pow(base, exponent), unit);
    }

}
