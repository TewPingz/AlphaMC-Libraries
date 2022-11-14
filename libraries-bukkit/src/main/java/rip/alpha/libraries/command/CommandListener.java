package rip.alpha.libraries.command;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("*")) {
            return;
        }

        String message = this.getFirstArgument(event.getMessage());

        if (message.contains(":")) {
            event.setCancelled(true);
        }
    }

    private String getFirstArgument(String string) {
        if (string.contains(" ")) {
            return string.split(" ")[0];
        }
        return string;
    }
}
