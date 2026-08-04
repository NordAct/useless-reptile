package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface DragonVariant {
    Codec<DragonVariant> CODEC = URRegistries.VARIANT_TYPE.byNameCodec()
            .dispatch("type", DragonVariant::getType, DragonVariantType::codec);

    DragonVariantType<?> getType();

    CommonDragonVariantData common();

    @Nullable
    static DragonVariant get(DragonVariantType<?> type, String variant, HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<DragonVariant> registry = provider.lookupOrThrow(URResourceKeys.DRAGON_VARIANT);
        return registry.listElements()
                .map(Holder.Reference::value)
                .filter(dragonVariant -> dragonVariant.getType().equals(type) && dragonVariant.common().name().equals(variant))
                .findFirst()
                .orElse(null);
    }

    static List<DragonVariant> getSameType(DragonVariantType<?> type, HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<DragonVariant> registry = provider.lookupOrThrow(URResourceKeys.DRAGON_VARIANT);
        return registry.listElements()
                .map(Holder.Reference::value)
                .filter(dragonVariant -> dragonVariant.getType().equals(type))
                .toList();
    }
}
