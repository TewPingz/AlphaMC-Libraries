package rip.alpha.libraries.spawner;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        SpawnerEntry spawnerEntry = SpawnerEntry.fromItemStack(itemStack);
        if (spawnerEntry == null) {
            return;
        }
        spawnerEntry.updateBlock(event.getBlock());
    }
}
