package rip.alpha.libraries.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamagerUtils {

    public static Player getDamager(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        if (damagerEntity instanceof Player) {
            return (Player) damagerEntity;
        }

        if (damagerEntity instanceof Projectile) {
            Projectile projectile = (Projectile) damagerEntity;
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player) {
                return (Player) source;
            }
        }

        return null;
    }

    private static final EntityDamageEvent.DamageModifier[] MODIFIERS = EntityDamageEvent.DamageModifier.values();

    public static void setDamage(EntityDamageEvent event, int damage) {
        clearModifier(event);
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);
    }

    public static void clearModifier(EntityDamageEvent event) {
        for (EntityDamageEvent.DamageModifier value : MODIFIERS) {
            if (value == EntityDamageEvent.DamageModifier.RESISTANCE) {
                continue;
            }
            if (value == EntityDamageEvent.DamageModifier.ABSORPTION) {
                continue;
            }
            try {
                event.setDamage(value, 0);
            } catch (Exception e) {
                //ignore couldnt apply modifier
            }
        }
    }
}
