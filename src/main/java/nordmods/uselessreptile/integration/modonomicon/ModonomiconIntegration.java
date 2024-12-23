package nordmods.uselessreptile.integration.modonomicon;

import com.klikli_dev.modonomicon.api.datagen.FabricBookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URItems;

public class ModonomiconIntegration {
    public static void init() {
        if (!FabricLoader.getInstance().isModLoaded("modonomicon")) return;
        //TODO
        ItemStack modonomicon = ItemRegistry.MODONOMICON.get().getDefaultStack();
        modonomicon.set(DataComponentRegistry.BOOK_ID.get(), UselessReptile.id("dragonarium"));
        ItemGroupEvents.modifyEntriesEvent(URItems.UR_ITEM_GROUP).register(c ->{
            c.add(modonomicon);
        });
    }

    public static void initDatagen(FabricDataGenerator fabricDataGenerator) {
        if (!FabricLoader.getInstance().isModLoaded("modonomicon")) return;
        //TODO
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        var enUsCache = new LanguageProviderCache("en_us");
        var ruRuCache = new LanguageProviderCache("ru_ru");

        pack.addProvider(
                FabricBookProvider.of(
                    new ModonomiconBookProvider(enUsCache)
                )
        );

        pack.addProvider((FabricDataOutput output) -> new ModonomiconLanguageProvider(output,"en_us", enUsCache));
        pack.addProvider((FabricDataOutput output) -> new ModonomiconLanguageProvider(output, "ru_ru", ruRuCache));
    }
}
