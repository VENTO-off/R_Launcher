package relevant_craft.vento.r_launcher.manager.cheat;

public class CheatVersion {

    private String version;
    private int amount;

    public CheatVersion(String version, int amount) {
        this.version = version;
        this.amount = amount;
    }

    public String getVersion() {
        return version;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() { return  version; }
}
