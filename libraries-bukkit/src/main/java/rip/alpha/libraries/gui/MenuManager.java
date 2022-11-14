package rip.alpha.libraries.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class MenuManager {

    @Getter
    private static final MenuManager instance = new MenuManager();
    private final Map<UUID, Menu> playerIdToMenu = new Object2ObjectOpenHashMap<>();

    @Getter
    @Setter
    private Consumer<InventoryClickEvent> globalButtonResponse = this::response;

    private MenuManager() {

    }

    public Optional<Menu> getOpenMenu(UUID playerID) {
        return Optional.ofNullable(this.playerIdToMenu.get(playerID));
    }

    public Optional<Menu> getOpenMenu(HumanEntity player) {
        return this.getOpenMenu(player.getUniqueId());
    }

    public Optional<Menu> getOpenMenu(Player player) {
        return this.getOpenMenu(player.getUniqueId());
    }

    protected void addInPlayerMenuMap(UUID playerID, Menu menu) {
        this.playerIdToMenu.put(playerID, menu);
    }

    protected void removeFromPlayerMap(UUID playerID) {
        this.playerIdToMenu.remove(playerID);
    }

    private void response(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.playSound(player.getEyeLocation(), Sound.CLICK, 0.7F, 1.2F);
    }

}
