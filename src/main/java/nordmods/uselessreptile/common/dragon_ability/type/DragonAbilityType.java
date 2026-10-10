package nordmods.uselessreptile.common.dragon_ability.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.init.URRegistries;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class DragonAbilityType<T extends DragonAbility> {
    private final MapCodec<T> codec;
    private final Holder.Reference<DragonAbilityType<?>> builtInRegistryHolder = URRegistries.ABILITY_TYPE.createIntrusiveHolder(this);

    public DragonAbilityType(MapCodec<T> codec) {
        this.codec = codec;
    }

    public String getTranslationKey() {
        return Util.makeDescriptionId("ability", URRegistries.ABILITY_TYPE.getKey(this));
    }

    public Identifier getId() {
        return URRegistries.ABILITY_TYPE.getKey(this);
    }

    @Nullable
    public static DragonAbilityType<?> fromId(Identifier id) {
        return URRegistries.ABILITY_TYPE.getValue(id);
    }

    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DragonAbilityType) obj;
        return Objects.equals(this.codec, that.codec);
    }

    public Holder.Reference<DragonAbilityType<?>> builtInRegistryHolder() {
        return builtInRegistryHolder;
    }
}
