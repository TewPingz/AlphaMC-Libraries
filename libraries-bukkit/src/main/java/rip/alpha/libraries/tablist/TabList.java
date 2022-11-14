package rip.alpha.libraries.tablist;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;
import rip.alpha.libraries.util.PlayerTeamUtils;

public class TabList {

    @Getter
    private final boolean v1_8;
    private final Int2ObjectMap<TabListEntry> tabListEntries;

    private String currentHeader, currentFooter;

    public TabList(Player player) {
        int protocolVersion =
                NetworkManager.getVersion(((CraftPlayer) player).getHandle().playerConnection.networkManager.m);
        this.v1_8 = protocolVersion >= 47;
        this.tabListEntries = new Int2ObjectOpenHashMap<>(this.v1_8 ? 80 : 60);
        this.currentHeader = "";
        this.currentFooter = "";
    }

    public void setup(Player player) {
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 4; c++) {
                int index = TabListUtils.convertToSlot(r, c);
                TabListEntry entry = TabListLayout.EMPTY_ENTRY.clone();
                boolean created = this.update(player, index, entry);
                if (created) {
                    this.tabListEntries.put(index, entry);
                }
            }
        }

        if (this.isV1_8()) {
            PlayerTeamUtils.sendTeamPacket(player, player.getName(), "zAlpha", "", "", false, 0);
        }

        if (!this.isV1_8()) {
            for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                PacketPlayOutPlayerInfo info = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) target).getHandle());
                PlayerTeamUtils.sendPacket(player, info);
            }
        }
    }

    public void updateHeaderAndFooter(Player player, String header, String footer) {
        if (!this.isV1_8()) {
            return;
        }

        if (this.currentHeader.equals(header) && this.currentFooter.equals(footer)) { //early return for not changed header & footer
            return;
        }

        this.currentFooter = footer;
        this.currentHeader = header;

        IChatBaseComponent headerComponent = new ChatComponentText(header == null ? "" : header);
        IChatBaseComponent footerComponent = new ChatComponentText(footer == null ? "" : footer);

        ProtocolInjector.PacketTabHeader packet = new ProtocolInjector.PacketTabHeader(headerComponent, footerComponent);
        PlayerTeamUtils.sendPacket(player, packet);
    }

    public boolean update(Player player, int index, TabListEntry entry) {
        if (index > 80) {
            return false;
        }

        if (index >= 60 && !this.v1_8) { //dont update 1.8 slots for 1.7 players
            return false;
        }

        TabListEntry currentEntry = this.tabListEntries.get(index);

        if (currentEntry == null) {
            TabListUtils.updatePlayerInfo(player, index, entry.getPing(), entry.getSkin());
            String[] strings = TabListUtils.formatLine(entry.getText());
            String prefix = strings[0];
            String suffix = strings[1];
            TabListUtils.sendTeamPacket(player, index, prefix, suffix, 0); //mode create
            return true;
        } else {
            if (currentEntry.compare(entry)) {
                return false;
            }

            if (this.v1_8) {
                if (!currentEntry.isSameSkin(entry) || !currentEntry.isSamePing(entry)) {
                    TabListUtils.updatePlayerInfo(player, index, entry.getPing(), entry.getSkin());
                    currentEntry.setSkin(entry.getSkin());
                    currentEntry.setPing(entry.getPing());
                }
            }

            if (currentEntry.getText().equals(entry.getText())) {
                return false;
            }

            String[] strings = TabListUtils.formatLine(entry.getText());
            String prefix = strings[0];
            String suffix = strings[1];
            TabListUtils.sendTeamPacket(player, index, prefix, suffix, 2); //mode update
            currentEntry.update(entry); //update entry
        }

        return false;
    }
}
