package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(PlayerEntityRenderState.class)
public abstract class PlayerEntityRenderStateMixin<E extends URDragonEntity, R extends EntityRenderState> implements HeadMountDragonOwner<E, R> {
    @Unique private GeoRenderState headMountDragonRenderState;
    @Unique private EntityRenderer<E, R> headMountDragonRenderer;

    @Override
    public GeoRenderState getHeadMountDragonRenderState() {
        return headMountDragonRenderState;
    }

    @Override
    public void setHeadMountDragonRenderState(GeoRenderState dragon) {
        headMountDragonRenderState = dragon;
    }

    public EntityRenderer<E, R> getHeadMountDragonRenderer() {
        return headMountDragonRenderer;
    }
    public void setHeadMountDragonRenderer(EntityRenderer<E, R> renderer) {
        headMountDragonRenderer = renderer;
    }
}
