package rip.alpha.libraries.chatinput;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

    private final ChatInputManager inputManager = ChatInputManager.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ChatInput input = this.inputManager.getInput(event.getPlayer().getUniqueId());
        if (input == null || input.isConsumed()) {
            return;
        }
        input.consume(event.getMessage());
        event.setCancelled(true);
    }

}
