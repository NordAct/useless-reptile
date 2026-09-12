package nordmods.uselessreptile.datagen.data;

import net.minecraft.data.recipes.BrewingProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.common.init.URPotions;

public class URBrewingProvider extends BrewingProvider {
    protected URBrewingProvider(RecipeOutput output) {
        super(output);
    }

    @Override
    protected void addContainers() {
        this.addContainer(Items.LINGERING_POTION);
        this.addContainer(Items.POTION);
        this.addContainer(Items.SPLASH_POTION);
    }

    @Override
    protected void addContainerTransformations() {
        this.addContainerTransformation(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        this.addContainerTransformation(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
    }

    @Override
    protected void buildMixes() {
        buildMix(URPotions.ACID, Items.GLOWSTONE_DUST, URPotions.STRONG_ACID);
        buildMix(URPotions.ACID, Items.REDSTONE, URPotions.LONG_ACID);
    }
}
