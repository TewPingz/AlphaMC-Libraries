package rip.alpha.libraries.nametag;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.util.message.MessageTranslator;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class NametagHandler {
    private final NametagProvider provider;
    private final Map<UUID, NametagViewer> nametagMap;
    private final LunarImplementation lunarImplementation;

    public NametagHandler(JavaPlugin plugin, NametagProvider provider) {
        this.provider = provider;
        this.nametagMap = new HashMap<>();

        if (plugin.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) {
            this.lunarImplementation = new LunarImplementation();
        } else {
            this.lunarImplementation = null;
        }

        plugin.getServer().getPluginManager().registerEvents(new NametagListener(this), plugin);
        int updateInterval = this.provider.getUpdateInterval();
        TaskUtil.scheduleAtFixedRateOnPool(new NametagTask(this), updateInterval, updateInterval, TimeUnit.MILLISECONDS);
        TaskUtil.scheduleAtFixedRateOnPool(new ExtraNametagTask(this), updateInterval, updateInterval, TimeUnit.MILLISECONDS);
        LibrariesPlugin.getInstance().registerNametagHandler(this);
    }

    void update(Player viewer) {
        NametagViewer nametagViewer = this.getViewer(viewer);
        if (nametagViewer != null) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.willBeOnline()) {
                    String prefix = this.getPrefix(viewer, target);
                    String suffix = this.getSuffix(viewer, target);
                    String teamName = this.getTeamName(viewer, target);
                    boolean friendly = this.isFriendly(viewer, target);
                    NametagVisibility visibility = this.getNametagVisibility(viewer, target);
                    nametagViewer.update(viewer, target, teamName, prefix, suffix, friendly, visibility);
                }
            }
        }
    }

    Map<UUID, NametagViewer> getNametagMap() {
        return this.nametagMap;
    }

    String getPrefix(Player viewer, Player target) {
        String prefix = this.provider.getPrefix(target, viewer);
        if (prefix == null) {
            prefix = "";
        }
        return MessageTranslator.translate(prefix.substring(0, Math.min(16, prefix.length())));
    }

    String getSuffix(Player viewer, Player target) {
        String suffix = this.provider.getSuffix(target, viewer);
        if (suffix == null) {
            suffix = "";
        }
        return MessageTranslator.translate(suffix.substring(0, Math.min(16, suffix.length())));
    }

    String getTeamName(Player viewer, Player target) {
        String teamName = this.provider.getTeamName(target, viewer);
        if (teamName == null) {
            teamName = "";
        }
        if (LibrariesPlugin.getInstance().isTabListHandlerEnabled()) {
            teamName = "z" + teamName;
        }
        return teamName.substring(0, Math.min(16, teamName.length()));
    }

    boolean isFriendly(Player viewer, Player target) {
        return this.provider.isFriendly(target, viewer);
    }

    NametagVisibility getNametagVisibility(Player viewer, Player target) {
        return this.provider.getNametagVisibility(target, viewer);
    }

    List<String> getExtraTags(Player viewer, Player target) {
        List<String> extraTags = this.provider.getExtraTags(target, viewer);
        ;
        return extraTags == null ? Collections.emptyList() : extraTags;
    }

    NametagViewer getViewer(UUID uuid) {
        return this.nametagMap.get(uuid);
    }

    NametagViewer getViewer(Player player) {
        return this.getViewer(player.getUniqueId());
    }

    void putViewer(UUID uuid, NametagViewer viewer) {
        this.nametagMap.put(uuid, viewer);
    }

    void removeViewer(Player player) {
        this.nametagMap.remove(player.getUniqueId());
    }
}
