package nordmods.uselessreptile.common.init;

import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.block.DepletedMagmaBlock;

import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class URBlocks {
    public static final Block DEPLETED_MAGMA = register(
            "depleted_magma",
            DepletedMagmaBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .randomTicks()
                    .requiresCorrectToolForDrops()
                    .strength(0.4f)
                    .sound(SoundType.NETHERRACK));

    public static void init() {

    }

    private static Block register(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, UselessReptile.id(id));
        Block block = factory.apply(settings.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
