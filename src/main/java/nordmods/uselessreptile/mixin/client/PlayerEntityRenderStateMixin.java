package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public abstract class PlayerEntityRenderStateMixin implements HeadMountDragonOwner {
    @Unique
    private HeadMountDragon headMountDragon;

    @Override
    public HeadMountDragon getHeadMountDragon() {
        return headMountDragon;
    }

    @Override
    public void setHeadMountDragon(HeadMountDragon dragon) {
        headMountDragon = dragon;
    }
}
