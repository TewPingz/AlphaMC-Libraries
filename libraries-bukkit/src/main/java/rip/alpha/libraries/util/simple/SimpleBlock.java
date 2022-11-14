package rip.alpha.libraries.util.simple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class SimpleBlock {
    private final int typeId;
    private final byte data;
    private final int x, y, z;
    private final String worldName;

    public SimpleLocation toSimpleLocation() {
        return new SimpleLocation(this.x, this.y, this.z, this.worldName);
    }

    public Location toLocation() {
        return new Location(this.getWorld(), this.x, this.y, this.z);
    }

    public Block toBukkit() {
        return this.getWorld().getBlockAt(this.toLocation());
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }
}
