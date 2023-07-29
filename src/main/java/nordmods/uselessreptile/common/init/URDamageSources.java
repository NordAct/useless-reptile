package nordmods.uselessreptile.common.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public class URDamageSources {
    public static DamageSource acid(World world) {
        return new DamageSource(URDamageTypes.getEntryOf(URDamageTypes.ACID, world));
    }
}
