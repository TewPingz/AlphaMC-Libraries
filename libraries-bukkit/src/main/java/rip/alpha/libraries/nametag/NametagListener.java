package rip.alpha.libraries.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.tablist.TabList;
import rip.alpha.libraries.util.PlayerTeamUtils;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public record NametagListener(NametagHandler nametagHandler) implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TaskUtil.runTaskLaterAsynchronously(() -> {
            NametagViewer viewer = new NametagViewer();

            if (LibrariesPlugin.getInstance().isTabListHandlerEnabled()) {
                Collection<String> name = Collections.singletonList(player.getName());
                for (Player online : Bukkit.getOnlinePlayers()) {
                    TabList tabList = LibrariesPlugin.getInstance().getTabListHandler().getTabList(player);
                    if (tabList != null && tabList.isV1_8()) {
                        PlayerTeamUtils.sendUpdatePlayers(online, "zAlpha", name, 4);
                    }
                }
            }

            this.nametagHandler.putViewer(player.getUniqueId(), viewer);
            this.nametagHandler.update(player);
        }, 10);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        this.nametagHandler.removeViewer(player);
        TaskUtil.executeWithPool(() -> {
            this.nametagHandler.getNametagMap()
                    .values()
                    .iterator()
                    .forEachRemaining(targetViewer -> targetViewer.removeTargetNametag(uuid));
        });
    }

}
