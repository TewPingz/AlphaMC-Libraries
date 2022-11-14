package rip.alpha.libraries.command.context.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.message.MessageColor;

import java.util.ArrayList;
import java.util.List;

public class OfflinePlayerContext implements ContextResolver<OfflinePlayer> {

    private final String errorMessage;

    public OfflinePlayerContext() {
        this.errorMessage = MessageColor.RED + "That player has not joined before.";
    }

    @Override
    public OfflinePlayer resolve(ArgumentContext<OfflinePlayer> input) {
        if (input.input().equalsIgnoreCase("self") && input.sender() instanceof Player) {
            return (OfflinePlayer) input.sender();
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input.input());

        if (!offlinePlayer.hasPlayedBefore()) {
            input.sender().sendMessage(this.errorMessage);
            return null;
        }

        return offlinePlayer;
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<OfflinePlayer> context) {
        List<String> tabComplete = new ArrayList<>();

        for (OfflinePlayer offlinePlayer : context.sender().getServer().getOfflinePlayers()) {
            if (offlinePlayer.hasPlayedBefore()) {
                tabComplete.add(offlinePlayer.getName());
            }
        }

        tabComplete.sort(String.CASE_INSENSITIVE_ORDER);

        return tabComplete;
    }
}
