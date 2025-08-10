package nordmods.uselessreptile.common.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.block.DepletedMagmaBlock;

import java.util.function.Function;

public class URBlocks {
    public static final Block DEPLETED_MAGMA = register(
            "depleted_magma",
            DepletedMagmaBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .ticksRandomly()
                    .requiresTool()
                    .strength(0.4f)
                    .sounds(BlockSoundGroup.NETHERRACK));

    public static void init() {

    }

    private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, UselessReptile.id(id));
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }
}
