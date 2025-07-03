package nordmods.uselessreptile.common.init;

import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    public static final ComponentType<FluteComponent> FLUTE_MODE_COMPONENT = registerComponent("flute_mode",
            builder -> builder.codec(FluteComponent.CODEC).packetCodec(FluteComponent.PACKET_CODEC));
    public static final ComponentType<URDragonDataStorageComponent> DRAGON_STORAGE_COMPONENT = registerComponent("dragon_storage",
            builder -> builder.codec(URDragonDataStorageComponent.CODEC).packetCodec(URDragonDataStorageComponent.PACKET_CODEC));
    public static final ComponentType<VortexHornCapacityComponent> VORTEX_HORN_CAPACITY_COMPONENT = registerComponent("vortex_horn_capacity",
            builder -> builder.codec(VortexHornCapacityComponent.CODEC).packetCodec(VortexHornCapacityComponent.PACKET_CODEC));

    public static final Item WYVERN_SKIN = registerItem("wyvern_skin", Item::new);
    public static final Item DRAGON_HELMET_IRON = registerItem("dragon_helmet_iron", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 2, 0, settings));
    public static final Item DRAGON_HELMET_GOLD = registerItem("dragon_helmet_gold", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 3, 0, settings));
    public static final Item DRAGON_HELMET_DIAMOND = registerItem("dragon_helmet_diamond", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 4, 0, settings));
    public static final Item DRAGON_HELMET_NETHERITE = registerItem("dragon_helmet_netherite", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 5, 2, settings));
    public static final Item DRAGON_CHESTPLATE_IRON = registerItem("dragon_chestplate_iron", settings -> createDragonArmorItem(EquipmentSlot.CHEST, 4, 0, settings));
    public static final Item DRAGON_CHESTPLATE_GOLD = registerItem("dragon_chestplate_gold", settings -> createDragonArmorItem(EquipmentSlot.CHEST, 5, 0, settings));
    public static final Item DRAGON_CHESTPLATE_DIAMOND = registerItem("dragon_chestplate_diamond", settings -> createDragonArmorItem(EquipmentSlot.CHEST, 6, 0, settings));
    public static final Item DRAGON_CHESTPLATE_NETHERITE = registerItem("dragon_chestplate_netherite", settings -> createDragonArmorItem(EquipmentSlot.CHEST, 7, 2, settings));
    public static final Item DRAGON_TAIL_ARMOR_IRON = registerItem("dragon_tail_armor_iron", settings -> createDragonArmorItem(EquipmentSlot.LEGS, 1, 0, settings));
    public static final Item DRAGON_TAIL_ARMOR_GOLD = registerItem("dragon_tail_armor_gold", settings -> createDragonArmorItem(EquipmentSlot.LEGS, 2, 0, settings));
    public static final Item DRAGON_TAIL_ARMOR_DIAMOND = registerItem("dragon_tail_armor_diamond", settings -> createDragonArmorItem(EquipmentSlot.LEGS, 3, 0, settings));
    public static final Item DRAGON_TAIL_ARMOR_NETHERITE = registerItem("dragon_tail_armor_netherite", settings -> createDragonArmorItem(EquipmentSlot.LEGS, 4, 1, settings));
    public static final Item MOLECLAW_HELMET_IRON = registerItem("moleclaw_helmet_iron", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 2, 0, settings));
    public static final Item MOLECLAW_HELMET_GOLD = registerItem("moleclaw_helmet_gold", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 3, 0, settings));
    public static final Item MOLECLAW_HELMET_DIAMOND = registerItem("moleclaw_helmet_diamond", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 4, 0, settings));
    public static final Item MOLECLAW_HELMET_NETHERITE = registerItem("moleclaw_helmet_netherite", settings -> createDragonArmorItem(EquipmentSlot.HEAD, 5, 2, settings));
    public static final Item WYVERN_SPAWN_EGG = registerItem("wyvern_spawn_egg", settings -> new SpawnEggItem(UREntities.WYVERN_ENTITY, settings));
    public static final Item MOLECLAW_SPAWN_EGG = registerItem("moleclaw_spawn_egg", settings -> new SpawnEggItem(UREntities.MOLECLAW_ENTITY, settings));
    public static final Item RIVER_PIKEHORN_SPAWN_EGG = registerItem("river_pikehorn_spawn_egg", settings -> new SpawnEggItem(UREntities.RIVER_PIKEHORN_ENTITY, settings));
    public static final Item LIGHTNING_CHASER_SPAWN_EGG = registerItem("lightning_chaser_spawn_egg", settings -> new SpawnEggItem(UREntities.LIGHTNING_CHASER_ENTITY, settings));
    public static final Item MAGMAMUNCHER_SPAWN_EGG = registerItem("magmamuncher_spawn_egg", settings -> new SpawnEggItem(UREntities.MAGMAMUNCHER_ENTITY, settings));
    public static final Item FLUTE = registerItem("flute", settings -> new FluteItem(settings.maxCount(1).component(FLUTE_MODE_COMPONENT, FluteComponent.DEFAULT)));
    public static final Item VORTEX_HORN = registerItem("vortex_horn", settings -> new VortexHornItem(createVortexHornItemSettings(settings), 1));
    public static final Item IRON_VORTEX_HORN = registerItem("iron_vortex_horn", settings -> new VortexHornItem(createVortexHornItemSettings(settings), 3));
    public static final Item GOLD_VORTEX_HORN = registerItem("gold_vortex_horn", settings -> new VortexHornItem(createVortexHornItemSettings(settings), 6));
    public static final Item DIAMOND_VORTEX_HORN = registerItem("diamond_vortex_horn", settings -> new VortexHornItem(createVortexHornItemSettings(settings), 9));
    public static final Item NETHERITE_VORTEX_HORN = registerItem("netherite_vortex_horn", settings -> new VortexHornItem(createVortexHornItemSettings(settings), 15));

    public static final RegistryKey<ItemGroup> UR_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, UselessReptile.id("item_group"));

    public static void init(){
        Registry.register(Registries.ITEM_GROUP, UR_ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(WYVERN_SKIN))
                .displayName(Text.translatable("itemGroup.uselessreptile.item_group"))
                .build());

        ItemGroupEvents.modifyEntriesEvent(UR_ITEM_GROUP).register(c ->{
            c.add(WYVERN_SPAWN_EGG);
            c.add(MOLECLAW_SPAWN_EGG);
            c.add(RIVER_PIKEHORN_SPAWN_EGG);
            c.add(LIGHTNING_CHASER_SPAWN_EGG);
            c.add(MAGMAMUNCHER_SPAWN_EGG);
            c.add(Items.CHICKEN);
            c.add(Items.BEETROOT);
            c.add(Items.TROPICAL_FISH_BUCKET);
            c.add(Items.BLAZE_ROD);
            c.add(Items.SADDLE);
            c.add(MOLECLAW_HELMET_IRON);
            c.add(MOLECLAW_HELMET_GOLD);
            c.add(MOLECLAW_HELMET_DIAMOND);
            c.add(MOLECLAW_HELMET_NETHERITE);
            c.add(DRAGON_HELMET_IRON);
            c.add(DRAGON_CHESTPLATE_IRON);
            c.add(DRAGON_TAIL_ARMOR_IRON);
            c.add(DRAGON_HELMET_GOLD);
            c.add(DRAGON_CHESTPLATE_GOLD);
            c.add(DRAGON_TAIL_ARMOR_GOLD);
            c.add(DRAGON_HELMET_DIAMOND);
            c.add(DRAGON_CHESTPLATE_DIAMOND);
            c.add(DRAGON_TAIL_ARMOR_DIAMOND);
            c.add(DRAGON_HELMET_NETHERITE);
            c.add(DRAGON_CHESTPLATE_NETHERITE);
            c.add(DRAGON_TAIL_ARMOR_NETHERITE);
            c.add(WYVERN_SKIN);
            c.add(FLUTE);
            c.getContext().lookup().getOptional(RegistryKeys.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.VORTEX_HORN, InstrumentTags.GOAT_HORNS, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().lookup().getOptional(RegistryKeys.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.IRON_VORTEX_HORN, InstrumentTags.GOAT_HORNS, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().lookup().getOptional(RegistryKeys.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.GOLD_VORTEX_HORN, InstrumentTags.GOAT_HORNS, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().lookup().getOptional(RegistryKeys.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.DIAMOND_VORTEX_HORN, InstrumentTags.GOAT_HORNS, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
            c.getContext().lookup().getOptional(RegistryKeys.INSTRUMENT).ifPresent((wrapper) ->
                    addInstruments(c, wrapper, URItems.NETHERITE_VORTEX_HORN, InstrumentTags.GOAT_HORNS, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
        });
    }

    private static DragonEquipmentItem createDragonArmorItem(EquipmentSlot equipmentSlot, int armor, int toughness, Item.Settings settings) {
        return new DragonEquipmentItem(Suppliers.memoize(() -> {
                    AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
                    AttributeModifierSlot attributeModifierSlot = AttributeModifierSlot.forEquipmentSlot(equipmentSlot);
                    Identifier id = DragonEquipmentItem.equipmentModifierID(equipmentSlot);
                    if (armor > 0) builder.add(EntityAttributes.ARMOR, new EntityAttributeModifier(id, armor, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    if (toughness > 0) builder.add(EntityAttributes.ARMOR_TOUGHNESS, new EntityAttributeModifier(id, toughness, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    return builder.build();
                }),
                settings.maxCount(1));
    }

    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, UselessReptile.id(id), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static Item registerItem(String id, Function<Item.Settings, Item> factory) {
        return Items.register(RegistryKey.of(RegistryKeys.ITEM, UselessReptile.id(id)), factory);
    }

    public static void addInstruments(ItemGroup.Entries entries, RegistryWrapper<Instrument> registryWrapper, Item item, TagKey<Instrument> instrumentTag, ItemGroup.StackVisibility visibility) {
        registryWrapper.getOptional(instrumentTag).ifPresent((entryList) ->
                entryList.stream().map((instrument) ->
                        GoatHornItem.getStackForInstrument(item, instrument)).forEach((stack) -> entries.add(stack, visibility)));
    }

    private static Item.Settings createVortexHornItemSettings(Item.Settings settings) {
        return settings.maxCount(1)
                .component(DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT)
                .component(VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
    }
}

