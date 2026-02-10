package nordmods.uselessreptile.common.dragon_variant.spawn;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public record DragonSpawnConditions(
        int weight,
        Optional<List<ExtraCodecs.TagOrElementLocation>> allowedBiomes,
        Optional<List<ExtraCodecs.TagOrElementLocation>> bannedBiomes,
        Optional<List<ExtraCodecs.TagOrElementLocation>> allowedBlocks,
        Optional<List<ExtraCodecs.TagOrElementLocation>> bannedBlocks,
        Optional<IntRange> altitudeRestriction,
        Optional<LightLevelRestriction> lightLevelRestriction,
        Optional<Pair<Integer, Integer>> timePeriod,
        Optional<Spacing> spacing
) {
    private static final Codec<Pair<Integer, Integer>> INT_PAIR_CODEC = Codec.INT_STREAM
            .comapFlatMap(
                    stream -> Util.fixedSize(stream, 2).map(values -> new Pair<>(values[0], values[1])),
                    pair -> IntStream.of(pair.getFirst(), pair.getSecond())
            ).stable();

    public static final Codec<DragonSpawnConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(DragonSpawnConditions::weight),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("allowed_biomes").forGetter(DragonSpawnConditions::allowedBiomes),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("banned_biomes").forGetter(DragonSpawnConditions::bannedBiomes),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("allowed_blocks").forGetter(DragonSpawnConditions::allowedBlocks),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("banned_blocks").forGetter(DragonSpawnConditions::bannedBlocks),
                    IntRange.CODEC.optionalFieldOf("altitude").forGetter(DragonSpawnConditions::altitudeRestriction),
                    LightLevelRestriction.CODEC.optionalFieldOf("light_level").forGetter(DragonSpawnConditions::lightLevelRestriction),
                    INT_PAIR_CODEC.optionalFieldOf("time_period").forGetter(DragonSpawnConditions::timePeriod),
                    Spacing.CODEC.optionalFieldOf("spacing").forGetter(DragonSpawnConditions::spacing)
            ).apply(instance, (DragonSpawnConditions::new)));

    public record LightLevelRestriction(Optional<IntRange> blockLightLevel, Optional<IntRange> skyLightLevel) {
        public static final Codec<LightLevelRestriction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IntRange.CODEC.optionalFieldOf("block").forGetter(LightLevelRestriction::blockLightLevel),
                IntRange.CODEC.optionalFieldOf("sky").forGetter(LightLevelRestriction::blockLightLevel)
        ).apply(instance, LightLevelRestriction::new));
    }

    public record Spacing(float range, int maxEntityCount) {
        public static final Codec<Spacing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("range").forGetter(Spacing::range),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_entity_count").forGetter(Spacing::maxEntityCount)
        ).apply(instance, (Spacing::new)));
    }

    public record IntRange(Pair<Optional<Integer>, Optional<Integer>> range) {
        public IntRange(Optional<Integer> min, Optional<Integer> max) {
            this(new Pair<>(min, max));
        }

        public static final Codec<Pair<Optional<Integer>, Optional<Integer>>> MIN_MAX_INT_CODEC = Codec.withAlternative(
                Codec.pair(
                        Codec.INT.optionalFieldOf("min").codec(),
                        Codec.INT.optionalFieldOf("max").codec()
                ),
                Codec.INT_STREAM
                        .comapFlatMap(
                                stream -> Util.fixedSize(stream, 2).map(values -> new Pair<>(Optional.of(values[0]), Optional.of(values[1]))),
                                pair -> IntStream.of(pair.getFirst().orElse(Integer.MIN_VALUE), pair.getSecond().orElse(Integer.MAX_VALUE))
                        )
                        .stable()
        );

        public static final Codec<IntRange> CODEC = MIN_MAX_INT_CODEC
                .comapFlatMap(
                        range1 -> DataResult.success(new IntRange(range1)),
                        IntRange::range
                ).stable();

        public int getMin() {
            return range.getFirst().orElse(Integer.MIN_VALUE);
        }

        public int getMax() {
            return range.getSecond().orElse(Integer.MAX_VALUE);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    //allowed - works as whitelist if presented and not empty
    //banned - works as blacklist if presented and not empty
    @SuppressWarnings("unused")
    public static class Builder {
        private Integer weight;
        private List<ExtraCodecs.TagOrElementLocation> allowedBiomes;
        private List<ExtraCodecs.TagOrElementLocation> bannedBiomes;
        private List<ExtraCodecs.TagOrElementLocation> allowedBlocks;
        private List<ExtraCodecs.TagOrElementLocation> bannedBlocks;
        private Integer minAltitude;
        private Integer maxAltitude;
        private Integer minBlockLightLevel;
        private Integer maxBlockLightLevel;
        private Integer minSkyLightLevel;
        private Integer maxSkyLightLevel;
        private Pair<Integer, Integer> timePeriod;
        private Spacing spacing;

        private Builder() {}

        public DragonSpawnConditions build() {
            if (weight == null) throw new IllegalStateException("Weight must be specified");
            Optional<List<ExtraCodecs.TagOrElementLocation>> allowedBiomes = Optional.ofNullable(this.allowedBiomes);
            Optional<List<ExtraCodecs.TagOrElementLocation>> bannedBiomes = Optional.ofNullable(this.bannedBiomes);
            Optional<List<ExtraCodecs.TagOrElementLocation>> allowedBlocks = Optional.ofNullable(this.allowedBlocks);
            Optional<List<ExtraCodecs.TagOrElementLocation>> bannedBlocks = Optional.ofNullable(this.bannedBlocks);
            Optional<IntRange> altitudeRestriction;
            if (this.minAltitude != null || this.maxAltitude != null) {
                Optional<Integer> minAltitude = Optional.ofNullable(this.minAltitude);
                Optional<Integer> maxAltitude = Optional.ofNullable(this.maxAltitude);
                altitudeRestriction = Optional.of(new IntRange(minAltitude, maxAltitude));
            }
            else altitudeRestriction = Optional.empty();

            Optional<LightLevelRestriction> lightLevelRestriction;
            if (this.minBlockLightLevel != null || this.maxBlockLightLevel != null || this.minSkyLightLevel != null || this.maxSkyLightLevel != null) {
                Optional<Integer> minBlockLightLevel = Optional.ofNullable(this.minBlockLightLevel);
                Optional<Integer> maxBlockLightLevel = Optional.ofNullable(this.maxBlockLightLevel);
                Optional<Integer> minSkyLightLevel = Optional.ofNullable(this.minSkyLightLevel);
                Optional<Integer> maxSkyLightLevel = Optional.ofNullable(this.maxSkyLightLevel);
                lightLevelRestriction = Optional.of(
                        new LightLevelRestriction(
                                Optional.of(new IntRange(minBlockLightLevel, maxBlockLightLevel)),
                                Optional.of(new IntRange(minSkyLightLevel, maxSkyLightLevel))
                        )
                );
            }
            else lightLevelRestriction = Optional.empty();

            Optional<Pair<Integer, Integer>> timePeriod = Optional.ofNullable(this.timePeriod);
            Optional<Spacing> spacing = Optional.ofNullable(this.spacing);

            return new DragonSpawnConditions(weight, allowedBiomes, bannedBiomes, allowedBlocks, bannedBlocks, altitudeRestriction, lightLevelRestriction, timePeriod, spacing);
        }

        public Builder setWeight(Integer weight) {
            this.weight = weight;
            return this;
        }

        public Builder setTimePeriod(int min, int max) {
            this.timePeriod = new Pair<>(min, max);
            return this;
        }

        public Builder setSpacing(float range, int maxEntityCount) {
            this.spacing = new Spacing(range, maxEntityCount);
            return this;
        }

        //altitude
        public Builder setMinAltitude(Integer minAltitude) {
            this.minAltitude = minAltitude;
            return this;
        }

        public Builder setMaxAltitude(Integer maxAltitude) {
            this.maxAltitude = maxAltitude;
            return this;
        }

        //allowed biomes
        public Builder addAllowedBiome(ResourceKey<Biome> biomeRegistryKey) {
            if (allowedBiomes == null) allowedBiomes = new ArrayList<>();
            allowedBiomes.add(new ExtraCodecs.TagOrElementLocation(biomeRegistryKey.identifier(), false));
            return this;
        }

        public Builder addAllowedBiomeTag(TagKey<Biome> biomeTagKey) {
            if (allowedBiomes == null) allowedBiomes = new ArrayList<>();
            allowedBiomes.add(new ExtraCodecs.TagOrElementLocation(biomeTagKey.location(), true));
            return this;
        }


        //banned biomes
        public Builder addBannedBiome(ResourceKey<Biome> biomeRegistryKey) {
            if (bannedBiomes == null) bannedBiomes = new ArrayList<>();
            bannedBiomes.add(new ExtraCodecs.TagOrElementLocation(biomeRegistryKey.identifier(), false));
            return this;
        }

        public Builder addBannedBiomeTag(TagKey<Biome> biomeTagKey) {
            if (bannedBiomes == null) bannedBiomes = new ArrayList<>();
            bannedBiomes.add(new ExtraCodecs.TagOrElementLocation(biomeTagKey.location(), true));
            return this;
        }

        //allowed blocks
        public Builder addAllowedBlock(ResourceKey<Block> blockRegistryKey) {
            if (allowedBlocks == null) allowedBlocks = new ArrayList<>();
            allowedBlocks.add(new ExtraCodecs.TagOrElementLocation(blockRegistryKey.identifier(), false));
            return this;
        }

        public Builder addAllowedBlockTag(TagKey<Block> blockTagKey) {
            if (allowedBlocks == null) allowedBlocks = new ArrayList<>();
            allowedBlocks.add(new ExtraCodecs.TagOrElementLocation(blockTagKey.location(), true));
            return this;
        }

        //banned blocks
        public Builder addBannedBlock(ResourceKey<Block> blockRegistryKey) {
            if (bannedBlocks == null) bannedBlocks = new ArrayList<>();
            bannedBlocks.add(new ExtraCodecs.TagOrElementLocation(blockRegistryKey.identifier(), false));
            return this;
        }

        public Builder addBannedBlockTag(TagKey<Block> blockTagKey) {
            if (bannedBlocks == null) bannedBlocks = new ArrayList<>();
            bannedBlocks.add(new ExtraCodecs.TagOrElementLocation(blockTagKey.location(), true));
            return this;
        }

        //light levels
        public Builder setMinBlockLightLevel(Integer minBlockLightLevel) {
            this.minBlockLightLevel = minBlockLightLevel;
            return this;
        }

        public Builder setMaxBlockLightLevel(Integer maxBlockLightLevel) {
            this.maxBlockLightLevel = maxBlockLightLevel;
            return this;
        }

        public Builder setMinSkyLightLevel(Integer minSkyLightLevel) {
            this.minSkyLightLevel = minSkyLightLevel;
            return this;
        }

        public Builder setMaxSkyLightLevel(Integer maxSkyLightLevel) {
            this.maxSkyLightLevel = maxSkyLightLevel;
            return this;
        }
    }
}
