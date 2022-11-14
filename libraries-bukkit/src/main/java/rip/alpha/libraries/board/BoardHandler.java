package rip.alpha.libraries.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.util.message.MessageTranslator;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BoardHandler {
    private final BoardProvider provider;
    private final Map<UUID, Board> boardMap;

    public BoardHandler(JavaPlugin plugin, BoardProvider provider) {
        this.provider = provider;
        this.boardMap = new HashMap<>();

        Bukkit.getServer().getPluginManager().registerEvents(new BoardListener(this), plugin);

        int updateInterval = this.provider.getUpdateInterval();
        TaskUtil.scheduleAtFixedRateOnPool(new BoardThread(this), updateInterval, updateInterval, TimeUnit.MILLISECONDS);

        LibrariesPlugin.getInstance().registerBoardHandler(this);
    }

    void putBoard(UUID uuid, Board board) {
        this.boardMap.put(uuid, board);
    }

    void putBoard(Player player, Board board) {
        this.putBoard(player.getUniqueId(), board);
        board.update(player, this.getBoardTitle(player), this.getBoardLines(player));
    }

    Board removeBoard(UUID uuid) {
        return this.boardMap.remove(uuid);
    }

    Board removeBoard(Player player) {
        Board board = this.removeBoard(player.getUniqueId());
        if (board != null) {
            board.sendObjectPacket(player, 1);
        }
        return board;
    }

    Board createBoard(Player player) {
        return new Board(player, this);
    }

    Board getBoard(UUID uuid) {
        return this.boardMap.get(uuid);
    }

    Board getBoard(Player player) {
        return this.getBoard(player.getUniqueId());
    }

    BoardProvider getProvider() {
        return this.provider;
    }

    String getBoardTitle(Player player) {
        String title = this.provider.getTitle(player);
        if (title == null) {
            title = "";
        }
        return title;
    }

    List<String> getBoardLines(Player player) {
        List<String> lines = this.provider.getLines(player);
        if (lines == null) {
            return Collections.emptyList();
        }
        return MessageTranslator.translateLines(lines);
    }
}
