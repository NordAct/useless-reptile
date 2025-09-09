package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;

import java.util.List;

public class URRegistryKeys {
    public static final RegistryKey<Registry<DragonVariant>> DRAGON_VARIANT = RegistryKey.ofRegistry(UselessReptile.id("variant"));
    public static final RegistryKey<Registry<DragonVariant>> DRAGON_VARIANT_CUSTOM_NAME = RegistryKey.ofRegistry(UselessReptile.id("custom_name"));
    public static final RegistryKey<Registry<DragonModel>> DRAGON_MODEL = RegistryKey.ofRegistry(UselessReptile.id("dragon_model"));
    public static final RegistryKey<Registry<DragonEquipment>> DRAGON_EQUIPMENT = RegistryKey.ofRegistry(UselessReptile.id("equipment"));
    public static final RegistryKey<Registry<DragonEquipment>> DRAGON_EQUIPMENT_INJECT = RegistryKey.ofRegistry(UselessReptile.id("equipment_inject"));
    public static final RegistryKey<Registry<List<DragonSpawnConditions>>> DRAGON_SPAWN_CONDITIONS = RegistryKey.ofRegistry(UselessReptile.id("spawn_conditions"));
    public static final RegistryKey<Registry<List<EntityAttributeModifier>>> DRAGON_VARIANT_ATTRIBUTE_MODIFIERS = RegistryKey.ofRegistry(UselessReptile.id("attribute_modifiers"));

    public static void init() {
        DynamicRegistries.registerSynced(DRAGON_MODEL, DragonModel.CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT_INJECT, DragonEquipment.CODEC);
        DynamicRegistries.registerSynced(DRAGON_EQUIPMENT, DragonEquipment.CODEC);
        DynamicRegistries.register(DRAGON_SPAWN_CONDITIONS, DragonSpawnConditions.CODEC.listOf());
        DynamicRegistries.register(DRAGON_VARIANT_ATTRIBUTE_MODIFIERS, EntityAttributeModifier.CODEC.listOf());
        DynamicRegistries.registerSynced(DRAGON_VARIANT, DragonVariant.CODEC, DragonVariant.CODEC_NO_SERVER_INFO);
        DynamicRegistries.registerSynced(DRAGON_VARIANT_CUSTOM_NAME, DragonVariant.CODEC_CUSTOM_NAME);

        DynamicRegistrySetupCallback.EVENT.register(registryView -> {
            DynamicRegistryManager registryManager = registryView.asDynamicRegistryManager();
            registryView.registerEntryAdded(DRAGON_VARIANT, ((rawId, id, object) -> verifyDragonVariantContent(registryManager, object, false)));
            registryView.registerEntryAdded(DRAGON_VARIANT_CUSTOM_NAME, ((rawId, id, object) -> verifyDragonVariantContent(registryManager, object, true)));
        });
    }

    private static void verifyDragonVariantContent(DynamicRegistryManager registryManager, DragonVariant variant, boolean isCustomName) {
        String type = isCustomName ? "Custom name model" : "Variant";
        String name = variant.name();
        String entity = variant.dragonId().toString();

        if (registryManager.getOrThrow(DRAGON_MODEL).get(variant.dragonModelData()) == null)
            UselessReptile.LOGGER.warn("{} {} for {} specifies invalid dragon model path: {}", type, name, entity, variant.dragonModelData());
        if (registryManager.getOrThrow(DRAGON_EQUIPMENT).get(variant.dragonEquipment()) == null)
            UselessReptile.LOGGER.warn("{} {} for {} specifies invalid equipment path: {}", type, name, entity, variant.dragonEquipment());

        if (isCustomName) return;

        if (variant.spawnConditions().isPresent() && registryManager.getOrThrow(DRAGON_SPAWN_CONDITIONS).get(variant.spawnConditions().get()) == null)
            UselessReptile.LOGGER.warn("{} {} for {} specifies invalid spawn conditions path: {}", type, name, entity, variant.spawnConditions().get());
    }
}
