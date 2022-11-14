package rip.alpha.libraries.command.context.impl.java;

import org.bukkit.ChatColor;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.List;

public class LongContext implements ContextResolver<Long> {
    @Override
    public Long resolve(ArgumentContext<Long> input) {
        try {
            return Long.valueOf(input.input());
        } catch (Exception e) {
            input.sender().sendMessage(ChatColor.RED + "That is an invalid long value");
            return null;
        }
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Long> context) {
        return null;
    }
}
