package rip.alpha.libraries.util;


import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerUtil {
    public static ItemStack[][] killOfflinePlayer(UUID playerId, String playerName) {
        Player target = Bukkit.getServer().getPlayer(playerId);

        ItemStack[][] items = new ItemStack[2][];

        if (target == null) {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            GameProfile gameProfile = new GameProfile(playerId, playerName);
            WorldServer worldServer = server.getWorldServer(0);
            PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);
            EntityPlayer entity = new EntityPlayer(server, worldServer, gameProfile, playerInteractManager);
            target = entity.getBukkitEntity();
            if (target != null) {
                target.loadData();
            }
        }

        if (target != null) {
            items[0] = target.getInventory().getContents();
            items[1] = target.getInventory().getArmorContents();

            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            humanTarget.setHealth(0.0F);
            target.saveData();
            return items;
        }

        return null;
    }
}
