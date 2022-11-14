package rip.alpha.libraries.command.context;

import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.List;

public interface ContextResolver<T> {

    T resolve(ArgumentContext<T> context);

    List<String> getTabComplete(TabCompleteArgumentContext<T> context);

    default boolean isResolvedAsync() {
        return true;
    }

}
