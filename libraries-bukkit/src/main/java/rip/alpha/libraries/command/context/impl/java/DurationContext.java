package rip.alpha.libraries.command.context.impl.java;

import org.bukkit.ChatColor;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.TimeUtil;

import java.time.Duration;
import java.util.List;

public class DurationContext implements ContextResolver<Duration> {

    @Override
    public Duration resolve(ArgumentContext<Duration> context) {
        String input = context.input();

        if (input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("perm")) {
            return Duration.ofMillis(-1);
        }
        Long millis = TimeUtil.parseTime(input);
        if (millis == null) {
            context.sender().sendMessage(ChatColor.RED + "That is an invalid duration: %s".formatted(input));
            return null;
        }

        return Duration.ofMillis(millis);
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Duration> context) {
        return List.of("perm", "5s", "5m", "5h", "5d", "5w", "5M", "5y");
    }

}
