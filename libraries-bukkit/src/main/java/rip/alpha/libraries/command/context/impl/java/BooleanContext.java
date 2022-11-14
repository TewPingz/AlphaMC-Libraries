package rip.alpha.libraries.command.context.impl.java;

import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.ArrayList;
import java.util.List;

public class BooleanContext implements ContextResolver<Boolean> {

    private final List<String> tabComplete;

    public BooleanContext() {
        this.tabComplete = new ArrayList<>();
        this.tabComplete.add("yes");
        this.tabComplete.add("no");
    }

    @Override
    public Boolean resolve(ArgumentContext<Boolean> input) {
        if (input.input().equalsIgnoreCase("yes") || input.input().equalsIgnoreCase("ye")) {
            return true;
        }

        if (input.input().equalsIgnoreCase("no") || input.input().equalsIgnoreCase("nah")) {
            return false;
        }

        return Boolean.valueOf(input.input());
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Boolean> context) {
        return this.tabComplete;
    }
}
