package rip.alpha.libraries.tablist;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TabListTask implements Runnable {
    private final TabListHandler tabListHandler;

    @Override
    public void run() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.willBeOnline()) {
                    this.tabListHandler.update(player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
