package nordmods.uselessreptile.common.data;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DragonVariant(String name, int weight, @Nullable AllowedBiomes allowedBiomes) {

    public boolean hasRestrictions() {
        return allowedBiomes != null
                && (allowedBiomes.hasBiomesByIdList() || allowedBiomes.hasBiomesByTagList());
    }

    public record AllowedBiomes(List<String> biomesById, List<String> biomesByTag) {

        public boolean hasBiomesByIdList() {
            return biomesById != null && !biomesById.isEmpty();
        }

        public boolean hasBiomesByTagList() {
            return biomesByTag != null && !biomesByTag.isEmpty();
        }

    }
}
