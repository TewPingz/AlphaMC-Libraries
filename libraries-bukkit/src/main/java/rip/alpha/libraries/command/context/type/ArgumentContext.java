package rip.alpha.libraries.command.context.type;

import org.bukkit.command.CommandSender;

public record ArgumentContext<T>(String input, CommandSender sender, Class<T> clazz) {

}
