package nordmods.uselessreptile.common.dragon_variant.spawn;

import net.minecraft.block.Block;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistryKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DragonSpawnUtil {
    public static boolean isBiomeInList(List<Codecs.TagEntryId> list, RegistryEntry<Biome> biome) {
        for (Codecs.TagEntryId tagEntryId : list) {
            if (tagEntryId.tag()) {
                if (biome.isIn(TagKey.of(RegistryKeys.BIOME, tagEntryId.id()))) return true;
            } else if (biome.matchesId(tagEntryId.id())) return true;
        }

        return false;
    }

    public static boolean isBlockInList(List<Codecs.TagEntryId> list, RegistryEntry<Block> block) {
        for (Codecs.TagEntryId tagEntryId : list) {
            if (tagEntryId.tag()) {
                if (block.isIn(TagKey.of(RegistryKeys.BLOCK, tagEntryId.id()))) return true;
            } else if (block.matchesId(tagEntryId.id())) return true;
        }

        return false;
    }

    public static void assignAvailableVariant(URDragonEntity entity, SpawnReason spawnReason) {
        BlockPos pos = entity.getBlockPos();
        WorldAccess world = entity.getEntityWorld();
        Identifier id = entity.getDragonId();
        Stream<DragonVariant> variantStream = getAvailableVariants(world, pos, id);
        boolean canWarn = spawnReason == SpawnReason.NATURAL
                || spawnReason == SpawnReason.EVENT
                || spawnReason == SpawnReason.CHUNK_GENERATION
                || spawnReason == SpawnReason.BREEDING;

        DynamicRegistryManager registryManager = entity.getEntityWorld().getRegistryManager();

        List<Pair<String, Integer>> variants = new ArrayList<>();
        variantStream.forEach(variant -> {
            registryManager.getOrThrow(URRegistryKeys.DRAGON_SPAWN_CONDITIONS).get(variant.spawnConditions().get()).forEach(conditions -> {
                if (checkConditions(conditions, world, pos)) variants.add(new Pair<>(variant.name(), conditions.weight()));
            });
        });

        if (variants.isEmpty()) {
            if (canWarn) UselessReptile.LOGGER.warn("Failed to set variant for {} at {} as none can spawn there. Setting default", entity.getName().getString(), entity.getBlockPos());
            entity.setVariant(entity.getDefaultVariant());
            return;
        }

        int totalWeight = 0;
        for (Pair<String, Integer> variant : variants) totalWeight += variant.getRight();

        int roll = entity.getRandom().nextInt(totalWeight);
        int previousBound = 0;
        for (Pair<String, Integer> variant : variants) {
            if (roll >= previousBound && roll < previousBound + variant.getRight()) {
                entity.setVariant(variant.getLeft());
                break;
            }
            previousBound += variant.getRight();
        }
    }

    public static Stream<DragonVariant> getAvailableVariants(WorldAccess world, BlockPos pos, Identifier dragonId) {
        DynamicRegistryManager registryManager = world.getRegistryManager();
        return getAllVariants(world, dragonId).filter(variant -> {
           for (DragonSpawnConditions conditions : registryManager.getOrThrow(URRegistryKeys.DRAGON_SPAWN_CONDITIONS).get(variant.spawnConditions().get())) {
               if (checkConditions(conditions, world, pos)) return true;
           }
           return false;
        });
    }

    /**
     * @param world WorldAccess
     * @param dragonId Identifier of the dragon
     * @return Stream of all variants that can spawn naturally
     */
    public static Stream<DragonVariant> getAllVariants(WorldAccess world, Identifier dragonId) {
        DynamicRegistryManager registryManager = world.getRegistryManager();
        return registryManager.getOrThrow(URRegistryKeys.DRAGON_VARIANT).stream()
                .filter(variant -> variant.dragonId().equals(dragonId))
                .filter(variant -> {
                    if (variant.spawnConditions().isPresent()) {
                        List<DragonSpawnConditions> conditionsList = registryManager.getOrThrow(URRegistryKeys.DRAGON_SPAWN_CONDITIONS).get(variant.spawnConditions().get());
                        if (conditionsList == null) return false;
                        for (DragonSpawnConditions conditions : conditionsList) if (conditions.weight() > 0) return true;
                    }
                    return false;
                });
    }

    private static boolean checkConditions(DragonSpawnConditions conditions, WorldAccess world, BlockPos pos) {
        //altitude check
        if (conditions.altitudeRestriction().isPresent()) {
            DragonSpawnConditions.AltitudeRestriction restriction = conditions.altitudeRestriction().get();
            if (restriction.getMin() > pos.getY() || restriction.getMax() <= pos.getY()) return false;
        }

        //allowed tagEntries check (whitelist)
        RegistryEntry<Biome> biome = world.getBiome(pos);
        if (conditions.allowedBiomes().isPresent()) {
            List <Codecs.TagEntryId> list = conditions.allowedBiomes().get();
            if (!list.isEmpty() && !isBiomeInList(list, biome)) return false;
        }
        //banned tagEntries check (blacklist)
        if (conditions.bannedBiomes().isPresent()) {
            List <Codecs.TagEntryId> list = conditions.bannedBiomes().get();
            if (!list.isEmpty() && isBiomeInList(list, biome)) return false;
        }

        RegistryEntry<Block> block = world.getBlockState(pos.down()).getRegistryEntry();
        //allowed blocks check (whitelist)
        if (conditions.allowedBlocks().isPresent()) {
            List <Codecs.TagEntryId> list = conditions.allowedBlocks().get();
            if (!list.isEmpty() && !isBlockInList(list, block)) return false;
        }
        //banned blocks check (blacklist)
        if (conditions.bannedBlocks().isPresent()) {
            List <Codecs.TagEntryId> list = conditions.bannedBlocks().get();
            if (!list.isEmpty() && isBlockInList(list, block)) return false;
        }

        return true;
    }
}
