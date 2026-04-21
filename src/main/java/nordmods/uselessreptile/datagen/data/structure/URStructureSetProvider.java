package nordmods.uselessreptile.datagen.data.structure;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URStructureSetProvider extends FabricDynamicRegistryProvider {
    public URStructureSetProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    public static final ResourceKey<StructureSet> LIGHTNING_CHASER_NEST = ResourceKey.create(Registries.STRUCTURE_SET, UselessReptile.id("lightning_chaser_nest"));

    @Override
    public void configure(HolderLookup.Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(Registries.STRUCTURE_SET));
    }

    public static void register(BootstrapContext<StructureSet> bootstrapContext) {
        HolderGetter<Structure> structureLookup = bootstrapContext.lookup(Registries.STRUCTURE);
        bootstrapContext.register(
                LIGHTNING_CHASER_NEST,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structureLookup.getOrThrow(URStructureProvider.LIGHTNING_CHASER_NEST_DESERT), 1)
                        ),
                        new RandomSpreadStructurePlacement(32, 16, RandomSpreadType.LINEAR, 5553535)
                )
        );
    }



    @Override
    public @NonNull String getName() {
        return "Structure Set";
    }
}
