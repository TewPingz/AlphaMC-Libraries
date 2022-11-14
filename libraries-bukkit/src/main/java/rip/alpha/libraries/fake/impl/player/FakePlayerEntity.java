package rip.alpha.libraries.fake.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.fake.FakeEntity;
import rip.alpha.libraries.util.PlayerTeamUtils;
import rip.alpha.libraries.util.message.MessageTranslator;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author TewPingz
 */
@Getter(AccessLevel.PROTECTED)
public class FakePlayerEntity implements FakeEntity {
    private final int id;
    private final String displayName;
    private final GameProfile gameProfile;

    private final EntityPlayer entityPlayer;
    private final Set<UUID> shouldBeAbleToView;
    private final Set<UUID> currentlyViewing;
    private final Map<UUID, FakePlayerLookGoal> lookGoals;

    @Getter
    @Setter
    private String command;

    @Getter
    private Location location;

    private PacketPlayOutEntityDestroy destroy;
    private PacketPlayOutPlayerInfo add;
    private PacketPlayOutNamedEntitySpawn namedEntitySpawn;
    private PacketPlayOutEntityHeadRotation entityHeadRotation;
    private PacketPlayOutEntityLook entityLook;
    private PacketPlayOutAnimation animation;

    public FakePlayerEntity(int id, String displayName, Location location) {
        this.id = id;
        this.displayName = MessageTranslator.translate(displayName);
        this.gameProfile = new GameProfile(UUID.randomUUID(), this.displayName);
        this.location = location;
        this.shouldBeAbleToView = new HashSet<>();
        this.currentlyViewing = new HashSet<>();
        this.lookGoals = new HashMap<>();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        PlayerInteractManager interactManager = new PlayerInteractManager(worldServer);
        this.entityPlayer = new EntityPlayer(server, worldServer, this.gameProfile, interactManager);
        this.entityPlayer.setPosition(location.getX(), location.getY(), location.getZ());
        this.entityPlayer.getDataWatcher().watch(10, (byte) 127);
        this.entityPlayer.getBukkitEntity().setRemoveWhenFarAway(false);
        this.entityPlayer.getBukkitEntity().setPlayerListName(displayName);
        this.setupPackets();
    }

    public void updateSkin(String val, String sig) {
        if (val == null || sig == null) {
            return;
        }
        this.entityPlayer.getProfile().getProperties().clear();
        this.entityPlayer.getProfile().getProperties().put("textures", new Property("textures", val, sig));
        this.setupPackets();
    }

    @Override
    public boolean show(Player player) {
        if (!player.getWorld().getUID().equals(this.location.getWorld().getUID())) {
            return false;
        }

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(this.destroy);
        playerConnection.sendPacket(this.add);
        playerConnection.sendPacket(this.namedEntitySpawn);
        playerConnection.sendPacket(this.entityHeadRotation);
        playerConnection.sendPacket(this.entityLook);
        playerConnection.sendPacket(this.animation);

        this.shouldBeAbleToView.add(player.getUniqueId());
        this.currentlyViewing.add(player.getUniqueId());

        if (!LibrariesPlugin.getInstance().isTabListHandlerEnabled()) { //no point of removing because we can just filter it to the end of the tab!
            TaskUtil.runTaskLaterAsynchronously(() -> {
                playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(this.entityPlayer));
            }, 6);
        }

        return true;
    }

    @Override
    public boolean showToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.show(player);
        }
        return true;
    }

    @Override
    public boolean hide(Player player) {
        if (!this.currentlyViewing.contains(player.getUniqueId())) {
            return false;
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.destroy);
        this.currentlyViewing.remove(player.getUniqueId());
        return true;
    }

    @Override
    public boolean hideFromAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.hide(player);
        }
        return true;
    }

    @Override
    public void teleport(Location location) {
        this.entityPlayer.setPosition(location.getX(), location.getY(), location.getZ());
        this.entityPlayer.world = ((CraftWorld) location.getWorld()).getHandle();
        this.location = location;

        this.setupPackets();

        PacketPlayOutEntityTeleport packetPlayOutEntityTeleport = new PacketPlayOutEntityTeleport(this.entityPlayer);
        for (UUID uuid : this.getCurrentlyViewing()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }
            if (!player.willBeOnline()) {
                continue;
            }

            if (!player.getWorld().getName().equals(this.location.getWorld().getName())) {
                this.hide(player);
                continue;
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityTeleport);
        }
    }

    @Override
    public boolean isShownToPlayer(UUID uuid) {
        return this.shouldBeAbleToView.contains(uuid);
    }

    @Override
    public String getName() {
        return this.displayName;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getEntityId() {
        return this.entityPlayer.getId();
    }

    @Override
    public UUID getUUID() {
        return this.entityPlayer.getUniqueID();
    }

    @Override
    public void addToTeam(Player player) {
        PlayerTeamUtils.sendUpdatePlayers(player, "zEntities", Collections.singletonList(this.displayName), 3);
    }

    @Override
    public void handleDisconnect(UUID uuid) {
        this.currentlyViewing.remove(uuid);
        this.shouldBeAbleToView.remove(uuid);
    }

    public Set<UUID> getCurrentlyViewing() {
        return this.currentlyViewing;
    }

    public Set<UUID> getShouldBeAbleToView() {
        return this.shouldBeAbleToView;
    }

    @Override
    public World getWorld() {
        return this.getLocation().getWorld();
    }

    public void updateRotation(Player player, float locationYaw, float locationPitch) {
        byte pitch = (byte) ((int) (locationPitch * 256.0F / 360.0F));
        byte yaw = (byte) ((int) (locationYaw * 256.0F / 360.0F)); // convert to radiant tings
        PacketPlayOutEntityLook look = new PacketPlayOutEntityLook(this.entityPlayer.getId(), yaw, pitch, this.entityPlayer.onGround);
        PacketPlayOutEntityHeadRotation rotation = new PacketPlayOutEntityHeadRotation(this.entityPlayer, yaw);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(look);
        connection.sendPacket(rotation);
    }

    public Location getCurrentLocation() {
        return this.entityPlayer.getBukkitEntity().getEyeLocation();
    }

    public float[] getCurrentAngles() {
        Location current = this.getLocation();
        return new float[]{current.getYaw(), current.getPitch()};
    }

    public float[] getGoalAngles(Location goal) {
        return this.getGoalAngles(this.getCurrentLocation(), goal);
    }

    public float[] getGoalAngles(Location current, Location goal) {
        Location targetLocation = current.clone();
        targetLocation.setDirection(goal.clone().add(0, 1.5, 0).subtract(current).toVector());
        return new float[]{targetLocation.getYaw(), targetLocation.getPitch()};
    }

    private void setupPackets() {
        this.destroy = new PacketPlayOutEntityDestroy(this.getEntityId());
        this.add = PacketPlayOutPlayerInfo.addPlayer(this.entityPlayer);
        this.namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(this.entityPlayer);
        this.entityHeadRotation = new PacketPlayOutEntityHeadRotation(this.entityPlayer, (byte) ((this.location.getYaw() * 256.0F) / 360.0F));
        this.entityLook = new PacketPlayOutEntityLook(this.getEntityId(), (byte) this.location.getYaw(), (byte) 0, true);
        this.animation = new PacketPlayOutAnimation(this.entityPlayer, 0);
    }
}
