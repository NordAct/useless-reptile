package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import nordmods.uselessreptile.UselessReptile;

public class URPotions extends Potions {

    public static final Holder.Reference<Potion> ACID = Registry.registerForHolder(BuiltInRegistries.POTION,
            UselessReptile.id("acid"),
            new Potion("acid", new MobEffectInstance(URMobEffect.ACID, 200, 1)) {
                @Override
                public boolean hasInstantEffects() {return false;}
            });
    public static final Holder.Reference<Potion> LONG_ACID = Registry.registerForHolder(BuiltInRegistries.POTION,
            UselessReptile.id("long_acid"),
            new Potion("long_acid", new MobEffectInstance(URMobEffect.ACID, 400, 1)) {
                @Override
                public boolean hasInstantEffects() {return false;}
            });
    public static final Holder.Reference<Potion> STRONG_ACID = Registry.registerForHolder(BuiltInRegistries.POTION,
            UselessReptile.id("strong_acid"),
            new Potion("strong_acid", new MobEffectInstance(URMobEffect.ACID, 200, 3)) {
                @Override
                public boolean hasInstantEffects() {return false;}
            });

    public static void init() {
        recipesRegister();
    }

    private static void recipesRegister() {
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.addMix(ACID, Items.GLOWSTONE_DUST,STRONG_ACID);
            builder.addMix(ACID, Items.REDSTONE, LONG_ACID);
        });
    }
}
