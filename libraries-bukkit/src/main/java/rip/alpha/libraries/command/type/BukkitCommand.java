package rip.alpha.libraries.command.type;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BukkitCommand extends Command {
    private final Consumer<CommandContext> commandConsumer;
    private final Function<CommandContext, List<String>> tabCompleteFunction;

    protected BukkitCommand(String name, Consumer<CommandContext> commandConsumer, Function<CommandContext, List<String>> tabCompleteFunction) {
        super(name);
        this.commandConsumer = commandConsumer;
        this.tabCompleteFunction = tabCompleteFunction;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] arguments) {
        this.commandConsumer.accept(new CommandContext(commandSender, label, arguments));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String label, String[] arguments) throws IllegalArgumentException {
        List<String> currentList = this.tabCompleteFunction.apply(new CommandContext(commandSender, label, arguments));

        if (currentList == null || currentList.isEmpty()) {
            return super.tabComplete(commandSender, label, arguments);
        }

        String lastWord = arguments[arguments.length - 1];
        currentList = new ArrayList<>(currentList);
        currentList.removeIf(entry -> !StringUtil.startsWithIgnoreCase(entry, lastWord));

        return currentList;
    }

    public record CommandContext(CommandSender sender, String label, String[] arguments) {

    }
}
