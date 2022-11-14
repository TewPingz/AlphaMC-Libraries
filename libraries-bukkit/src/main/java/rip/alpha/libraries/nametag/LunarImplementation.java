package rip.alpha.libraries.nametag;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketNametagsOverride;
import org.bukkit.entity.Player;

import java.util.List;

public class LunarImplementation {
    public void sendNametagPacket(Player viewer, Player target, List<String> extraTag) {
        LCPacketNametagsOverride packet = new LCPacketNametagsOverride(target.getUniqueId(), extraTag);
        LunarClientAPI.getInstance().sendPacket(viewer, packet);
    }
}
