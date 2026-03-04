package nordmods.uselessreptile.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import nordmods.uselessreptile.UselessReptile;

public class URDamageTypes {
    public static final ResourceKey<DamageType> ACID = ResourceKey.create(Registries.DAMAGE_TYPE,UselessReptile.id("acid"));

    public static void init() {}
}
