package rip.alpha.libraries.command.context.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;

import java.util.ArrayList;
import java.util.List;

public class WorldContext implements ContextResolver<World> {
    @Override
    public World resolve(ArgumentContext<World> input) {
        World world;

        if (input.input().equalsIgnoreCase("self") && input.sender() instanceof Player player) {
            return player.getWorld();
        }

        try {
            world = Bukkit.getWorlds().get(Integer.parseInt(input.input()));
        } catch (Exception e) {
            world = Bukkit.getWorld(input.input());
        }

        if (world == null) {
            input.sender().sendMessage(ChatColor.RED + "That world was not found.");
            return null;
        }

        return world;
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<World> context) {
        List<String> tabComplete = new ArrayList<>();

        for (World world : context.sender().getServer().getWorlds()) {
            tabComplete.add(world.getName());
        }

        tabComplete.sort(String.CASE_INSENSITIVE_ORDER);
        return tabComplete;
    }
}
