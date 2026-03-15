package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
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

    public static VortexHornSmithingRecipeJsonBuilder smithing(@NonNull Ingredient template, @NonNull Ingredient base, @NonNull Ingredient addition, @NonNull RecipeCategory category, @NonNull ItemStackTemplate result) {
        return new VortexHornSmithingRecipeJsonBuilder(template, base, addition, category, result);
    }

    @Override
    public @NonNull VortexHornSmithingRecipeJsonBuilder unlocks(@NonNull String string, @NonNull Criterion<?> advancementCriterion) {
        return (VortexHornSmithingRecipeJsonBuilder) super.unlocks(string, advancementCriterion);
    }

    @Override
    public void save(RecipeOutput exporter, @NonNull ResourceKey<Recipe<?>> recipeKey) {
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                new Recipe.CommonInfo(true),
                Optional.of(template),
                base,
                Optional.of(addition),
                result
        );
        exporter.accept(recipeKey, recipe, advancementBuilder.build(exporter, recipeKey, category));
    }
}
