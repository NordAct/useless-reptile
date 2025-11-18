package nordmods.uselessreptile.common.statuseffect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class URStatusEffect extends MobEffect {
    public URStatusEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public URStatusEffect(MobEffectCategory category, int color, ParticleOptions particleEffect) {
        super(category, color, particleEffect);
    }
}
