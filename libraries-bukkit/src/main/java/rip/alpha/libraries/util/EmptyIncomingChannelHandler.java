package rip.alpha.libraries.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class EmptyIncomingChannelHandler implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        //ignore
    }
}
