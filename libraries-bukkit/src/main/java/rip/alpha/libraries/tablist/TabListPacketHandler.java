package rip.alpha.libraries.tablist;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import rip.alpha.libraries.LibrariesPlugin;
import rip.foxtrot.spigot.handler.PacketHandler;

public class TabListPacketHandler implements PacketHandler {
    @Override
    public boolean handleSentPacketCancellable(PlayerConnection connection, Packet packet) {
        if (packet instanceof PacketPlayOutScoreboardTeam) {
            PacketPlayOutScoreboardTeam team = (PacketPlayOutScoreboardTeam) packet;
            if (team.a.equalsIgnoreCase("zAlpha")) { //stupid 1.7 catch
                TabList tabList = LibrariesPlugin.getInstance().getTabListHandler().getTabList(connection.getPlayer());
                return tabList == null || tabList.isV1_8();
            }
        }
        return true;
    }

    @Override
    public boolean handlesCancellable() {
        return true;
    }
}
