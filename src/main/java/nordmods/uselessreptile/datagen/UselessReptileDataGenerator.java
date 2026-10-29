package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import nordmods.uselessreptile.common.init.URResourceKeys;
import nordmods.uselessreptile.datagen.assets.URModelProvider;
import nordmods.uselessreptile.datagen.assets.URSoundsProvider;
import nordmods.uselessreptile.datagen.data.URAdvancementProvider;
import nordmods.uselessreptile.datagen.data.URDamageTypeProvider;
import nordmods.uselessreptile.datagen.data.URRecipeProvider;
import nordmods.uselessreptile.datagen.data.loot.URBlockLootTableProvider;
import nordmods.uselessreptile.datagen.data.loot.URChestLootTableProvider;
import nordmods.uselessreptile.datagen.data.loot.UREntityLootTableProvider;
import nordmods.uselessreptile.datagen.data.mod.*;
import nordmods.uselessreptile.datagen.data.structure.URProcessorsListProvider;
import nordmods.uselessreptile.datagen.data.structure.URStructureProvider;
import nordmods.uselessreptile.datagen.data.structure.URStructureSetProvider;
import nordmods.uselessreptile.datagen.data.structure.URTemplatePoolProvider;
import nordmods.uselessreptile.datagen.data.tags.*;
import org.jspecify.annotations.NonNull;

public class UselessReptileDataGenerator implements DataGeneratorEntrypoint {
    public static boolean enableDatagen = true;
    @Override
    public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
        if (!enableDatagen) return;

        final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(URItemTagsProvider::new);
        pack.addProvider(URBiomeTagsProvider::new);
        pack.addProvider(URBlockTagsProvider::new);
        pack.addProvider(URDamageTypeTagsProvider::new);
        pack.addProvider(URDimensionTypeTagsProvider::new);
        pack.addProvider(UREntityTypeTagsProvider::new);
        pack.addProvider(URDragonAbilityTypesTagsProvider::new);

        pack.addProvider(URRecipeProvider::new);
        pack.addProvider(URAdvancementProvider::new);
        pack.addProvider(URDamageTypeProvider::new);

        pack.addProvider(UREntityLootTableProvider::new);
        pack.addProvider(URBlockLootTableProvider::new);
        pack.addProvider(URChestLootTableProvider::new);

        pack.addProvider(URDragonBaseAttributeValuesProvider::new);
        pack.addProvider(URDragonTargetingSettings::new);
        pack.addProvider(URDragonInteractionSettingsProvider::new);
        pack.addProvider(UREquipmentProvider::new);
        pack.addProvider(URDragonVariantProvider::new);
        pack.addProvider(URSpawnConditionsProvider::new);
        pack.addProvider(URDragonModelProvider::new);
        pack.addProvider(URDragonAbilitiesProvider::new);

        pack.addProvider(URModelProvider::new);
        pack.addProvider(URSoundsProvider::new);

        pack.addProvider(URProcessorsListProvider::new);
        pack.addProvider(URTemplatePoolProvider::new);
        pack.addProvider(URStructureProvider::new);
        pack.addProvider(URStructureSetProvider::new);
    }

    @Override
    public void buildRegistry(@NonNull RegistrySetBuilder registryBuilder) {
        registryBuilder.add(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, URDragonBaseAttributeValuesProvider::register);
        registryBuilder.add(URResourceKeys.DRAGON_TARGETING_SETTINGS, URDragonTargetingSettings::register);
        registryBuilder.add(URResourceKeys.DRAGON_INTERACTION_SETTINGS, URDragonInteractionSettingsProvider::register);
        registryBuilder.add(URResourceKeys.DRAGON_SPAWN_CONDITIONS, URSpawnConditionsProvider::register);
        registryBuilder.add(URResourceKeys.DRAGON_MODEL, URDragonModelProvider::register);
        registryBuilder.add(URResourceKeys.DRAGON_ABILITIES, URDragonAbilitiesProvider::register);
        registryBuilder.add(URResourceKeys.DRAGON_EQUIPMENT, UREquipmentProvider::register);
        registryBuilder.add(URResourceKeys.DRAGON_VARIANT, URDragonVariantProvider::register);
        //todo remove structures
        registryBuilder.add(Registries.PROCESSOR_LIST, URProcessorsListProvider::register);
        registryBuilder.add(Registries.TEMPLATE_POOL, URTemplatePoolProvider::register);
        registryBuilder.add(Registries.STRUCTURE, URStructureProvider::register);
        registryBuilder.add(Registries.STRUCTURE_SET, URStructureSetProvider::register);
    }
}
