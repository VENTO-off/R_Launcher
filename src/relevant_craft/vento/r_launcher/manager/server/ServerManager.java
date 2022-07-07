package relevant_craft.vento.r_launcher.manager.server;

import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.settings.SettingsManager;
import relevant_craft.vento.r_launcher.minecraft.servers.CompressedStreamTools;
import relevant_craft.vento.r_launcher.minecraft.servers.nbt.NBTTagCompound;
import relevant_craft.vento.r_launcher.minecraft.servers.nbt.NBTTagList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerManager {

    public static List<Server> loadServers(File file) throws IOException {
        final List<Server> list = new ArrayList<>();
        final NBTTagCompound compound = CompressedStreamTools.read(file);
        if (compound == null) {
            return list;
        }
        final NBTTagList servers = compound.getTagList("servers");
        for (int i = 0; i < servers.tagCount(); ++i) {
            list.add(Server.loadFromNBT((NBTTagCompound) servers.tagAt(i)));
        }

        return list;
    }

    public static void saveServers(List<Server> list, File file) throws IOException {
        final NBTTagList servers = new NBTTagList();
        for (Server server : list) {
            servers.appendTag(server.getNBT());
        }
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("servers", servers);
        CompressedStreamTools.safeWrite(compound, file);
    }

    public static void addServerToList(Server server) {
        File servers_dat = new File(SettingsManager.minecraftDirectory + File.separator + "servers.dat");
        if (!servers_dat.exists()) {
            List<Server> list = new ArrayList<>();
            list.add(server);
            createCache(server);
            try {
                servers_dat.createNewFile();
                saveServers(list, servers_dat);

            } catch (IOException e) {}
            return;
        }

        List<Server> list;
        try {
            list = loadServers(servers_dat);
        } catch (IOException e) {
            return;
        }

        for (Server serv : list) {
            if (serv.getAddress().equalsIgnoreCase(server.getAddress())) {
                return;
            }
        }

        list.add(0, server);
        createCache(server);

        try {
            saveServers(list, servers_dat);
        } catch (IOException e) {
            return;
        }
    }

    public static boolean hasCache(Server server) {
        File cache = new File(VENTO.LAUNCHER_DIR + File.separator + "server-cache" + File.separator + server.getAddress().replace(":", "_"));
        if (!cache.getParentFile().exists()) { cache.getParentFile().mkdir(); }
        return cache.exists();
    }

    public static boolean isCacheExpired(Server server) {
        File cache = new File(VENTO.LAUNCHER_DIR + File.separator + "server-cache" + File.separator + server.getAddress().replace(":", "_"));
        try {
            long creationTime = Files.readAttributes(cache.toPath(), BasicFileAttributes.class).lastModifiedTime().to(TimeUnit.MILLISECONDS);
            return System.currentTimeMillis() - creationTime >= 60 * 60 * 24 * 7 * 1000;
        } catch (Exception e) {
            return false;
        }
    }

    private static void createCache(Server server) {
        File cache = new File(VENTO.LAUNCHER_DIR + File.separator + "server-cache" + File.separator + server.getAddress().replace(":", "_"));
        if (cache.exists()) {
            cache.delete();
        }
        try {
            cache.createNewFile();
        } catch (IOException e) {}
    }
}
