package nordmods.uselessreptile.common.entity.ability;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import nordmods.uselessreptile.common.init.URRegistries;
import org.jspecify.annotations.Nullable;

public record DragonAbilityType<T extends DragonAbility>(MapCodec<T> codec) {
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
}
