package nordmods.uselessreptile.common.init;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.recipe.VortexHornRecipe;
import nordmods.uselessreptile.common.recipe.VortexHornSmithingRecipe;

public class URRecipeSerializers {
    public static final RecipeSerializer<VortexHornRecipe> VORTEX_HORN = Registry.register(Registries.RECIPE_SERIALIZER, UselessReptile.id("vortex_horn"), new VortexHornRecipe.Serializer());
    public static final RecipeSerializer<VortexHornSmithingRecipe> VORTEX_HORN_SMITHING = Registry.register(Registries.RECIPE_SERIALIZER, UselessReptile.id("vortex_horn_smithing"), new VortexHornSmithingRecipe.Serializer());

    public static void init() {}
}
