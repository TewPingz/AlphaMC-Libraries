package rip.alpha.libraries.command.context.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.message.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

public class PlayerContext implements ContextResolver<Player> {
    private static final String notCurrentlyOnline = "{} is not currently online.";

    @Override
    public Player resolve(ArgumentContext<Player> input) {
        if (input.input().equalsIgnoreCase("self") && input.sender() instanceof Player) {
            return (Player) input.sender();
        }

        Player player = Bukkit.getPlayer(input.input());

        if (player == null) {
            input.sender().sendMessage(MessageBuilder.constructError(notCurrentlyOnline, input.input()));
            return null;
        }

        if (input.sender() instanceof Player sender) {
            if (!sender.canSee(player)) {
                sender.sendMessage(MessageBuilder.constructError(notCurrentlyOnline, input.input()));
                return null;
            }
        }

        return player;
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Player> context) {
        List<String> tabComplete = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (context.sender() instanceof Player sender) {
                if (!sender.canSee(player)) {
                    continue;
                }
            }
            tabComplete.add(player.getName());
        }

        tabComplete.sort(String.CASE_INSENSITIVE_ORDER);

        return tabComplete;
    }
}
