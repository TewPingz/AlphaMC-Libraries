package rip.alpha.libraries.util.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

public class ItemUtils {

    public static ItemStack minusItem(ItemStack itemStack) {
        int count = itemStack.getAmount() - 1;
        if (count <= 0) {
            return new ItemStack(Material.AIR);
        }
        ItemStack stack = itemStack.clone();
        stack.setAmount(count);
        return stack;
    }

    public static void removeItem(Inventory inventory, Material type, short data, int quantity) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;

        for (int i = quantity; i > 0; i--) {
            for (ItemStack content : contents) {
                if (content == null || content.getType() != type) {
                    continue;
                }
                if (compareDamage && content.getData().getData() != data) {
                    continue;
                }

                if (content.getAmount() <= 1) {
                    inventory.removeItem(content);
                } else {
                    content.setAmount(content.getAmount() - 1);
                }
                break;
            }
        }
    }

    public static ItemStack enchantItem(ItemStack is, Enchantment enc, int level) {
        if (enc.canEnchantItem(is) && !is.containsEnchantment(enc) && !hasConflictingEnchant(is, enc)) {
            is.addUnsafeEnchantment(enc, level);
        }

        return is;
    }

    private static boolean hasConflictingEnchant(ItemStack is, Enchantment testing) {
        for (Entry<Enchantment, Integer> enc : is.getEnchantments().entrySet()) {
            if (testing.conflictsWith(enc.getKey()) && testing != enc.getKey()) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack maxEnchant(Material item, Enchantment... enchants) {
        return maxEnchant(new ItemStack(item, 1), enchants);
    }

    public static ItemStack[] maxEnchant(ItemStack[] items, Enchantment... enchants) {
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                maxEnchant(items[i], enchants);
            }
        }

        return items;
    }

    public static ItemStack maxEnchant(ItemStack item, Enchantment... enchants) {
        if (item != null) {
            if (enchants != null && enchants.length > 0) {
                for (Enchantment wanted : enchants) {
                    ItemUtils.enchantItem(item, wanted, wanted.getMaxLevel());
                }
            } else {
                for (Enchantment enc : Enchantment.values()) {
                    ItemUtils.enchantItem(item, enc, enc.getMaxLevel());
                }
            }
        }

        return item;
    }

    public static int countAmount(Inventory inventory, Material type, short data) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;

        int counter = 0;
        for (ItemStack item : contents) {
            if (item == null || item.getType() != type) {
                continue;
            }
            if (compareDamage && item.getData().getData() != data) {
                continue;
            }

            counter += item.getAmount();
        }

        return counter;
    }
}
