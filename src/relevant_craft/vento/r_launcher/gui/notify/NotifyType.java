package relevant_craft.vento.r_launcher.gui.notify;

import de.jensd.fx.glyphs.icons525.Icons525;

public enum NotifyType {
    INFO(Icons525.INFO_CIRCLE),
    WARNING(Icons525.WARNING_SIGN),
    QUESTION(Icons525.QUESTION),
    ;

    private Icons525 icon;

    NotifyType(Icons525 icon) {
        this.icon = icon;
    }

    public Icons525 getIcon() {
        return icon;
    }
}
