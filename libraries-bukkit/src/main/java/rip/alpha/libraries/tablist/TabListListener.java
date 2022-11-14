package rip.alpha.libraries.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.util.PlayerTeamUtils;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public record TabListListener(TabListHandler tabListHandler) implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        TaskUtil.runTaskAsynchronously(() -> {
            TabList tabList = new TabList(player);
            tabList.setup(player);

            if (tabList.isV1_8()) {
                String header = this.tabListHandler.getHeader(player);
                String footer = this.tabListHandler.getFooter(player);
                tabList.updateHeaderAndFooter(player, header, footer);
            }

            this.tabListHandler.putTabList(player.getUniqueId(), tabList);

            if (tabList.isV1_8()) {
                Collection<String> players = new HashSet<>();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    players.add(online.getName());
                }
                PlayerTeamUtils.sendUpdatePlayers(player, "zAlpha", players, 3);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TabList tabList = LibrariesPlugin.getInstance().getTabListHandler().getTabList(player);
            if (tabList != null && tabList.isV1_8()) {
                PlayerTeamUtils.sendUpdatePlayers(player, "zAlpha", Collections.singleton(event.getName()), 3);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.tabListHandler.removeTabList(event.getPlayer());
    }
}
