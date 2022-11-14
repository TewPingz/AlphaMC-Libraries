package rip.alpha.libraries.command.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Getter
@SuppressWarnings("ALL")
@RequiredArgsConstructor
public class ContextEntry<T> {

    private final String name, defaultValue;
    private final boolean optional, wildcard;
    private final Class<T> clazz;

    public T transform(CommandSender sender, String value) {
        if (value == null) {
            return null;
        }

        ContextResolver<T> resolver = this.getResolver();

        if (resolver == null) {
            return null;
        }

        if (!resolver.isResolvedAsync()) {
            Future<T> future = TaskUtil.callMethodSync(() -> resolver.resolve(new ArgumentContext<T>(value, sender, clazz)));
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return resolver.resolve(new ArgumentContext<T>(value, sender, clazz));
    }

    public List<String> tabComplete(CommandSender sender) {
        ContextResolver<T> resolver = this.getResolver();

        if (resolver == null) {
            return null;
        }

        return resolver.getTabComplete(new TabCompleteArgumentContext<T>(sender, clazz));
    }

    protected ContextResolver<T> getResolver() {
        return LibrariesPlugin.getCommandFramework().getContextResolver(clazz);
    }
}
