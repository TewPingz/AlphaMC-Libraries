package rip.alpha.libraries.nametag.example;

import org.bukkit.entity.Player;
import rip.alpha.libraries.board.BoardUtil;
import rip.alpha.libraries.nametag.NametagProvider;
import rip.alpha.libraries.nametag.NametagVisibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExampleNametagProvider implements NametagProvider {

    private final Random random = new Random();
    private final static String REVERSED_ALPHABET = "zyxwvutsrqponmlkjihgfedcba";

    @Override
    public String getPrefix(Player player, Player viewer) {
        return BoardUtil.IDS[random.nextInt(15)];
    }

    @Override
    public String getSuffix(Player player, Player viewer) {
        return BoardUtil.IDS[random.nextInt(15)];
    }

    @Override
    public String getTeamName(Player player, Player viewer) {
        return String.valueOf(REVERSED_ALPHABET.charAt(random.nextInt(REVERSED_ALPHABET.length())));
    }

    @Override
    public boolean isFriendly(Player player, Player viewer) {
        return true;
    }

    @Override
    public NametagVisibility getNametagVisibility(Player player, Player viewer) {
        return NametagVisibility.ALWAYS;
    }

    @Override
    public List<String> getExtraTags(Player player, Player viewer) {
        if (!viewer.isOp()) {
            return null;
        }
        List<String> strings = new ArrayList<>();
        strings.add("&6[&eHELLOWORLD&6]");
        strings.add("&eDTR: &a1.0");
        return strings;
    }

    @Override
    public int getUpdateInterval() {
        return 50;
    }
}
