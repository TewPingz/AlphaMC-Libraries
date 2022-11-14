package rip.alpha.libraries.util;

import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;

public final class EntityUtils {

    private static final Map<EntityType, String> DISPLAY_NAMES = new EnumMap<>(EntityType.class);

    static {
        DISPLAY_NAMES.put(EntityType.ARROW, "Arrow");
        DISPLAY_NAMES.put(EntityType.BAT, "Bat");
        DISPLAY_NAMES.put(EntityType.BLAZE, "Blaze");
        DISPLAY_NAMES.put(EntityType.BOAT, "Boat");
        DISPLAY_NAMES.put(EntityType.CAVE_SPIDER, "Cave Spider");
        DISPLAY_NAMES.put(EntityType.CHICKEN, "Chicken");
        DISPLAY_NAMES.put(EntityType.COMPLEX_PART, "Complex Part");
        DISPLAY_NAMES.put(EntityType.COW, "Cow");
        DISPLAY_NAMES.put(EntityType.CREEPER, "Creeper");
        DISPLAY_NAMES.put(EntityType.DROPPED_ITEM, "Item");
        DISPLAY_NAMES.put(EntityType.EGG, "Egg");
        DISPLAY_NAMES.put(EntityType.ENDER_CRYSTAL, "Ender Crystal");
        DISPLAY_NAMES.put(EntityType.ENDER_DRAGON, "Ender Dragon");
        DISPLAY_NAMES.put(EntityType.ENDER_PEARL, "Ender Pearl");
        DISPLAY_NAMES.put(EntityType.ENDER_SIGNAL, "Ender Signal");
        DISPLAY_NAMES.put(EntityType.ENDERMAN, "Enderman");
        DISPLAY_NAMES.put(EntityType.EXPERIENCE_ORB, "Experience Orb");
        DISPLAY_NAMES.put(EntityType.FALLING_BLOCK, "Falling Block");
        DISPLAY_NAMES.put(EntityType.FIREBALL, "Fireball");
        DISPLAY_NAMES.put(EntityType.FIREWORK, "Firework");
        DISPLAY_NAMES.put(EntityType.FISHING_HOOK, "Fishing Rod Hook");
        DISPLAY_NAMES.put(EntityType.GHAST, "Ghast");
        DISPLAY_NAMES.put(EntityType.GIANT, "Giant");
        DISPLAY_NAMES.put(EntityType.HORSE, "Horse");
        DISPLAY_NAMES.put(EntityType.IRON_GOLEM, "Iron Golem");
        DISPLAY_NAMES.put(EntityType.ITEM_FRAME, "Item Frame");
        DISPLAY_NAMES.put(EntityType.LEASH_HITCH, "Lead Hitch");
        DISPLAY_NAMES.put(EntityType.LIGHTNING, "Lightning");
        DISPLAY_NAMES.put(EntityType.MAGMA_CUBE, "Magma Cube");
        DISPLAY_NAMES.put(EntityType.MINECART, "Minecart");
        DISPLAY_NAMES.put(EntityType.MINECART_CHEST, "Chest Minecart");
        DISPLAY_NAMES.put(EntityType.MINECART_FURNACE, "Furnace Minecart");
        DISPLAY_NAMES.put(EntityType.MINECART_HOPPER, "Hopper Minecart");
        DISPLAY_NAMES.put(EntityType.MINECART_MOB_SPAWNER, "Spawner Minecart");
        DISPLAY_NAMES.put(EntityType.MINECART_TNT, "TNT Minecart");
        DISPLAY_NAMES.put(EntityType.OCELOT, "Ocelot");
        DISPLAY_NAMES.put(EntityType.PAINTING, "Painting");
        DISPLAY_NAMES.put(EntityType.PIG, "Pig");
        DISPLAY_NAMES.put(EntityType.PIG_ZOMBIE, "Zombie Pigman");
        DISPLAY_NAMES.put(EntityType.PLAYER, "Player");
        DISPLAY_NAMES.put(EntityType.PRIMED_TNT, "TNT");
        DISPLAY_NAMES.put(EntityType.SHEEP, "Sheep");
        DISPLAY_NAMES.put(EntityType.SILVERFISH, "Silverfish");
        DISPLAY_NAMES.put(EntityType.SKELETON, "Skeleton");
        DISPLAY_NAMES.put(EntityType.SLIME, "Slime");
        DISPLAY_NAMES.put(EntityType.SMALL_FIREBALL, "Fireball");
        DISPLAY_NAMES.put(EntityType.SNOWBALL, "Snowball");
        DISPLAY_NAMES.put(EntityType.SNOWMAN, "Snowman");
        DISPLAY_NAMES.put(EntityType.SPIDER, "Spider");
        DISPLAY_NAMES.put(EntityType.SPLASH_POTION, "Potion");
        DISPLAY_NAMES.put(EntityType.SQUID, "Squid");
        DISPLAY_NAMES.put(EntityType.THROWN_EXP_BOTTLE, "Experience Bottle");
        DISPLAY_NAMES.put(EntityType.UNKNOWN, "Custom");
        DISPLAY_NAMES.put(EntityType.VILLAGER, "Villager");
        DISPLAY_NAMES.put(EntityType.WEATHER, "Weather");
        DISPLAY_NAMES.put(EntityType.WITCH, "Witch");
        DISPLAY_NAMES.put(EntityType.WITHER, "Wither");
        DISPLAY_NAMES.put(EntityType.WITHER_SKULL, "Wither Skull");
        DISPLAY_NAMES.put(EntityType.WOLF, "Wolf");
        DISPLAY_NAMES.put(EntityType.ZOMBIE, "Zombie");
    }

    public static String getName(EntityType type) {
        return DISPLAY_NAMES.get(type);
    }

    public static EntityType parse(String input) {
        for (Map.Entry<EntityType, String> entry : DISPLAY_NAMES.entrySet()) {
            if (entry.getValue().replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))) {
                return entry.getKey();
            }
        }

        for (EntityType type : EntityType.values()) {
            if (input.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }

        return null;
    }

}