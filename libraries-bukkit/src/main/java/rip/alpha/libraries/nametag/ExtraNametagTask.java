package rip.alpha.libraries.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public record ExtraNametagTask(NametagHandler nametagHandler) implements Runnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.willBeOnline()) {
                continue;
            }

            NametagViewer viewer = this.nametagHandler.getViewer(player);
            if (viewer == null) {
                continue;
            }

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (!target.willBeOnline()) {
                    continue;
                }
                viewer.updateLunarNametag(player, target);
            }
        }
    }
}
