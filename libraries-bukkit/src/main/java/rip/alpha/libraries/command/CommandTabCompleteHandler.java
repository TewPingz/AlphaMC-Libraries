package rip.alpha.libraries.command;

import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.command.type.BaseCommand;
import rip.foxtrot.spigot.handler.TabCompleteHandler;

import java.util.List;

public class CommandTabCompleteHandler implements TabCompleteHandler {
    @Override
    public List<String> handleCommandTabComplete(Player player, String commandLabel, List<String> list) {
        commandLabel = commandLabel.toLowerCase();

        if (commandLabel.startsWith("/help") || commandLabel.startsWith("/?")) {
            return null;
        }

        if (!commandLabel.contains(" ") && list != null) {
            list.removeIf(string -> {
                String commandName = string.replace("/", "");

                if (commandName.contains(":")) {
                    return true;
                }

                BaseCommand command = LibrariesPlugin.getCommandFramework().getBaseCommand(commandName);

                if (command == null) {
                    return false;
                }

                return !command.hasPermission(player);
            });
        }

        return list;
    }

    @Override
    public List<String> handleChatTabComplete(Player player, String s, List<String> list) {
        return list;
    }
}
