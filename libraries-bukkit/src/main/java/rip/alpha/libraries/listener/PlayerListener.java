package rip.alpha.libraries.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import rip.alpha.libraries.util.data.NameCache;
import rip.alpha.libraries.util.message.MessageColor;
import rip.alpha.libraries.util.message.MessageTranslator;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        NameCache.getInstance().updateEntry(event.getUniqueId(), event.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SKULL) {
            if (event.getClickedBlock().getState() instanceof Skull skull) {
                if (skull.getOwner() != null && !skull.getOwner().equalsIgnoreCase("null")) {
                    event.getPlayer().sendMessage(MessageColor.YELLOW + "This is the head of: " + skull.getOwner()); //thanks for the format minehq!
                }
            }
        }
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if (event.getPlayer().isOp()) {
            for (int i = 0; i < event.getLines().length; i++) {
                String line = event.getLine(i);
                event.setLine(i, MessageTranslator.translate(line));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreProcess(PlayerCommandPreprocessEvent event){
        if (event.getPlayer().isOp()) {
            return;
        }

        switch (event.getMessage().toLowerCase()) {
            case "//evaluate", "//eval", "//solve", "//calculate", "//calc", "/bukkit:me", "/minecraft:me", "/me" -> {
                event.getPlayer().sendMessage(MessageColor.RED + "You do not have permission to execute this command.");
                event.setCancelled(true);
            }
        }
    }
}
