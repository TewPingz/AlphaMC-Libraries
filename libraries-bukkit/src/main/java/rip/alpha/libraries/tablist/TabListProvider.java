package rip.alpha.libraries.tablist;

import org.bukkit.entity.Player;

public interface TabListProvider {

    String getHeader(Player player);

    String getFooter(Player player);

    TabListLayout getLayout(Player player, boolean v1_8);

    default int getUpdateInterval() {
        return 1000;
    }
}
