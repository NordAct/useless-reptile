package nordmods.uselessreptile.datagen.assets;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;
import nordmods.uselessreptile.client.util.model_data.base.ModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UREquipmentModelDataProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public UREquipmentModelDataProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "dragon_equipment_model_data");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            EquipmentModelData.getEntries().forEach(entry -> {
                String dragon = entry.getKey();
                List<EquipmentModelData> equipmentModelData = entry.getValue();
                Path path = this.pathResolver.resolveJson(UselessReptile.id(dragon));
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, EquipmentModelData.CODEC.listOf(), equipmentModelData, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntries() {
        addSaddle(UREntities.WYVERN_ENTITY);
        addSaddle(UREntities.LIGHTNING_CHASER_ENTITY);
        addSaddle(UREntities.MOLECLAW_ENTITY);

        addCommonArmor(UREntities.LIGHTNING_CHASER_ENTITY);
        addCommonArmor(UREntities.MOLECLAW_ENTITY);

        Identifier moleclawHelmet = UselessReptile.id("geo/entity/moleclaw/helmet.geo.json");
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_IRON, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_iron.png"), moleclawHelmet, true);
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_GOLD, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_gold.png"), moleclawHelmet, true);
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_DIAMOND, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_diamond.png"), moleclawHelmet, true);
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_NETHERITE, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_netherite.png"), moleclawHelmet, true);
    }

    protected void addEntry(EntityType<? extends URDragonEntity> type, Item item, Identifier texture, Identifier model, boolean translucent) {
        EquipmentModelData equipmentModelData = new EquipmentModelData(Registries.ITEM.getId(item), new ModelData(texture, Optional.of(model), Optional.empty(), false, translucent));
        EquipmentModelData.add(EntityType.getId(type).getPath(), equipmentModelData);
    }

    protected void addSaddle(EntityType<? extends URDragonEntity> type) {
        Identifier id = EntityType.getId(type);
        Identifier texture = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/saddle.png");
        Identifier model = Identifier.of(id.getNamespace(), "geo/entity/" + id.getPath() + "/saddle.geo.json");
        addEntry(type, Items.SADDLE, texture, model, false);
    }

    protected void addCommonArmor(EntityType<? extends URDragonEntity> type) {
        Identifier id = EntityType.getId(type);
        Identifier textureIron = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_iron.png");
        Identifier textureGold = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_gold.png");
        Identifier textureDiamond = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_diamond.png");
        Identifier textureNetherite = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_netherite.png");
        Identifier modelHelmet = Identifier.of(id.getNamespace(), "geo/entity/" + id.getPath() + "/helmet.geo.json");
        Identifier modelChestplate = Identifier.of(id.getNamespace(), "geo/entity/" + id.getPath() + "/chestplate.geo.json");
        Identifier modelTailArmor = Identifier.of(id.getNamespace(), "geo/entity/" + id.getPath() + "/tail_armor.geo.json");
        addEntry(type, URItems.DRAGON_HELMET_IRON, textureIron, modelHelmet, false);
        addEntry(type, URItems.DRAGON_HELMET_GOLD, textureGold, modelHelmet, false);
        addEntry(type, URItems.DRAGON_HELMET_DIAMOND, textureDiamond, modelHelmet, false);
        addEntry(type, URItems.DRAGON_HELMET_NETHERITE, textureNetherite, modelHelmet, false);
        addEntry(type, URItems.DRAGON_CHESTPLATE_IRON, textureIron, modelChestplate, false);
        addEntry(type, URItems.DRAGON_CHESTPLATE_GOLD, textureGold, modelChestplate, false);
        addEntry(type, URItems.DRAGON_CHESTPLATE_DIAMOND, textureDiamond, modelChestplate, false);
        addEntry(type, URItems.DRAGON_CHESTPLATE_NETHERITE, textureNetherite, modelChestplate, false);
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_IRON, textureIron, modelTailArmor, false);
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_GOLD, textureGold, modelTailArmor, false);
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_DIAMOND, textureDiamond, modelTailArmor, false);
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_NETHERITE, textureNetherite, modelTailArmor, false);
    }

    @Override
    public String getName() {
        return "Equipment Model Data";
    }
}
