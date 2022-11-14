package rip.alpha.libraries.command.context.type;

import org.bukkit.command.CommandSender;

public record TabCompleteArgumentContext<T>(CommandSender sender, Class<T> clazz) {

}
