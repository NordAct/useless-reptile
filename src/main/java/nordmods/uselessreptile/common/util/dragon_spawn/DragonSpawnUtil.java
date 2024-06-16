package nordmods.uselessreptile.common.util.dragon_spawn;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.ArrayList;
import java.util.List;

public class DragonSpawnUtil {

    public static List<DragonSpawn> getAllVariants(String name) {
        return DragonSpawn.dragonSpawnsHolder.get(name);
    }

    public static List<DragonSpawn> getAllVariants(URDragonEntity entity) {
        return DragonSpawnUtil.getAllVariants(entity.getDragonID());
    }

    public static boolean isBiomeInList(DragonSpawn.SpawnCondition.BiomeRestrictions list, WorldAccess world, BlockPos blockPos) {
        RegistryEntry<Biome> biome = world.getBiome(blockPos);
        List<String> id = list.hasBiomesByIdList() ? list.biomesById() : List.of();
        List<String> tags = list.hasBiomesByTagList() ?list.biomesByTag() : List.of();

        boolean isIn = false;
        for (String s : id) {
            Identifier name = Identifier.of(s);
            if (biome.matchesId(name)) {
                isIn = true;
                break;
            }
        }

        if (!isIn) for (String tag : tags) {
            Identifier name = Identifier.of(tag);
            if (biome.isIn(TagKey.of(RegistryKeys.BIOME, name))) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }

    public static boolean isBlockInList(DragonSpawn.SpawnCondition.BlockRestrictions list, WorldAccess world, BlockPos blockPos) {
        RegistryEntry<Block> block = world.getBlockState(blockPos.down()).getRegistryEntry();
        List<String> id = list.hasBlocksByIdList() ? list.blocksById() : List.of();
        List<String> tags = list.hasBlocksByTagList() ?list.blocksById() : List.of();

        boolean isIn = false;
        for (String s : id) {
            Identifier name = Identifier.of(s);
            if (block.matchesId(name)) {
                isIn = true;
                break;
            }
        }

        if (!isIn) for (String tag : tags) {
            Identifier name = Identifier.of(tag);
            if (block.isIn(TagKey.of(RegistryKeys.BLOCK, name))) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }

    public static void assignVariantFromList(URDragonEntity entity, List<DragonSpawn> variants) {
        int totalWeight = 0;
        for (DragonSpawn variant : variants) totalWeight += variant.condition().weight();
        if (totalWeight <= 0) {
            UselessReptile.LOGGER.warn("Failed to set variant for {} at {} as none can spawn there. Setting default", entity.getName().getString(), entity.getBlockPos());
            entity.setVariant(entity.getDefaultVariant());
            return;
        }

        int roll = entity.getRandom().nextInt(totalWeight);
        int previousBound = 0;

        for (DragonSpawn variant : variants) {
            if (roll >= previousBound && roll < previousBound + variant.condition().weight()) {
                entity.setVariant(variant.variant());
                break;
            }
            previousBound += variant.condition().weight();
        }
    }

    public static List<DragonSpawn> getAvailableVariants(WorldAccess world, URDragonEntity entity) {
        return getAvailableVariants(world, entity.getBlockPos(), entity.getDragonID());
    }

    public static List<DragonSpawn> getAvailableVariants(WorldAccess world, BlockPos pos, String name) {
        List<DragonSpawn> variants = getAllVariants(name);
        if (variants == null) throw new RuntimeException("Failed to get variants for " + name);

        List<DragonSpawn> allowedVariants = new ArrayList<>(variants.size());
        variants.forEach(variant -> {
            //altitude check
            if (variant.condition().altitudeRestriction().min() > pos.getY()
                    || pos.getY() > variant.condition().altitudeRestriction().max()) return;
            //banned biomes check (blacklist)
            if (variant.condition().hasBannedBiomes()
                    && isBiomeInList(variant.condition().bannedBiomes(), world, pos)) return;
            //allowed biomes check (whitelist)
            if (variant.condition().hasAllowedBiomes())
                if (!isBiomeInList(variant.condition().allowedBiomes(), world, pos)) return;
            //banned blocks check (blacklist)
            if (variant.condition().hasBannedBlocks()
                    && isBlockInList(variant.condition().bannedBlocks(), world, pos)) return;
            //allowed blocks check (whitelist)
            if (variant.condition().hasAllowedBlocks())
                if (!isBlockInList(variant.condition().allowedBlocks(), world, pos)) return;

            allowedVariants.add(variant);
        });
        return allowedVariants;
    }
}
