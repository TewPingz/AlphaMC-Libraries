package rip.alpha.libraries.chatinput;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import rip.foxtrot.spigot.handler.PacketHandler;

import java.util.UUID;

public class ChatQueuePacketListener implements PacketHandler {

    private final ChatInputManager manager = ChatInputManager.getInstance();

    @Override
    public boolean handleSentPacketCancellable(PlayerConnection connection, Packet packet) {
        if (!(packet instanceof PacketPlayOutChat)) {
            return true;
        }
        UUID playerID = connection.player.uniqueID;
        ChatInput chatInput = this.manager.getInput(playerID);
        if (chatInput == null || chatInput.getPacketQueue().isEmpty()) {
            return true;
        }
        chatInput.getPacketQueue().append(packet);
        return false;
    }

}
