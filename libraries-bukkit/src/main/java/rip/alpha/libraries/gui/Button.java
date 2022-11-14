package rip.alpha.libraries.gui;

import lombok.Builder;
import lombok.Data;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

@Data
@Builder
public class Button {

    private final Function<HumanEntity, ItemStack> itemCreator;
    private final Consumer<InventoryClickEvent> eventConsumer;

}
