package relevant_craft.vento.r_launcher.manager.server;

import relevant_craft.vento.r_launcher.minecraft.servers.nbt.NBTTagCompound;

public class Server {

    private String name;
    private String address;
    private String ip;
    private String port;
    private boolean hideAddress;
    private int acceptTextures;

    public boolean isHideAddress() {
        return this.hideAddress;
    }

    public void setHideAddress(final boolean hideAddress) {
        this.hideAddress = hideAddress;
    }

    public int getAcceptTextures() {
        return this.acceptTextures;
    }

    public void setAcceptTextures(final int acceptTextures) {
        this.acceptTextures = acceptTextures;
    }

    @Override
    public String toString() {
        return "Server{name='" + this.name + '\'' + ", address='" + this.address + '\'' + ", ip='" + this.ip + '\'' + ", port='" + this.port + '\'' + ", hideAddress=" + this.hideAddress + ", acceptTextures=" + this.acceptTextures + '}';
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
        this.updateAddress();
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(final String port) {
        this.port = port;
        this.updateAddress();
    }

    private void updateAddress() {
        this.address = this.ip + ((this.port == null) ? "" : (":" + this.port));
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        if (address == null) {
            ip = null;
            port = null;
        } else {
            String[] split = splitAddress(address);
            this.ip = split[0];
            this.port = split[1];
        }
        this.address = address;
    }

    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", this.name);
        compound.setString("ip", this.address);
        compound.setBoolean("hideAddress", this.hideAddress);
        if (this.acceptTextures != 0) {
            compound.setBoolean("acceptTextures", this.acceptTextures == 1);
        }
        return compound;
    }

    public static Server loadFromNBT(NBTTagCompound nbt) {
        Server server = new Server();
        server.setName(nbt.getString("name"));
        server.setAddress(nbt.getString("ip"));
        server.hideAddress = nbt.getBoolean("hideAddress");
        if (nbt.hasKey("acceptTextures")) {
            server.acceptTextures = (nbt.getBoolean("acceptTextures") ? 1 : -1);
        }
        return server;
    }

    public static String[] splitAddress(String address) {
        String[] array = address.split(":");
        if (array.length == 1) {
            return new String[]{array[0], "25565"};
        } else if (array.length == 2) {
            return new String[]{array[0], array[1]};
        } else {
            return new String[]{"127.0.0.1", "25565"};
        }
    }

}
