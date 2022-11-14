package rip.alpha.libraries.board;

import org.bukkit.ChatColor;

public class BoardUtil {
    public static final String[] IDS = new String[16];

    static {
        for (int i = 0; i < 16; i++) {
            IDS[i] = ChatColor.values()[i].toString();
        }
    }
}
