package rip.alpha.libraries.board;

import org.bukkit.entity.Player;

import java.util.List;

public interface BoardProvider {

    /**
     * Get the title of the board
     *
     * @param player the player that has the board
     * @return the title of the board
     */
    String getTitle(Player player);

    /**
     * Get the lines for the board
     *
     * @param player the player that has the board
     * @return the lines that the board has
     */
    List<String> getLines(Player player);

    /**
     * Get the update interval for the scoreboard
     *
     * @return the update interval for the scoreboard
     */
    default int getUpdateInterval() {
        return 1000;
    }

    ;

    /**
     * A function that is called when the player has his board created.
     *
     * @param player the player that had the board created
     * @param board  the board that was created
     */
    default void onBoardCreate(Player player, Board board) {
    }

    ;

    /**
     * A function that is called when the player has his board updated.
     *
     * @param player the player that had the board updated
     * @param board  the board that was updated
     */
    default void onBoardUpdate(Player player, Board board) {
    }

    ;

    /**
     * A function that is called when the player had his board removed
     *
     * @param player the player that had his board removed
     * @param board  the board that was removed
     */
    default void onBoardRemove(Player player, Board board) {
    }

    ;

}
