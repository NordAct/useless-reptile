package nordmods.uselessreptile.datagen.data.structure;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendLoot;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.block.DragonPlaceholderBlock;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.datagen.data.loot.URChestLootTableProvider;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URProcessorsListProvider extends FabricDynamicRegistryProvider {
    public URProcessorsListProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final ResourceKey<StructureProcessorList> REMOVE_DRAGON_PLACEHOLDER = ResourceKey.create(Registries.PROCESSOR_LIST, UselessReptile.id("remove_dragon_placeholder"));
    public static final ResourceKey<StructureProcessorList> LIGHTNING_CHASER_NEST_APPLY_LOOT = ResourceKey.create(Registries.PROCESSOR_LIST, UselessReptile.id("lightning_chaser_nest/apply_loot"));

    @Override
    protected void configure(HolderLookup.@NonNull Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(Registries.PROCESSOR_LIST));
    }

    public static void register(BootstrapContext<StructureProcessorList> bootstrapContext) {
        bootstrapContext.register(
                REMOVE_DRAGON_PLACEHOLDER,
                new StructureProcessorList(
                        List.of(
                                new RuleProcessor(
                                        List.of(
                                                new ProcessorRule(
                                                        new BlockStateMatchTest(URBlocks.DRAGON_PLACEHOLDER.defaultBlockState()),
                                                        AlwaysTrueTest.INSTANCE,
                                                        URBlocks.DRAGON_PLACEHOLDER.defaultBlockState().setValue(DragonPlaceholderBlock.CAN_CREATE_DRAGON, true)
                                                )
                                        )
                                )
                        )
                )
        );
        bootstrapContext.register(
                LIGHTNING_CHASER_NEST_APPLY_LOOT,
                new StructureProcessorList(
                        List.of(
                                new RuleProcessor(
                                        List.of(
                                                new ProcessorRule(
                                                        new BlockMatchTest(Blocks.CHEST),
                                                        AlwaysTrueTest.INSTANCE,
                                                        PosAlwaysTrueTest.INSTANCE,
                                                        Blocks.CHEST.defaultBlockState(),
                                                        new AppendLoot(URChestLootTableProvider.LIGHTNING_CHASER_NEST)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public @NonNull String getName() {
        return "Structure Processors List";
    }
}
