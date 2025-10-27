package nordmods.uselessreptile.mixin.client.render;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonRenderState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(PlayerEntityRenderState.class)
public abstract class PlayerEntityRenderStateMixin<E extends URDragonEntity, R extends EntityRenderState> implements HeadMountDragonRenderState<E, R> {
    @Unique private GeoRenderState headMountDragonRenderState;
    @Unique private EntityRenderer<E, R> headMountDragonRenderer;

    @Override
    public GeoRenderState useless_reptile$getHeadMountDragonRenderState() {
        return headMountDragonRenderState;
    }

    @Override
    public void useless_reptile$setHeadMountDragonRenderState(GeoRenderState dragon) {
        headMountDragonRenderState = dragon;
    }

    public EntityRenderer<E, R> useless_reptile$getHeadMountDragonRenderer() {
        return headMountDragonRenderer;
    }
    public void useless_reptile$setHeadMountDragonRenderer(EntityRenderer<E, R> renderer) {
        headMountDragonRenderer = renderer;
    }
}
