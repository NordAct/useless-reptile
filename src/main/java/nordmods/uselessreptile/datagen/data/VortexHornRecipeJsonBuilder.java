package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import nordmods.uselessreptile.common.recipe.VortexHornRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class VortexHornRecipeJsonBuilder extends ShapedRecipeBuilder {

    public VortexHornRecipeJsonBuilder(HolderGetter<Item> registryLookup, RecipeCategory category, ItemLike output, int count) {
        super(registryLookup, category, output, count);
    }

    public static VortexHornRecipeJsonBuilder shaped(HolderGetter<Item> registryLookup, RecipeCategory category, ItemLike output) {
        return shaped(registryLookup, category, output, 1);
    }

    public static VortexHornRecipeJsonBuilder shaped(HolderGetter<Item> registryLookup, RecipeCategory category, ItemLike output, int count) {
        return new VortexHornRecipeJsonBuilder(registryLookup, category, output, count);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder define(Character c, TagKey<Item> tag) {
        return (VortexHornRecipeJsonBuilder) super.define(c, tag);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder define(Character c, ItemLike itemProvider) {
        return (VortexHornRecipeJsonBuilder) super.define(c, itemProvider);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder define(Character c, Ingredient ingredient) {
        return (VortexHornRecipeJsonBuilder) super.define(c, ingredient);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder pattern(String patternStr) {
        return (VortexHornRecipeJsonBuilder) super.pattern(patternStr);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
        return (VortexHornRecipeJsonBuilder) super.unlockedBy(string, advancementCriterion);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder group(@Nullable String string) {
        return (VortexHornRecipeJsonBuilder) super.group(string);
    }

    @Override
    public @NotNull VortexHornRecipeJsonBuilder showNotification(boolean showNotification) {
        return (VortexHornRecipeJsonBuilder) super.showNotification(showNotification);
    }

    @Override
    public void save(RecipeOutput exporter, ResourceKey<Recipe<?>> recipeKey) {
        ShapedRecipePattern rawShapedRecipe = ShapedRecipePattern.of(this.key, this.rows);
        Advancement.Builder builder = exporter
                .advancement()
                .addCriterion(
                        "has_the_recipe",
                        RecipeUnlockedTrigger.unlocked(recipeKey)
                )
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        VortexHornRecipe shapedRecipe = new VortexHornRecipe(Objects.requireNonNullElse(group, ""), RecipeBuilder.determineBookCategory(category), rawShapedRecipe, new ItemStack(result, count), showNotification);
        exporter.accept(recipeKey, shapedRecipe, builder.build(recipeKey.identifier().withPrefix("recipes/" + category.getFolderName() + "/")));
    }
}
