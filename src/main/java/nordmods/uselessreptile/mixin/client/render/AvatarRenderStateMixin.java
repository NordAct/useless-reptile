package nordmods.uselessreptile.mixin.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonRenderState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/// HeadMountDragonRenderState impl
@Mixin(AvatarRenderState.class)
public abstract class AvatarRenderStateMixin<E extends URDragonEntity, R extends EntityRenderState> implements HeadMountDragonRenderState<E, R> {
    @Unique private BRState headMountDragonRenderState;
    @Unique private EntityRenderer<E, R> headMountDragonRenderer;

    @Override
    public BRState useless_reptile$getHeadMountDragonRenderState() {
        return headMountDragonRenderState;
    }

    @Override
    public void useless_reptile$setHeadMountDragonRenderState(BRState dragon) {
        headMountDragonRenderState = dragon;
    }

    public EntityRenderer<E, R> useless_reptile$getHeadMountDragonRenderer() {
        return headMountDragonRenderer;
    }
    public void useless_reptile$setHeadMountDragonRenderer(EntityRenderer<E, R> renderer) {
        headMountDragonRenderer = renderer;
    }
}
