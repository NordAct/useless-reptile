package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class URRecipeProvider extends FabricRecipeProvider {
    public URRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerDragonHelmetRecipe(exporter, URItems.DRAGON_HELMET_IRON,ConventionalItemTags.IRON_INGOTS);
        offerDragonHelmetRecipe(exporter, URItems.DRAGON_HELMET_GOLD, ConventionalItemTags.GOLD_INGOTS);
        offerDragonHelmetRecipe(exporter, URItems.DRAGON_HELMET_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

        offerDragonChestplateRecipe(exporter, URItems.DRAGON_CHESTPLATE_IRON,ConventionalItemTags.IRON_INGOTS);
        offerDragonChestplateRecipe(exporter, URItems.DRAGON_CHESTPLATE_GOLD, ConventionalItemTags.GOLD_INGOTS);
        offerDragonChestplateRecipe(exporter, URItems.DRAGON_CHESTPLATE_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

        offerDragonTailArmorRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_IRON,ConventionalItemTags.IRON_INGOTS);
        offerDragonTailArmorRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_GOLD, ConventionalItemTags.GOLD_INGOTS);
        offerDragonTailArmorRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_IRON, URItems.DRAGON_HELMET_IRON);
        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_GOLD, URItems.DRAGON_HELMET_GOLD);
        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_DIAMOND, URItems.DRAGON_HELMET_DIAMOND);
        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_NETHERITE, URItems.DRAGON_HELMET_NETHERITE);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, URItems.FLUTE)
                .input('R', ConventionalItemTags.RED_DYES)
                .input('G', ConventionalItemTags.GREEN_DYES)
                .input('B', ConventionalItemTags.BLUE_DYES)
                .input('W', ItemTags.PLANKS)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .pattern("RGI")
                .pattern("BW ")
                .pattern("W  ")
                .criterion("entity_tamed", AdvancementCriterions.entityTamedCondition(UREntities.RIVER_PIKEHORN_ENTITY))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LEATHER, 2)
                .input(URItems.WYVERN_SKIN)
                .criterion("has_material", conditionsFromItem(URItems.WYVERN_SKIN))
                .offerTo(exporter);

        offerVortexHornRecipe(exporter, URItems.VORTEX_HORN, Items.GOAT_HORN, (ItemConvertible) null);
        offerVortexHornRecipe(exporter, URItems.IRON_VORTEX_HORN, URItems.VORTEX_HORN, ConventionalItemTags.IRON_INGOTS);
        offerVortexHornRecipe(exporter, URItems.GOLD_VORTEX_HORN, URItems.IRON_VORTEX_HORN, ConventionalItemTags.GOLD_INGOTS);
        offerVortexHornRecipe(exporter, URItems.DIAMOND_VORTEX_HORN, URItems.GOLD_VORTEX_HORN, ConventionalItemTags.DIAMOND_GEMS);

        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, URItems.DIAMOND_VORTEX_HORN, RecipeCategory.TOOLS, URItems.NETHERITE_VORTEX_HORN);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, URItems.DRAGON_HELMET_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_HELMET_NETHERITE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, URItems.DRAGON_CHESTPLATE_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_CHESTPLATE_NETHERITE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_TAIL_ARMOR_NETHERITE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, URItems.MOLECLAW_HELMET_DIAMOND, RecipeCategory.TOOLS, URItems.MOLECLAW_HELMET_NETHERITE);
    }

    protected static void offerDragonHelmetRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }

    protected static void offerDragonChestplateRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }


    protected static void offerDragonHelmetRecipe(RecipeExporter exporter, ItemConvertible output, TagKey<Item> input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .criterion("has_material", conditionsFromTag(input))
                .offerTo(exporter);
    }

    protected static void offerDragonChestplateRecipe(RecipeExporter exporter, ItemConvertible output, TagKey<Item> input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .criterion("has_material", conditionsFromTag(input))
                .offerTo(exporter);
    }

    protected static void offerDragonTailArmorRecipe(RecipeExporter exporter, ItemConvertible output, TagKey<Item> input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern(" L ")
                .criterion("has_material", conditionsFromTag(input))
                .offerTo(exporter);
    }

    protected static void offerMoleclawHelmetRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('G', ConventionalItemTags.GLASS_BLOCKS_TINTED)
                .input('H', input)
                .pattern("GHG")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }

    protected static void offerVortexHornRecipe(RecipeExporter exporter, ItemConvertible outputHorn, ItemConvertible inputHorn, @Nullable ItemConvertible inputMaterial) {
        VortexHornRecipeJsonBuilder builder = VortexHornRecipeJsonBuilder.create(RecipeCategory.TOOLS, outputHorn)
                .input('R', Items.BREEZE_ROD)
                .input('H', inputHorn);

        char corner = inputMaterial == null ? ' ' : 'I';
        if (inputMaterial != null) builder.input(corner, inputMaterial);

        builder
                .pattern( corner + "R" + corner)
                .pattern("RHR")
                .pattern( corner + "R" + corner)
                .criterion("has_material", conditionsFromItem(Items.GOAT_HORN))
                .offerTo(exporter);
    }

    protected static void offerVortexHornRecipe(RecipeExporter exporter, ItemConvertible outputHorn, ItemConvertible inputHorn, @Nullable TagKey<Item> inputMaterial) {
        VortexHornRecipeJsonBuilder builder = VortexHornRecipeJsonBuilder.create(RecipeCategory.TOOLS, outputHorn)
                .input('R', Items.BREEZE_ROD)
                .input('H', inputHorn);

        char corner = inputMaterial == null ? ' ' : 'I';
        if (inputMaterial != null) builder.input(corner, inputMaterial);

        builder
                .pattern( corner + "R" + corner)
                .pattern("RHR")
                .pattern( corner + "R" + corner)
                .criterion("has_material", conditionsFromItem(Items.GOAT_HORN))
                .offerTo(exporter);

    }
}
