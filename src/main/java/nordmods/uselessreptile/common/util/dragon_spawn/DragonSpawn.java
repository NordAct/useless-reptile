package nordmods.uselessreptile.common.util.dragon_spawn;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DragonSpawn {
    private final String variant;
    private final SpawnConditions conditions;
    public static final Codec<DragonSpawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codecs.NON_EMPTY_STRING.fieldOf("variant").forGetter(DragonSpawn::variant),
                    SpawnConditions.CODEC.fieldOf("conditions").forGetter(DragonSpawn::conditions))
            .apply(instance, DragonSpawn::new));
    private static final Map<String, List<DragonSpawn>> dragonSpawnsHolder = new HashMap<>();

    protected DragonSpawn(String variant, SpawnConditions conditions) {
        this.variant = variant;
        this.conditions = conditions;
    }

    public String variant() {
        return variant;
    }

    public SpawnConditions conditions() {
        return conditions;
    }

    public static List<DragonSpawn> getAllVariants(String name) {
        return dragonSpawnsHolder.get(name);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void clearSpawns() {
        dragonSpawnsHolder.clear();
    }

    public static void addSpawn(EntityType<? extends URDragonEntity> type, DragonSpawn data) {
        addSpawn(EntityType.getId(type).getPath(), data);
    }

    public static void addSpawn(String name, DragonSpawn data) {
        List<DragonSpawn> content = DragonSpawn.dragonSpawnsHolder.get(name);
        if (content == null) content = new ArrayList<>();
        content.add(data);
        DragonSpawn.dragonSpawnsHolder.put(name, content);
    }

    public static void debugPrint() {
        for (Map.Entry<String, List<DragonSpawn>> entry : DragonSpawn.dragonSpawnsHolder.entrySet()) {
            for (DragonSpawn spawn : entry.getValue()) {
                UselessReptile.LOGGER.debug("{}: added spawn entry for variant \"{}\" with conditions: {}", entry.getKey(), spawn.variant(), spawn.conditions());
            }
        }
    }

    public static Set<Map.Entry<String, List<DragonSpawn>>> getEntries() {
        return DragonSpawn.dragonSpawnsHolder.entrySet();
    }

    //allowed - works as whitelist if presented and not empty
    //banned - works as blacklist if presented and not empty
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class SpawnConditions {
        private final int weight;
        @NotNull private final Optional<List<Codecs.TagEntryId>> allowedBiomes;
        @NotNull private final Optional<List<Codecs.TagEntryId>> bannedBiomes;
        @NotNull private final Optional<List<Codecs.TagEntryId>> allowedBlocks;
        @NotNull private final Optional<List<Codecs.TagEntryId>> bannedBlocks;
        @NotNull private final Optional<AltitudeRestriction> altitudeRestriction;

        private SpawnConditions(int weight, @NotNull Optional<List<Codecs.TagEntryId>> allowedBiomes, @NotNull Optional<List<Codecs.TagEntryId>> bannedBiomes, @NotNull Optional<List<Codecs.TagEntryId>> allowedBlocks, @NotNull Optional<List<Codecs.TagEntryId>> bannedBlocks, @NotNull Optional<AltitudeRestriction> altitudeRestriction) {
            this.weight = weight;
            this.allowedBiomes = allowedBiomes;
            this.bannedBiomes = bannedBiomes;
            this.allowedBlocks = allowedBlocks;
            this.bannedBlocks = bannedBlocks;
            this.altitudeRestriction = altitudeRestriction;
        }

        public static final Codec<SpawnConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(SpawnConditions::weight),
                        Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("allowed_biomes").forGetter(SpawnConditions::allowedBiomes),
                        Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("banned_biomes").forGetter(SpawnConditions::bannedBiomes),
                        Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("allowed_blocks").forGetter(SpawnConditions::allowedBlocks),
                        Codecs.TAG_ENTRY_ID.listOf().optionalFieldOf("banned_blocks").forGetter(SpawnConditions::bannedBlocks),
                        AltitudeRestriction.CODEC.optionalFieldOf("altitude").forGetter(SpawnConditions::altitudeRestriction))
                .apply(instance, (SpawnConditions::new)));

        public int weight() {
            return weight;
        }

        public Optional<List<Codecs.TagEntryId>> allowedBiomes() {
            return allowedBiomes;
        }

        public Optional<List<Codecs.TagEntryId>> bannedBiomes() {
            return bannedBiomes;
        }

        public Optional<List<Codecs.TagEntryId>> allowedBlocks() {
            return allowedBlocks;
        }

        public Optional<List<Codecs.TagEntryId>> bannedBlocks() {
            return bannedBlocks;
        }

        public Optional<AltitudeRestriction> altitudeRestriction() {
            return altitudeRestriction;
        }


        public static class AltitudeRestriction {
            private final Optional<Integer> min;
            private final Optional<Integer> max;

            private AltitudeRestriction(Optional<Integer> min, Optional<Integer> max) {
                this.min = min;
                this.max = max;
            }

            public static final Codec<AltitudeRestriction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.INT.optionalFieldOf("min").forGetter(altitudeRestriction -> altitudeRestriction.min),
                            Codec.INT.optionalFieldOf("max").forGetter(altitudeRestriction -> altitudeRestriction.max))
                    .apply(instance, AltitudeRestriction::new));

            public int min() {
                return min.orElse(Integer.MIN_VALUE);
            }

            public int max() {
                return max.orElse(Integer.MAX_VALUE);
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("Altitude: {");
                if (min.isPresent()) {
                    builder.append("Min: ").append(min());
                    if (max.isPresent()) builder.append(", ");
                }
                if (max.isPresent()) builder.append("Max: ").append(max());
                builder.append("}");

                return builder.toString();
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{Weight: ").append(weight());
            if (allowedBiomes().isPresent()) {
                builder.append(", Allowed Biomes: [");
                List<Codecs.TagEntryId> entryIdList = allowedBiomes().get();
                for (Codecs.TagEntryId entryId :entryIdList) {
                    if (!entryIdList.getFirst().equals(entryId)) builder.append(", ");
                    builder.append(entryId.toString());
                }
                builder.append("]");
            }

            if (bannedBiomes().isPresent()) {
                builder.append(", Banned Biomes: [");
                List<Codecs.TagEntryId> entryIdList = bannedBiomes().get();
                for (Codecs.TagEntryId entryId :entryIdList) {
                    if (!entryIdList.getFirst().equals(entryId)) builder.append(", ");
                    builder.append(entryId.toString());
                }
                builder.append("]");
            }

            if (allowedBlocks().isPresent()) {
                builder.append(", Allowed Blocks: [");
                List<Codecs.TagEntryId> entryIdList = allowedBlocks().get();
                for (Codecs.TagEntryId entryId :entryIdList) {
                    if (!entryIdList.getFirst().equals(entryId)) builder.append(", ");
                    builder.append(entryId.toString());
                }
                builder.append("]");
            }

            if (bannedBlocks().isPresent()) {
                builder.append(", Banned Blocks: [");
                List<Codecs.TagEntryId> entryIdList = bannedBlocks().get();
                for (Codecs.TagEntryId entryId :entryIdList) {
                    if (!entryIdList.getFirst().equals(entryId)) builder.append(", ");
                    builder.append(entryId.toString());
                }
                builder.append("]");
            }

            if (altitudeRestriction().isPresent()) {
                builder.append(", ").append(altitudeRestriction().get());
            }
            builder.append("}");
            return builder.toString();
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private String variant;
        private Integer weight;
        private List<Codecs.TagEntryId> allowedBiomes;
        private List<Codecs.TagEntryId> bannedBiomes;
        private List<Codecs.TagEntryId> allowedBlocks;
        private List<Codecs.TagEntryId> bannedBlocks;
        private Integer minAltitude;
        private Integer maxAltitude;

        private Builder() {}

        public DragonSpawn build() {
            if (variant == null) throw new IllegalStateException("Variant must be specified");
            if (weight == null) throw new IllegalStateException("Weight must be specified");
            Optional<List<Codecs.TagEntryId>> allowedBiomes = this.allowedBiomes != null ? Optional.of(this.allowedBiomes) : Optional.empty();
            Optional<List<Codecs.TagEntryId>> bannedBiomes = this.bannedBiomes != null ? Optional.of(this.bannedBiomes) : Optional.empty();
            Optional<List<Codecs.TagEntryId>> allowedBlocks = this.allowedBlocks != null ? Optional.of(this.allowedBlocks) : Optional.empty();
            Optional<List<Codecs.TagEntryId>> bannedBlocks = this.bannedBlocks != null ? Optional.of(this.bannedBlocks) : Optional.empty();
            Optional<SpawnConditions.AltitudeRestriction> altitudeRestriction;
            if (this.minAltitude != null || this.maxAltitude != null) {
                Optional<Integer> minAltitude = this.minAltitude != null ? Optional.of(this.minAltitude) : Optional.empty();
                Optional<Integer> maxAltitude = this.maxAltitude != null ? Optional.of(this.maxAltitude) : Optional.empty();
                altitudeRestriction = Optional.of(new SpawnConditions.AltitudeRestriction(minAltitude, maxAltitude));
            }
            else altitudeRestriction = Optional.empty();

            return new DragonSpawn(variant, new SpawnConditions(weight, allowedBiomes, bannedBiomes, allowedBlocks, bannedBlocks, altitudeRestriction));
        }

        //mandatory
        public Builder setVariant(String variant) {
            this.variant = variant;
            return this;
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
