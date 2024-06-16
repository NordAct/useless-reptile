package nordmods.uselessreptile.common.init;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import nordmods.uselessreptile.UselessReptile;

public class URDamageTypes {
    public static final RegistryKey<DamageType> ACID = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,UselessReptile.id("acid"));
}
