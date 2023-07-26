package nordmods.uselessreptile.common.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;

public class URDamageSources extends DamageSource {
    public URDamageSources(RegistryEntry<DamageType> type) {
        super(type);
    }
    public static final DamageSource ACID = new URDamageSources(URDamageTypes.ACID);
}
