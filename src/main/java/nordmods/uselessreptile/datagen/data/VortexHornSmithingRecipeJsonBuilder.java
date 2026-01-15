package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import nordmods.uselessreptile.common.recipe.VortexHornSmithingRecipe;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class VortexHornSmithingRecipeJsonBuilder extends SmithingTransformRecipeBuilder {

    public VortexHornSmithingRecipeJsonBuilder(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
        super(template, base, addition, category, result);
    }

    public static VortexHornSmithingRecipeJsonBuilder smithing(@NonNull Ingredient template, @NonNull Ingredient base, @NonNull Ingredient addition, @NonNull RecipeCategory category, @NonNull Item result) {
        return new VortexHornSmithingRecipeJsonBuilder(template, base, addition, category, result);
    }

    @Override
    public @NonNull VortexHornSmithingRecipeJsonBuilder unlocks(@NonNull String string, @NonNull Criterion<?> advancementCriterion) {
        return (VortexHornSmithingRecipeJsonBuilder) super.unlocks(string, advancementCriterion);
    }

    @Override
    public void save(RecipeOutput exporter, @NonNull ResourceKey<Recipe<?>> recipeKey) {
        Advancement.Builder builder = exporter
                .advancement()
                .addCriterion(
                        "has_the_recipe",
                        RecipeUnlockedTrigger.unlocked(recipeKey)
                )
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        VortexHornSmithingRecipe recipe = new VortexHornSmithingRecipe(
                Optional.of(template),
                base,
                Optional.of(addition),
                new TransmuteResult(result)
        );
        exporter.accept(recipeKey, recipe, builder.build(recipeKey.identifier().withPrefix("recipes/" + category.getFolderName() + "/")));
    }
}
