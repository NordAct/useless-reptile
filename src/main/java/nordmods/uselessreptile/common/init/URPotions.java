package nordmods.uselessreptile.common.init;

import net.minecraft.entity.effect.StatusEffectInstance;

import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import nordmods.uselessreptile.UselessReptile;

public class URPotions extends Potions {

    public static final Potion ACID = new Potion(new StatusEffectInstance(URStatusEffects.ACID, 200, 1)) {
        @Override
        public boolean hasInstantEffect() {return false;}
    };
    public static final Potion LONG_ACID = new Potion(new StatusEffectInstance(URStatusEffects.ACID, 400, 1)) {
        @Override
        public boolean hasInstantEffect() {return false;}
    };
    public static final Potion STRONG_ACID = new Potion(new StatusEffectInstance(URStatusEffects.ACID, 200, 3)) {
        @Override
        public boolean hasInstantEffect() {return false;}
    };

    public static void init() {
        Registry.register(Registry.POTION, new Identifier(UselessReptile.MODID,"acid"), ACID);
        Registry.register(Registry.POTION, new Identifier(UselessReptile.MODID,"long_acid"), LONG_ACID);
        Registry.register(Registry.POTION, new Identifier(UselessReptile.MODID,"strong_acid"), STRONG_ACID);

        recipesRegister();
    }

    private static void recipesRegister() {
        BrewingRecipeRegistry.registerPotionRecipe(ACID, Items.GLOWSTONE_DUST,STRONG_ACID);
        BrewingRecipeRegistry.registerPotionRecipe(ACID, Items.REDSTONE, LONG_ACID);
    }
}
