package rip.alpha.libraries.command.context.impl.bukkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModeContext implements ContextResolver<GameMode> {
    private final List<String> tabComplete = new ArrayList<>();

    public GameModeContext() {
        for (GameModeType type : GameModeType.types) {
            this.tabComplete.add(type.getName());
            this.tabComplete.addAll(Arrays.asList(type.getAliases()));
        }
        this.tabComplete.sort(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public GameMode resolve(ArgumentContext<GameMode> input) {
        GameModeType gameModeType = GameModeType.getGameModeType(input.input());

        if (gameModeType == null) {
            input.sender().sendMessage(ChatColor.RED + "That gamemode does not exist");
            return null;
        }

        return gameModeType.getGameMode();
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<GameMode> context) {
        return this.tabComplete;
    }

    @Getter
    @RequiredArgsConstructor
    public enum GameModeType {
        CREATIVE("Creative", new String[]{"1", "c"}, GameMode.CREATIVE),
        SURVIVAL("Survival", new String[]{"0", "s"}, GameMode.SURVIVAL);

        private final String name;
        private final String[] aliases;
        private final GameMode gameMode;

        private static final GameModeType[] types = GameModeType.values();

        public static GameModeType getGameModeType(String s) {
            for (GameModeType type : types) {
                if (s.equalsIgnoreCase(type.getName())) {
                    return type;
                }
                for (String alias : type.getAliases()) {
                    if (s.equalsIgnoreCase(alias)) {
                        return type;
                    }
                }
            }
            return null;
        }
    }
}
