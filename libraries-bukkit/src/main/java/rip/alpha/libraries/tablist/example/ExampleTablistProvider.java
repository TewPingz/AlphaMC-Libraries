package rip.alpha.libraries.tablist.example;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.tablist.TabListLayout;
import rip.alpha.libraries.tablist.TabListProvider;

import java.util.Random;

public class ExampleTablistProvider implements TabListProvider {

    private final Random random = new Random();
    private static final String LETTERS = "abcdefghijklmnopqrtuvwyz";

    @Override
    public String getHeader(Player player) {
        return null;
    }

    @Override
    public String getFooter(Player player) {
        return null;
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Override
    public TabListLayout getLayout(Player player, boolean v1_8) {
        TabListLayout layout = new TabListLayout(v1_8);

        int r = 0;

        for (Player online : Bukkit.getOnlinePlayers()) {
            layout.put(0, r, online.getName(), LibrariesPlugin.getInstance().getMojangSkinHandler().getMojangSkinFromPlayer(online));
            if (++r >= 20) { // r++                         ++r
                break;
            }
        }

        r = 0;
        String message = "WELCOME TO ALPHA";
        for (char c : message.toCharArray()) {
            layout.put(1, r, "", LibrariesPlugin.getInstance().getMojangSkinHandler().getLetterSkin("orange", c));
            r++;
        }

        return layout;
    }
}
