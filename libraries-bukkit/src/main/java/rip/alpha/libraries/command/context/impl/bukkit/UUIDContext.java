package rip.alpha.libraries.command.context.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.message.MessageBuilder;
import rip.alpha.libraries.util.uuid.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDContext implements ContextResolver<UUID> {
    @Override
    public UUID resolve(ArgumentContext<UUID> input) {
        if (input.input().equalsIgnoreCase("self") && input.sender() instanceof Player) {
            return ((Player) input.sender()).getUniqueId();
        }

        UUID id = UUIDFetcher.getUUID(input.input());

        if (id == null) {
            input.sender().sendMessage(MessageBuilder.constructError("Couldn't find a player with the name '{}'", input.input()));
            return null;
        }

        return id;
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<UUID> context) {
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
