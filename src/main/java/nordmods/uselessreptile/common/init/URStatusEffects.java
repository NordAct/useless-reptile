package nordmods.uselessreptile.common.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.statuseffects.AcidStatusEffect;

public class URStatusEffects extends StatusEffects {

    public static final StatusEffect ACID = new AcidStatusEffect();

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(UselessReptile.MODID, "acid"), ACID);
    }
}
