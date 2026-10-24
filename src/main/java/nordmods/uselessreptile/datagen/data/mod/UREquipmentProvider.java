package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public class UREquipmentProvider extends FabricDynamicRegistryProvider {
    private static final Identifier EMPTY_ANIMATION = UselessReptile.id("biscuit_roll/animations/entity/empty.animation.json");
    public static final ResourceKey<EquipmentModelData> WYVERN = ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, UselessReptile.id("wyvern/default"));
    public static final ResourceKey<EquipmentModelData> LIGHTNING_CHASER = ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, UselessReptile.id("lightning_chaser/default"));
    public static final ResourceKey<EquipmentModelData> MOLECLAW = ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, UselessReptile.id("moleclaw/default"));
    public static final ResourceKey<EquipmentModelData> MAGMAMUNCHER = ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, UselessReptile.id("magmamuncher/default"));
    public static final ResourceKey<EquipmentModelData> RIVER_PIKEHORN = ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, UselessReptile.id("river_pikehorn/default"));
    public static final ResourceKey<EquipmentModelData> EMPTY = ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, UselessReptile.id("empty"));
    public UREquipmentProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void configure(HolderLookup.@NonNull Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT));
    }

    public static void register(BootstrapContext<EquipmentModelData> bootstrapContext) {
        bootstrapContext.register(EMPTY, new EquipmentModelData(Optional.empty(), Map.of()));
        bootstrapContext.register(MAGMAMUNCHER, new EquipmentModelData(Optional.empty(), Map.of()));
        bootstrapContext.register(RIVER_PIKEHORN, new EquipmentModelData(Optional.empty(), Map.of()));

        EquipmentModelData.Builder builder = EquipmentModelData.builder();
        addSaddle(builder, UREntities.WYVERN, Items.SADDLE, List.of("spikes_front"), 1);
        addSaddle(builder, UREntities.WYVERN, URItems.DUAL_SADDLE, List.of("spikes_front", "spikes_back"), 2);
        bootstrapContext.register(WYVERN, builder.build());

        builder = EquipmentModelData.builder();
        addSaddle(builder, UREntities.LIGHTNING_CHASER, Items.SADDLE, List.of(), 1);
        addCommonArmor(builder, UREntities.LIGHTNING_CHASER);
        bootstrapContext.register(LIGHTNING_CHASER, builder.build());

        builder = EquipmentModelData.builder();
        addSaddle(builder, UREntities.MOLECLAW, Items.SADDLE, List.of(), 1);
        addCommonArmor(builder, UREntities.MOLECLAW);
        Identifier moleclawHelmet = UselessReptile.id("biscuit_roll/models/entity/moleclaw/helmet.geo.json");
        builder.addEquipment(URItems.MOLECLAW_HELMET_COPPER, makeArmor(UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_copper.png"), moleclawHelmet, EMPTY_ANIMATION, true, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.MOLECLAW_HELMET_IRON, makeArmor(UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_iron.png"), moleclawHelmet, EMPTY_ANIMATION, true, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.MOLECLAW_HELMET_GOLD, makeArmor(UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_gold.png"), moleclawHelmet, EMPTY_ANIMATION, true, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.MOLECLAW_HELMET_DIAMOND, makeArmor(UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_diamond.png"), moleclawHelmet, EMPTY_ANIMATION, true, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.MOLECLAW_HELMET_NETHERITE, makeArmor(UselessReptile.id("textures/entity/moleclaw/moleclaw_helmet_netherite.png"), moleclawHelmet, EMPTY_ANIMATION, true, DragonInventory.Slot.HELMET));
        bootstrapContext.register(MOLECLAW, builder.build());
    }

    protected static void addSaddle(EquipmentModelData.Builder builder, EntityType<? extends URDragonEntity> type, Item saddleItem, List<String> hidBones, int maxPassengers) {
        Identifier id = EntityType.getKey(type);
        builder.addEquipment(
                        saddleItem,
                        EquipmentModelData.Equipment.builder(DragonInventory.Slot.SADDLE)
                                .addHidBones(hidBones)
                                .setMaxPassengers(maxPassengers)
                                .setModel(Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/" + saddleItem.builtInRegistryHolder().key().identifier().getPath() + ".geo.json"))
                                .setTexture(Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/" + saddleItem.builtInRegistryHolder().key().identifier().getPath() + ".png"))
                                .setAnimation(Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/animations/entity/" + id.getPath() + "/" + saddleItem.builtInRegistryHolder().key().identifier().getPath() + ".animation.json"))
                                .build()
                );
    }

    protected static void addCommonArmor(EquipmentModelData.Builder builder, EntityType<? extends URDragonEntity> type) {
        Identifier id = EntityType.getKey(type);
        Identifier textureCopper = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_copper.png");
        Identifier textureIron = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_iron.png");
        Identifier textureGold = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_gold.png");
        Identifier textureDiamond = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_diamond.png");
        Identifier textureNetherite = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "/armor_netherite.png");
        Identifier modelHelmet = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/helmet.geo.json");
        Identifier modelChestplate = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/chestplate.geo.json");
        Identifier modelTailArmor = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/tail_armor.geo.json");
        builder.addEquipment(URItems.DRAGON_HELMET_COPPER, makeArmor(textureCopper, modelHelmet, EMPTY_ANIMATION, false, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.DRAGON_HELMET_IRON, makeArmor(textureIron, modelHelmet, EMPTY_ANIMATION, false, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.DRAGON_HELMET_GOLD, makeArmor(textureGold, modelHelmet, EMPTY_ANIMATION, false, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.DRAGON_HELMET_DIAMOND, makeArmor(textureDiamond, modelHelmet, EMPTY_ANIMATION, false, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.DRAGON_HELMET_NETHERITE, makeArmor(textureNetherite, modelHelmet, EMPTY_ANIMATION, false, DragonInventory.Slot.HELMET));
        builder.addEquipment(URItems.DRAGON_CHESTPLATE_COPPER, makeArmor(textureCopper, modelChestplate, EMPTY_ANIMATION, false, DragonInventory.Slot.CHESTPLATE));
        builder.addEquipment(URItems.DRAGON_CHESTPLATE_IRON, makeArmor(textureIron, modelChestplate, EMPTY_ANIMATION, false, DragonInventory.Slot.CHESTPLATE));
        builder.addEquipment(URItems.DRAGON_CHESTPLATE_GOLD, makeArmor(textureGold, modelChestplate, EMPTY_ANIMATION, false, DragonInventory.Slot.CHESTPLATE));
        builder.addEquipment(URItems.DRAGON_CHESTPLATE_DIAMOND, makeArmor(textureDiamond, modelChestplate, EMPTY_ANIMATION, false, DragonInventory.Slot.CHESTPLATE));
        builder.addEquipment(URItems.DRAGON_CHESTPLATE_NETHERITE, makeArmor(textureNetherite, modelChestplate, EMPTY_ANIMATION, false, DragonInventory.Slot.CHESTPLATE));
        builder.addEquipment(URItems.DRAGON_TAIL_ARMOR_COPPER, makeArmor(textureCopper, modelTailArmor, EMPTY_ANIMATION, false, DragonInventory.Slot.TAIL_ARMOR));
        builder.addEquipment(URItems.DRAGON_TAIL_ARMOR_IRON, makeArmor(textureIron, modelTailArmor, EMPTY_ANIMATION, false, DragonInventory.Slot.TAIL_ARMOR));
        builder.addEquipment(URItems.DRAGON_TAIL_ARMOR_GOLD, makeArmor(textureGold, modelTailArmor, EMPTY_ANIMATION, false, DragonInventory.Slot.TAIL_ARMOR));
        builder.addEquipment(URItems.DRAGON_TAIL_ARMOR_DIAMOND, makeArmor(textureDiamond, modelTailArmor, EMPTY_ANIMATION, false, DragonInventory.Slot.TAIL_ARMOR));
        builder.addEquipment(URItems.DRAGON_TAIL_ARMOR_NETHERITE, makeArmor(textureNetherite, modelTailArmor, EMPTY_ANIMATION, false, DragonInventory.Slot.TAIL_ARMOR));
    }

    protected static EquipmentModelData.Equipment makeArmor(Identifier texture, Identifier model, Identifier animation, boolean translucent, DragonInventory.Slot slot, String... hidBones) {
        return EquipmentModelData.Equipment.builder(slot)
                .setTexture(texture)
                .setModel(model)
                .setAnimation(animation)
                .setTranslucent(translucent)
                .addHidBones(hidBones)
                .build();
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Equipment";
    }
}
