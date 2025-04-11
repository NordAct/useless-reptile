package nordmods.uselessreptile.datagen.data.mod;

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
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UREquipmentProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final Map<Identifier, DragonEquipment> holder = new HashMap<>();

    public UREquipmentProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "ur_dragon_variant/equipment");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach((key, val) -> {
                Path path = this.pathResolver.resolveJson(key);
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, DragonEquipment.CODEC, val, path));
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

        Identifier moleclawHelmet = UselessReptile.id("entity/moleclaw/helmet");
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_IRON, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_iron.png"), moleclawHelmet, true);
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_GOLD, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_gold.png"), moleclawHelmet, true);
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_DIAMOND, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_diamond.png"), moleclawHelmet, true);
        addEntry(UREntities.MOLECLAW_ENTITY, URItems.MOLECLAW_HELMET_NETHERITE, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_netherite.png"), moleclawHelmet, true);
        holder.put(UselessReptile.id("empty"), new DragonEquipment(Optional.empty(), List.of()));
    }

    protected void addEntry(EntityType<? extends URDragonEntity> type, Item item, Identifier texture, Identifier model, boolean translucent) {
        DragonEquipment.Equipment equipmentModelData = new DragonEquipment.Equipment(Registries.ITEM.getId(item), new ModelData(texture, model, Optional.empty(), false, translucent));
        Identifier id = EntityType.getId(type);
        if (holder.containsKey(id)) holder.get(id).equipment().add(equipmentModelData);
        else holder.put(id, new DragonEquipment(Optional.empty(), new ArrayList<>(Collections.singleton(equipmentModelData))));
    }

    protected void addSaddle(EntityType<? extends URDragonEntity> type) {
        Identifier id = EntityType.getId(type);
        Identifier texture = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/saddle.png");
        Identifier model = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/saddle");
        addEntry(type, Items.SADDLE, texture, model, false);
    }

    protected void addCommonArmor(EntityType<? extends URDragonEntity> type) {
        Identifier id = EntityType.getId(type);
        Identifier textureIron = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_iron.png");
        Identifier textureGold = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_gold.png");
        Identifier textureDiamond = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_diamond.png");
        Identifier textureNetherite = Identifier.of(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_netherite.png");
        Identifier modelHelmet = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/helmet");
        Identifier modelChestplate = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/chestplate");
        Identifier modelTailArmor = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/tail_armor");
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
        return "Dragon Equipment";
    }
}
