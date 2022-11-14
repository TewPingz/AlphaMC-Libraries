package rip.alpha.libraries.nametag;

import lombok.Getter;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.util.message.MessageTranslator;

import java.util.*;

/**
 * A nametag handler that will be able to make nametags using packets & teams
 * This packet handler will also allow you to change the teamName allowing you to change priority for 1.8 & above players
 * Without the limits of the default bukkit team system you will be able to create powerful nametags with this api
 *
 * @author TewPingz
 */
public class NametagViewer {

    private final Set<String> createdTeams;
    @Getter
    private final Map<UUID, NametagTarget> nametagMap;

    //The viewer of the nametags
    public NametagViewer() {
        this.createdTeams = new HashSet<>();
        this.nametagMap = new HashMap<>();
    }

    //The target here is the player to update the nametag for this player.
    public void update(Player viewer, Player target, String teamName, String prefix, String suffix, boolean friendly, NametagVisibility visibility) {
        UUID uuid = target.getUniqueId();
        NametagTarget nametagTarget = this.getNametagTarget(uuid);

        String currentTeamName = null;
        boolean flag = false;
        boolean teamFlag = false;

        if (nametagTarget == null) {
            nametagTarget = new NametagTarget(teamName, prefix, suffix, friendly, visibility);
            this.nametagMap.put(uuid, nametagTarget);
            flag = true;
        } else {
            currentTeamName = nametagTarget.getTeamName();
            String currentPrefix = nametagTarget.getPrefix();
            String currentSuffix = nametagTarget.getSuffix();
            boolean currentFriendly = nametagTarget.isFriendly();
            NametagVisibility currentVisibility = nametagTarget.getVisibility();

            if (currentTeamName.equals(teamName) && currentPrefix.equals(prefix) &&
                    currentSuffix.equals(suffix) && currentFriendly == friendly && currentVisibility.ordinal() == visibility.ordinal()) {
                return; //Return because no changes were made.
            }

            teamFlag = !currentTeamName.equals(teamName);
            nametagTarget.setTeamName(teamName);
            nametagTarget.setPrefix(prefix);
            nametagTarget.setSuffix(suffix);
            nametagTarget.setFriendly(friendly);
            nametagTarget.setVisibility(visibility);
        }

        if (teamFlag) {
            this.sendPlayerTeamPacket(viewer, target.getName(), currentTeamName.toLowerCase(), false);
        }

        if (!this.createdTeams.contains(teamName.toLowerCase())) {
            this.sendTeamPacket(viewer, target.getName(), teamName, prefix, suffix, friendly, visibility, 0);
            this.createdTeams.add(teamName.toLowerCase());
        } else {
            this.sendTeamPacket(viewer, null, teamName, prefix, suffix, friendly, visibility, 2);
            if (flag || teamFlag) {
                this.sendPlayerTeamPacket(viewer, target.getName(), teamName, true);
            }
        }

        this.updateLunarNametag(viewer, target);
    }

    public void updateLunarNametag(Player viewer, Player target) {
        LunarImplementation implementation = LibrariesPlugin
                .getInstance().getNametagHandler().getLunarImplementation();
        if (implementation == null) {
            return;
        }
        String prefix = "", suffix = "";
        NametagTarget nametagTarget = this.getNametagTarget(target.getUniqueId());
        if (nametagTarget != null) {
            prefix = nametagTarget.getPrefix();
            suffix = nametagTarget.getSuffix();
        }
        List<String> extraTag = new ArrayList<>(LibrariesPlugin.getInstance().getNametagHandler().getExtraTags(viewer, target));
        extraTag.add(prefix + target.getName() + suffix);
        implementation.sendNametagPacket(viewer, target, MessageTranslator.translateLines(extraTag));
    }

    private NametagTarget getNametagTarget(UUID uuid) {
        return this.nametagMap.get(uuid);
    }

    public void removeTargetNametag(UUID target) {
        this.nametagMap.remove(target);
    }

    public void sendRemoveTeamsPacket(Player player) {
        this.createdTeams.forEach(teams ->
                this.sendTeamPacket(player, null, null, null, null, false, NametagVisibility.ALWAYS, 1));
    }

    private void sendPlayerTeamPacket(Player viewer, String targetName, String teamName, boolean add) {
        this.sendTeamPacket(viewer, targetName, teamName, null, null, true, NametagVisibility.ALWAYS, add ? 3 : 4);
    }

    //a = name
    //b = display //16
    //c = prefix //16
    //d = suffix //16
    //e = players
    //f = mode
    //g = friendlyFire 1 = on 0 = off
    //mode 0 = create, 1 = remove, 2 = update, 3 = new players, 4 = remove players
    private void sendTeamPacket(Player viewer, String targetName, String teamName, String prefix, String suffix, boolean friendly, NametagVisibility visibility, int mode) {
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam();
        teamPacket.a = teamName;
        teamPacket.b = teamName;

        if (mode == 0 || mode == 2) {
            teamPacket.c = prefix;
            teamPacket.d = suffix;
            teamPacket.nametagVisibility = visibility.getNmsName();
        }

        if (mode == 0 || mode == 3 || mode == 4) {
            teamPacket.e = new ArrayList<String>();
            teamPacket.e.add(targetName);
        }

        teamPacket.f = mode;

        if (mode == 0 || mode == 2) {
            teamPacket.g = friendly ? 3 : 0;
        }

        this.sendPacket(viewer, teamPacket);
    }


    void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
