package relevant_craft.vento.r_launcher.utils;

public class StringUtils {

    public static String fixString(String string) {
        StringBuilder fixed = new StringBuilder();

        String[] s = string.replaceAll("^ +| +$|( )+", "$1").split("");
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("§")) {
                i++;
                continue;
            }
            fixed.append(s[i]);
        }

        return fixed.toString();
    }

    public static String fixStringForFile(String s) {
        return s.replaceAll("[^\\d\\w\\s._-]", "");
    }
}
