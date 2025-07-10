package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
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
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                offerDragonHelmetRecipe(this, exporter, URItems.DRAGON_HELMET_IRON,ConventionalItemTags.IRON_INGOTS);
                offerDragonHelmetRecipe(this, exporter, URItems.DRAGON_HELMET_GOLD, ConventionalItemTags.GOLD_INGOTS);
                offerDragonHelmetRecipe(this, exporter, URItems.DRAGON_HELMET_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

                offerDragonChestplateRecipe(this, exporter, URItems.DRAGON_CHESTPLATE_IRON,ConventionalItemTags.IRON_INGOTS);
                offerDragonChestplateRecipe(this, exporter, URItems.DRAGON_CHESTPLATE_GOLD, ConventionalItemTags.GOLD_INGOTS);
                offerDragonChestplateRecipe(this, exporter, URItems.DRAGON_CHESTPLATE_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

                offerDragonTailArmorRecipe(this, exporter, URItems.DRAGON_TAIL_ARMOR_IRON,ConventionalItemTags.IRON_INGOTS);
                offerDragonTailArmorRecipe(this, exporter, URItems.DRAGON_TAIL_ARMOR_GOLD, ConventionalItemTags.GOLD_INGOTS);
                offerDragonTailArmorRecipe(this, exporter, URItems.DRAGON_TAIL_ARMOR_DIAMOND, ConventionalItemTags.DIAMOND_GEMS);

                offerMoleclawHelmetRecipe(this, exporter, URItems.MOLECLAW_HELMET_IRON, URItems.DRAGON_HELMET_IRON);
                offerMoleclawHelmetRecipe(this, exporter, URItems.MOLECLAW_HELMET_GOLD, URItems.DRAGON_HELMET_GOLD);
                offerMoleclawHelmetRecipe(this, exporter, URItems.MOLECLAW_HELMET_DIAMOND, URItems.DRAGON_HELMET_DIAMOND);
                offerMoleclawHelmetRecipe(this, exporter, URItems.MOLECLAW_HELMET_NETHERITE, URItems.DRAGON_HELMET_NETHERITE);

                createShaped(RecipeCategory.TOOLS, (URItems.FLUTE))
                        .input('R', ConventionalItemTags.RED_DYES)
                        .input('G', ConventionalItemTags.GREEN_DYES)
                        .input('B', ConventionalItemTags.BLUE_DYES)
                        .input('W', ItemTags.PLANKS)
                        .input('I', ConventionalItemTags.IRON_INGOTS)
                        .pattern("RGI")
                        .pattern("BW ")
                        .pattern("W  ")
                        .criterion("entity_tamed", AdvancementCriterions.entityTamedCondition(wrapperLookup.getOrThrow(RegistryKeys.ENTITY_TYPE), UREntities.RIVER_PIKEHORN_ENTITY))
                        .offerTo(exporter);

                createShapeless(RecipeCategory.MISC, Items.LEATHER, 2)
                        .input(URItems.WYVERN_SKIN)
                        .criterion("has_material", conditionsFromItem(URItems.WYVERN_SKIN))
                        .offerTo(exporter);

                RegistryEntryLookup<Item> registryEntryLookup = wrapperLookup.getOrThrow(RegistryKeys.ITEM);
                offerVortexHornRecipe(this, registryEntryLookup, exporter, URItems.VORTEX_HORN, Items.GOAT_HORN, (ItemConvertible) null);
                offerVortexHornRecipe(this, registryEntryLookup, exporter, URItems.IRON_VORTEX_HORN, URItems.VORTEX_HORN, ConventionalItemTags.IRON_INGOTS);
                offerVortexHornRecipe(this, registryEntryLookup, exporter, URItems.GOLD_VORTEX_HORN, URItems.IRON_VORTEX_HORN, ConventionalItemTags.GOLD_INGOTS);
                offerVortexHornRecipe(this, registryEntryLookup, exporter, URItems.DIAMOND_VORTEX_HORN, URItems.GOLD_VORTEX_HORN, ConventionalItemTags.DIAMOND_GEMS);

                offerNetheriteUpgradeRecipe(URItems.DIAMOND_VORTEX_HORN, RecipeCategory.TOOLS, URItems.NETHERITE_VORTEX_HORN);
                offerNetheriteUpgradeRecipe(URItems.DRAGON_HELMET_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_HELMET_NETHERITE);
                offerNetheriteUpgradeRecipe(URItems.DRAGON_CHESTPLATE_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_CHESTPLATE_NETHERITE);
                offerNetheriteUpgradeRecipe(URItems.DRAGON_TAIL_ARMOR_DIAMOND, RecipeCategory.TOOLS, URItems.DRAGON_TAIL_ARMOR_NETHERITE);
                offerNetheriteUpgradeRecipe(URItems.MOLECLAW_HELMET_DIAMOND, RecipeCategory.TOOLS, URItems.MOLECLAW_HELMET_NETHERITE);
            }
        };
    }

    protected static void offerDragonHelmetRecipe(RecipeGenerator generator, RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        generator.createShaped(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .criterion("has_material", generator.conditionsFromItem(input))
                .offerTo(exporter);
    }

    protected static void offerDragonChestplateRecipe(RecipeGenerator generator, RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        generator.createShaped(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .criterion("has_material", generator.conditionsFromItem(input))
                .offerTo(exporter);
    }


    protected static void offerDragonHelmetRecipe(RecipeGenerator generator, RecipeExporter exporter, ItemConvertible output, TagKey<Item> input) {
        generator.createShaped(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern("M M")
                .pattern("L L")
                .criterion("has_material", generator.conditionsFromTag(input))
                .offerTo(exporter);
    }

    protected static void offerDragonChestplateRecipe(RecipeGenerator generator,RecipeExporter exporter, ItemConvertible output, TagKey<Item> input) {
        generator.createShaped(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MLM")
                .pattern("MMM")
                .pattern("LML")
                .criterion("has_material", generator.conditionsFromTag(input))
                .offerTo(exporter);
    }

    protected static void offerDragonTailArmorRecipe(RecipeGenerator generator,RecipeExporter exporter, ItemConvertible output, TagKey<Item> input) {
        generator.createShaped(RecipeCategory.COMBAT, output)
                .input('L', Items.LEATHER)
                .input('M', input)
                .pattern("MMM")
                .pattern(" L ")
                .criterion("has_material", generator.conditionsFromTag(input))
                .offerTo(exporter);
    }

    protected static void offerMoleclawHelmetRecipe(RecipeGenerator generator, RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        generator.createShaped(RecipeCategory.COMBAT, output)
                .input('G', ConventionalItemTags.GLASS_BLOCKS_TINTED)
                .input('H', input)
                .pattern("GHG")
                .criterion("has_material", generator.conditionsFromItem(input))
                .offerTo(exporter);
    }

    protected static void offerVortexHornRecipe(RecipeGenerator generator,RegistryEntryLookup<Item> registryEntryLookup, RecipeExporter exporter, ItemConvertible outputHorn, ItemConvertible inputHorn, @Nullable ItemConvertible inputMaterial) {
        VortexHornRecipeJsonBuilder builder = VortexHornRecipeJsonBuilder.create(registryEntryLookup, RecipeCategory.TOOLS, outputHorn)
                .input('R', Items.BREEZE_ROD)
                .input('H', inputHorn);

        char corner = inputMaterial == null ? ' ' : 'I';
        if (inputMaterial != null) builder.input(corner, inputMaterial);

        builder
                .pattern( corner + "R" + corner)
                .pattern("RHR")
                .pattern( corner + "R" + corner)
                .criterion("has_material", generator.conditionsFromItem(Items.GOAT_HORN))
                .offerTo(exporter);
    }

    protected static void offerVortexHornRecipe(RecipeGenerator generator,RegistryEntryLookup<Item> registryEntryLookup, RecipeExporter exporter, ItemConvertible outputHorn, ItemConvertible inputHorn, @Nullable TagKey<Item> inputMaterial) {
        VortexHornRecipeJsonBuilder builder = VortexHornRecipeJsonBuilder.create(registryEntryLookup, RecipeCategory.TOOLS, outputHorn)
                .input('R', Items.BREEZE_ROD)
                .input('H', inputHorn);

        char corner = inputMaterial == null ? ' ' : 'I';
        if (inputMaterial != null) builder.input(corner, inputMaterial);

        builder
                .pattern( corner + "R" + corner)
                .pattern("RHR")
                .pattern( corner + "R" + corner)
                .criterion("has_material", generator.conditionsFromItem(Items.GOAT_HORN))
                .offerTo(exporter);

    }

    @Override
    public String getName() {
        return "UR Recipes";
    }
}
