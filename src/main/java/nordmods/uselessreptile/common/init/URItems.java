package nordmods.uselessreptile.common.init;

import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.item.DragonEquipmentItem;
import nordmods.uselessreptile.common.item.FluteItem;
import nordmods.uselessreptile.common.item.VortexHornItem;
import nordmods.uselessreptile.common.item.component.FluteComponent;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class URItems {
    public static final DataComponentType<FluteComponent> FLUTE_MODE_COMPONENT = registerComponent("flute_mode",
            builder -> builder.persistent(FluteComponent.CODEC).networkSynchronized(FluteComponent.PACKET_CODEC));
    public static final DataComponentType<URDragonDataStorageComponent> DRAGON_STORAGE_COMPONENT = registerComponent("dragon_storage",
            builder -> builder.persistent(URDragonDataStorageComponent.CODEC).networkSynchronized(URDragonDataStorageComponent.PACKET_CODEC));
    public static final DataComponentType<VortexHornCapacityComponent> VORTEX_HORN_CAPACITY_COMPONENT = registerComponent("vortex_horn_capacity",
            builder -> builder.persistent(VortexHornCapacityComponent.CODEC).networkSynchronized(VortexHornCapacityComponent.PACKET_CODEC));

    public static final Item WYVERN_SKIN = registerItem("wyvern_skin", Item::new);

    public static final Item DRAGON_HELMET_IRON = registerItem("dragon_helmet_iron", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 2, 0, 0, properties));
    public static final Item DRAGON_HELMET_GOLD = registerItem("dragon_helmet_gold", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 3, 0, 0, properties));
    public static final Item DRAGON_HELMET_DIAMOND = registerItem("dragon_helmet_diamond", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 5, 1, 0, properties));
    public static final Item DRAGON_HELMET_NETHERITE = registerItem("dragon_helmet_netherite", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 7, 2, 1, properties));

    public static final Item DRAGON_CHESTPLATE_IRON = registerItem("dragon_chestplate_iron", properties -> createDragonArmorItem(EquipmentSlot.CHEST, 4, 0, 0, properties));
    public static final Item DRAGON_CHESTPLATE_GOLD = registerItem("dragon_chestplate_gold", properties -> createDragonArmorItem(EquipmentSlot.CHEST, 5, 0, 0, properties));
    public static final Item DRAGON_CHESTPLATE_DIAMOND = registerItem("dragon_chestplate_diamond", properties -> createDragonArmorItem(EquipmentSlot.CHEST, 7, 1, 0, properties));
    public static final Item DRAGON_CHESTPLATE_NETHERITE = registerItem("dragon_chestplate_netherite", properties -> createDragonArmorItem(EquipmentSlot.CHEST, 10, 2, 1, properties));

    public static final Item DRAGON_TAIL_ARMOR_IRON = registerItem("dragon_tail_armor_iron", properties -> createDragonArmorItem(EquipmentSlot.LEGS, 1, 0, 0, properties));
    public static final Item DRAGON_TAIL_ARMOR_GOLD = registerItem("dragon_tail_armor_gold", properties -> createDragonArmorItem(EquipmentSlot.LEGS, 2, 0, 0, properties));
    public static final Item DRAGON_TAIL_ARMOR_DIAMOND = registerItem("dragon_tail_armor_diamond", properties -> createDragonArmorItem(EquipmentSlot.LEGS, 4, 1, 0, properties));
    public static final Item DRAGON_TAIL_ARMOR_NETHERITE = registerItem("dragon_tail_armor_netherite", properties -> createDragonArmorItem(EquipmentSlot.LEGS, 6, 2, 1, properties));

    public static final Item MOLECLAW_HELMET_IRON = registerItem("moleclaw_helmet_iron", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 2, 0, 0, properties));
    public static final Item MOLECLAW_HELMET_GOLD = registerItem("moleclaw_helmet_gold", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 3, 0, 0, properties));
    public static final Item MOLECLAW_HELMET_DIAMOND = registerItem("moleclaw_helmet_diamond", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 5, 1, 0, properties));
    public static final Item MOLECLAW_HELMET_NETHERITE = registerItem("moleclaw_helmet_netherite", properties -> createDragonArmorItem(EquipmentSlot.HEAD, 7, 2,1,  properties));

    public static final Item WYVERN_SPAWN_EGG = registerItem("wyvern_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(UREntities.WYVERN_ENTITY)));
    public static final Item MOLECLAW_SPAWN_EGG = registerItem("moleclaw_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(UREntities.MOLECLAW_ENTITY)));
    public static final Item RIVER_PIKEHORN_SPAWN_EGG = registerItem("river_pikehorn_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(UREntities.RIVER_PIKEHORN_ENTITY)));
    public static final Item LIGHTNING_CHASER_SPAWN_EGG = registerItem("lightning_chaser_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(UREntities.LIGHTNING_CHASER_ENTITY)));
    public static final Item MAGMAMUNCHER_SPAWN_EGG = registerItem("magmamuncher_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(UREntities.MAGMAMUNCHER_ENTITY)));

    public static final Item FLUTE = registerItem("flute", properties -> new FluteItem(properties.stacksTo(1).component(FLUTE_MODE_COMPONENT, FluteComponent.DEFAULT)));
    public static final Item VORTEX_HORN = registerItem("vortex_horn", properties -> new VortexHornItem(createVortexHornItemProperties(properties, 1)));
    public static final Item IRON_VORTEX_HORN = registerItem("iron_vortex_horn", properties -> new VortexHornItem(createVortexHornItemProperties(properties, 3)));
    public static final Item GOLD_VORTEX_HORN = registerItem("gold_vortex_horn", properties -> new VortexHornItem(createVortexHornItemProperties(properties, 6)));
    public static final Item DIAMOND_VORTEX_HORN = registerItem("diamond_vortex_horn", properties -> new VortexHornItem(createVortexHornItemProperties(properties, 9)));
    public static final Item NETHERITE_VORTEX_HORN = registerItem("netherite_vortex_horn", properties -> new VortexHornItem(createVortexHornItemProperties(properties, 15)));

    public static final ResourceKey<CreativeModeTab> UR_ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, UselessReptile.id("item_group"));

    public static void init(){
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, UR_ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(WYVERN_SKIN))
                .title(Component.translatable("itemGroup.uselessreptile.item_group"))
                .build());

        ItemGroupEvents.modifyEntriesEvent(UR_ITEM_GROUP).register(c ->{
            c.accept(WYVERN_SPAWN_EGG);
            c.accept(MOLECLAW_SPAWN_EGG);
            c.accept(RIVER_PIKEHORN_SPAWN_EGG);
            c.accept(LIGHTNING_CHASER_SPAWN_EGG);
            c.accept(MAGMAMUNCHER_SPAWN_EGG);
            c.accept(Items.CHICKEN);
            c.accept(Items.BEETROOT);
            c.accept(Items.TROPICAL_FISH_BUCKET);
            c.accept(Items.BLAZE_ROD);
            c.accept(Items.SADDLE);
            c.accept(MOLECLAW_HELMET_IRON);
            c.accept(MOLECLAW_HELMET_GOLD);
            c.accept(MOLECLAW_HELMET_DIAMOND);
            c.accept(MOLECLAW_HELMET_NETHERITE);
            c.accept(DRAGON_HELMET_IRON);
            c.accept(DRAGON_CHESTPLATE_IRON);
            c.accept(DRAGON_TAIL_ARMOR_IRON);
            c.accept(DRAGON_HELMET_GOLD);
            c.accept(DRAGON_CHESTPLATE_GOLD);
            c.accept(DRAGON_TAIL_ARMOR_GOLD);
            c.accept(DRAGON_HELMET_DIAMOND);
            c.accept(DRAGON_CHESTPLATE_DIAMOND);
            c.accept(DRAGON_TAIL_ARMOR_DIAMOND);
            c.accept(DRAGON_HELMET_NETHERITE);
            c.accept(DRAGON_CHESTPLATE_NETHERITE);
            c.accept(DRAGON_TAIL_ARMOR_NETHERITE);
            c.accept(WYVERN_SKIN);
            c.accept(FLUTE);
            c.getContext().holders().lookup(Registries.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.VORTEX_HORN, InstrumentTags.GOAT_HORNS, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().holders().lookup(Registries.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.IRON_VORTEX_HORN, InstrumentTags.GOAT_HORNS, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().holders().lookup(Registries.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.GOLD_VORTEX_HORN, InstrumentTags.GOAT_HORNS, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().holders().lookup(Registries.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.DIAMOND_VORTEX_HORN, InstrumentTags.GOAT_HORNS, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().holders().lookup(Registries.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.NETHERITE_VORTEX_HORN, InstrumentTags.GOAT_HORNS, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        });
    }

    private static DragonEquipmentItem createDragonArmorItem(EquipmentSlot equipmentSlot, float armor, float toughness, float knockbackResistance, Item.Properties properties) {
        return new DragonEquipmentItem(Suppliers.memoize(() -> {
                    ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
                    EquipmentSlotGroup attributeModifierSlot = EquipmentSlotGroup.bySlot(equipmentSlot);
                    Identifier id = DragonEquipmentItem.equipmentModifierID(equipmentSlot);
                    if (armor > 0) builder.add(Attributes.ARMOR, new AttributeModifier(id, armor, AttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    if (toughness > 0) builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    if (knockbackResistance > 0) builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, knockbackResistance/10f, AttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    return builder.build();
                }),
                properties.stacksTo(1));
    }

    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, UselessReptile.id(id), (builderOperator.apply(DataComponentType.builder())).build());
    }

    public static Item registerItem(String id, Function<Item.Properties, Item> factory) {
        return Items.registerItem(ResourceKey.create(Registries.ITEM, UselessReptile.id(id)), factory);
    }

    public static void addInstruments(CreativeModeTab.Output entries, HolderLookup<Instrument> registryWrapper, Item item, TagKey<Instrument> instrumentTag, CreativeModeTab.TabVisibility visibility) {
        registryWrapper.get(instrumentTag).ifPresent((entryList) ->
                entryList.stream().map((instrument) ->
                        InstrumentItem.create(item, instrument)).forEach((stack) -> entries.accept(stack, visibility)));
    }

    private static Item.Properties createVortexHornItemProperties(Item.Properties properties, int maxCapacity) {
        return properties.stacksTo(1)
                .component(DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT)
                .component(VORTEX_HORN_CAPACITY_COMPONENT, new VortexHornCapacityComponent(0, maxCapacity));
    }
}

