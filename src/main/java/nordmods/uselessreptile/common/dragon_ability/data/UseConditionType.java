package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.common.init.URRegistries;
import org.jspecify.annotations.Nullable;

public record UseConditionType<T extends UseCondition>(MapCodec<T> codec) {
    public Identifier getId() {
        return URRegistries.USE_CONDITION_TYPE.getKey(this);
    }

    @Nullable
    public static UseConditionType<?> fromId(Identifier id) {
        return URRegistries.USE_CONDITION_TYPE.getValue(id);
    }
}
