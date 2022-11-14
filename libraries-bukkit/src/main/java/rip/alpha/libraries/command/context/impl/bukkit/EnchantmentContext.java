package rip.alpha.libraries.command.context.impl.bukkit;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.message.MessageColor;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentContext implements ContextResolver<Enchantment> {

    private final List<String> tabComplete;
    private final String errorMessage;

    public EnchantmentContext() {
        this.tabComplete = new ArrayList<>();
        for (EnchantmentWrapper value : EnchantmentWrapper.values()) {
            tabComplete.add(value.getFriendlyName());
            tabComplete.add(value.getEnchant().getName());
        }
        this.errorMessage = MessageColor.RED + "That is an invalid enchantment";
    }

    @Override
    public Enchantment resolve(ArgumentContext<Enchantment> input) {
        EnchantmentWrapper enchantment = EnchantmentWrapper.parse(input.input());
        if (enchantment == null) {
            input.sender().sendMessage(this.errorMessage);
            return null;
        }
        return enchantment.getEnchant();
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Enchantment> context) {
        return this.tabComplete;
    }

    @Getter
    public enum EnchantmentWrapper {
        PROTECTION(Enchantment.PROTECTION_ENVIRONMENTAL, new String[]{"prot", "protect"}),
        FIRE_PROTECTION(Enchantment.PROTECTION_FIRE,
                new String[]{"fireprot", "fireprotection", "firep"}),
        BLAST_PROTECTION(Enchantment.PROTECTION_EXPLOSIONS,
                new String[]{"explosionsprotection", "explosionprotection", "bprotection", "bprotect",
                        "blastprotect"}),
        PROJECTILE_PROTECTION(Enchantment.PROTECTION_PROJECTILE,
                new String[]{"projprot", "projprotection", "projp", "pprot"}),
        THORNS(Enchantment.THORNS, new String[0]),
        UNBREAKING(Enchantment.DURABILITY, new String[]{"dura", "unbr"}),
        SHARPNESS(Enchantment.DAMAGE_ALL, new String[]{"sharp"}),
        SMITE(Enchantment.DAMAGE_UNDEAD, new String[0]),
        BANE_OF_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS,
                new String[]{"bane", "ardmg", "baneofarthropod", "arthropod"}),
        KNOCKBACK(Enchantment.KNOCKBACK, new String[]{"kb", "knock"}),
        FIRE_ASPECT(Enchantment.FIRE_ASPECT, new String[]{"fire"}),
        RESPIRATION(Enchantment.OXYGEN, new String[]{"breathing"}),
        AQUA_AFFINITY(Enchantment.WATER_WORKER, new String[0]),
        LOOTING(Enchantment.LOOT_BONUS_MOBS, new String[]{"moblooting", "loot"}),
        EFFICIENCY(Enchantment.DIG_SPEED, new String[]{"eff", "digspeed"}),
        SILK_TOUCH(Enchantment.SILK_TOUCH, new String[]{"silk"}),
        FORTUNE(Enchantment.LOOT_BONUS_BLOCKS, new String[]{"fort"}),
        POWER(Enchantment.ARROW_DAMAGE, new String[]{"adamage"}),
        PUNCH(Enchantment.ARROW_KNOCKBACK, new String[]{"arrowkb"}),
        FLAME(Enchantment.ARROW_FIRE, new String[]{"afire"}),
        INFINITY(Enchantment.ARROW_INFINITE, new String[]{"infinite", "unlimited", "inf"}),
        LUCK_OF_THE_SEA(Enchantment.LUCK, new String[]{"rodluck", "luckofsea", "los"}),
        LURE(Enchantment.LURE, new String[]{"rodlure"});


        public String friendlyName;
        public Enchantment enchant;
        public String[] alias;

        private EnchantmentWrapper(Enchantment enchant, String[] alias) {
            this.friendlyName = name().toLowerCase();
            this.enchant = enchant;
            this.alias = alias;
        }

        public static EnchantmentWrapper parse(final String input) {
            for (final EnchantmentWrapper enchantment : values()) {
                for (final String str : enchantment.getAlias()) {
                    if (str.equalsIgnoreCase(input)) {
                        return enchantment;
                    }
                }
                if (enchantment.getEnchant().getName().replace("_", "").equalsIgnoreCase(input)) {
                    return enchantment;
                }
                if (enchantment.getEnchant().getName().equalsIgnoreCase(input)) {
                    return enchantment;
                }
                if (enchantment.getFriendlyName().equalsIgnoreCase(input)) {
                    return enchantment;
                }
            }
            return null;
        }

    }
}
