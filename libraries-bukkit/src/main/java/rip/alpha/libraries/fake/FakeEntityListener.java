package rip.alpha.libraries.fake;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import rip.alpha.libraries.util.PlayerTeamUtils;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * @author TewPingz
 */
public record FakeEntityListener(FakeEntityHandler handler) implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        PlayerTeamUtils.sendUpdatePlayers(player, "zEntities", Collections.emptyList(), 0);
        TaskUtil.runTaskLaterAsynchronously(() -> {
            for (FakeEntity fakeEntity : handler.getEntities()) {
                fakeEntity.addToTeam(player);

                if (!fakeEntity.showOnJoin()) {
                    continue;
                }

                fakeEntity.getShouldBeAbleToView().add(player.getUniqueId());

                if (!fakeEntity.getWorld().getUID().equals(location.getWorld().getUID())) {
                    continue;
                }


                if (!(fakeEntity.getLocation().distanceSquared(location) < fakeEntity.range())) {
                    continue;
                }

                fakeEntity.show(player);
            }
        }, 10);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        this.handler.getEntityExecutor().execute(() ->
                handler.getAllEntitiesPlayerCanSee(uuid).forEach(fakeEntity -> fakeEntity.handleDisconnect(uuid)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        this.handler.handleMovement(player, from, to);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        this.handler.handleMovement(player, from, to);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        this.updateWithDelay(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        this.updateWithDelay(event.getPlayer());
    }

    private void updateWithDelay(Player player) {
        Set<FakeEntity> entityList = this.handler.getAllEntitiesPlayerCanSee(player.getUniqueId());

        if (entityList.isEmpty()) {
            return;
        }

        TaskUtil.runTaskLaterAsynchronously(() -> {
            for (FakeEntity fakeEntity : entityList) {
                fakeEntity.show(player);
            }
        }, 2);
    }
}
