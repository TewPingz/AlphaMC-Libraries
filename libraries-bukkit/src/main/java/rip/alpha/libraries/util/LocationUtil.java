package rip.alpha.libraries.util;

import org.bukkit.Location;
import org.bukkit.World;
import rip.alpha.libraries.util.simple.SimpleLocation;

import java.util.HashSet;
import java.util.Set;

public class LocationUtil {

    public static Set<SimpleLocation> getCylinder(SimpleLocation center, int radius) {
        Set<SimpleLocation> safeLocationSet = new HashSet<>();
        for (double i = 0.0; i < 360.0; i += 0.1) {
            double angle = i * Math.PI / 180;
            for (int r = 0; r < radius; r++) {
                int x = (int) Math.floor(center.getX() + r * Math.cos(angle));
                int z = (int) Math.floor(center.getZ() + r * Math.sin(angle));

                for (int rel = -1; rel < 1; rel++) {
                    safeLocationSet.add(new SimpleLocation(x + rel, center.getY(), z, center.getWorldName()));
                }

                for (int rel = -1; rel < 1; rel++) {
                    safeLocationSet.add(new SimpleLocation(x, center.getY(), z + rel, center.getWorldName()));
                }
            }
        }
        return safeLocationSet;
    }

    public static Set<SimpleLocation> getOuterCylinder(SimpleLocation center, int radius) {
        Set<SimpleLocation> safeLocationSet = new HashSet<>();
        for (double i = 0.0; i < 360.0; i += 0.1) {
            double angle = i * Math.PI / 180;
            int x = (int) Math.floor(center.getX() + radius * Math.cos(angle));
            int z = (int) Math.floor(center.getZ() + radius * Math.sin(angle));
            safeLocationSet.add(new SimpleLocation(x, center.getY(), z, center.getWorldName()));
        }
        return safeLocationSet;
    }

    public static Set<SimpleLocation> getSquare(String worldName, int minX, int maxX, int minZ, int maxZ) {
        Set<SimpleLocation> set = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                set.add(new SimpleLocation(x, 0, z, worldName));
            }
        }
        return set;
    }

    public static Set<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        Set<Location> locations = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }


    public static Set<SimpleLocation> getSquareBorder(String worldName, int minX, int maxX, int minZ, int maxZ) {
        Set<SimpleLocation> set = new HashSet<>();

        for (int x = minX; x <= maxX; ++x) {
            set.add(new SimpleLocation(x, 0, minZ, worldName));
            set.add(new SimpleLocation(x, 0, maxZ, worldName));
        }

        for (int z = minZ; z <= maxZ; ++z) {
            set.add(new SimpleLocation(minX, 0, z, worldName));
            set.add(new SimpleLocation(maxX, 0, z, worldName));
        }

        return set;
    }

    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }
}
