package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import nordmods.uselessreptile.common.recipe.VortexHornSmithingRecipe;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class VortexHornSmithingRecipeJsonBuilder extends SmithingTransformRecipeBuilder {
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStackTemplate result;
    private final RecipeCategory category;

    public VortexHornSmithingRecipeJsonBuilder(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, ItemStackTemplate result) {
        super(template, base, addition, category, result);
        this.category = category;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static VortexHornSmithingRecipeJsonBuilder smithing(@NonNull Ingredient template, @NonNull Ingredient base, @NonNull Ingredient addition, @NonNull RecipeCategory category, @NonNull Item result) {
        return new VortexHornSmithingRecipeJsonBuilder(template, base, addition, category, new ItemStackTemplate(result));
    }

    @Override
    public @NonNull VortexHornSmithingRecipeJsonBuilder unlocks(@NonNull String name, @NonNull Criterion<?> advancementCriterion) {
        advancementBuilder.unlockedBy(name, advancementCriterion);
        return this;
    }

    @Override
    public void save(RecipeOutput exporter, @NonNull ResourceKey<Recipe<?>> recipeKey) {
        VortexHornSmithingRecipe recipe = new VortexHornSmithingRecipe(
                new Recipe.CommonInfo(true),
                Optional.of(template),
                base,
                Optional.of(addition),
                result
        );
        exporter.accept(recipeKey, recipe, advancementBuilder.build(exporter, recipeKey, category));
    }
}
