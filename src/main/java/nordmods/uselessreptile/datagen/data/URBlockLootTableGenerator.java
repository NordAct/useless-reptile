package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import nordmods.uselessreptile.common.init.URBlocks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class URBlockLootTableGenerator extends BlockLootSubProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    public URBlockLootTableGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(Set.of(), FeatureFlagSet.of(), registryLookupFuture.join());
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            generate();
            List<CompletableFuture<?>> list = new ArrayList<>();
            map.forEach((key, loot) -> {
                LootTable lootTable = loot.build();
                Path path = pathResolver.json(key.location());
                list.add(DataProvider.saveStable(writer, registryLookupFuture, LootTable.DIRECT_CODEC, lootTable, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public void generate() {
        dropOther(URBlocks.DEPLETED_MAGMA, Items.NETHERRACK);
    }


    @Override
    public String getName() {
        return "Block Loot Tables";
    }
}
