package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public class UREquipmentProvider implements DataProvider {
    protected final FabricPackOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    private final Map<Identifier, EquipmentModelData> holder = new HashMap<>();

    public UREquipmentProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "uselessreptile/equipment");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach((key, val) -> {
                Path path = this.pathResolver.json(key);
                list.add(DataProvider.saveStable(writer, registryLookupFuture, EquipmentModelData.CODEC, val, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntries() {
        addSaddle(UREntities.WYVERN, Items.SADDLE, List.of("spikes_front"), List.of(Vec3.ZERO));
        addSaddle(UREntities.WYVERN, URItems.DUAL_SADDLE, List.of("spikes_front", "spikes_back"), List.of(Vec3.ZERO, new Vec3(0, 0, -0.8125f)));
        addSaddle(UREntities.LIGHTNING_CHASER, Items.SADDLE, List.of(), List.of(Vec3.ZERO));
        addSaddle(UREntities.MOLECLAW, Items.SADDLE, List.of(), List.of(Vec3.ZERO));

        addCommonArmor(UREntities.LIGHTNING_CHASER);
        addCommonArmor(UREntities.MOLECLAW);

        Identifier moleclawHelmet = UselessReptile.id("biscuit_roll/models/entity/moleclaw/helmet.geo.json");
        Identifier moleclawEmptyAnimation = UselessReptile.id("biscuit_roll/animations/entity/moleclaw/empty.animation.json");
        addEntry(UREntities.MOLECLAW, URItems.MOLECLAW_HELMET_IRON, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_iron.png"), moleclawHelmet, moleclawEmptyAnimation, true,  Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(UREntities.MOLECLAW, URItems.MOLECLAW_HELMET_GOLD, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_gold.png"), moleclawHelmet, moleclawEmptyAnimation, true,  Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(UREntities.MOLECLAW, URItems.MOLECLAW_HELMET_DIAMOND, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_diamond.png"), moleclawHelmet, moleclawEmptyAnimation, true,  Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(UREntities.MOLECLAW, URItems.MOLECLAW_HELMET_NETHERITE, UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_netherite.png"), moleclawHelmet, moleclawEmptyAnimation, true,  Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        holder.put(UselessReptile.id("empty"), new EquipmentModelData(Optional.empty(), Map.of()));
    }

    protected void addEntry(EntityType<? extends URDragonEntity> type, Item item, Identifier texture, Identifier model, Identifier animation, boolean translucent, Optional<List<String>> hidBones, DragonInventory.Slot slot, Optional<List<Vec3>> passengerPositions) {
        EquipmentModelData.Equipment equipmentModelData = new EquipmentModelData.Equipment(new ModelData(texture, model, animation, true, translucent), hidBones, slot, passengerPositions);
        Identifier id = EntityType.getKey(type);
        if (holder.containsKey(id)) holder.get(id).equipment().put(BuiltInRegistries.ITEM.getKey(item), equipmentModelData);
        else holder.put(id, new EquipmentModelData(Optional.empty(), Util.make(new HashMap<>(), map -> map.put(BuiltInRegistries.ITEM.getKey(item), equipmentModelData))));
    }

    protected void addSaddle(EntityType<? extends URDragonEntity> type, Item saddleItem, List<String> hidBones, List<Vec3> passengerPositions) {
        Identifier id = EntityType.getKey(type);
        Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/" + saddleItem.builtInRegistryHolder().key().identifier().getPath() + ".png");
        Identifier model = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/" + saddleItem.builtInRegistryHolder().key().identifier().getPath() + ".geo.json");
        Identifier animation = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/animations/entity/" + id.getPath() + "/" + saddleItem.builtInRegistryHolder().key().identifier().getPath() + ".animation.json");
        addEntry(type, saddleItem, texture, model, animation, false, hidBones.isEmpty() ? Optional.empty() : Optional.of(hidBones), DragonInventory.Slot.SADDLE, Optional.of(passengerPositions));
    }

    protected void addCommonArmor(EntityType<? extends URDragonEntity> type) {
        Identifier id = EntityType.getKey(type);
        Identifier textureIron = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_iron.png");
        Identifier textureGold = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_gold.png");
        Identifier textureDiamond = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_diamond.png");
        Identifier textureNetherite = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_netherite.png");
        Identifier modelHelmet = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/helmet.geo.json");
        Identifier modelChestplate = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/chestplate.geo.json");
        Identifier modelTailArmor = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/tail_armor.geo.json");
        Identifier emptyAnimation = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/animations/entity/" + id.getPath() + "/empty.animation.json");
        addEntry(type, URItems.DRAGON_HELMET_IRON, textureIron, modelHelmet, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(type, URItems.DRAGON_HELMET_GOLD, textureGold, modelHelmet, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(type, URItems.DRAGON_HELMET_DIAMOND, textureDiamond, modelHelmet, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(type, URItems.DRAGON_HELMET_NETHERITE, textureNetherite, modelHelmet, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.HELMET, Optional.empty());
        addEntry(type, URItems.DRAGON_CHESTPLATE_IRON, textureIron, modelChestplate, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.CHESTPLATE, Optional.empty());
        addEntry(type, URItems.DRAGON_CHESTPLATE_GOLD, textureGold, modelChestplate, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.CHESTPLATE, Optional.empty());
        addEntry(type, URItems.DRAGON_CHESTPLATE_DIAMOND, textureDiamond, modelChestplate, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.CHESTPLATE, Optional.empty());
        addEntry(type, URItems.DRAGON_CHESTPLATE_NETHERITE, textureNetherite, modelChestplate, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.CHESTPLATE, Optional.empty());
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_IRON, textureIron, modelTailArmor, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.TAIL_ARMOR, Optional.empty());
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_GOLD, textureGold, modelTailArmor, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.TAIL_ARMOR, Optional.empty());
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_DIAMOND, textureDiamond, modelTailArmor, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.TAIL_ARMOR, Optional.empty());
        addEntry(type, URItems.DRAGON_TAIL_ARMOR_NETHERITE, textureNetherite, modelTailArmor, emptyAnimation, false, Optional.empty(), DragonInventory.Slot.TAIL_ARMOR, Optional.empty());
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Equipment";
    }
}
