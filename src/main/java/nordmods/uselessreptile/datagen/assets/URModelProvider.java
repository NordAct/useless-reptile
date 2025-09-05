package nordmods.uselessreptile.datagen.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.item_property.FluteModeProperty;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.item.FluteItem;

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
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        ModelVariant modelVariant = new ModelVariant(Identifier.of("block/netherrack"));
        blockStateModelGenerator.blockStateCollector
                .accept(
                        VariantsBlockModelDefinitionCreator.of(
                                URBlocks.DEPLETED_MAGMA,
                                BlockStateModelGenerator.createWeightedVariant(
                                        modelVariant,
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_X_90),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_X_180),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_X_270),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_90),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_90.then(BlockStateModelGenerator.ROTATE_X_90)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_90.then(BlockStateModelGenerator.ROTATE_X_180)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_90.then(BlockStateModelGenerator.ROTATE_X_270)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_180),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_180.then(BlockStateModelGenerator.ROTATE_X_90)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_180.then(BlockStateModelGenerator.ROTATE_X_180)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_180.then(BlockStateModelGenerator.ROTATE_X_270)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_270),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_270.then(BlockStateModelGenerator.ROTATE_X_90)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_270.then(BlockStateModelGenerator.ROTATE_X_180)),
                                        modelVariant.with(BlockStateModelGenerator.ROTATE_Y_270.then(BlockStateModelGenerator.ROTATE_X_270))
                                )
                        )
                );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(URItems.WYVERN_SKIN, Models.GENERATED);
        itemModelGenerator.register(URItems.WYVERN_SPAWN_EGG, Models.GENERATED);
        itemModelGenerator.register(URItems.LIGHTNING_CHASER_SPAWN_EGG, Models.GENERATED);
        itemModelGenerator.register(URItems.MOLECLAW_SPAWN_EGG, Models.GENERATED);
        itemModelGenerator.register(URItems.RIVER_PIKEHORN_SPAWN_EGG, Models.GENERATED);
        itemModelGenerator.register(URItems.MAGMAMUNCHER_SPAWN_EGG, Models.GENERATED);

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

    protected static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    protected void generateDragonArmor(Item item, Identifier texture,  BiConsumer<Identifier, ModelSupplier> modelCollector) {
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

        modelCollector.accept(ModelIds.getItemModelId(item), () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("layer0", texture.toString());
            jsonObject.add("textures", jsonObject2);
            jsonObject.add("display", display);
            return jsonObject;
        });
    }

    protected void registerDragonArmorModel(ItemModelGenerator itemModelGenerator, Item item, Identifier texture) {
        generateDragonArmor(item, texture, itemModelGenerator.modelCollector);
        itemModelGenerator.register(item);
    }

    protected void generateVotexHorn(Item item, BiConsumer<Identifier, ModelSupplier> modelCollector) {
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

        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        modelCollector.accept(ModelIds.getItemModelId(item), () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("layer0", Identifier.of(itemID.getNamespace(), "item/vortex_horn/" + itemID.getPath()).toString());
            jsonObject.add("textures", jsonObject2);
            jsonObject.add("display", display);
            return jsonObject;
        });
    }

    protected void generateTootingVotexHorn(Item item, BiConsumer<Identifier, ModelSupplier> modelCollector) {
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

        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        modelCollector.accept(Identifier.of(itemID.getNamespace(), "item/tooting_" + itemID.getPath()), () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("layer0", Identifier.of(itemID.getNamespace(), "item/vortex_horn/" + itemID.getPath()).toString());
            jsonObject.add("textures", jsonObject2);
            jsonObject.add("display", display);
            return jsonObject;
        });
    }

    protected void registerVortexHorn(ItemModelGenerator itemModelGenerator, Item item) {
        generateVotexHorn(item, itemModelGenerator.modelCollector);
        generateTootingVotexHorn(item, itemModelGenerator.modelCollector);

        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        ItemModel.Unbaked unbaked = ItemModels.basic(Identifier.of(itemID.getNamespace(), "item/" + itemID.getPath()));
        ItemModel.Unbaked unbaked2 = ItemModels.basic(Identifier.of(itemID.getNamespace(), "item/tooting_" + itemID.getPath()));
        itemModelGenerator.registerCondition(item, ItemModels.usingItemProperty(), unbaked2, unbaked);
    }

    private JsonArray updateValues(double x, double y, double z) {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    protected void registerFlute(ItemModelGenerator itemModelGenerator, Item item) {
        int i = 0;
        List<RangeDispatchItemModel.Entry> entries = new ArrayList<>();
        for (Map.Entry<String, Pair<SoundEvent, FluteItem.FluteAction>> entry :FluteItem.FLUTE_MODES.entrySet()) {
            ItemModel.Unbaked mode = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_" + entry.getKey(), Models.HANDHELD));
            entries.add(ItemModels.rangeDispatchEntry(mode, i));
            i++;
        }

        itemModelGenerator.output.accept(item, ItemModels.rangeDispatch(new FluteModeProperty(), entries.getFirst().model(), entries));
    }
}
