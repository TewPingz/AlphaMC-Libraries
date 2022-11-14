package rip.alpha.libraries.villager;

import lombok.Getter;
import net.minecraft.server.v1_7_R4.*;

@Getter
public class BaseVillager extends EntityVillager {

    private final boolean canCollide;

    public BaseVillager(World world, boolean canCollide) {
        super(world);
        this.canCollide = canCollide;
    }

    @Override
    public void h() {
        this.motX = 0;
        this.motY = 0;
        this.motZ = 0;
        super.h();
    }

    @Override
    public void collide(Entity entity) {
        if (!canCollide) {
            return;
        }
        super.collide(entity);
    }

    @Override
    public EntityAgeable createChild(final EntityAgeable entityAgeable) {
        return null;
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
    }

    @Override
    public boolean c(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return false;
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return false;
    }

    @Override
    public void e(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
    }

    @Override
    public void g(double d0, double d1, double d2) {
        //cant push these entities
    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return false;
    }

    public void makeSound(String s, float f, float f1) {
        if (s.equalsIgnoreCase("mob.villager.hit")) { //only allow damage sound
            super.makeSound(s, f, f1);
        }
    }

    @Override
    public void die() {
        super.die();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return super.damageEntity(damagesource, f);
    }
}
