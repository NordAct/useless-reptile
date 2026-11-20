package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.dragon_ability.type.DragonAbilityType;
import nordmods.uselessreptile.common.dragon_ability.use_condition.UseConditionType;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.InteractionSettings;
import nordmods.uselessreptile.common.dragon_variant.TargetingSettings;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.item.FluteItem;

import java.util.List;
import java.util.Map;

public class URResourceKeys {
    public static final ResourceKey<Registry<DragonVariant>> DRAGON_VARIANT = ResourceKey.createRegistryKey(UselessReptile.id("variant"));
    public static final ResourceKey<Registry<DragonVariantType<?>>> DRAGON_VARIANT_TYPE = ResourceKey.createRegistryKey(UselessReptile.id("variant_type"));
    public static final ResourceKey<Registry<List<DragonAbility>>> DRAGON_ABILITIES = ResourceKey.createRegistryKey(UselessReptile.id("abilities"));
    public static final ResourceKey<Registry<DragonAbilityType<?>>> DRAGON_ABILITY_TYPE = ResourceKey.createRegistryKey(UselessReptile.id("dragon_ability_type"));
    public static final ResourceKey<Registry<DragonModelData>> DRAGON_MODEL = ResourceKey.createRegistryKey(UselessReptile.id("dragon_model"));
    public static final ResourceKey<Registry<EquipmentModelData>> DRAGON_EQUIPMENT = ResourceKey.createRegistryKey(UselessReptile.id("equipment"));
    public static final ResourceKey<Registry<EquipmentModelData>> DRAGON_EQUIPMENT_INJECT = ResourceKey.createRegistryKey(UselessReptile.id("equipment_inject"));
    public static final ResourceKey<Registry<List<DragonSpawnConditions>>> DRAGON_SPAWN_CONDITIONS = ResourceKey.createRegistryKey(UselessReptile.id("spawn_conditions"));
    public static final ResourceKey<Registry<Map<Holder<Attribute>, Float>>> DRAGON_BASE_ATTRIBUTE_VALUES = ResourceKey.createRegistryKey(UselessReptile.id("base_attribute_values"));
    public static final ResourceKey<Registry<UseConditionType<?>>> USE_CONDITION_TYPE = ResourceKey.createRegistryKey(UselessReptile.id("use_condition_type"));
    public static final ResourceKey<Registry<FluteItem.FluteMode>> FLUTE_MODE = ResourceKey.createRegistryKey(UselessReptile.id("flute_mode"));
    public static final ResourceKey<Registry<TargetingSettings>> DRAGON_TARGETING_SETTINGS = ResourceKey.createRegistryKey(UselessReptile.id("targeting_settings"));
    public static final ResourceKey<Registry<InteractionSettings>> DRAGON_INTERACTION_SETTINGS = ResourceKey.createRegistryKey(UselessReptile.id("interaction_settings"));

    public static void init() {
        DynamicRegistries.registerSynced(DRAGON_MODEL, DragonModelData.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT_INJECT, EquipmentModelData.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT, EquipmentModelData.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DRAGON_SPAWN_CONDITIONS, DragonSpawnConditions.DIRECT_CODEC.listOf());
        DynamicRegistries.registerSynced(DRAGON_BASE_ATTRIBUTE_VALUES, CommonDragonVariantData.BASE_ATTRIBUTE_VALUES_DIRECT_CODEC);
        DynamicRegistries.registerSynced(DRAGON_ABILITIES, DragonAbility.DIRECT_CODEC.listOf());
        DynamicRegistries.registerSynced(DRAGON_TARGETING_SETTINGS, TargetingSettings.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DRAGON_INTERACTION_SETTINGS, InteractionSettings.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DRAGON_VARIANT, DragonVariant.CODEC);
    }
}
