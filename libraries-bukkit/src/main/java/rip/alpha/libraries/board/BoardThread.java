package rip.alpha.libraries.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public record BoardThread(BoardHandler boardHandler) implements Runnable {
    @Override
    public void run() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.willBeOnline()) {
                    Board board = this.boardHandler.getBoard(player);
                    if (board != null) {
                        String title = this.boardHandler.getBoardTitle(player);
                        List<String> lines = this.boardHandler.getBoardLines(player);
                        board.update(player, title, lines);
                        this.boardHandler.getProvider().onBoardUpdate(player, board);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
