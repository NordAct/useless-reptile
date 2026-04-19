package nordmods.uselessreptile.datagen.data.structure;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URStructureProvider extends FabricDynamicRegistryProvider {
    public URStructureProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    public void configure(HolderLookup.Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(Registries.STRUCTURE));
    }

    public static final ResourceKey<Structure> LIGHTNING_CHASER_NEST_DESERT = ResourceKey.create(Registries.STRUCTURE, UselessReptile.id("lightning_chaser_nest_desert"));

    public static void register(BootstrapContext<Structure> bootstrapContext) {
        HolderGetter<Biome> biomes = bootstrapContext.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templates = bootstrapContext.lookup(Registries.TEMPLATE_POOL);
        bootstrapContext.register(
                LIGHTNING_CHASER_NEST_DESERT,
                new JigsawStructure(
                        new Structure.StructureSettings.Builder(biomes.getOrThrow(URTags.HAS_LIGHTNING_CHASER_NEST_DESERT))
                                .terrainAdapation(TerrainAdjustment.BEARD_THIN)
                                .build(),
                        templates.getOrThrow(URTemplatePoolProvider.LIGHTNING_CHASER_NEST_DESERT),
                        2,
                        ConstantHeight.of(VerticalAnchor.absolute(0)),
                        false,
                        Heightmap.Types.WORLD_SURFACE_WG
                )
        );
    }

    @Override
    public String getName() {
        return "Structure";
    }
}
