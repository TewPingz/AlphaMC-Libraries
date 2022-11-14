package rip.alpha.libraries.board;

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BoardLine {
    private final Board board;
    private final String lineId;

    private final int index;
    private String currentLine, currentPrefix, currentSuffix;
    private boolean teamCreated, scoreCreated = false;

    public BoardLine(int index, Board board) {
        this.lineId = BoardUtil.IDS[index] + ChatColor.RESET;
        this.board = board;
        this.index = index;
    }

    public void updateLine(Player player, String line) {
        if (line == null) {
            if (this.scoreCreated) {
                this.sendScorePacket(player, 1);
            }
            if (this.teamCreated) {
                this.sendTeamPacket(player, 1);
            }

            this.scoreCreated = false;
            this.teamCreated = false;
            this.currentPrefix = null;
            this.currentSuffix = null;
            this.currentLine = null;
            return;
        }

        if (this.currentLine != null && this.currentLine.equals(line)) {
            return; //No need to update since it's the exact same line
        }

        this.currentLine = line;
        String[] strings = this.formatLine(this.currentLine);

        this.currentPrefix = strings[0];
        this.currentSuffix = strings[1];

        if (!scoreCreated) {
            this.sendScorePacket(player, 0);
            this.scoreCreated = true;
        }

        if (!teamCreated) {
            this.teamCreated = true;
            this.sendTeamPacket(player, 0);
        } else {
            this.sendTeamPacket(player, 2);
        }
    }

    public String[] formatLine(String line) {
        String[] strings = this.split(line);
        String prefix = strings[0];
        String suffix = strings[1];
        suffix = suffix.substring(0, Math.min(16, suffix.length()));
        return new String[]{prefix, suffix};
    }

    private String[] split(String string) {
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
                if (ChatColor.getLastColors(stringOne).equalsIgnoreCase(this.lineId)) {
                    stringTwo = string.substring(16);
                } else {
                    stringTwo = ChatColor.getLastColors(stringOne) + string.substring(16);
                }
            }

            return new String[]{stringOne, stringTwo};
        } else {
            return new String[]{string, ""};
        }
    }

    //a = name
    //b = name of board
    //c = score
    //d = mode 0 = create / update 1 = remove
    private void sendScorePacket(Player player, int mode) {
        PacketPlayOutScoreboardScore scoreboardScore = new PacketPlayOutScoreboardScore();
        scoreboardScore.a = this.lineId;
        scoreboardScore.b = Board.BOARD_ID;
        if (mode != 1) {
            scoreboardScore.c = this.index;
        }
        scoreboardScore.d = mode;
        this.board.sendPacket(player, scoreboardScore);
    }

    //a = name
    //b = display //16
    //c = prefix //16
    //d = suffix //16
    //e = players
    //f = mode
    //g = friendlyFire 1 = on 0 = off
    //mode 0 = create, 1 = remove, 2 = updated, 3 = new players, 4 = remove players
    private void sendTeamPacket(Player player, int mode) {
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam();
        teamPacket.a = this.lineId;
        teamPacket.b = "";

        if (mode == 0 || mode == 2) {
            if (this.checkString(this.currentPrefix)) {
                teamPacket.c = this.currentPrefix;
            }
            if (this.checkString(this.currentSuffix)) {
                teamPacket.d = this.currentSuffix;
            }
        }

        if (mode == 0 || mode == 3 || mode == 4) {
            if (mode == 0 || mode == 3) {
                teamPacket.e = new ArrayList<String>();
                teamPacket.e.add(this.lineId);
            } else {
                teamPacket.e.add(this.lineId);
            }
        }

        teamPacket.f = mode;
        teamPacket.g = 0;

        this.board.sendPacket(player, teamPacket);
    }

    private boolean checkString(String s) {
        return s != null && 16 >= s.length();
    }
}
