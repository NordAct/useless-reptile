package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import nordmods.uselessreptile.datagen.assets.URModelProvider;
import nordmods.uselessreptile.datagen.data.URAdvancementProvider;
import nordmods.uselessreptile.datagen.data.URDamageTypeProvider;
import nordmods.uselessreptile.datagen.data.UREntityLootTableProvider;
import nordmods.uselessreptile.datagen.data.URRecipeProvider;
import nordmods.uselessreptile.datagen.data.mod.URDragonModelProvider;
import nordmods.uselessreptile.datagen.data.mod.URDragonVariantProvider;
import nordmods.uselessreptile.datagen.data.mod.UREquipmentProvider;
import nordmods.uselessreptile.datagen.data.mod.URSpawnConditionsProvider;
import nordmods.uselessreptile.datagen.data.tag.URBiomeTagProvider;
import nordmods.uselessreptile.datagen.data.tag.URBlockTagProvider;
import nordmods.uselessreptile.datagen.data.tag.URDamageTypeTagProvider;
import nordmods.uselessreptile.datagen.data.tag.URItemTagProvider;

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
        pack.addProvider(URDamageTypeProvider::new);
        pack.addProvider(UREntityLootTableProvider::new);

        pack.addProvider(UREquipmentProvider::new);
        pack.addProvider(URDragonVariantProvider::new);
        pack.addProvider(URSpawnConditionsProvider::new);
        pack.addProvider(URDragonModelProvider::new);

        pack.addProvider(URModelProvider::new);
        //ModonomiconIntegration.initDatagen(fabricDataGenerator);
    }
}
