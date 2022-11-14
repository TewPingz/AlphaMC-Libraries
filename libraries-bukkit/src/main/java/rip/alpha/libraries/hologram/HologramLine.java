package rip.alpha.libraries.hologram;

import lombok.Getter;
import net.minecraft.server.v1_7_R4.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.fake.impl.hologram.FakeEntityArmorStand;
import rip.alpha.libraries.fake.impl.hologram.FakeEntityHorse;
import rip.alpha.libraries.fake.impl.hologram.FakeEntitySkull;
import rip.alpha.libraries.hologram.placeholder.Placeholder;
import rip.alpha.libraries.util.message.MessageTranslator;

import java.util.Set;
import java.util.UUID;

public class HologramLine {

    private static final double SKULL_OFFSET = 55.87D;

    //1.7 packets
    private final FakeEntitySkull fakeEntitySkull;
    private final FakeEntityHorse fakeEntityHorse;
    private final PacketPlayOutAttachEntity attachEntityPacket;

    //1.8 packets
    private final FakeEntityArmorStand fakeEntityArmorStand;

    @Getter
    private String line;

    public HologramLine(String line, Location location, int offset) {
        this.fakeEntitySkull = new FakeEntitySkull(-1, location.clone().add(0, SKULL_OFFSET + offset, 0));
        this.fakeEntityHorse = new FakeEntityHorse(-1, location);
        this.fakeEntityArmorStand = new FakeEntityArmorStand(-1, location.clone().add(0, offset, 0));

        this.fakeEntityHorse.setSize(-1700000);
        this.fakeEntityArmorStand.setSmall(true);
        this.fakeEntityArmorStand.setInvisible(true);
        this.fakeEntityArmorStand.setGravity(false);

        this.attachEntityPacket = new PacketPlayOutAttachEntity();
        attachEntityPacket.a = 0;
        attachEntityPacket.b = this.fakeEntityHorse.getEntityId();
        attachEntityPacket.c = this.fakeEntitySkull.getEntityId();

        this.updateLocation(location, offset);
        this.updateLine(line);
    }

    public void updateLocation(Location location, double offset) {
        this.fakeEntityArmorStand.teleport(location.clone().add(0, offset, 0));
        this.fakeEntitySkull.teleport(location.clone().add(0, SKULL_OFFSET + offset, 0));
        this.fakeEntityHorse.teleport(location);
    }

    public void setup(Player player) {
        int version = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();

        //reset coz logging in & out
        this.fakeEntityArmorStand.getCurrentlyViewing().remove(player.getUniqueId());
        this.fakeEntityHorse.getCurrentlyViewing().remove(player.getUniqueId());
        this.fakeEntitySkull.getCurrentlyViewing().remove(player.getUniqueId());

        if (version >= 47) {
            this.fakeEntityArmorStand.show(player);
            this.update(player);
        } else {
            this.fakeEntitySkull.show(player);
            this.fakeEntityHorse.show(player);
            this.update(player);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.attachEntityPacket);
        }
    }

    public void update(Player player) {
        String formattedLine = this.line;

        for (Placeholder placeholder : LibrariesPlugin.getInstance().getHologramHandler().getPlaceholders()) {
            formattedLine = placeholder.formatLine(player, formattedLine);
        }

        if (this.fakeEntityArmorStand.getCurrentlyViewing().contains(player.getUniqueId())) {
            this.fakeEntityArmorStand.setName(formattedLine);
            PacketPlayOutEntityMetadata metadata = this.fakeEntityArmorStand.updateMetadata();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadata);
        } else if (this.fakeEntityHorse.getCurrentlyViewing().contains(player.getUniqueId())) {
            this.fakeEntityHorse.setName(formattedLine);
            PacketPlayOutEntityMetadata metadata = this.fakeEntityHorse.updateMetadata();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadata);
        }
    }

    public void hide(Player player) {
        this.fakeEntitySkull.hide(player);
        this.fakeEntityHorse.hide(player);
        this.fakeEntityArmorStand.hide(player);
    }

    public void updateLine(String line) {
        line = MessageTranslator.translate(line);
        if (this.line != null && this.line.equals(line)) {
            return;
        }
        this.line = line;

        for (UUID uuid : this.fakeEntityArmorStand.getCurrentlyViewing()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (!player.willBeOnline()) {
                continue;
            }
            this.update(player);
        }

        for (UUID uuid : this.fakeEntityHorse.getCurrentlyViewing()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (!player.willBeOnline()) {
                continue;
            }
            this.update(player);
        }
    }

    public void setupBulk(Set<UUID> uuids) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!uuids.contains(player.getUniqueId())) {
                continue;
            }
            this.setup(player);
        }
    }

    public void updateBulk(Set<UUID> uuids) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!uuids.contains(player.getUniqueId())) {
                continue;
            }
            this.update(player);
        }
    }

    public void hideBulk(Set<UUID> uuids) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!uuids.contains(player.getUniqueId())) {
                continue;
            }
            this.hide(player);
        }
    }
}
