package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
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
    public static boolean ENABLE_DATAGEN = true;
    @Override
    public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
        if (!ENABLE_DATAGEN) return;

        final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(URItemTagsProvider::new);
        pack.addProvider(URBiomeTagsProvider::new);
        pack.addProvider(URBlockTagsProvider::new);
        pack.addProvider(URDamageTypeTagsProvider::new);
        pack.addProvider(URDimensionTypeTagsProvider::new);
        pack.addProvider(URRecipeProvider::new);
        pack.addProvider(URAdvancementProvider::new);
        pack.addProvider(URDamageTypeProvider::new);
        pack.addProvider(UREntityTypeTagsProvider::new);

        pack.addProvider(UREntityLootTableProvider::new);
        pack.addProvider(URBlockLootTableProvider::new);
        pack.addProvider(URChestLootTableProvider::new);

        pack.addProvider(UREquipmentProvider::new);
        pack.addProvider(URDragonVariantProvider::new);
        pack.addProvider(URSpawnConditionsProvider::new);
        pack.addProvider(URDragonModelProvider::new);
        pack.addProvider(URDragonAbilityListProvider::new);

        pack.addProvider(URModelProvider::new);
        pack.addProvider(URSoundsProvider::new);

        pack.addProvider(URProcessorsListProvider::new);
        pack.addProvider(URTemplatePoolProvider::new);
        pack.addProvider(URStructureProvider::new);
        pack.addProvider(URStructureSetProvider::new);
    }

    @Override
    public void buildRegistry(@NonNull RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.PROCESSOR_LIST, URProcessorsListProvider::register);
        registryBuilder.add(Registries.TEMPLATE_POOL, URTemplatePoolProvider::register);
        registryBuilder.add(Registries.STRUCTURE, URStructureProvider::register);
        registryBuilder.add(Registries.STRUCTURE_SET, URStructureSetProvider::register);
    }
}
