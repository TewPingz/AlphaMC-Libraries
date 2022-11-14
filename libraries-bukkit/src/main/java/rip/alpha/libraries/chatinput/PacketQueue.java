package rip.alpha.libraries.chatinput;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@RequiredArgsConstructor
public class PacketQueue {

    private static final int elementsPerFlush = 5;

    private final UUID playerID;
    private final ConcurrentLinkedDeque<Packet> packetQueue = new ConcurrentLinkedDeque<>();
    @Getter
    private boolean flushedToEnd = false;

    public void append(Packet packet) {
        this.packetQueue.add(packet);
    }

    public void flush() {
        Player player = Bukkit.getPlayer(this.playerID);
        if (player == null) {
            this.flushedToEnd = true;
            return;
        }
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (int i = 0; i < elementsPerFlush; i++) {
            Packet packet = this.packetQueue.poll();
            if (packet == null) {
                this.flushedToEnd = true;
                return;
            }
            connection.sendPacket(packet);
        }
    }

    public boolean isEmpty() {
        return this.flushedToEnd || this.packetQueue.isEmpty();
    }

}
