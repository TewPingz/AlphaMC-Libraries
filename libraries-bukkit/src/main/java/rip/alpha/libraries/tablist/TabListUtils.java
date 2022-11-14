package rip.alpha.libraries.tablist;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.alpha.libraries.skin.MojangSkin;
import rip.alpha.libraries.util.PlayerTeamUtils;

import java.util.UUID;

public final class TabListUtils {
    private static final String[] TABLIST_IDS = new String[80];
    private static final UUID[] TABLIST_UUIDS = new UUID[80];

    public static void init() {
        for (int i = 0; i < 80; i++) {
            TABLIST_IDS[i] = makeTabListEntry(i);
            TABLIST_UUIDS[i] = UUID.randomUUID();
        }
    }

    public static int convertToSlot(int row, int column) {
        return row + (column * 20);
    }

    public static String nameEntry(int slot) {
        return TABLIST_IDS[slot];
    }

    public static UUID uuidEntry(int slot) {
        return TABLIST_UUIDS[slot];
    }

    private static String makeTabListEntry(int slot) {
        return ChatColor.BOLD + ChatColor.GREEN.toString() + ChatColor.UNDERLINE +
                ChatColor.YELLOW +
                (slot >= 10 ? ChatColor.COLOR_CHAR + String.valueOf(slot / 10) +
                        ChatColor.COLOR_CHAR + slot % 10
                        : ChatColor.BLACK.toString() +
                        ChatColor.COLOR_CHAR + slot) + ChatColor.RESET;
    }

    public static String[] formatLine(String line) {
        String[] strings = split(line);
        String prefix = strings[0];
        String suffix = strings[1];
        suffix = suffix.substring(0, Math.min(16, suffix.length()));
        return new String[]{prefix, suffix};
    }

    private static String[] split(String string) {
        if (string.length() > 16) {
            String stringOne = string.substring(0, 16);
            String stringTwo;

            if (stringOne.charAt(15) == ChatColor.COLOR_CHAR) {
                stringOne = string.substring(0, 15);
                stringTwo = string.substring(15);
            } else if (stringOne.charAt(14) == ChatColor.COLOR_CHAR) {
                stringOne = string.substring(0, 14);
                stringTwo = string.substring(14);
            } else {
                stringTwo = ChatColor.getLastColors(stringOne) + string.substring(16);
            }

            return new String[]{stringOne, stringTwo};
        } else {
            return new String[]{string, ""};
        }
    }

    public static void updatePlayerInfo(Player player, Player target, int mode) {
        GameProfile gameProfile = ((CraftPlayer) target).getProfile();
        PlayerTeamUtils.sendPlayerInfoPacket(player, 0, player.getName(), gameProfile, mode); //remove the player first
    }


    public static void updatePlayerInfo(Player player, int index, int ping, MojangSkin skin) {
        String name = TabListUtils.nameEntry(index);
        UUID uuid = TabListUtils.uuidEntry(index);
        GameProfile gameProfile = new GameProfile(uuid, name);
        gameProfile.getProperties().put("textures", skin.toProperty());
        PlayerTeamUtils.sendPlayerInfoPacket(player, ping, name, gameProfile, 4); //remove the player first
        PlayerTeamUtils.sendPlayerInfoPacket(player, ping, name, gameProfile, 0); //add the player back
        TabListUtils.sendTeamPacket(player, index, "", "", 3); //add the player to the team
    }

    public static void sendTeamPacket(Player viewer, int index, String prefix, String suffix, int mode) {
        String name = TabListUtils.nameEntry(index);
        String teamName = "$" + name;
        PlayerTeamUtils.sendTeamPacket(viewer, name, teamName, prefix, suffix, false, mode);
    }
}
