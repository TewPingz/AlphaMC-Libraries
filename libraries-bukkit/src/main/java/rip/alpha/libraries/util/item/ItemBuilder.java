package rip.alpha.libraries.util.item;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import rip.alpha.libraries.util.message.MessageTranslator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class ItemBuilder {

    private static final EnumSet<Material> LEATHER = EnumSet.of(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    );

    private final ItemStack is;

    public ItemBuilder(final Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(final int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(final String name) {
        final ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final String line) {
        final ItemMeta meta = this.is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(MessageTranslator.translate(line));
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(MessageTranslator.translateLines(lore));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(final int durability) {
        this.is.setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte) data));
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(final Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        final ItemMeta meta = this.is.getItemMeta();
        meta.setLore(new ArrayList<>());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        final ItemMeta meta = this.is.getItemMeta();
        meta.addItemFlags(flags);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (final Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }
        return this;
    }

    public ItemBuilder clearAttributes() {
        throw new UnsupportedOperationException();
    }

    public ItemBuilder unbreakable() {
        final ItemMeta meta = this.is.getItemMeta();
        meta.spigot().setUnbreakable(true);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder woolColor(DyeColor color) {
        if (this.is.getType() == Material.WOOL) {
            this.is.setData(new Wool(color));
            return this;
        } else {
            throw new IllegalArgumentException("woolColor() only applicable for wool!");
        }
    }

    public ItemBuilder color(Color color) {
        if (LEATHER.contains(this.is.getType())) {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return this.is;
    }

}
