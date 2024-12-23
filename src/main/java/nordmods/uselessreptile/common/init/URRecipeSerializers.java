package nordmods.uselessreptile.common.init;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.recipe.VortexHornRecipe;

public class URRecipeSerializers {
    public static final RecipeSerializer<VortexHornRecipe> VORTEX_HORN = Registry.register(Registries.RECIPE_SERIALIZER, UselessReptile.id("vortex_horn"), new VortexHornRecipe.Serializer());

    public static void init() {}
}
