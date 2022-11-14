package rip.alpha.libraries.fake.impl.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.alpha.libraries.fake.FakeEntity;
import rip.alpha.libraries.fake.FakeEntityHandler;

import java.util.UUID;

public record FakePlayerEntityTask(FakeEntityHandler fakeEntityHandler) implements Runnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Location playerLocation = player.getLocation();
            UUID worldId = player.getWorld().getUID();

            for (FakeEntity fakeEntity : this.fakeEntityHandler.getAllEntitiesPlayerCanSee(uuid)) {
                if (!fakeEntity.getWorld().getUID().equals(worldId)) {
                    if (fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.hide(player);
                    }
                    continue;
                }

                if (fakeEntity.getLocation().distanceSquared(playerLocation) < fakeEntity.range()) {
                    if (!fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.show(player);
                    }
                } else {
                    if (fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.hide(player);
                    }
                }
            }
        }
    }
}
