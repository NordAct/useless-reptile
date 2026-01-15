package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class URRecipeProvider extends FabricRecipeProvider {
    public URRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider wrapperLookup, @NonNull RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                offerDragonHelmetRecipe(this, output, URItems.DRAGON_HELMET_IRON,ConventionalItemTags.IRON_INGOTS);
                offerDragonHelmetRecipe(this, output, URItems.DRAGON_HELMET_GOLD, ConventionalItemTags.GOLD_INGOTS);
                offerDragonHelmetRecipe(this, output, URItems.DRAGON_HELMET_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

                offerDragonChestplateRecipe(this, output, URItems.DRAGON_CHESTPLATE_IRON,ConventionalItemTags.IRON_INGOTS);
                offerDragonChestplateRecipe(this, output, URItems.DRAGON_CHESTPLATE_GOLD, ConventionalItemTags.GOLD_INGOTS);
                offerDragonChestplateRecipe(this, output, URItems.DRAGON_CHESTPLATE_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

                offerDragonTailArmorRecipe(this, output, URItems.DRAGON_TAIL_ARMOR_IRON,ConventionalItemTags.IRON_INGOTS);
                offerDragonTailArmorRecipe(this, output, URItems.DRAGON_TAIL_ARMOR_GOLD, ConventionalItemTags.GOLD_INGOTS);
                offerDragonTailArmorRecipe(this, output, URItems.DRAGON_TAIL_ARMOR_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

                offerMoleclawHelmetRecipe(this, output, URItems.MOLECLAW_HELMET_IRON, URItems.DRAGON_HELMET_IRON);
                offerMoleclawHelmetRecipe(this, output, URItems.MOLECLAW_HELMET_GOLD, URItems.DRAGON_HELMET_GOLD);
                offerMoleclawHelmetRecipe(this, output, URItems.MOLECLAW_HELMET_DIAMOND, URItems.DRAGON_HELMET_DIAMOND);
                offerMoleclawHelmetRecipe(this, output, URItems.MOLECLAW_HELMET_NETHERITE, URItems.DRAGON_HELMET_NETHERITE);

                shaped(RecipeCategory.TOOLS, (URItems.FLUTE))
                        .define('R', ConventionalItemTags.RED_DYES)
                        .define('G', ConventionalItemTags.GREEN_DYES)
                        .define('B', ConventionalItemTags.BLUE_DYES)
                        .define('W', ItemTags.PLANKS)
                        .define('I', ConventionalItemTags.IRON_INGOTS)
                        .pattern("RGI")
                        .pattern("BW ")
                        .pattern("W  ")
                        .unlockedBy("entity_tamed", AdvancementCriterions.entityTamedCondition(wrapperLookup.lookupOrThrow(Registries.ENTITY_TYPE), UREntities.RIVER_PIKEHORN_ENTITY))
                        .save(output);

                shapeless(RecipeCategory.MISC, Items.LEATHER, 2)
                        .requires(URItems.WYVERN_SKIN)
                        .unlockedBy("has_material", has(URItems.WYVERN_SKIN))
                        .save(output);

                HolderGetter<Item> registryEntryLookup = wrapperLookup.lookupOrThrow(Registries.ITEM);
                offerVortexHornRecipe(this, registryEntryLookup, output, null);
                offerVortexHornRecipe(this, registryEntryLookup, output, URItems.IRON_VORTEX_HORN, URItems.VORTEX_HORN, ConventionalItemTags.IRON_INGOTS);
                offerVortexHornRecipe(this, registryEntryLookup, output, URItems.GOLD_VORTEX_HORN, URItems.IRON_VORTEX_HORN, ConventionalItemTags.GOLD_INGOTS);
                offerVortexHornRecipe(this, registryEntryLookup, output, URItems.DIAMOND_VORTEX_HORN, URItems.GOLD_VORTEX_HORN, ConventionalItemTags.DIAMOND_GEMS);

                VortexHornSmithingRecipeJsonBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(URItems.DIAMOND_VORTEX_HORN),
                        tag(ItemTags.NETHERITE_TOOL_MATERIALS),
                        RecipeCategory.TOOLS,
                        URItems.NETHERITE_VORTEX_HORN
                        )
                        .unlocks(
                                "has_netherite_ingot",
                                has(ItemTags.NETHERITE_TOOL_MATERIALS)
                        )
                        .save(this.output, UselessReptile.id(getItemName(URItems.NETHERITE_VORTEX_HORN) + "_smithing").toString());

                netheriteSmithing(URItems.DRAGON_HELMET_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_HELMET_NETHERITE);
                netheriteSmithing(URItems.DRAGON_CHESTPLATE_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_CHESTPLATE_NETHERITE);
                netheriteSmithing(URItems.DRAGON_TAIL_ARMOR_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_TAIL_ARMOR_NETHERITE);
                netheriteSmithing(URItems.MOLECLAW_HELMET_DIAMOND, RecipeCategory.TOOLS, URItems.MOLECLAW_HELMET_NETHERITE);
            }
        };
    }

    protected static void offerDragonHelmetRecipe(RecipeProvider generator, RecipeOutput exporter, ItemLike output, ItemLike input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('L', Items.LEATHER)
                .define('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerDragonChestplateRecipe(RecipeProvider generator, RecipeOutput exporter, ItemLike output, ItemLike input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('L', Items.LEATHER)
                .define('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerDragonTailArmorRecipe(RecipeProvider generator,RecipeOutput exporter, ItemLike output, ItemLike input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('L', Items.LEATHER)
                .define('M', input)
                .pattern("MMM")
                .pattern(" L ")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerDragonHelmetRecipe(RecipeProvider generator, RecipeOutput exporter, ItemLike output, TagKey<Item> input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('L', Items.LEATHER)
                .define('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerDragonChestplateRecipe(RecipeProvider generator,RecipeOutput exporter, ItemLike output, TagKey<Item> input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('L', Items.LEATHER)
                .define('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerDragonTailArmorRecipe(RecipeProvider generator,RecipeOutput exporter, ItemLike output, TagKey<Item> input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('L', Items.LEATHER)
                .define('M', input)
                .pattern("MMM")
                .pattern(" L ")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerMoleclawHelmetRecipe(RecipeProvider generator, RecipeOutput exporter, ItemLike output, ItemLike input) {
        generator.shaped(RecipeCategory.COMBAT, output)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_TINTED)
                .define('H', input)
                .pattern("GHG")
                .unlockedBy("has_material", generator.has(input))
                .save(exporter);
    }

    protected static void offerVortexHornRecipe(RecipeProvider generator, HolderGetter<Item> registryEntryLookup, RecipeOutput exporter, @Nullable ItemLike inputMaterial) {
        VortexHornRecipeJsonBuilder builder = VortexHornRecipeJsonBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, URItems.VORTEX_HORN)
                .define('R', Items.BREEZE_ROD)
                .define('H', Items.GOAT_HORN);

        char corner = inputMaterial == null ? ' ' : 'I';
        if (inputMaterial != null) builder.define(corner, inputMaterial);

        builder
                .pattern( corner + "R" + corner)
                .pattern("RHR")
                .pattern( corner + "R" + corner)
                .unlockedBy("has_material", generator.has(Items.GOAT_HORN))
                .save(exporter);
    }

    protected static void offerVortexHornRecipe(RecipeProvider generator,HolderGetter<Item> registryEntryLookup, RecipeOutput exporter, ItemLike outputHorn, ItemLike inputHorn, @Nullable TagKey<Item> inputMaterial) {
        VortexHornRecipeJsonBuilder builder = VortexHornRecipeJsonBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, outputHorn)
                .define('R', Items.BREEZE_ROD)
                .define('H', inputHorn);

        char corner = inputMaterial == null ? ' ' : 'I';
        if (inputMaterial != null) builder.define(corner, inputMaterial);

        builder
                .pattern( corner + "R" + corner)
                .pattern("RHR")
                .pattern( corner + "R" + corner)
                .unlockedBy("has_material", generator.has(Items.GOAT_HORN))
                .save(exporter);

    }

    @Override
    public @NonNull String getName() {
        return "UR Recipes";
    }
}
