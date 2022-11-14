package rip.alpha.libraries.tablist;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.util.task.TaskUtil;
import rip.foxtrot.spigot.fSpigot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TabListHandler {
    private final TabListProvider provider;
    private final Map<UUID, TabList> tabListMap;

    public TabListHandler(JavaPlugin plugin, TabListProvider provider) {
        if (plugin.getServer().getMaxPlayers() < 80) {
            ((CraftServer) Bukkit.getServer()).getHandle().setMaxPlayers(80);
        }

        fSpigot.INSTANCE.addPacketHandler(new TabListPacketHandler());

        TabListUtils.init();
        this.provider = provider;
        this.tabListMap = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new TabListListener(this), plugin);

        int updateInterval = this.provider.getUpdateInterval();
        TaskUtil.scheduleAtFixedRateOnPool(new TabListTask(this), updateInterval, updateInterval, TimeUnit.MILLISECONDS);

        LibrariesPlugin.getInstance().registerTabListHandler(this);
    }

    public TabList getTabList(Player player) {
        return this.getTabList(player.getUniqueId());
    }

    void putTabList(UUID uuid, TabList tabList) {
        this.tabListMap.put(uuid, tabList);
    }

    TabList getTabList(UUID uuid) {
        return this.tabListMap.get(uuid);
    }

    void removeTabList(UUID uuid) {
        this.tabListMap.remove(uuid);
    }

    void removeTabList(Player player) {
        this.removeTabList(player.getUniqueId());
    }

    String getHeader(Player player) {
        String header = this.provider.getHeader(player);
        if (header == null) {
            header = "";
        }
        return header;
    }

    String getFooter(Player player) {
        String footer = this.provider.getFooter(player);
        if (footer == null) {
            footer = "";
        }
        return footer;
    }

    TabListLayout getLayout(Player player, boolean v1_8) {
        TabListLayout layout = this.provider.getLayout(player, v1_8);
        if (layout == null) {
            layout = new TabListLayout(v1_8);
        }
        return layout;
    }

    void update(Player player) {
        TabList tabList = this.getTabList(player);
        if (tabList == null) {
            return;
        }
        tabList.updateHeaderAndFooter(player, this.getHeader(player), this.getFooter(player));
        Map<Integer, TabListEntry> entryMap = this.getLayout(player, tabList.isV1_8()).getEntryMap();
        entryMap.forEach((index, entry) -> tabList.update(player, index, entry));
    }
}
