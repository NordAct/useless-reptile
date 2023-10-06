package nordmods.uselessreptile.common.util.dragonVariant;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DragonVariant(String name, int weight, @Nullable BiomeRestrictions allowedBiomes, @Nullable BiomeRestrictions bannedBiomes, AltitudeRestriction altitudeRestriction) {
    //allowed biomes - works as whitelist if presented
    //banned biomes - works as blacklist if presented
    public boolean hasAllowedBiomes() {
        return allowedBiomes != null && (allowedBiomes.hasBiomesByIdList() || allowedBiomes.hasBiomesByTagList());
    }

    public boolean hasBannedBiomes() {
        return bannedBiomes != null && (bannedBiomes.hasBiomesByIdList() || bannedBiomes.hasBiomesByTagList());
    }

    public record BiomeRestrictions(List<String> biomesById, List<String> biomesByTag) {
        public boolean hasBiomesByIdList() {
            return biomesById != null && !biomesById.isEmpty();
        }

        public boolean hasBiomesByTagList() {
            return biomesByTag != null && !biomesByTag.isEmpty();
        }

    }

    public record AltitudeRestriction(int min, int max) {}
}
