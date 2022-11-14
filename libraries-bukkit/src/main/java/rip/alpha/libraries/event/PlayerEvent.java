package rip.alpha.libraries.event;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class PlayerEvent extends BaseEvent {
    private Player player;

    public PlayerEvent(Player player) {
        this(player, false);
    }

    public PlayerEvent(Player player, boolean async) {
        super(async);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }
}
