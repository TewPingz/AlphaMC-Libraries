package rip.alpha.libraries.gui;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.alpha.libraries.util.item.ItemBuilder;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ConfirmationMenu extends Menu {

    public static void ask(Player player, String question, Consumer<Boolean> choiceCallback) {
        new ConfirmationMenu(question, choiceCallback).open(player);
    }

    private final String question;
    private final Consumer<Boolean> choiceConsumer;

    @Override
    protected Inventory createEmptyInventory(HumanEntity player) {
        String title = ChatColor.YELLOW + this.question;
        if (title.length() > 32) {
            title = title.substring(0, 27) + "...";
        }
        return Bukkit.createInventory(null, 9 * 5, title);
    }

    @Override
    protected void setup(HumanEntity player) {
        for (int x = 0; x < 3; x++) {
            for (int y = 1; y < 4; y++) {
                this.setButton(ButtonPosition.of(x, y), this.getAcceptButton());
            }
        }

        for (int x = 6; x < 9; x++) {
            for (int y = 1; y < 4; y++) {
                this.setButton(ButtonPosition.of(x, y), this.getDeclineButton());
            }
        }

        this.setButton(ButtonPosition.of(4, 2), this.getQuestionButton());
        this.fillWithPlaceholder(9 * 5);
    }

    private Button getAcceptButton() {
        ItemStack itemStack = new ItemBuilder(Material.WOOL).durability(13).name("&aAccept").build();
        return Button
                .builder()
                .itemCreator(player -> itemStack)
                .eventConsumer(event -> this.choiceConsumer.accept(true))
                .build();
    }

    private Button getDeclineButton() {
        ItemStack itemStack = new ItemBuilder(Material.WOOL).durability(14).name("&cDecline").build();
        return Button
                .builder()
                .itemCreator(player -> itemStack)
                .eventConsumer(event -> this.choiceConsumer.accept(false))
                .build();
    }

    private Button getQuestionButton() {
        ItemStack itemStack = new ItemBuilder(Material.WOOL).durability(4).name("&6" + this.question).build();
        return Button
                .builder()
                .itemCreator(player -> itemStack)
                .build();
    }
}
