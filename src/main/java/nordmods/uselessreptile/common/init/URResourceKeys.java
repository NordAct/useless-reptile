package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.data.UseConditionType;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.dragon_ability.DragonAbilityType;
import nordmods.uselessreptile.common.item.FluteItem;

import java.util.List;

public class URResourceKeys {
    public static final ResourceKey<Registry<DragonVariant>> DRAGON_VARIANT = ResourceKey.createRegistryKey(UselessReptile.id("variant"));
    public static final ResourceKey<Registry<DragonVariantType<?>>> DRAGON_VARIANT_TYPE = ResourceKey.createRegistryKey(UselessReptile.id("variant_type"));
    public static final ResourceKey<Registry<List<DragonAbility>>> DRAGON_ABILITIES = ResourceKey.createRegistryKey(UselessReptile.id("abilities"));
    public static final ResourceKey<Registry<DragonAbilityType<?>>> DRAGON_ABILITY_TYPE = ResourceKey.createRegistryKey(UselessReptile.id("dragon_ability_type"));
    public static final ResourceKey<Registry<DragonModelData>> DRAGON_MODEL = ResourceKey.createRegistryKey(UselessReptile.id("dragon_model"));
    public static final ResourceKey<Registry<EquipmentModelData>> DRAGON_EQUIPMENT = ResourceKey.createRegistryKey(UselessReptile.id("equipment"));
    public static final ResourceKey<Registry<EquipmentModelData>> DRAGON_EQUIPMENT_INJECT = ResourceKey.createRegistryKey(UselessReptile.id("equipment_inject"));
    public static final ResourceKey<Registry<List<DragonSpawnConditions>>> DRAGON_SPAWN_CONDITIONS = ResourceKey.createRegistryKey(UselessReptile.id("spawn_conditions"));
    public static final ResourceKey<Registry<List<AttributeModifier>>> DRAGON_VARIANT_ATTRIBUTE_MODIFIERS = ResourceKey.createRegistryKey(UselessReptile.id("attribute_modifiers"));
    public static final ResourceKey<Registry<UseConditionType<?>>> USE_CONDITION_TYPE = ResourceKey.createRegistryKey(UselessReptile.id("use_condition_type"));
    public static final ResourceKey<Registry<FluteItem.FluteMode>> FLUTE_MODE = ResourceKey.createRegistryKey(UselessReptile.id("flute_mode"));


    public static void init() {
        DynamicRegistries.registerSynced(DRAGON_MODEL, DragonModelData.CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT_INJECT, EquipmentModelData.CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT, EquipmentModelData.CODEC);
        DynamicRegistries.register(DRAGON_SPAWN_CONDITIONS, DragonSpawnConditions.CODEC.listOf());
        DynamicRegistries.register(DRAGON_VARIANT_ATTRIBUTE_MODIFIERS, AttributeModifier.CODEC.listOf());
        DynamicRegistries.registerSynced(DRAGON_VARIANT, DragonVariant.CODEC);
        DynamicRegistries.registerSynced(DRAGON_ABILITIES, DragonAbility.CODEC.listOf());
    }
}
