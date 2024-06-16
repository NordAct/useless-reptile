package nordmods.uselessreptile.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import nordmods.uselessreptile.datagen.tag.BiomeTagGenerator;
import nordmods.uselessreptile.datagen.tag.BlockTagGenerator;
import nordmods.uselessreptile.datagen.tag.ItemTagGenerator;

public class UselessReptileDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(BiomeTagGenerator::new);
        pack.addProvider(BlockTagGenerator::new);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(AdvancementGenerator::new);
    }
}
