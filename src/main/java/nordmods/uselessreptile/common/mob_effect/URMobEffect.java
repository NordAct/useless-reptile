package nordmods.uselessreptile.common.mob_effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class URMobEffect extends MobEffect {
    public URMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public URMobEffect(MobEffectCategory category, int color, ParticleOptions particleEffect) {
        super(category, color, particleEffect);
    }
}
