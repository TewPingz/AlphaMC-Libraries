package rip.alpha.libraries.spawner;

import rip.alpha.libraries.LibrariesPlugin;

public class SpawnerHandler {
    public SpawnerHandler(LibrariesPlugin instance) {
        instance.getBukkitPlugin().getServer().getPluginManager().registerEvents(new SpawnerListener(), instance.getBukkitPlugin());
    }
}
