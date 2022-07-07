package relevant_craft.vento.r_launcher.utils;

public class DateUtils {

    public static String translateDateToRussian(String date) {
        if (date.contains("ago")) {
            return date.replace("min", "минуту")
                       .replace("mins", "минут")
                       .replace("hour", "час")
                       .replace("hours", "часов")
                       .replace("day", "день")
                       .replace("days", "дней")
                       .replace("ago", "назад");
        }

        String[] sDate = date.replace(",", "").split(" ");
        date = sDate[1] + ' ' + sDate[0] + ' ' + sDate[2];
        return date.replace("Jan", "января")
                   .replace("Feb", "февраля")
                   .replace("Mar", "марта")
                   .replace("Apr", "апреля")
                   .replace("May", "мая")
                   .replace("Jun", "июня")
                   .replace("Jul", "июля")
                   .replace("Aug", "августа")
                   .replace("Sep", "сентября")
                   .replace("Oct", "октября")
                   .replace("Nov", "ноября")
                   .replace("Dec", "декабря");
    }
}
