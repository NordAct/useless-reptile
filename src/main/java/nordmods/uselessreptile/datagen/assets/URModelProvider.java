package nordmods.uselessreptile.datagen.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.item.FluteItem;
import nordmods.uselessreptile.common.item.component.FluteComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class URModelProvider extends FabricModelProvider {
    public URModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Variant modelVariant = new Variant(Identifier.parse("block/netherrack"));
        blockStateModelGenerator.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(
                                URBlocks.DEPLETED_MAGMA,
                                BlockModelGenerators.variants(
                                        modelVariant,
                                        modelVariant.with(BlockModelGenerators.X_ROT_90),
                                        modelVariant.with(BlockModelGenerators.X_ROT_180),
                                        modelVariant.with(BlockModelGenerators.X_ROT_270),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_90),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_90.then(BlockModelGenerators.X_ROT_90)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_90.then(BlockModelGenerators.X_ROT_180)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_90.then(BlockModelGenerators.X_ROT_270)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_180),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_180.then(BlockModelGenerators.X_ROT_90)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_180.then(BlockModelGenerators.X_ROT_180)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_180.then(BlockModelGenerators.X_ROT_270)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_270),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_270.then(BlockModelGenerators.X_ROT_90)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_270.then(BlockModelGenerators.X_ROT_180)),
                                        modelVariant.with(BlockModelGenerators.Y_ROT_270.then(BlockModelGenerators.X_ROT_270))
                                )
                        )
                );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(URItems.WYVERN_SKIN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.DUAL_SADDLE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.VARIANT_CHANGING_ORB, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.WYVERN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.LIGHTNING_CHASER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.MOLECLAW_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.RIVER_PIKEHORN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(URItems.MAGMAMUNCHER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);

        registerVortexHorn(itemModelGenerator, URItems.VORTEX_HORN);
        registerVortexHorn(itemModelGenerator, URItems.IRON_VORTEX_HORN);
        registerVortexHorn(itemModelGenerator, URItems.GOLD_VORTEX_HORN);
        registerVortexHorn(itemModelGenerator, URItems.DIAMOND_VORTEX_HORN);
        registerVortexHorn(itemModelGenerator, URItems.NETHERITE_VORTEX_HORN);

        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_CHESTPLATE_IRON, UselessReptile.id("item/armor/dragon/armor_iron_body"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_CHESTPLATE_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_body"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_CHESTPLATE_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_body"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_CHESTPLATE_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_body"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_TAIL_ARMOR_IRON, UselessReptile.id("item/armor/dragon/armor_iron_tail"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_TAIL_ARMOR_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_tail"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_TAIL_ARMOR_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_tail"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_TAIL_ARMOR_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_tail"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_HELMET_IRON, UselessReptile.id("item/armor/dragon/armor_iron_head"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_HELMET_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_head"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_HELMET_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_head"));
        registerDragonArmorModel(itemModelGenerator, URItems.DRAGON_HELMET_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_head"));
        registerDragonArmorModel(itemModelGenerator, URItems.MOLECLAW_HELMET_IRON, UselessReptile.id("item/armor/dragon/armor_iron_head_moleclaw"));
        registerDragonArmorModel(itemModelGenerator, URItems.MOLECLAW_HELMET_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_head_moleclaw"));
        registerDragonArmorModel(itemModelGenerator, URItems.MOLECLAW_HELMET_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_head_moleclaw"));
        registerDragonArmorModel(itemModelGenerator, URItems.MOLECLAW_HELMET_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_head_moleclaw"));

        registerFlute(itemModelGenerator, URItems.FLUTE);
    }

    protected static ModelTemplate item(String parent, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(Identifier.parse("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    protected void generateDragonArmor(Item item, Identifier texture,  BiConsumer<Identifier, ModelInstance> modelCollector) {
        JsonArray translation = new JsonArray();
        translation.add(0);
        translation.add(-0.4);
        translation.add(0);

        JsonArray scale = new JsonArray();
        scale.add(0.55);
        scale.add(0.55);
        scale.add(0.55);

        JsonObject thirdpersonRighthand = new JsonObject();
        thirdpersonRighthand.add("translation", translation);
        thirdpersonRighthand.add("scale", scale);

        JsonObject display = new JsonObject();
        display.add("thirdperson_righthand", thirdpersonRighthand);

        modelCollector.accept(ModelLocationUtils.getModelLocation(item), () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("layer0", texture.toString());
            jsonObject.add("textures", jsonObject2);
            jsonObject.add("display", display);
            return jsonObject;
        });
    }

    protected void registerDragonArmorModel(ItemModelGenerators itemModelGenerator, Item item, Identifier texture) {
        generateDragonArmor(item, texture, itemModelGenerator.modelOutput);
        itemModelGenerator.declareCustomModelItem(item);
    }

    protected void generateVotexHorn(Item item, BiConsumer<Identifier, ModelInstance> modelCollector) {
        JsonArray translation;
        JsonArray rotation;
        JsonArray scale;

        JsonObject thirdpersonRighthand = new JsonObject();
        JsonObject thirdpersonLefthand = new JsonObject();
        JsonObject firstpersonRighthand = new JsonObject();
        JsonObject firstpersonLefthand = new JsonObject();
        JsonObject display = new JsonObject();

        translation = updateValues(0, 3, 1);
        rotation = updateValues(0, 180, 0);
        scale = updateValues( 0.55, 0.55, 0.55);
        thirdpersonRighthand.add("translation", translation);
        thirdpersonRighthand.add("scale", scale);
        thirdpersonRighthand.add("rotation", rotation);

        translation = updateValues( 0, 3, 1);
        rotation = updateValues( 0, 0, 0);
        scale = updateValues( 0.55, 0.55, 0.55);
        thirdpersonLefthand.add("translation", translation);
        thirdpersonLefthand.add("scale", scale);
        thirdpersonLefthand.add("rotation", rotation);

        translation = updateValues( 1.13, 3.2, 1.13);
        rotation = updateValues( 0, -90, 25);
        scale = updateValues( 0.68, 0.68, 0.68);
        firstpersonRighthand.add("translation", translation);
        firstpersonRighthand.add("scale", scale);
        firstpersonRighthand.add("rotation", rotation);

        translation = updateValues( 1.13, 3.2, 1.13);
        rotation = updateValues( 0, -90, 25);
        scale = updateValues( 0.68, 0.68, 0.68);
        firstpersonLefthand.add("translation", translation);
        firstpersonLefthand.add("scale", scale);
        firstpersonLefthand.add("rotation", rotation);

        display.add("thirdperson_righthand", thirdpersonRighthand);
        display.add("thirdperson_lefthand", thirdpersonLefthand);
        display.add("firstperson_righthand", firstpersonRighthand);
        display.add("firstperson_lefthand", firstpersonLefthand);

        Identifier itemID = item.builtInRegistryHolder().key().identifier();
        modelCollector.accept(ModelLocationUtils.getModelLocation(item), () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("layer0", Identifier.fromNamespaceAndPath(itemID.getNamespace(), "item/vortex_horn/" + itemID.getPath()).toString());
            jsonObject.add("textures", jsonObject2);
            jsonObject.add("display", display);
            return jsonObject;
        });
    }

    protected void generateTootingVotexHorn(Item item, BiConsumer<Identifier, ModelInstance> modelCollector) {
        JsonArray translation;
        JsonArray rotation;
        JsonArray scale;

        JsonObject thirdpersonRighthand = new JsonObject();
        JsonObject thirdpersonLefthand = new JsonObject();
        JsonObject firstpersonRighthand = new JsonObject();
        JsonObject firstpersonLefthand = new JsonObject();
        JsonObject display = new JsonObject();

        translation = updateValues(-1, 2, 2);
        rotation = updateValues(0, -125, 0);
        scale = updateValues( 0.5, 0.5, 0.5);
        thirdpersonRighthand.add("translation", translation);
        thirdpersonRighthand.add("scale", scale);
        thirdpersonRighthand.add("rotation", rotation);

        translation = updateValues( -1, 2, 2);
        rotation = updateValues( 0, 55, 0);
        scale = updateValues( 0.5, 0.5, 0.5);
        thirdpersonLefthand.add("translation", translation);
        thirdpersonLefthand.add("scale", scale);
        thirdpersonLefthand.add("rotation", rotation);

        translation = updateValues( -1, -2.5, -7.5);
        rotation = updateValues( 0, -55, 5);
        firstpersonRighthand.add("translation", translation);
        firstpersonRighthand.add("rotation", rotation);

        translation = updateValues( 0  , -2.5, -7.5);
        rotation = updateValues( 0, 155, 5);
        firstpersonLefthand.add("translation", translation);
        firstpersonLefthand.add("rotation", rotation);

        display.add("thirdperson_righthand", thirdpersonRighthand);
        display.add("thirdperson_lefthand", thirdpersonLefthand);
        display.add("firstperson_righthand", firstpersonRighthand);
        display.add("firstperson_lefthand", firstpersonLefthand);

        Identifier itemID = item.builtInRegistryHolder().key().identifier();
        modelCollector.accept(Identifier.fromNamespaceAndPath(itemID.getNamespace(), "item/tooting_" + itemID.getPath()), () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("layer0", Identifier.fromNamespaceAndPath(itemID.getNamespace(), "item/vortex_horn/" + itemID.getPath()).toString());
            jsonObject.add("textures", jsonObject2);
            jsonObject.add("display", display);
            return jsonObject;
        });
    }

    protected void registerVortexHorn(ItemModelGenerators itemModelGenerator, Item item) {
        generateVotexHorn(item, itemModelGenerator.modelOutput);
        generateTootingVotexHorn(item, itemModelGenerator.modelOutput);

        Identifier itemID = item.builtInRegistryHolder().key().identifier();
        ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(itemID.getNamespace(), "item/" + itemID.getPath()));
        ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(itemID.getNamespace(), "item/tooting_" + itemID.getPath()));
        itemModelGenerator.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), unbaked2, unbaked);
    }

    private JsonArray updateValues(double x, double y, double z) {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    protected void registerFlute(ItemModelGenerators itemModelGenerator, Item item) {
        List<SelectItemModel.SwitchCase<FluteComponent>> entries = new ArrayList<>();
        for (Map.Entry<String, Tuple<SoundEvent, FluteItem.FluteAction>> entry :FluteItem.FLUTE_MODES.entrySet()) {
            String mode = entry.getKey();
            ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModelGenerator.createFlatItemModel(item, "/" + mode, ModelTemplates.FLAT_HANDHELD_ITEM));
            entries.add(new SelectItemModel.SwitchCase<>(List.of(new FluteComponent(mode)), model));
        }

        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.select(new ComponentContents<>(URItems.FLUTE_MODE_COMPONENT), entries));
    }
}
