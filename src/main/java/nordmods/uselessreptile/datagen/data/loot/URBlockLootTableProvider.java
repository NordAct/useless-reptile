package nordmods.uselessreptile.datagen.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.common.init.URBlocks;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URBlockLootTableProvider extends FabricBlockLootSubProvider {
    protected final FabricPackOutput output;
    public URBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
        this.output = output;
    }

    @Override
    public void generate() {
        dropOther(URBlocks.DEPLETED_MAGMA, Items.NETHERRACK);
    }


    @Override
    public @NonNull String getName() {
        return "Block Loot Tables";
    }
}
