package rip.alpha.libraries.fake.impl.player;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.fake.FakeEntity;
import rip.alpha.libraries.fake.FakeEntityHandler;
import rip.foxtrot.spigot.handler.PacketHandler;

/**
 * @author TewPingz
 */
public record FakePlayerPacketHandler(FakeEntityHandler handler) implements PacketHandler {
    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInUseEntity useEntity) {
            if (useEntity.c() != EnumEntityUseAction.INTERACT) {
                return;
            }

            Entity entity = useEntity.a(playerConnection.player.world);
            if (entity != null) {
                return;
            }

            int id = useEntity.a;
            FakeEntity fakeEntity = handler.getEntityByEntityId(id);

            if (!(fakeEntity instanceof FakePlayerEntity fakePlayer)) {
                return;
            }

            Player player = playerConnection.player.getBukkitEntity();

            if (player.getLocation().distanceSquared(fakePlayer.getCurrentLocation()) > 36) {
                return;
            }

            FakePlayerInteractEvent interactEvent = new FakePlayerInteractEvent(player, fakePlayer.getName(), fakePlayer.getCommand());
            interactEvent.call(LibrariesPlugin.getInstance().getBukkitPlugin());
            String command = interactEvent.getCommand();

            if (command == null) {
                return;
            }

            if (command.startsWith("/")) {
                command = command.substring(1);
            }

            if (command.isEmpty()) {
                return;
            }

            player.performCommand(command);
        }
    }
}
