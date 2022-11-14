package rip.alpha.libraries.board;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardObjective;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Packet based scoreboard for players
 * Uses PacketPlayOutScoreboardObjective, PacketPlayOutScoreboardDisplayObjective, PacketPlayOutScoreboardScore & PacketPlayOutScoreboardTeam
 * By using the packets to handle scoreboards you dodge the Bukkit storage of teams and the boards
 * Allowing for better efficiency
 * <p>
 * This has been used on Alpha & can be seen at moments with server lag to completely
 * continue working as it is completely on a separate thread
 * allowing tps lag to happen and not be noticed through scoreboard!
 *
 * @author TewPingz
 */
public class Board {
    public static final String BOARD_ID = "b-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

    private final Int2ObjectMap<BoardLine> boardLines;
    private String lastTitle;

    public Board(Player player, BoardHandler boardHandler) {
        this.lastTitle = boardHandler.getBoardTitle(player);
        this.boardLines = new Int2ObjectOpenHashMap<>(15);

        this.sendObjectPacket(player, 0);
        this.sendScoreboardDisplayObject(player);

        for (int i = 0; i < 15; i++) {
            BoardLine line = new BoardLine(i, this);
            boardLines.put(i, line);
        }
    }

    public void update(Player player, String title, List<String> lines) {
        if (!this.lastTitle.equals(title)) {
            this.lastTitle = title;
            this.sendObjectPacket(player, 2);
        }

        Collections.reverse(lines);

        for (int index = 0; index < 15; index++) {
            BoardLine line = this.boardLines.get(index);
            String updatedLine = index >= lines.size() ? null : lines.get(index);
            line.updateLine(player, updatedLine);
        }
    }

    private void sendScoreboardDisplayObject(Player player) {
        PacketPlayOutScoreboardDisplayObjective scoreboardDisplayObjective = new PacketPlayOutScoreboardDisplayObjective();
        scoreboardDisplayObjective.a = 1; //sidebar
        scoreboardDisplayObjective.b = BOARD_ID;
        this.sendPacket(player, scoreboardDisplayObjective);
    }

    //0 = Create, 1 = Remove, 2 = Update
    public void sendObjectPacket(Player player, int mode) {
        PacketPlayOutScoreboardObjective scoreboardObjective = new PacketPlayOutScoreboardObjective();
        scoreboardObjective.a = BOARD_ID;
        scoreboardObjective.b = this.lastTitle;
        scoreboardObjective.c = mode;
        this.sendPacket(player, scoreboardObjective);
    }

    void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
