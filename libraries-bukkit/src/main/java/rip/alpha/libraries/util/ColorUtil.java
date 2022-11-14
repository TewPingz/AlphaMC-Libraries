package rip.alpha.libraries.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.HashMap;
import java.util.Map;

public final class ColorUtil {

    private static final Map<ChatColor, Color> colorMap = new HashMap<>();

    static {
        colorMap.put(ChatColor.BLACK, Color.BLACK);
        colorMap.put(ChatColor.DARK_BLUE, Color.fromRGB(0, 0, 170));
        colorMap.put(ChatColor.DARK_GREEN, Color.fromRGB(0, 170, 0));
        colorMap.put(ChatColor.DARK_AQUA, Color.fromRGB(0, 170, 170));
        colorMap.put(ChatColor.DARK_RED, Color.fromRGB(170, 0, 0));
        colorMap.put(ChatColor.DARK_PURPLE, Color.fromRGB(170, 0, 170));
        colorMap.put(ChatColor.GOLD, Color.fromRGB(255, 170, 0));
        colorMap.put(ChatColor.GRAY, Color.fromRGB(170, 170, 170));
        colorMap.put(ChatColor.DARK_GRAY, Color.fromRGB(85, 85, 85));
        colorMap.put(ChatColor.BLUE, Color.fromRGB(85, 85, 255));
        colorMap.put(ChatColor.GREEN, Color.fromRGB(85, 255, 85));
        colorMap.put(ChatColor.AQUA, Color.fromRGB(85, 255, 255));
        colorMap.put(ChatColor.RED, Color.fromRGB(255, 85, 85));
        colorMap.put(ChatColor.LIGHT_PURPLE, Color.fromRGB(255, 85, 255));
        colorMap.put(ChatColor.YELLOW, Color.fromRGB(255, 255, 85));
        colorMap.put(ChatColor.WHITE, Color.fromRGB(255, 255, 255));
    }

    public static Color translateChatColorToColor(ChatColor chatColor) {
        return colorMap.get(chatColor);
    }
}
