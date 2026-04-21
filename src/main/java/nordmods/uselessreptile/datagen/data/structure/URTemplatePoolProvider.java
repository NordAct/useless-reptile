package nordmods.uselessreptile.datagen.data.structure;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URTemplatePoolProvider extends FabricDynamicRegistryProvider {
    public URTemplatePoolProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    public static final ResourceKey<StructureTemplatePool> LIGHTNING_CHASER_NEST_DESERT = ResourceKey.create(Registries.TEMPLATE_POOL, UselessReptile.id("lightning_chaser_nest/desert"));
    public static final ResourceKey<StructureTemplatePool> LOOT_BIG = ResourceKey.create(Registries.TEMPLATE_POOL, UselessReptile.id("lightning_chaser_nest/loot/big"));
    public static final ResourceKey<StructureTemplatePool> LOOT_MEDIUM = ResourceKey.create(Registries.TEMPLATE_POOL, UselessReptile.id("lightning_chaser_nest/loot/medium"));
    public static final ResourceKey<StructureTemplatePool> LOOT_SMALL = ResourceKey.create(Registries.TEMPLATE_POOL, UselessReptile.id("lightning_chaser_nest/loot/small"));

    @Override
    public void configure(HolderLookup.Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(Registries.TEMPLATE_POOL));
    }

    public static void register(BootstrapContext<StructureTemplatePool> bootstrapContext) {
        Holder<StructureTemplatePool> empty = bootstrapContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
        HolderGetter<StructureProcessorList> processorLists = bootstrapContext.lookup(Registries.PROCESSOR_LIST);
        bootstrapContext.register(
                LIGHTNING_CHASER_NEST_DESERT,
                new StructureTemplatePool(
                        empty,
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(
                                                UselessReptile.id("lightning_chaser_nest/desert/nest1").toString(),
                                                processorLists.getOrThrow(URProcessorsListProvider.REMOVE_DRAGON_PLACEHOLDER)
                                        ),
                                        1
                                ),
                                Pair.of(
                                        StructurePoolElement.single(
                                                UselessReptile.id("lightning_chaser_nest/desert/nest2").toString(),
                                                processorLists.getOrThrow(URProcessorsListProvider.REMOVE_DRAGON_PLACEHOLDER)
                                        ),
                                        1
                                )
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );
        bootstrapContext.register(
                LOOT_BIG,
                new StructureTemplatePool(
                        empty,
                        List.of(
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_big1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_big1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_big1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_big1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_big1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_big1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_medium1").toString()), 5),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_medium1").toString()), 5),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_medium1").toString()), 5),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_medium2").toString()), 5),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_medium2").toString()), 5),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_medium2").toString()), 5),
                                Pair.of(
                                        StructurePoolElement.single(
                                                UselessReptile.id("lightning_chaser_nest/loot/chest").toString(),
                                                processorLists.getOrThrow(URProcessorsListProvider.LIGHTNING_CHASER_NEST_APPLY_LOOT)
                                        ),
                                        10
                                ),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/heavy_core").toString()), 1),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/nothing").toString()), 35)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );
        bootstrapContext.register(
                LOOT_MEDIUM,
                new StructureTemplatePool(
                        empty,
                        List.of(
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_medium1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_medium1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_medium1").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_medium2").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_medium2").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_medium2").toString()), 4),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_small1").toString()), 6),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_small1").toString()), 6),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_small1").toString()), 6),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_small2").toString()), 6),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_small2").toString()), 6),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_small2").toString()), 6),
                                Pair.of(
                                        StructurePoolElement.single(
                                                UselessReptile.id("lightning_chaser_nest/loot/chest").toString(),
                                                processorLists.getOrThrow(URProcessorsListProvider.LIGHTNING_CHASER_NEST_APPLY_LOOT)
                                        ),
                                        14
                                ),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/heavy_core").toString()), 1),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/nothing").toString()), 25)

                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );

        bootstrapContext.register(
                LOOT_SMALL,
                new StructureTemplatePool(
                        empty,
                        List.of(
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_small1").toString()), 12),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_small1").toString()), 12),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_small1").toString()), 12),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/copper_pile_small2").toString()), 12),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/gold_pile_small2").toString()), 12),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/iron_pile_small2").toString()), 12),
                                Pair.of(
                                        StructurePoolElement.single(
                                                UselessReptile.id("lightning_chaser_nest/loot/chest").toString(),
                                                processorLists.getOrThrow(URProcessorsListProvider.LIGHTNING_CHASER_NEST_APPLY_LOOT)
                                        ),
                                        15
                                ),
                                Pair.of(StructurePoolElement.single(UselessReptile.id("lightning_chaser_nest/loot/nothing").toString()), 13)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );
    }

    @Override
    public @NonNull String getName() {
        return "Template Pools";
    }
}
