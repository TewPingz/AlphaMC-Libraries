package rip.alpha.libraries.command.context.impl.java;

import org.bukkit.ChatColor;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.List;

public class IntegerContext implements ContextResolver<Integer> {
    @Override
    public Integer resolve(ArgumentContext<Integer> input) {
        try {
            return Integer.valueOf(input.input());
        } catch (Exception e) {
            input.sender().sendMessage(ChatColor.RED + "That is an invalid int value");
            return null;
        }
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Integer> context) {
        return null;
    }
}
