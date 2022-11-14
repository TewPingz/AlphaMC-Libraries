package rip.alpha.libraries.spawner;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import rip.alpha.libraries.util.EntityUtils;
import rip.alpha.libraries.util.item.ItemBuilder;
import rip.alpha.libraries.util.item.LoreUtil;
import rip.alpha.libraries.util.message.MessageColor;

@RequiredArgsConstructor
public class SpawnerEntry {
    private static final String FIRST_SPLIT = MessageColor.GOLD + "This is a " + MessageColor.YELLOW;
    private static final String LAST_SPLIT = MessageColor.GOLD + " spawner.";

    private final EntityType entityType;

    public ItemStack toItemStack() {
        return new ItemBuilder(Material.MOB_SPAWNER)
                .name(MessageColor.GOLD + EntityUtils.getName(entityType) + " Spawner")
                .lore(FIRST_SPLIT + EntityUtils.getName(entityType) + LAST_SPLIT)
                .build();
    }

    public void updateBlock(Block block) {
        BlockState state = block.getState();
        if (state instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) state;
            creatureSpawner.setSpawnedType(this.entityType);
        }
    }

    public static SpawnerEntry fromItemStack(ItemStack itemStack) {
        String line = LoreUtil.getFirstLoreLine(itemStack);
        if (line == null) {
            return null;
        }
        String[] split = line.split(LAST_SPLIT);
        if (split.length <= 0) {
            return null;
        }
        split = split[0].split(FIRST_SPLIT);
        if (split.length <= 1) {
            return null;
        }
        EntityType entityType = EntityUtils.parse(split[1]);
        if (entityType == null) {
            return null;
        }
        return new SpawnerEntry(entityType);
    }

}
