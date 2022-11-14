package rip.alpha.libraries.util;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ListenerHandler {

    public static void loadListenersFromPackage(Plugin plugin, String packageName) {
        for (Class<?> clazz : PackageUtil.getClasses(plugin, packageName)) {
            if (isListener(clazz)) {
                try {
                    plugin.getServer().getPluginManager().registerEvents((Listener) clazz.newInstance(), plugin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isListener(Class<?> clazz) {
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (interfaceClass.equals(Listener.class)) {
                return true;
            }
        }

        return false;
    }
}

