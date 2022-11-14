package rip.alpha.libraries.util.item;

import org.bukkit.inventory.ItemStack;

public class LoreUtil {

    public static String getFirstLoreLine(ItemStack itemStack) {
        return getLoreLine(itemStack, 0);
    }

    public static String getLoreLine(ItemStack itemStack, int index) {
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            return null;
        }

        if (index >= itemStack.getItemMeta().getLore().size()) {
            return null;
        }

        return itemStack.getItemMeta().getLore().get(index);
    }
}
