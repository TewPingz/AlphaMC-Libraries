package rip.alpha.libraries.util;

import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;

public class BungeeUtil {
    public static void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server.toLowerCase());
        player.sendPluginMessage(LibrariesPlugin.getInstance().getBukkitPlugin(), "BungeeCord", out.toByteArray());
    }
}
