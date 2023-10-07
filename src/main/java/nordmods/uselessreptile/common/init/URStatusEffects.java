package nordmods.uselessreptile.common.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.statuseffects.AcidStatusEffect;

public class URStatusEffects extends StatusEffects {

    public static final StatusEffect ACID = new AcidStatusEffect();

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(UselessReptile.MODID, "acid"), ACID);
    }
}
