package nordmods.uselessreptile.datagen.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import nordmods.uselessreptile.common.recipe.VortexHornRecipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class VortexHornRecipeJsonBuilder extends ShapedRecipeBuilder {

    private final Map<Character, Ingredient> key;
    private final List<String> rows;
    private String group;
    private final RecipeCategory category;
    private boolean showNotification;
    private final ItemStackTemplate result;
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private final HolderGetter<Item> items;

    public VortexHornRecipeJsonBuilder(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        super(items, category, result);
        this.rows = Lists.newArrayList();
        this.key = Maps.newLinkedHashMap();
        this.showNotification = true;
        this.category = category;
        this.result = result;
        this.items = items;
    }

    private VortexHornRecipeJsonBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        this(items, category, new ItemStackTemplate(result.asItem(), count));
    }

    public static VortexHornRecipeJsonBuilder shaped(@NonNull HolderGetter<Item> registryLookup, @NonNull RecipeCategory category, ItemLike output) {
        return shaped(registryLookup, category, output, 1);
    }

    public static VortexHornRecipeJsonBuilder shaped(@NonNull HolderGetter<Item> registryLookup, @NonNull RecipeCategory category, ItemLike output, int count) {
        return new VortexHornRecipeJsonBuilder(registryLookup, category, output, count);
    }

    @Override
    public @NonNull ShapedRecipeBuilder define(@NonNull Character c, @NonNull TagKey<Item> tag) {
        return this.define(c, Ingredient.of(items.getOrThrow(tag)));
    }

    @Override
    public @NonNull VortexHornRecipeJsonBuilder define(@NonNull Character c, @NonNull ItemLike itemProvider) {
        return (VortexHornRecipeJsonBuilder) super.define(c, itemProvider);
    }

    @Override
    public @NonNull ShapedRecipeBuilder define(@NonNull Character symbol, @NonNull Ingredient ingredient) {
        if (key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            key.put(symbol, ingredient);
            return this;
        }
    }

    @Override
    public @NonNull ShapedRecipeBuilder pattern(@NonNull String row) {
        if (!rows.isEmpty() && row.length() != rows.getFirst().length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            rows.add(row);
            return this;
        }
    }

    @Override
    public @NonNull VortexHornRecipeJsonBuilder unlockedBy(@NonNull String name, @NonNull Criterion<?> advancementCriterion) {
        this.advancementBuilder.unlockedBy(name, advancementCriterion);
        return this;
    }

    public @NonNull VortexHornRecipeJsonBuilder group(final @Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public @NonNull VortexHornRecipeJsonBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public void save(RecipeOutput exporter, @NonNull ResourceKey<Recipe<?>> recipeKey) {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(key, rows);
        VortexHornRecipe recipe = new VortexHornRecipe(
                RecipeBuilder.createCraftingCommonInfo(showNotification),
                RecipeBuilder.createCraftingBookInfo(category, group),
                pattern,
                result
        );
        exporter.accept(recipeKey, recipe, advancementBuilder.build(exporter, recipeKey, category));
    }
}
