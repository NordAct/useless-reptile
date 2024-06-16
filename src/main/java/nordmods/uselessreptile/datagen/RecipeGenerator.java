package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerDragonHelmetRecipe(exporter, URItems.DRAGON_HELMET_IRON, Items.IRON_INGOT);
        offerDragonHelmetRecipe(exporter, URItems.DRAGON_HELMET_GOLD, Items.GOLD_INGOT);
        offerDragonHelmetRecipe(exporter, URItems.DRAGON_HELMET_DIAMOND, Items.DIAMOND);

        offerDragonChestplateRecipe(exporter, URItems.DRAGON_CHESTPLATE_IRON, Items.IRON_INGOT);
        offerDragonChestplateRecipe(exporter, URItems.DRAGON_CHESTPLATE_GOLD, Items.GOLD_INGOT);
        offerDragonChestplateRecipe(exporter, URItems.DRAGON_CHESTPLATE_DIAMOND, Items.DIAMOND);

        offerDragonTailArmorRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_IRON, Items.IRON_INGOT);
        offerDragonTailArmorRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_GOLD, Items.GOLD_INGOT);
        offerDragonTailArmorRecipe(exporter, URItems.DRAGON_TAIL_ARMOR_DIAMOND, Items.DIAMOND);

        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_IRON, URItems.DRAGON_HELMET_IRON);
        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_GOLD, URItems.DRAGON_HELMET_GOLD);
        offerMoleclawHelmetRecipe(exporter, URItems.MOLECLAW_HELMET_DIAMOND, URItems.DRAGON_HELMET_DIAMOND);

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
    }

    private static void offerDragonHelmetRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }

    private static void offerDragonChestplateRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }

    private static void offerDragonTailArmorRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern(" L ")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }

    private static void offerMoleclawHelmetRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('G', Items.TINTED_GLASS)
                .input('H', input)
                .pattern("GHG")
                .criterion("has_material", conditionsFromItem(input))
                .offerTo(exporter);
    }

}
