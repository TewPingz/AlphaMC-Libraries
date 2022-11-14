package rip.alpha.libraries.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        MenuManager menuManager = MenuManager.getInstance();
        menuManager.getOpenMenu(event.getWhoClicked()).ifPresent(menu -> {
            if (event.getInventory().equals(event.getClickedInventory())) {
                menu.onTopClick(event);
                int slot = event.getSlot();
                Button button = menu.getButtonForSlot(slot);
                if (button != null) {
                    Consumer<InventoryClickEvent> consumer = button.getEventConsumer();
                    if (consumer != null) {
                        consumer.accept(event);
                        menuManager.getGlobalButtonResponse().accept(event);
                    }
                    event.setCancelled(true);
                }
            } else {
                menu.onBottomClick(event);
            }
        });
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        MenuManager menuManager = MenuManager.getInstance();
        menuManager.getOpenMenu(event.getPlayer()).ifPresent(menu -> {
            menu.onClose(event);
            menuManager.removeFromPlayerMap(event.getPlayer().getUniqueId());
        });
    }
}
