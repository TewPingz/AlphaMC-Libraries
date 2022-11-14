package rip.alpha.libraries.command.context.impl.java;

import org.bukkit.ChatColor;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.List;

public class DoubleContext implements ContextResolver<Double> {
    @Override
    public Double resolve(ArgumentContext<Double> input) {
        try {
            return Double.valueOf(input.input());
        } catch (Exception e) {
            input.sender().sendMessage(ChatColor.RED + "That is an invalid double value");
            return null;
        }
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Double> context) {
        return null;
    }
}
