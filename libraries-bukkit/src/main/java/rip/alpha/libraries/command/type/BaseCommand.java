package rip.alpha.libraries.command.type;

import lombok.Getter;

import java.util.List;

@Getter
public class BaseCommand extends RootCommand {

    private final BukkitCommand bukkitCommand;

    public BaseCommand(String label) {
        super(label);
        this.bukkitCommand = new BukkitCommand(this.getLabel(), this::executeCommand, this::tabComplete);
    }

    private void executeCommand(BukkitCommand.CommandContext commandContext) {
        this.executeCommand(commandContext.sender(), commandContext.label(), commandContext.arguments());
    }

    private List<String> tabComplete(BukkitCommand.CommandContext commandContext) {
        return this.tabComplete(commandContext.sender(), commandContext.label(), commandContext.arguments());
    }
}
