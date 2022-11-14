package rip.alpha.libraries.util.uuid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.alpha.libraries.util.data.NameCache;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class UUIDFetcher {
    public static UUID getUUID(String name) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Tried to do a fetch UUID call on sync thread");
        }

        try {
            return NameCache.getInstance().getIDAsync(name).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UUID getCachedUUID(String playerName) {
        if (playerName.length() > 16) {
            return null;
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        }

        return NameCache.getInstance().getID(playerName);
    }

    public static String getName(UUID playerId) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Tried to do a fetch name call on sync thread");
        }

        try {
            return NameCache.getInstance().getNameAsync(playerId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCachedName(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            return player.getName();
        }

        return NameCache.getInstance().getName(playerId);
    }
}