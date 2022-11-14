package rip.alpha.libraries.villager;

import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityTypes;

public class VillagerRegister {

    public VillagerRegister(Class<? extends Entity> clazz, String name, int id) {
        EntityTypes.d.put(clazz, name);
        EntityTypes.f.put(clazz, id);
    }
}
