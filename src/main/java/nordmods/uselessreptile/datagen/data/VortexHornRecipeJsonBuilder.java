package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import nordmods.uselessreptile.common.recipe.VortexHornRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class VortexHornRecipeJsonBuilder extends ShapedRecipeJsonBuilder {

    public VortexHornRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
        super(registryLookup, category, output, count);
    }

    public static VortexHornRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output) {
        return create(registryLookup, category, output, 1);
    }

    public static VortexHornRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
        return new VortexHornRecipeJsonBuilder(registryLookup, category, output, count);
    }

    @Override
    public VortexHornRecipeJsonBuilder input(Character c, TagKey<Item> tag) {
        return (VortexHornRecipeJsonBuilder) super.input(c, tag);
    }

    @Override
    public VortexHornRecipeJsonBuilder input(Character c, ItemConvertible itemProvider) {
        return (VortexHornRecipeJsonBuilder) super.input(c, itemProvider);
    }

    @Override
    public VortexHornRecipeJsonBuilder input(Character c, Ingredient ingredient) {
        return (VortexHornRecipeJsonBuilder) super.input(c, ingredient);
    }

    @Override
    public VortexHornRecipeJsonBuilder pattern(String patternStr) {
        return (VortexHornRecipeJsonBuilder) super.pattern(patternStr);
    }

    @Override
    public VortexHornRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        return (VortexHornRecipeJsonBuilder) super.criterion(string, advancementCriterion);
    }

    @Override
    public VortexHornRecipeJsonBuilder group(@Nullable String string) {
        return (VortexHornRecipeJsonBuilder) super.group(string);
    }

    @Override
    public VortexHornRecipeJsonBuilder showNotification(boolean showNotification) {
        return (VortexHornRecipeJsonBuilder) super.showNotification(showNotification);
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        RawShapedRecipe rawShapedRecipe = RawShapedRecipe.create(this.inputs, this.pattern);
        Advancement.Builder builder = exporter
                .getAdvancementBuilder()
                .criterion(
                        "has_the_recipe",
                        RecipeUnlockedCriterion.create(recipeKey)
                )
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        criteria.forEach(builder::criterion);
        VortexHornRecipe shapedRecipe = new VortexHornRecipe(Objects.requireNonNullElse(group, ""), CraftingRecipeJsonBuilder.toCraftingCategory(category), rawShapedRecipe, new ItemStack(output, count), showNotification);
        exporter.accept(recipeKey, shapedRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + category.getName() + "/")));
    }
}
