package rip.alpha.libraries.skin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record MojangSkinListener(MojangSkinHandler skinHandler) implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.skinHandler.removeTemporarySkinEntry(event.getPlayer().getUniqueId());
    }
}
