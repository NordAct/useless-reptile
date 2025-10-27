package nordmods.uselessreptile.mixin.client.render;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.util.duck.DragonPassengerRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements DragonPassengerRenderState {
    @Unique private boolean isRidingDragon;
    @Unique @Nullable private UUID uuid;

    @Override
    public boolean useless_reptile$isRidingDragon() {
        return isRidingDragon;
    }

    @Override
    public void useless_reptile$setRidingDragon(boolean state) {
        isRidingDragon = state;
    }

    @Override
    public @Nullable UUID useless_reptile$getUUID() {
        return uuid;
    }

    @Override
    public void useless_reptile$setUUID(UUID state) {
        uuid = state;
    }
}
