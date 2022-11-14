package rip.alpha.libraries.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public record NametagTask(NametagHandler nametagHandler) implements Runnable {
    @Override
    public void run() {
        try {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (viewer.willBeOnline()) {
                    this.nametagHandler.update(viewer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
