package rip.alpha.libraries.util.area;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class Area {

    private final String worldName;
    private final int minX, minZ, maxX, maxZ, centerX, centerZ, sizeX, sizeZ;

    public Area(String worldName, int minX, int minZ, int maxX, int maxZ) {
        this.worldName = worldName;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.centerX = this.minX + (this.maxX - this.minX) / 2;
        this.centerZ = this.minZ + (this.maxZ - this.minZ) / 2;
        this.sizeX = (this.maxX - this.minX) + 1;
        this.sizeZ = (this.maxZ - this.minZ) + 1;
    }

    public Area(Location cornerOne, Location cornerTwo) throws AreaException {
        if (cornerOne.getWorld().getUID() != cornerTwo.getWorld().getUID()) {
            throw new AreaException("Incorrect worlds");
        }

        this.worldName = cornerOne.getWorld().getName();
        this.minX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
        this.maxX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
        this.minZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        this.maxZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());

        this.centerX = this.minX + (this.maxX - this.minX) / 2;
        this.centerZ = this.minZ + (this.maxZ - this.minZ) / 2;

        this.sizeX = (this.maxX - this.minX) + 1;
        this.sizeZ = (this.maxZ - this.minZ) + 1;
    }

    public Area expand(int amount) {
        int minX = this.minX - amount;
        int maxX = this.maxX + amount;
        int minZ = this.minZ - amount;
        int maxZ = this.maxZ + amount;
        return new Area(worldName, minX, minZ, maxX, maxZ);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public Location[] getCorners() {
        Location[] res = new Location[4];
        World world = this.getWorld();
        res[0] = new Location(world, minX, 0, minZ);
        res[1] = new Location(world, maxX, 0, minZ);
        res[2] = new Location(world, maxX, 0, maxZ);
        res[3] = new Location(world, minX, 0, maxZ);
        return res;
    }

    public Set<Location> generateBorderLocations(Player player, Location targetLocation, int height, int horizontal) {
        Location min = new Location(getWorld(), this.minX, 0, this.minZ);
        Location max = new Location(getWorld(), this.maxX, 256, this.maxZ);

        int minX = min.getBlockX();
        int maxX = max.getBlockX();
        int minZ = min.getBlockZ();
        int maxZ = max.getBlockZ();
        int capacity = ((maxX - minX) * 4 + (maxZ - minZ) * 4) + 4;

        Set<Location> result = new HashSet<>(capacity);
        if (capacity <= 0) {
            return result;
        }
        World world = this.getWorld();

        for (int x = minX; x <= maxX; ++x) {
            result.add(new Location(world, x, 0, minZ));
            result.add(new Location(world, x, 0, maxZ));
        }

        for (int z = minZ; z <= maxZ; ++z) {
            result.add(new Location(world, minX, 0, z));
            result.add(new Location(world, maxX, 0, z));
        }

        return result;
    }

    public Location getCenter(double y) {
        return new Location(this.getWorld(), centerX, y, centerZ);
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public boolean contains(Location l) {
        if (!worldName.equals(l.getWorld().getName())) {
            return false;
        }
        return contains(l.getBlockX(), l.getBlockZ());
    }

    public boolean contains(Block b) {
        return contains(b.getLocation());
    }

    public boolean overlaps(Area other) {
        // Faster checks, lets us skip anything that clearly isnt valid
        if (!this.worldName.equals(other.worldName) || other.maxX < minX || other.minX > maxX || other.maxZ < minZ
                || other.minZ > maxZ) {
            return false;
        }

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                if (x >= other.minX && x <= other.maxX && z >= other.minZ && z <= other.maxZ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Area area)) {
            return false;
        }

        return area.getMinX() == this.minX &&
                area.getMaxX() == this.maxX &&
                area.getMinZ() == this.minZ &&
                area.getMaxZ() == this.maxZ &&
                area.getWorldName().equals(this.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minX, this.maxX, this.minZ, this.maxZ, this.worldName);
    }
}
