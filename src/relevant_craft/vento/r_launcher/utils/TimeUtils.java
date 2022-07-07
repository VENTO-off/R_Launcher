package relevant_craft.vento.r_launcher.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String formatTime(long millis) {
        long time = millis / 1000;
        int seconds = (int)(time % 60);
        int minutes = (int)((time % 3600) / 60);
        int hours = (int)(time / 3600);

        StringBuilder result = new StringBuilder();
        if (hours != 0) {
            result.append(hours + "h ");
        }
        if (minutes != 0) {
            result.append(minutes + "m ");
        }
        if (seconds != 0) {
            result.append(seconds + "s");
        }

        return result.toString();
    }

    public static String getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yy");
        return dateFormat.format(date);
    }
}
