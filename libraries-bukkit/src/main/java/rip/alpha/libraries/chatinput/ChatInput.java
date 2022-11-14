package rip.alpha.libraries.chatinput;

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.alpha.libraries.timer.Timer;
import rip.alpha.libraries.util.message.MessageBuilder;
import rip.alpha.libraries.util.simple.SimpleText;
import rip.alpha.libraries.util.task.AlphaRunnable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class ChatInput extends Timer {

    public static final String EXIT = "--EXIT--";

    public static void request(Player player, String question, Consumer<String> inputConsumer) {
        ChatInput input = new ChatInput(player.getUniqueId(), question, inputConsumer);
        ChatInputManager.getInstance().addInput(input);
    }

    public static void request(Player player, String question, List<String> suggestions, Consumer<String> inputConsumer) {
        ChatInput input = new ChatInput(player.getUniqueId(), question, suggestions, inputConsumer);
        ChatInputManager.getInstance().addInput(input);
    }

    private final UUID playerID;
    private final Consumer<String> inputConsumer;
    private final PacketQueue packetQueue;
    private final String message;
    private final List<String> suggestions;
    private boolean consumed = false;

    public ChatInput(UUID playerID, String message, Consumer<String> inputConsumer) {
        this(playerID, message, Collections.emptyList(), inputConsumer);
    }

    public ChatInput(UUID playerID, String message, List<String> suggestions, Consumer<String> inputConsumer) {
        super(30000);
        this.playerID = playerID;
        this.message = message;
        this.inputConsumer = inputConsumer;
        this.packetQueue = new PacketQueue(playerID);
        this.suggestions = List.copyOf(suggestions);

        Player player = Bukkit.getPlayer(playerID);
        if (player == null) {
            return;
        }

        String line = "§r§7§m------------------------------------";

        player.sendMessage(line);

        String messageText = MessageBuilder.construct(message);
        String buttonText = " §r§7[§c§lx§e§7]";

        TextComponent button = new TextComponent(buttonText);
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click to cancel")));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, EXIT));

        TextComponent component = new TextComponent(messageText);
        component.addExtra(button);

        player.spigot().sendMessage(component);

        player.sendMessage(line);

        if (suggestions.isEmpty()) {
            return;
        }

        SimpleText simpleText = new SimpleText("");
        for (String suggestion : suggestions) {
            SimpleText sugText = new SimpleText("§7" + suggestion + "§f, ");
            sugText.hover("§7Click");
            sugText.click(suggestion);
            simpleText.add(sugText);
        }
        simpleText.send(player);
    }

    public void consume(String message) {
        this.dereference();
        this.consumed = true;
        if (message.equals(EXIT)) {
            Player player = Bukkit.getPlayer(this.playerID);
            if (player != null) {
                player.sendMessage(MessageBuilder.constructError("Cancelled."));
            }
            return;
        }
        this.inputConsumer.accept(message);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onUnpause() {

    }

    @Override
    public void onExpire() {
        Player player = Bukkit.getPlayer(this.playerID);
        if (player == null) {
            return;
        }
        new AlphaRunnable() {
            @Override
            public void run(BukkitRunnable runnable) {
                if (ChatInput.this.packetQueue.isEmpty()) {
                    ChatInputManager.getInstance().removeInput(ChatInput.this.playerID);
                    runnable.cancel();
                }
                ChatInput.this.packetQueue.flush();
            }
        }.runTaskTimer(40, 2);
        player.sendMessage(MessageBuilder.constructError("The input expired..."));
        player.playSound(player.getEyeLocation(), Sound.NOTE_PIANO, 0.7F, 0.25F);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onApply() {

    }
}
