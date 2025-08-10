package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import nordmods.uselessreptile.common.init.URBlocks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class URBlockLootTableGenerator extends BlockLootTableGenerator implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    public URBlockLootTableGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(Set.of(), FeatureSet.empty(), registryLookupFuture.join());
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_table");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            generate();
            List<CompletableFuture<?>> list = new ArrayList<>();
            lootTables.forEach((key, loot) -> {
                LootTable lootTable = loot.build();
                Path path = pathResolver.resolveJson(key.getValue());
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, LootTable.CODEC, lootTable, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public void generate() {
        addDrop(URBlocks.DEPLETED_MAGMA, Items.NETHERRACK);
    }


    @Override
    public String getName() {
        return "Block Loot Tables";
    }
}
