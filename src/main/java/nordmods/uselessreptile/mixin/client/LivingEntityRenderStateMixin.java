package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.util.duck.DragonRider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements DragonRider {
    @Unique private boolean isRidingDragon;
    @Unique @Nullable private UUID uuid;

    @Override
    public boolean isRidingDragon() {
        return isRidingDragon;
    }

    @Override
    public void setRidingDragon(boolean state) {
        isRidingDragon = state;
    }

    @Override
    public @Nullable UUID getUUID() {
        return null;
    }

    @Override
    public void setUUID(UUID state) {
        uuid = state;
    }
}
