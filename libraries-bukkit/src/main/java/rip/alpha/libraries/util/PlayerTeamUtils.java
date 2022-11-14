package rip.alpha.libraries.util;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerTeamUtils {
    public static void sendPlayerInfoPacket(Player player, int ping, String username, GameProfile profile, int mode) {
        PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo();
        playerInfo.ping = ping;
        playerInfo.action = mode;
        playerInfo.gamemode = 0;
        playerInfo.username = username;
        playerInfo.player = profile;
        sendPacket(player, playerInfo);
    }

    public static void sendUpdatePlayers(Player viewer, String teamName, Collection<String> players, int mode) {
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam();
        teamPacket.a = teamName;
        teamPacket.b = teamName;
        teamPacket.c = "";
        teamPacket.d = "";

        if (mode == 0 || mode == 3 || mode == 4) {
            teamPacket.e = players;
        }

        teamPacket.f = mode;
        teamPacket.g = 0;

        sendPacket(viewer, teamPacket);
    }

    //a = name
    //b = display //16
    //c = prefix //16
    //d = suffix //16
    //e = players
    //f = mode
    //g = friendlyFire 1 = on 0 = off
    //mode 0 = create, 1 = remove, 2 = update, 3 = new players, 4 = remove players
    public static void sendTeamPacket(Player viewer, String targetName, String teamName, String prefix, String suffix, boolean friendly, int mode) {
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam();
        teamPacket.a = teamName;
        teamPacket.b = teamName;

        if (mode == 0 || mode == 2) {
            teamPacket.c = prefix;
            teamPacket.d = suffix;
        }

        if (mode == 0 || mode == 3 || mode == 4) {
            if (mode == 0 || mode == 3) {
                teamPacket.e = new ArrayList<String>();
                teamPacket.e.add(targetName);
            } else {
                teamPacket.e.add(targetName);
            }
        }

        teamPacket.f = mode;

        if (mode == 0 || mode == 2) {
            teamPacket.g = friendly ? 3 : 0;
        }

        sendPacket(viewer, teamPacket);
    }

    public static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
