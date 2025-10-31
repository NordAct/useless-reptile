package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.recipe.VortexHornSmithingRecipe;

public class VortexHornSmithingRecipeJsonBuilder extends SmithingTransformRecipeJsonBuilder {

    public VortexHornSmithingRecipeJsonBuilder(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
        super(template, base, addition, category, result);
    }

    public static VortexHornSmithingRecipeJsonBuilder create(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
        return new VortexHornSmithingRecipeJsonBuilder(template, base, addition, category, result);
    }

    @Override
    public VortexHornSmithingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        return (VortexHornSmithingRecipeJsonBuilder) super.criterion(string, advancementCriterion);
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeKey) {
        Advancement.Builder builder = exporter
                .getAdvancementBuilder()
                .criterion(
                        "has_the_recipe",
                        RecipeUnlockedCriterion.create(recipeKey)
                )
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        criteria.forEach(builder::criterion);
        VortexHornSmithingRecipe recipe = new VortexHornSmithingRecipe(
                template,
                base,
                addition,
                new ItemStack(result)
        );
        exporter.accept(recipeKey, recipe, builder.build(recipeKey.withPrefixedPath("recipes/" + category.getName() + "/")));
    }
}
