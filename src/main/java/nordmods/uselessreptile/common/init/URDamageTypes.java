package nordmods.uselessreptile.common.init;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nordmods.uselessreptile.UselessReptile;

public class URDamageTypes {
    public static final RegistryKey<DamageType> ACID = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(UselessReptile.MODID, "acid"));
}
