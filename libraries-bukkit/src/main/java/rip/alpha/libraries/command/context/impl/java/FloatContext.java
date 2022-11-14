package rip.alpha.libraries.command.context.impl.java;

import org.bukkit.ChatColor;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.List;

public class FloatContext implements ContextResolver<Float> {
    @Override
    public Float resolve(ArgumentContext<Float> input) {
        try {
            return Float.valueOf(input.input());
        } catch (Exception e) {
            input.sender().sendMessage(ChatColor.RED + "That is an invalid float value");
            return null;
        }
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Float> context) {
        return null;
    }
}
