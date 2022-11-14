package rip.alpha.libraries.fake.impl.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import rip.alpha.libraries.event.PlayerEvent;

/**
 * @author TewPingz
 */

@Getter
public class FakePlayerInteractEvent extends PlayerEvent {

    private final String fakePlayerName;

    @Setter
    private String command;

    public FakePlayerInteractEvent(Player player, String fakePlayerName, String command) {
        super(player);
        this.fakePlayerName = fakePlayerName;
        this.command = command;
    }
}
