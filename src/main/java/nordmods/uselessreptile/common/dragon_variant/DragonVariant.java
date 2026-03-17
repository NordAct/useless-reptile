package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.Nullable;

public interface DragonVariant {
    Codec<DragonVariant> CODEC = URRegistries.VARIANT_TYPE.byNameCodec()
            .dispatch("type", DragonVariant::getType, DragonVariantType::codec);

    DragonVariantType<?> getType();

    CommonDragonVariantData common();

    @Nullable
    static DragonVariant get(DragonVariantType<?> type, String variant, Level world) {
        Registry<DragonVariant> registry = world.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_VARIANT);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.getType().equals(type) && dragonVariant.common().name().equals(variant))
                .findFirst()
                .orElse(null);
    }
}
