package rip.alpha.libraries.command.context.impl.java;

import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.List;

public class StringContext implements ContextResolver<String> {

    @Override
    public String resolve(ArgumentContext<String> input) {
        return input.input();
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<String> context) {
        return null;
    }
}
