package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import nordmods.uselessreptile.datagen.assets.URDragonModelDataProvider;
import nordmods.uselessreptile.datagen.assets.UREquipmentModelDataProvider;
import nordmods.uselessreptile.datagen.assets.URModelProvider;
import nordmods.uselessreptile.datagen.data.URAdvancementProvider;
import nordmods.uselessreptile.datagen.data.URDamageTypeProvider;
import nordmods.uselessreptile.datagen.data.UREntityLootTableProvider;
import nordmods.uselessreptile.datagen.data.URRecipeProvider;
import nordmods.uselessreptile.datagen.data.dragon_spawn.URDragonSpawnProvider;
import nordmods.uselessreptile.datagen.data.tag.*;

public class UselessReptileDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(URItemTagProvider::new);
        pack.addProvider(URBiomeTagProvider::new);
        pack.addProvider(URBlockTagProvider::new);
        pack.addProvider(URDamageTypeTagProvider::new);
        pack.addProvider(URRecipeProvider::new);
        pack.addProvider(URAdvancementProvider::new);
        pack.addProvider(URDragonSpawnProvider::new);
        pack.addProvider(URDamageTypeProvider::new);
        pack.addProvider(UREntityLootTableProvider::new);
        pack.addProvider(UREntityTypeTagProvider::new);

        pack.addProvider(URModelProvider::new);
        pack.addProvider(UREquipmentModelDataProvider::new);
        pack.addProvider(URDragonModelDataProvider::new);

        //ModonomiconIntegration.initDatagen(fabricDataGenerator);
    }
}
