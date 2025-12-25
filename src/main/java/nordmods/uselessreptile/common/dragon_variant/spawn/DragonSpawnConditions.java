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
        Optional<AltitudeRestriction> altitudeRestriction
) {

    public static final Codec<DragonSpawnConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(DragonSpawnConditions::weight),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("allowed_biomes").forGetter(DragonSpawnConditions::allowedBiomes),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("banned_biomes").forGetter(DragonSpawnConditions::bannedBiomes),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("allowed_blocks").forGetter(DragonSpawnConditions::allowedBlocks),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("banned_blocks").forGetter(DragonSpawnConditions::bannedBlocks),
                    AltitudeRestriction.CODEC.optionalFieldOf("altitude").forGetter(DragonSpawnConditions::altitudeRestriction))
            .apply(instance, (DragonSpawnConditions::new)));

    public record AltitudeRestriction(Pair<Optional<Integer>, Optional<Integer>> range) {
        public AltitudeRestriction(Optional<Integer> min, Optional<Integer> max) {
            this(new Pair<>(min, max));
        }

        public static final Codec<Pair<Optional<Integer>, Optional<Integer>>> PAIR_CODEC = Codec.withAlternative(
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

        public static final Codec<AltitudeRestriction> CODEC = PAIR_CODEC
                .comapFlatMap(
                        range1 -> DataResult.success(new AltitudeRestriction(range1)),
                        AltitudeRestriction::range
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

        private Builder() {}

        public DragonSpawnConditions build() {
            if (weight == null) throw new IllegalStateException("Weight must be specified");
            Optional<List<ExtraCodecs.TagOrElementLocation>> allowedBiomes = this.allowedBiomes != null ? Optional.of(this.allowedBiomes) : Optional.empty();
            Optional<List<ExtraCodecs.TagOrElementLocation>> bannedBiomes = this.bannedBiomes != null ? Optional.of(this.bannedBiomes) : Optional.empty();
            Optional<List<ExtraCodecs.TagOrElementLocation>> allowedBlocks = this.allowedBlocks != null ? Optional.of(this.allowedBlocks) : Optional.empty();
            Optional<List<ExtraCodecs.TagOrElementLocation>> bannedBlocks = this.bannedBlocks != null ? Optional.of(this.bannedBlocks) : Optional.empty();
            Optional<AltitudeRestriction> altitudeRestriction;
            if (this.minAltitude != null || this.maxAltitude != null) {
                Optional<Integer> minAltitude = this.minAltitude != null ? Optional.of(this.minAltitude) : Optional.empty();
                Optional<Integer> maxAltitude = this.maxAltitude != null ? Optional.of(this.maxAltitude) : Optional.empty();
                altitudeRestriction = Optional.of(new AltitudeRestriction(minAltitude, maxAltitude));
            }
            else altitudeRestriction = Optional.empty();

            return new DragonSpawnConditions(weight, allowedBiomes, bannedBiomes, allowedBlocks, bannedBlocks, altitudeRestriction);
        }

        public Builder setWeight(Integer weight) {
            this.weight = weight;
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
    }
}
