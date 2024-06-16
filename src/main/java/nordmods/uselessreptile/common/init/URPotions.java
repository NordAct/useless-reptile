package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;

import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import nordmods.uselessreptile.UselessReptile;

public class URPotions extends Potions {

    public static final RegistryEntry.Reference<Potion> ACID = Registry.registerReference(Registries.POTION,
            UselessReptile.id("acid"),
            new Potion(new StatusEffectInstance(URStatusEffects.ACID, 200, 1)) {
                @Override
                public boolean hasInstantEffect() {return false;}
            });
    public static final RegistryEntry.Reference<Potion> LONG_ACID = Registry.registerReference(Registries.POTION,
            UselessReptile.id("long_acid"),
            new Potion(new StatusEffectInstance(URStatusEffects.ACID, 400, 1)) {
                @Override
                public boolean hasInstantEffect() {return false;}
            });
    public static final RegistryEntry.Reference<Potion> STRONG_ACID = Registry.registerReference(Registries.POTION,
            UselessReptile.id("strong_acid"),
            new Potion(new StatusEffectInstance(URStatusEffects.ACID, 200, 3)) {
                @Override
                public boolean hasInstantEffect() {return false;}
            });

    public static void init() {
        recipesRegister();
    }

    private static void recipesRegister() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(ACID, Items.GLOWSTONE_DUST,STRONG_ACID);
            builder.registerPotionRecipe(ACID, Items.REDSTONE, LONG_ACID);
        });
    }
}
