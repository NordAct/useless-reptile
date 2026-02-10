package nordmods.uselessreptile.common.dragon_variant.spawn;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URResourceKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DragonSpawnUtil {
    public static boolean isBiomeInList(List<ExtraCodecs.TagOrElementLocation> list, Holder<Biome> biome) {
        for (ExtraCodecs.TagOrElementLocation tagEntryId : list) {
            if (tagEntryId.tag()) {
                if (biome.is(TagKey.create(Registries.BIOME, tagEntryId.id()))) return true;
            } else if (biome.is(tagEntryId.id())) return true;
        }

        return false;
    }

    public static boolean isBlockInList(List<ExtraCodecs.TagOrElementLocation> list, Holder<Block> block) {
        for (ExtraCodecs.TagOrElementLocation tagEntryId : list) {
            if (tagEntryId.tag()) {
                if (block.is(TagKey.create(Registries.BLOCK, tagEntryId.id()))) return true;
            } else if (block.is(tagEntryId.id())) return true;
        }

        return false;
    }

    public static void assignAvailableVariant(URDragonEntity entity, EntitySpawnReason spawnReason) {
        BlockPos pos = entity.blockPosition();
        LevelAccessor world = entity.level();
        Identifier id = entity.getDragonId();
        Stream<DragonVariant> variantStream = getAvailableVariants(world, pos, id);
        boolean canWarn = spawnReason == EntitySpawnReason.NATURAL
                || spawnReason == EntitySpawnReason.EVENT
                || spawnReason == EntitySpawnReason.CHUNK_GENERATION
                || spawnReason == EntitySpawnReason.BREEDING;

        RegistryAccess registryManager = entity.level().registryAccess();

        List<Tuple<String, Integer>> variants = new ArrayList<>();
        variantStream.forEach(variant -> {
            registryManager.lookupOrThrow(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getValue(variant.spawnConditions().get()).forEach(conditions -> {
                if (checkConditions(conditions, world, pos)) variants.add(new Tuple<>(variant.name(), conditions.weight()));
            });
        });

        if (variants.isEmpty()) {
            if (canWarn) UselessReptile.LOGGER.warn("Failed to set variant for {} at {} as none can spawn there. Setting default", entity.getName().getString(), entity.blockPosition());
            entity.setVariant(entity.getDefaultVariant());
            return;
        }

        int totalWeight = 0;
        for (Tuple<String, Integer> variant : variants) totalWeight += variant.getB();

        int roll = entity.getRandom().nextInt(totalWeight);
        int previousBound = 0;
        for (Tuple<String, Integer> variant : variants) {
            if (roll >= previousBound && roll < previousBound + variant.getB()) {
                entity.setVariant(variant.getA());
                break;
            }
            previousBound += variant.getB();
        }
    }

    public static Stream<DragonVariant> getAvailableVariants(LevelAccessor world, BlockPos pos, Identifier dragonId) {
        RegistryAccess registryManager = world.registryAccess();
        return getAllVariants(world, dragonId).filter(variant -> {
           for (DragonSpawnConditions conditions : registryManager.lookupOrThrow(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getValue(variant.spawnConditions().get())) {
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
    public static Stream<DragonVariant> getAllVariants(LevelAccessor world, Identifier dragonId) {
        RegistryAccess registryManager = world.registryAccess();
        return registryManager.lookupOrThrow(URResourceKeys.DRAGON_VARIANT).stream()
                .filter(variant -> variant.dragonId().equals(dragonId))
                .filter(variant -> {
                    if (variant.spawnConditions().isPresent()) {
                        List<DragonSpawnConditions> conditionsList = registryManager.lookupOrThrow(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getValue(variant.spawnConditions().get());
                        if (conditionsList == null) return false;
                        for (DragonSpawnConditions conditions : conditionsList) if (conditions.weight() > 0) return true;
                    }
                    return false;
                });
    }

    private static boolean checkConditions(DragonSpawnConditions conditions, LevelAccessor world, BlockPos pos) {
        //altitude check
        if (conditions.altitudeRestriction().isPresent()) {
            DragonSpawnConditions.IntRange restriction = conditions.altitudeRestriction().get();
            if (restriction.getMin() > pos.getY() || restriction.getMax() < pos.getY()) return false;
        }

        //time of day check
        if (conditions.timePeriod().isPresent()) {
            Pair<Integer, Integer> restriction = conditions.timePeriod().get();
            long time = world.getGameTime() % 24000;
            if (restriction.getFirst() > time || restriction.getSecond() < time) return false;
        }

        //light level check
        if (conditions.lightLevelRestriction().isPresent()) {
            DragonSpawnConditions.LightLevelRestriction restriction = conditions.lightLevelRestriction().get();
            if (restriction.skyLightLevel().isPresent()) {
                DragonSpawnConditions.IntRange skyRestriction = restriction.skyLightLevel().get();
                int light = world.getBrightness(LightLayer.SKY, pos);
                if (skyRestriction.getMin() > light || skyRestriction.getMax() < light) return false;
            }

            if (restriction.blockLightLevel().isPresent()) {
                DragonSpawnConditions.IntRange blockRestriction = restriction.blockLightLevel().get();
                int light = world.getBrightness(LightLayer.BLOCK, pos);
                if (blockRestriction.getMin() > light
                        || blockRestriction.getMax() < light)
                    return false;
            }
        }

        //allowed tagEntries check (whitelist)
        Holder<Biome> biome = world.getBiome(pos);
        if (conditions.allowedBiomes().isPresent()) {
            List <ExtraCodecs.TagOrElementLocation> list = conditions.allowedBiomes().get();
            if (!list.isEmpty() && !isBiomeInList(list, biome)) return false;
        }
        //banned tagEntries check (blacklist)
        if (conditions.bannedBiomes().isPresent()) {
            List <ExtraCodecs.TagOrElementLocation> list = conditions.bannedBiomes().get();
            if (!list.isEmpty() && isBiomeInList(list, biome)) return false;
        }

        Holder<Block> block = world.getBlockState(pos.below()).getBlockHolder();
        //allowed blocks check (whitelist)
        if (conditions.allowedBlocks().isPresent()) {
            List <ExtraCodecs.TagOrElementLocation> list = conditions.allowedBlocks().get();
            if (!list.isEmpty() && !isBlockInList(list, block)) return false;
        }
        //banned blocks check (blacklist)
        if (conditions.bannedBlocks().isPresent()) {
            List <ExtraCodecs.TagOrElementLocation> list = conditions.bannedBlocks().get();
            if (!list.isEmpty() && isBlockInList(list, block)) return false;
        }

        return true;
    }
}
