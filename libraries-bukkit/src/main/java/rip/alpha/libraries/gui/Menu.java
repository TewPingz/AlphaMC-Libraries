package rip.alpha.libraries.gui;


import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import rip.alpha.libraries.util.item.ItemBuilder;

public abstract class Menu {

    private final Int2ObjectMap<Button> buttonMap = new Int2ObjectOpenHashMap<>();

    protected abstract Inventory createEmptyInventory(HumanEntity player);

    protected abstract void setup(HumanEntity player);

    protected Button getButtonForSlot(int index) {
        return this.buttonMap.get(index);
    }

    protected void decorate(Inventory inventory, HumanEntity humanEntity) {
        inventory.clear();
        this.buttonMap.forEach((index, button) -> inventory.setItem(index, button.getItemCreator().apply(humanEntity)));
    }

    protected void onOpen(InventoryOpenEvent event) {

    }

    protected void onClose(InventoryCloseEvent event) {

    }

    protected void addButton(Button button) {
        int index = 0;
        while (this.buttonMap.containsKey(index)) {
            index++;
        }
        this.setButton(index, button);
    }

    protected void fillWithButton(Button button, int size) {
        for (int i = 0; i < size; i++) {
            Button foundButton = this.getButtonForSlot(i);
            if (foundButton != null) {
                continue;
            }
            this.setButton(i, button);
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected void fillWithPlaceholder(int size) {
        this.fillWithButton(this.getPlaceholderButton(), size);
    }

    protected void setButton(int slot, Button button) {
        this.buttonMap.put(slot, button);
    }

    protected void setButton(ButtonPosition position, Button button) {
        this.setButton(position.toIndex(), button);
    }

    protected void onBottomClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    protected void onTopClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public void open(HumanEntity humanEntity) {
        this.buttonMap.clear();
        this.setup(humanEntity);
        Inventory inventory = this.createEmptyInventory(humanEntity);
        this.decorate(inventory, humanEntity);

        InventoryView view = humanEntity.getOpenInventory();
        Inventory topInventory = view == null ? null : humanEntity.getOpenInventory().getTopInventory();
        if (topInventory != null && topInventory.getSize() == inventory.getSize() && topInventory.getTitle().equals(inventory.getTitle())){
            topInventory.setContents(inventory.getContents());
            ((Player)humanEntity).updateInventory();
        } else {
            humanEntity.openInventory(inventory);
        }

        MenuManager.getInstance().addInPlayerMenuMap(humanEntity.getUniqueId(), this);
    }

    public record ButtonPosition(int x, int y) {
        public static ButtonPosition of(int x, int y) {
            return new ButtonPosition(x, y);
        }

        public int toIndex() {
            return this.y * 9 + this.x % 9;
        }
    }

    private Button getPlaceholderButton() {
        return Button.builder()
                .itemCreator(entity -> new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(15).build())
                .build();
    }
}