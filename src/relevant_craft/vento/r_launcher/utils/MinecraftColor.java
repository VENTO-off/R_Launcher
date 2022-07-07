package relevant_craft.vento.r_launcher.utils;

public enum MinecraftColor {
    COLOR_4('4', "#be0000", "#bd2025"),
    COLOR_C('c', "#fe3f3f", "#ef4344"),
    COLOR_6('6', "#d9a334", "#d9a332"),
    COLOR_E('e', "#fefe3f", "#f7ed45"),
    COLOR_2('2', "#00be00", "#2bb34b"),
    COLOR_A('a', "#3ffe3f", "#79c143"),
    COLOR_B('b', "#3ffefe", "#3ffefe"),
    COLOR_3('3', "#00bebe", "#16bcbc"),
    COLOR_1('1', "#0000be", "#2f3f99"),
    COLOR_9('9', "#3f3ffe", "#4d5aa8"),
    COLOR_D('d', "#fe3ffe", "#bf60a5"),
    COLOR_5('5', "#be00be", "#a43d97"),
    COLOR_F('f', "#ffffff", "#ffffff"),
    COLOR_7('7', "#bebebe", "#bfbebe"),
    COLOR_8('8', "#3f3f3f", "#404040"),
    COLOR_0('0', "#000000", "#000000"),
    ;

    private char code;
    private String color;
    private String pale;

    MinecraftColor(char code, String color, String pale) {
        this.code = code;
        this.color = color;
        this.pale = pale;
    }

    public static String convertToHex(char code) {
        for (MinecraftColor color : MinecraftColor.values()) {
            if (color.code == code) {
                return color.color;
            }
        }

        return COLOR_F.color;
    }

    public static String convertToPaleHex(char code) {
        for (MinecraftColor color : MinecraftColor.values()) {
            if (color.code == code) {
                return color.pale;
            }
        }

        return COLOR_F.pale;
    }
}
