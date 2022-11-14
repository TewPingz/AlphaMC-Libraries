package rip.alpha.libraries.util;

import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;

public class BlockUtil {
    private static final ByteSet BLOCK_SOLID_PASS_SET;
    private static final ByteSet BLOCK_STAIRS_SET;
    private static final ByteSet BLOCK_LIQUIDS_SET;
    private static final ByteSet BLOCK_WEBS_SET;
    private static final ByteSet BLOCK_ICE_SET;
    private static final ByteSet BLOCK_CARPET_SET;
    private static final IntSet BLOCK_NOT_SOLID;

    private static final Random RANDOM = new Random();
    public static final List<Material> DISPLAYABLE_BLOCKS;

    static {
        BLOCK_SOLID_PASS_SET = new ByteOpenHashSet();
        BLOCK_STAIRS_SET = new ByteOpenHashSet();
        BLOCK_LIQUIDS_SET = new ByteOpenHashSet();
        BLOCK_WEBS_SET = new ByteOpenHashSet();
        BLOCK_ICE_SET = new ByteOpenHashSet();
        BLOCK_CARPET_SET = new ByteOpenHashSet();
        BLOCK_NOT_SOLID = new IntOpenHashSet();
        DISPLAYABLE_BLOCKS = new ArrayList<>();
    }

    public static void init() {
        BLOCK_SOLID_PASS_SET.add((byte) 0);
        BLOCK_SOLID_PASS_SET.add((byte) 6);
        BLOCK_SOLID_PASS_SET.add((byte) 8);
        BLOCK_SOLID_PASS_SET.add((byte) 9);
        BLOCK_SOLID_PASS_SET.add((byte) 10);
        BLOCK_SOLID_PASS_SET.add((byte) 11);
        BLOCK_SOLID_PASS_SET.add((byte) 27);
        BLOCK_SOLID_PASS_SET.add((byte) 28);
        BLOCK_SOLID_PASS_SET.add((byte) 30);
        BLOCK_SOLID_PASS_SET.add((byte) 31);
        BLOCK_SOLID_PASS_SET.add((byte) 32);
        BLOCK_SOLID_PASS_SET.add((byte) 37);
        BLOCK_SOLID_PASS_SET.add((byte) 38);
        BLOCK_SOLID_PASS_SET.add((byte) 39);
        BLOCK_SOLID_PASS_SET.add((byte) 40);
        BLOCK_SOLID_PASS_SET.add((byte) 50);
        BLOCK_SOLID_PASS_SET.add((byte) 51);
        BLOCK_SOLID_PASS_SET.add((byte) 55);
        BLOCK_SOLID_PASS_SET.add((byte) 59);
        BLOCK_SOLID_PASS_SET.add((byte) 63);
        BLOCK_SOLID_PASS_SET.add((byte) 66);
        BLOCK_SOLID_PASS_SET.add((byte) 68);
        BLOCK_SOLID_PASS_SET.add((byte) 69);
        BLOCK_SOLID_PASS_SET.add((byte) 70);
        BLOCK_SOLID_PASS_SET.add((byte) 72);
        BLOCK_SOLID_PASS_SET.add((byte) 75);
        BLOCK_SOLID_PASS_SET.add((byte) 76);
        BLOCK_SOLID_PASS_SET.add((byte) 77);
        BLOCK_SOLID_PASS_SET.add((byte) 78);
        BLOCK_SOLID_PASS_SET.add((byte) 83);
        BLOCK_SOLID_PASS_SET.add((byte) 90);
        BLOCK_SOLID_PASS_SET.add((byte) 104);
        BLOCK_SOLID_PASS_SET.add((byte) 105);
        BLOCK_SOLID_PASS_SET.add((byte) 115);
        BLOCK_SOLID_PASS_SET.add((byte) 119);
        BLOCK_SOLID_PASS_SET.add((byte) (-124));
        BLOCK_SOLID_PASS_SET.add((byte) (-113));
        BLOCK_SOLID_PASS_SET.add((byte) (-81));
        BLOCK_STAIRS_SET.add((byte) 53);
        BLOCK_STAIRS_SET.add((byte) 67);
        BLOCK_STAIRS_SET.add((byte) 108);
        BLOCK_STAIRS_SET.add((byte) 109);
        BLOCK_STAIRS_SET.add((byte) 114);
        BLOCK_STAIRS_SET.add((byte) (-128));
        BLOCK_STAIRS_SET.add((byte) (-122));
        BLOCK_STAIRS_SET.add((byte) (-121));
        BLOCK_STAIRS_SET.add((byte) (-120));
        BLOCK_STAIRS_SET.add((byte) (-100));
        BLOCK_STAIRS_SET.add((byte) (-93));
        BLOCK_STAIRS_SET.add((byte) (-92));
        BLOCK_STAIRS_SET.add((byte) (-76));
        BLOCK_STAIRS_SET.add((byte) 126);
        BLOCK_STAIRS_SET.add((byte) (-74));
        BLOCK_STAIRS_SET.add((byte) 44);
        BLOCK_STAIRS_SET.add((byte) 78);
        BLOCK_STAIRS_SET.add((byte) 99);
        BLOCK_STAIRS_SET.add((byte) (-112));
        BLOCK_STAIRS_SET.add((byte) (-115));
        BLOCK_STAIRS_SET.add((byte) (-116));
        BLOCK_STAIRS_SET.add((byte) (-105));
        BLOCK_STAIRS_SET.add((byte) (-108));
        BLOCK_STAIRS_SET.add((byte) 100);
        BLOCK_LIQUIDS_SET.add((byte) 8);
        BLOCK_LIQUIDS_SET.add((byte) 9);
        BLOCK_LIQUIDS_SET.add((byte) 10);
        BLOCK_LIQUIDS_SET.add((byte) 11);
        BLOCK_WEBS_SET.add((byte) 30);
        BLOCK_ICE_SET.add((byte) 79);
        BLOCK_ICE_SET.add((byte) (-82));
        BLOCK_CARPET_SET.add((byte) (-85));

        BLOCK_NOT_SOLID.add(32);
        BLOCK_NOT_SOLID.add(68);
        BLOCK_NOT_SOLID.add(63);
        BLOCK_NOT_SOLID.add(70);
        BLOCK_NOT_SOLID.add(72);
        BLOCK_NOT_SOLID.add(148);
        BLOCK_NOT_SOLID.add(147);
        BLOCK_NOT_SOLID.add(171);
        BLOCK_NOT_SOLID.add(0);
        BLOCK_NOT_SOLID.add(8);
        BLOCK_NOT_SOLID.add(9);
        BLOCK_NOT_SOLID.add(10);
        BLOCK_NOT_SOLID.add(11);
        BLOCK_NOT_SOLID.add(37);
        BLOCK_NOT_SOLID.add(38);
        BLOCK_NOT_SOLID.add(31);
        BLOCK_NOT_SOLID.add(330);
        BLOCK_NOT_SOLID.add(324);
        BLOCK_NOT_SOLID.add(64);
        BLOCK_NOT_SOLID.add(71);
        BLOCK_NOT_SOLID.add(323);
        BLOCK_NOT_SOLID.add(69);
        BLOCK_NOT_SOLID.add(77);
        BLOCK_NOT_SOLID.add(143);
        BLOCK_NOT_SOLID.add(175);
        BLOCK_NOT_SOLID.add(66);
        BLOCK_NOT_SOLID.add(157);
        BLOCK_NOT_SOLID.add(28);
        BLOCK_NOT_SOLID.add(27);
        BLOCK_NOT_SOLID.add(287);
        BLOCK_NOT_SOLID.add(132);
        BLOCK_NOT_SOLID.add(131);
        BLOCK_NOT_SOLID.add(331);
        BLOCK_NOT_SOLID.add(96);

        for (Material material : Material.values()) {

            if (material.hasGravity()) {
                continue;
            }

            if (!material.isSolid()) {
                continue;
            }

            if (material.isTransparent()) {
                continue;
            }

            if (material.name().contains("_DOOR")) {
                continue;
            }
            if (material == Material.CAKE_BLOCK) {
                continue;
            }
            if (material.name().contains("BED")) {
                continue;
            }
            if (material.name().contains("FURNACE")) {
                continue;
            }
            if (material.name().contains("DIODE")) {
                continue;
            }
            if (material.name().contains("GLOWING")) {
                continue;
            }
            if (material.name().contains("_BLOCK") || material.name().contains("_ORE")) {
                DISPLAYABLE_BLOCKS.add(material);
            }
        }
    }

    public static boolean isStandingOn(Player player, Material material) {
        Block legs = player.getLocation().getBlock();
        Block head = legs.getRelative(BlockFace.UP);
        return legs.getType() == material || head.getType() == material;

    }

    public static boolean isSameLocation(final Location location, final Location check) {
        return location.getWorld().getName().equalsIgnoreCase(check.getWorld().getName()) && location.getBlockX() == check.getBlockX() && location.getBlockY() == check.getBlockY() && location.getBlockZ() == check.getBlockZ();
    }

    public static boolean isOnStairs(final Location location, final int down) {
        return isUnderBlock(location, BLOCK_STAIRS_SET, down);
    }

    public static boolean isOnLiquid(final Location location, final int down) {
        return isUnderBlock(location, BLOCK_LIQUIDS_SET, down);
    }

    public static boolean isOnWeb(final Location location, final int down) {
        return isUnderBlock(location, BLOCK_WEBS_SET, down);
    }

    public static boolean isOnIce(final Location location, final int down) {
        return isUnderBlock(location, BLOCK_ICE_SET, down);
    }

    public static boolean isOnCarpet(final Location location, final int down) {
        return isUnderBlock(location, BLOCK_CARPET_SET, down);
    }

    private static boolean isUnderBlock(final Location location, final Set<Byte> itemIDs, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            } else if (fracZ > 0.7) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        } else if (fracX > 0.7) {
            if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            } else if (fracZ > 0.7) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        } else if (fracZ < 0.3) {
            if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                return true;
            }
        } else if (fracZ > 0.7 && itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
            return true;
        }
        return false;
    }

    public static boolean isOnGround(final Location location, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            } else if (fracZ > 0.7) {
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        } else if (fracX > 0.7) {
            if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            } else if (fracZ > 0.7) {
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        } else if (fracZ < 0.3) {
            if (!BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                return true;
            }
        } else if (fracZ > 0.7 && !BLOCK_SOLID_PASS_SET.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
            return true;
        }
        return false;
    }

    public static Set<Block> getNearbyBlocks(Location location, int circleDiameter) {
        return getNearbyBlocks(location, circleDiameter, circleDiameter, circleDiameter);
    }

    public static Set<Block> getNearbyBlocks(Location location, int xDiameter, int yDiameter, int zDiameter) {
        int halfX = xDiameter / 2;
        int halfY = yDiameter / 2;
        int halfZ = zDiameter / 2;

        World world = location.getWorld();
        Set<Block> blocks = new HashSet<>();

        for (int x = -halfX; x <= halfX; x++) {
            for (int y = -halfY; y <= halfY; y++) {
                for (int z = -halfZ; z <= halfZ; z++) {
                    blocks.add(world.getBlockAt(location.clone().subtract(x, y, z)));
                }
            }
        }

        return blocks;
    }

    public static boolean isNonSolidBlock(Block block) {
        return isNonSolidBlock(block.getType());
    }

    public static boolean isNonSolidBlock(Material material) {
        return isNonSolidBlock(material.getId());
    }

    public static boolean isNonSolidBlock(int id) {
        return BLOCK_NOT_SOLID.contains(id);
    }

    public static Material getRandom() {
        int random = RANDOM.nextInt(DISPLAYABLE_BLOCKS.size());
        return DISPLAYABLE_BLOCKS.get(random);
    }
}
