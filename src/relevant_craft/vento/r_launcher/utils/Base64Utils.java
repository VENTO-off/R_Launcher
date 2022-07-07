package relevant_craft.vento.r_launcher.utils;

import java.nio.charset.Charset;
import java.util.Base64;

public class Base64Utils {

    public static String decode(String string) {
        try {
            byte[] base64decodedBytes = Base64.getDecoder().decode(string);
            return new String(base64decodedBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encode(String string) {
        try {
            byte[] base64encodedBytes = Base64.getEncoder().encode(string.getBytes(Charset.defaultCharset()));
            return new String(base64encodedBytes);
        } catch (Exception e) {
            return null;
        }
    }
}
