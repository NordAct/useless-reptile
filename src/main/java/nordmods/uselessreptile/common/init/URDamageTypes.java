package nordmods.uselessreptile.common.init;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;

public class URDamageTypes {
    public static final RegistryEntry<DamageType> ACID = RegistryEntry.of(new DamageType("acid", 0f)) ;

    public static void init () {
    }
}
