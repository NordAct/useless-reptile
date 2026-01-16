package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URItemTagProvider extends FabricTagProvider.ItemTagProvider{
    public URItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        //Moleclaw armor items
        getOrCreateRawBuilder(URTags.PROTECTS_MOLECLAW_FROM_LIGHT)
                .addElement(URItems.MOLECLAW_HELMET_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.MOLECLAW_HELMET_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.MOLECLAW_HELMET_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.MOLECLAW_HELMET_NETHERITE.builtInRegistryHolder().key().identifier());

        //other items
        getOrCreateRawBuilder(URTags.VORTEX_HORNS)
                .addElement(URItems.VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.IRON_VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.GOLD_VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DIAMOND_VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.NETHERITE_VORTEX_HORN.builtInRegistryHolder().key().identifier());
    }
}
