package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;

import java.util.List;

public class URResourceKeys {
    public static final ResourceKey<Registry<DragonVariant>> DRAGON_VARIANT = ResourceKey.createRegistryKey(UselessReptile.id("variant"));
    public static final ResourceKey<Registry<DragonVariant>> DRAGON_VARIANT_CUSTOM_NAME = ResourceKey.createRegistryKey(UselessReptile.id("custom_name"));
    public static final ResourceKey<Registry<DragonModel>> DRAGON_MODEL = ResourceKey.createRegistryKey(UselessReptile.id("dragon_model"));
    public static final ResourceKey<Registry<DragonEquipment>> DRAGON_EQUIPMENT = ResourceKey.createRegistryKey(UselessReptile.id("equipment"));
    public static final ResourceKey<Registry<DragonEquipment>> DRAGON_EQUIPMENT_INJECT = ResourceKey.createRegistryKey(UselessReptile.id("equipment_inject"));
    public static final ResourceKey<Registry<List<DragonSpawnConditions>>> DRAGON_SPAWN_CONDITIONS = ResourceKey.createRegistryKey(UselessReptile.id("spawn_conditions"));
    public static final ResourceKey<Registry<List<AttributeModifier>>> DRAGON_VARIANT_ATTRIBUTE_MODIFIERS = ResourceKey.createRegistryKey(UselessReptile.id("attribute_modifiers"));

    public static void init() {
        DynamicRegistries.registerSynced(DRAGON_MODEL, DragonModel.CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT_INJECT, DragonEquipment.CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT, DragonEquipment.CODEC);
        DynamicRegistries.register(DRAGON_SPAWN_CONDITIONS, DragonSpawnConditions.CODEC.listOf());
        DynamicRegistries.register(DRAGON_VARIANT_ATTRIBUTE_MODIFIERS, AttributeModifier.CODEC.listOf());
        DynamicRegistries.registerSynced(DRAGON_VARIANT, DragonVariant.CODEC, DragonVariant.CODEC_NO_SERVER_INFO);
        DynamicRegistries.registerSynced(DRAGON_VARIANT_CUSTOM_NAME, DragonVariant.CODEC_CUSTOM_NAME);

        DynamicRegistrySetupCallback.EVENT.register(registryView -> {
            RegistryAccess registryManager = registryView.asDynamicRegistryManager();
            registryView.registerEntryAdded(DRAGON_VARIANT, ((rawId, id, object) -> verifyDragonVariantContent(registryManager, object, false)));
            registryView.registerEntryAdded(DRAGON_VARIANT_CUSTOM_NAME, ((rawId, id, object) -> verifyDragonVariantContent(registryManager, object, true)));
        });
    }

    private static void verifyDragonVariantContent(RegistryAccess registryManager, DragonVariant variant, boolean isCustomName) {
        String type = isCustomName ? "Custom name model" : "Variant";
        String name = variant.name();
        String entity = variant.dragonId().toString();

        if (registryManager.lookupOrThrow(DRAGON_MODEL).getValue(variant.dragonModelData()) == null)
            UselessReptile.LOGGER.warn("{} {} for {} specifies invalid dragon model path: {}", type, name, entity, variant.dragonModelData());
        if (registryManager.lookupOrThrow(DRAGON_EQUIPMENT).getValue(variant.dragonEquipment()) == null)
            UselessReptile.LOGGER.warn("{} {} for {} specifies invalid equipment path: {}", type, name, entity, variant.dragonEquipment());

        if (isCustomName) return;

        if (variant.spawnConditions().isPresent() && registryManager.lookupOrThrow(DRAGON_SPAWN_CONDITIONS).getValue(variant.spawnConditions().get()) == null)
            UselessReptile.LOGGER.warn("{} {} for {} specifies invalid spawn conditions path: {}", type, name, entity, variant.spawnConditions().get());
    }
}
