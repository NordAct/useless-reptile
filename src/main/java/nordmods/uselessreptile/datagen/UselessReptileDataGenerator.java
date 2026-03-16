package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import nordmods.uselessreptile.datagen.assets.URModelProvider;
import nordmods.uselessreptile.datagen.assets.URSoundsProvider;
import nordmods.uselessreptile.datagen.data.*;
import nordmods.uselessreptile.datagen.data.mod.URDragonModelProvider;
import nordmods.uselessreptile.datagen.data.mod.URDragonVariantProvider;
import nordmods.uselessreptile.datagen.data.mod.UREquipmentProvider;
import nordmods.uselessreptile.datagen.data.mod.URSpawnConditionsProvider;
import nordmods.uselessreptile.datagen.data.tags.*;

public class UselessReptileDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(URItemTagsProvider::new);
        pack.addProvider(URBiomeTagsProvider::new);
        pack.addProvider(URBlockTagsProvider::new);
        pack.addProvider(URDamageTypeTagsProvider::new);
        pack.addProvider(URDimensionTypeTagsProvider::new);
        pack.addProvider(URRecipeProvider::new);
        pack.addProvider(URAdvancementProvider::new);
        pack.addProvider(URDamageTypeProvider::new);
        pack.addProvider(UREntityLootTableProvider::new);
        pack.addProvider(URBlockLootTableGenerator::new);
        pack.addProvider(UREntityTypeTagsProvider::new);

        pack.addProvider(UREquipmentProvider::new);
        pack.addProvider(URDragonVariantProvider::new);
        pack.addProvider(URSpawnConditionsProvider::new);
        pack.addProvider(URDragonModelProvider::new);

        pack.addProvider(URModelProvider::new);
        pack.addProvider(URSoundsProvider::new);
    }
}
