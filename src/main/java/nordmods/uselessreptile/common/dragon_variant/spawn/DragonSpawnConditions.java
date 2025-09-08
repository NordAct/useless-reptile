package nordmods.uselessreptile.common.dragon_variant.spawn;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public record DragonSpawnConditions(
        int weight,
        Optional<List<Codecs.TagEntryId>> allowedBiomes,
        Optional<List<Codecs.TagEntryId>> bannedBiomes,
        Optional<List<Codecs.TagEntryId>> allowedBlocks,
        Optional<List<Codecs.TagEntryId>> bannedBlocks,
        Optional<AltitudeRestriction> altitudeRestriction
) {

    public static final Codec<DragonSpawnConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(DragonSpawnConditions::weight),
                    Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("allowed_biomes").forGetter(DragonSpawnConditions::allowedBiomes),
                    Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("banned_biomes").forGetter(DragonSpawnConditions::bannedBiomes),
                    Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("allowed_blocks").forGetter(DragonSpawnConditions::allowedBlocks),
                    Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("banned_blocks").forGetter(DragonSpawnConditions::bannedBlocks),
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
                                stream -> Util.decodeFixedLengthArray(stream, 2).map(values -> new com.mojang.datafixers.util.Pair<>(Optional.of(values[0]), Optional.of(values[1]))),
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
        private List<Codecs.TagEntryId> allowedBiomes;
        private List<Codecs.TagEntryId> bannedBiomes;
        private List<Codecs.TagEntryId> allowedBlocks;
        private List<Codecs.TagEntryId> bannedBlocks;
        private Integer minAltitude;
        private Integer maxAltitude;

        private Builder() {}

        public DragonSpawnConditions build() {
            if (weight == null) throw new IllegalStateException("Weight must be specified");
            Optional<List<Codecs.TagEntryId>> allowedBiomes = this.allowedBiomes != null ? Optional.of(this.allowedBiomes) : Optional.empty();
            Optional<List<Codecs.TagEntryId>> bannedBiomes = this.bannedBiomes != null ? Optional.of(this.bannedBiomes) : Optional.empty();
            Optional<List<Codecs.TagEntryId>> allowedBlocks = this.allowedBlocks != null ? Optional.of(this.allowedBlocks) : Optional.empty();
            Optional<List<Codecs.TagEntryId>> bannedBlocks = this.bannedBlocks != null ? Optional.of(this.bannedBlocks) : Optional.empty();
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
        public Builder addAllowedBiome(RegistryKey<Biome> biomeRegistryKey) {
            if (allowedBiomes == null) allowedBiomes = new ArrayList<>();
            allowedBiomes.add(new Codecs.TagEntryId(biomeRegistryKey.getValue(), false));
            return this;
        }

        public Builder addAllowedBiomeTag(TagKey<Biome> biomeTagKey) {
            if (allowedBiomes == null) allowedBiomes = new ArrayList<>();
            allowedBiomes.add(new Codecs.TagEntryId(biomeTagKey.id(), true));
            return this;
        }


        //banned biomes
        public Builder addBannedBiome(RegistryKey<Biome> biomeRegistryKey) {
            if (bannedBiomes == null) bannedBiomes = new ArrayList<>();
            bannedBiomes.add(new Codecs.TagEntryId(biomeRegistryKey.getValue(), false));
            return this;
        }

        public Builder addBannedBiomeTag(TagKey<Biome> biomeTagKey) {
            if (bannedBiomes == null) bannedBiomes = new ArrayList<>();
            bannedBiomes.add(new Codecs.TagEntryId(biomeTagKey.id(), true));
            return this;
        }

        //allowed blocks
        public Builder addAllowedBlock(RegistryKey<Block> blockRegistryKey) {
            if (allowedBlocks == null) allowedBlocks = new ArrayList<>();
            allowedBlocks.add(new Codecs.TagEntryId(blockRegistryKey.getValue(), false));
            return this;
        }

        public Builder addAllowedBlockTag(TagKey<Block> blockTagKey) {
            if (allowedBlocks == null) allowedBlocks = new ArrayList<>();
            allowedBlocks.add(new Codecs.TagEntryId(blockTagKey.id(), true));
            return this;
        }

        //banned blocks
        public Builder addBannedBlock(RegistryKey<Block> blockRegistryKey) {
            if (bannedBlocks == null) bannedBlocks = new ArrayList<>();
            bannedBlocks.add(new Codecs.TagEntryId(blockRegistryKey.getValue(), false));
            return this;
        }

        public Builder addBannedBlockTag(TagKey<Block> blockTagKey) {
            if (bannedBlocks == null) bannedBlocks = new ArrayList<>();
            bannedBlocks.add(new Codecs.TagEntryId(blockTagKey.id(), true));
            return this;
        }
    }
}
