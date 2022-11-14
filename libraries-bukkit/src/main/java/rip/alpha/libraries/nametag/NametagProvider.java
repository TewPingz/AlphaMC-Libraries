package rip.alpha.libraries.nametag;

import org.bukkit.entity.Player;

import java.util.List;

public interface NametagProvider {

    /**
     * A function to get the prefix of the player
     *
     * @param player the player that will have the prefix
     * @param viewer the viewer that will see the prefix
     * @return the prefix itself
     */
    String getPrefix(Player player, Player viewer);

    /**
     * A function to get the suffix of the player
     *
     * @param player the player that will have the suffix
     * @param viewer the viewer that will see the suffix
     * @return the suffix itself
     */
    String getSuffix(Player player, Player viewer);

    /**
     * A function to get the teamName of the player
     *
     * @param player the player that will have the teamName
     * @param viewer the viewer that will see the teamName
     * @return the teamName itself
     */
    String getTeamName(Player player, Player viewer);

    /**
     * A function to check if the team will be friendly
     *
     * @param player the player that will have the team
     * @param viewer the viewer that will view the team
     * @return if the team between those two players is friendly
     */
    boolean isFriendly(Player player, Player viewer);

    /**
     * A function to get the nametag visibility for the team that will be created
     *
     * @param player the player that will have the team
     * @param viewer the viewer that will view the team
     * @return the type of nametag visibility that will be used
     */
    NametagVisibility getNametagVisibility(Player player, Player viewer);

    /**
     * A function to get the extra tags for a player
     *
     * @param player the player to get the extra tags for
     * @param viewer the viewer that will see the tags
     * @return the list of tags
     */
    List<String> getExtraTags(Player player, Player viewer);

    /**
     * A function to get the update interval of the nametag task
     *
     * @return if the team between those two players is friendly
     */
    default int getUpdateInterval() {
        return 1000;
    }

}
