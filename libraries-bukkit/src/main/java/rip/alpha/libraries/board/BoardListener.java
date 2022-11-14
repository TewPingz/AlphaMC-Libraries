package rip.alpha.libraries.board;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class BoardListener implements Listener {
    private final BoardHandler boardHandler;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Board board = this.boardHandler.createBoard(player);
        this.boardHandler.putBoard(player, board);
        this.boardHandler.getProvider().onBoardCreate(player, board);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Board board = this.boardHandler.removeBoard(player);
        this.boardHandler.getProvider().onBoardRemove(player, board);
    }
}
