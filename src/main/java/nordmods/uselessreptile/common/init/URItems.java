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
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.item.DragonEquipmentItem;
import nordmods.uselessreptile.common.item.FluteItem;
import nordmods.uselessreptile.common.item.component.FluteComponent;

import java.util.function.UnaryOperator;

public class URItems {
    public static final ComponentType<FluteComponent> FLUTE_MODE_COMPONENT = register("flute_mode", builder -> builder.codec(FluteComponent.CODEC).packetCodec(FluteComponent.PACKET_CODEC));

    public static final Item WYVERN_SKIN = new Item(new Item.Settings());
    public static final DragonEquipmentItem DRAGON_HELMET_IRON = createDragonArmorItem(EquipmentSlot.HEAD, 2, 0);
    public static final DragonEquipmentItem DRAGON_HELMET_GOLD = createDragonArmorItem(EquipmentSlot.HEAD,3, 0);
    public static final DragonEquipmentItem DRAGON_HELMET_DIAMOND = createDragonArmorItem(EquipmentSlot.HEAD, 4, 0);
    public static final DragonEquipmentItem DRAGON_CHESTPLATE_IRON = createDragonArmorItem(EquipmentSlot.CHEST, 4, 0);
    public static final DragonEquipmentItem DRAGON_CHESTPLATE_GOLD = createDragonArmorItem(EquipmentSlot.CHEST, 5, 0);
    public static final DragonEquipmentItem DRAGON_CHESTPLATE_DIAMOND = createDragonArmorItem(EquipmentSlot.CHEST, 6, 0);
    public static final DragonEquipmentItem DRAGON_TAIL_ARMOR_IRON = createDragonArmorItem(EquipmentSlot.LEGS, 1, 0);
    public static final DragonEquipmentItem DRAGON_TAIL_ARMOR_GOLD = createDragonArmorItem(EquipmentSlot.LEGS, 2, 0);
    public static final DragonEquipmentItem DRAGON_TAIL_ARMOR_DIAMOND = createDragonArmorItem(EquipmentSlot.LEGS, 3, 0);
    public static final DragonEquipmentItem MOLECLAW_HELMET_IRON = createDragonArmorItem(EquipmentSlot.HEAD, 2, 0);
    public static final DragonEquipmentItem MOLECLAW_HELMET_GOLD = createDragonArmorItem(EquipmentSlot.HEAD, 3, 0);
    public static final DragonEquipmentItem MOLECLAW_HELMET_DIAMOND = createDragonArmorItem(EquipmentSlot.HEAD, 4, 0);
    public static final Item WYVERN_SPAWN_EGG = new SpawnEggItem(UREntities.WYVERN_ENTITY, 5462570, 3094045, new Item.Settings());
    public static final Item MOLECLAW_SPAWN_EGG = new SpawnEggItem(UREntities.MOLECLAW_ENTITY,2105119, 458752, new Item.Settings());
    public static final Item RIVER_PIKEHORN_SPAWN_EGG = new SpawnEggItem(UREntities.RIVER_PIKEHORN_ENTITY,2910895, 1457243, new Item.Settings());
    public static final Item LIGHTNING_CHASER_SPAWN_EGG = new SpawnEggItem(UREntities.LIGHTNING_CHASER_ENTITY,4145472, 10922151, new Item.Settings());
    public static final FluteItem FLUTE = new FluteItem(new Item.Settings().maxCount(1).component(FLUTE_MODE_COMPONENT, FluteComponent.DEFAULT));

    public static final RegistryKey<ItemGroup> UR_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, UselessReptile.id("item_group"));

    public static void init(){
        register(WYVERN_SKIN, "wyvern_skin");
        register(DRAGON_HELMET_IRON, "dragon_helmet_iron");
        register(DRAGON_CHESTPLATE_IRON, "dragon_chestplate_iron");
        register(DRAGON_TAIL_ARMOR_IRON, "dragon_tail_armor_iron");
        register(DRAGON_HELMET_GOLD, "dragon_helmet_gold");
        register(DRAGON_CHESTPLATE_GOLD, "dragon_chestplate_gold");
        register(DRAGON_TAIL_ARMOR_GOLD, "dragon_tail_armor_gold");
        register(DRAGON_HELMET_DIAMOND, "dragon_helmet_diamond");
        register(DRAGON_CHESTPLATE_DIAMOND, "dragon_chestplate_diamond");
        register(DRAGON_TAIL_ARMOR_DIAMOND,"dragon_tail_armor_diamond");
        register(MOLECLAW_HELMET_IRON,"moleclaw_helmet_iron");
        register(MOLECLAW_HELMET_GOLD,"moleclaw_helmet_gold");
        register(MOLECLAW_HELMET_DIAMOND,"moleclaw_helmet_diamond");
        register(MOLECLAW_SPAWN_EGG, "moleclaw_spawn_egg");
        register(RIVER_PIKEHORN_SPAWN_EGG, "river_pikehorn_spawn_egg");
        register(WYVERN_SPAWN_EGG, "wyvern_spawn_egg");
        register(LIGHTNING_CHASER_SPAWN_EGG, "lightning_chaser_spawn_egg");
        register(FLUTE, "flute");

        Registry.register(Registries.ITEM_GROUP, UR_ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(WYVERN_SKIN))
                .displayName(Text.translatable("itemGroup.uselessreptile.item_group"))
                .build());

        ItemGroupEvents.modifyEntriesEvent(UR_ITEM_GROUP).register(c ->{
            c.add(WYVERN_SPAWN_EGG);
            c.add(MOLECLAW_SPAWN_EGG);
            c.add(RIVER_PIKEHORN_SPAWN_EGG);
            c.add(LIGHTNING_CHASER_SPAWN_EGG);
            c.add(MOLECLAW_HELMET_IRON);
            c.add(MOLECLAW_HELMET_GOLD);
            c.add(MOLECLAW_HELMET_DIAMOND);
            c.add(DRAGON_HELMET_IRON);
            c.add(DRAGON_CHESTPLATE_IRON);
            c.add(DRAGON_TAIL_ARMOR_IRON);
            c.add(DRAGON_HELMET_GOLD);
            c.add(DRAGON_CHESTPLATE_GOLD);
            c.add(DRAGON_TAIL_ARMOR_GOLD);
            c.add(DRAGON_HELMET_DIAMOND);
            c.add(DRAGON_CHESTPLATE_DIAMOND);
            c.add(DRAGON_TAIL_ARMOR_DIAMOND);
            c.add(WYVERN_SKIN);
            c.add(FLUTE);
        });
    }

    private static DragonEquipmentItem createDragonArmorItem(EquipmentSlot equipmentSlot, int armor, int toughness) {
        return new DragonEquipmentItem(equipmentSlot,
                Suppliers.memoize(() -> {
                    AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
                    AttributeModifierSlot attributeModifierSlot = AttributeModifierSlot.forEquipmentSlot(equipmentSlot);
                    Identifier id = DragonEquipmentItem.equipmentModifierID(equipmentSlot);
                    if (armor > 0) builder.add(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(id, armor, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    if (toughness > 0) builder.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(id, toughness, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
                    return builder.build();
                }),
                new Item.Settings().maxCount(1));
    }

    private static void register(Item item, String id) {
        Registry.register(Registries.ITEM, UselessReptile.id(id), item);
    }

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, UselessReptile.id(id), (builderOperator.apply(ComponentType.builder())).build());
    }
}

