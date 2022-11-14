package rip.alpha.libraries.util.simple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class SimpleLocation {
    private final int x, y, z;
    private final String worldName;

    public SimpleLocation(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.worldName = location.getWorld().getName();
    }

    public Location toBukkit() {
        World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z);
    }
}
