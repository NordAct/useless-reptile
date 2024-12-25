package nordmods.uselessreptile.datagen.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URItems;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class URModelProvider extends FabricModelProvider {
    public URModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(URItems.WYVERN_SPAWN_EGG, item("template_spawn_egg"));
        itemModelGenerator.register(URItems.LIGHTNING_CHASER_SPAWN_EGG, item("template_spawn_egg"));
        itemModelGenerator.register(URItems.MOLECLAW_SPAWN_EGG, item("template_spawn_egg"));
        itemModelGenerator.register(URItems.RIVER_PIKEHORN_SPAWN_EGG, item("template_spawn_egg"));

        itemModelGenerator.register(URItems.WYVERN_SKIN, Models.GENERATED);

        generateVortexHorn(itemModelGenerator.writer, URItems.VORTEX_HORN);
        generateVortexHorn(itemModelGenerator.writer, URItems.IRON_VORTEX_HORN);
        generateVortexHorn(itemModelGenerator.writer, URItems.GOLD_VORTEX_HORN);
        generateVortexHorn(itemModelGenerator.writer, URItems.DIAMOND_VORTEX_HORN);
        generateVortexHorn(itemModelGenerator.writer, URItems.NETHERITE_VORTEX_HORN);

        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_CHESTPLATE_IRON, UselessReptile.id("item/armor/dragon/armor_iron_body"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_CHESTPLATE_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_body"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_CHESTPLATE_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_body"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_CHESTPLATE_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_body"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_TAIL_ARMOR_IRON, UselessReptile.id("item/armor/dragon/armor_iron_tail"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_TAIL_ARMOR_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_tail"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_TAIL_ARMOR_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_tail"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_TAIL_ARMOR_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_tail"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_HELMET_IRON, UselessReptile.id("item/armor/dragon/armor_iron_head"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_HELMET_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_head"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_HELMET_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_head"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.DRAGON_HELMET_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_head"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.MOLECLAW_HELMET_IRON, UselessReptile.id("item/armor/dragon/armor_iron_head_moleclaw"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.MOLECLAW_HELMET_GOLD, UselessReptile.id("item/armor/dragon/armor_gold_head_moleclaw"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.MOLECLAW_HELMET_DIAMOND, UselessReptile.id("item/armor/dragon/armor_diamond_head_moleclaw"));
        registerDragonArmorModel(itemModelGenerator.writer, URItems.MOLECLAW_HELMET_NETHERITE, UselessReptile.id("item/armor/dragon/armor_netherite_head_moleclaw"));
    }

    protected static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    protected JsonObject generateDragonArmor(Item item, Map<TextureKey, Identifier> textureMap) {
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

        Identifier itemID = ModelIds.getItemModelId(item);
        JsonObject root = Models.GENERATED.createJson(Identifier.of(itemID.getNamespace(), itemID.getPath()), textureMap);
        root.add("display", display);

        return root;
    }

    protected void registerDragonArmorModel(BiConsumer<Identifier, Supplier<JsonElement>> writer, Item item, Identifier texture) {
        Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(texture), writer, ((id, textures) -> generateDragonArmor(item, textures)));
    }

    protected JsonObject generateVortexHorn(Item item, Map<TextureKey, Identifier> textureMap) {
        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        Identifier itemModelID = ModelIds.getItemModelId(item);
        JsonObject root = item("goat_horn", TextureKey.LAYER0).createJson(Identifier.of(itemModelID.getNamespace(), itemModelID.getPath()), textureMap);

        JsonObject override = new JsonObject();
        JsonObject predicate = new JsonObject();
        predicate.add("tooting", new JsonPrimitive(1));
        override.add("predicate", predicate);
        override.add("model", new JsonPrimitive( itemID.getNamespace() + ":item/tooting_" + itemID.getPath()));

        JsonArray overrides = new JsonArray();
        overrides.add(override);
        root.add("overrides", overrides);
        return root;
    }

    protected JsonObject generateTootingVortexHorn(Item item, Map<TextureKey, Identifier> textureMap) {
        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        return item("tooting_goat_horn", TextureKey.LAYER0).createJson(Identifier.of(itemID.getNamespace(), "item/tooting_" + itemID.getPath()), textureMap);
    }

    protected void generateVortexHorn(BiConsumer<Identifier, Supplier<JsonElement>> writer, Item item) {
        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        Identifier texture = Identifier.of(itemID.getNamespace(), "item/vortex_horn/" + itemID.getPath());
        Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(texture), writer, ((id, textures) -> generateVortexHorn(item, textures)));
        generateTootingVortexHorn(writer, item);
    }

    protected void generateTootingVortexHorn(BiConsumer<Identifier, Supplier<JsonElement>> writer, Item item) {
        Identifier itemID = item.getRegistryEntry().registryKey().getValue();
        Identifier texture = Identifier.of(itemID.getNamespace(), "item/vortex_horn/" + itemID.getPath());
        Models.GENERATED.upload(Identifier.of(itemID.getNamespace(), "item/tooting_" + itemID.getPath()), TextureMap.layer0(texture), writer, ((id, textures) -> generateTootingVortexHorn(item, textures)));
    }
}
