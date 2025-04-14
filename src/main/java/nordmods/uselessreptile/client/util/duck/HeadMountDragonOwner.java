package nordmods.uselessreptile.client.util.duck;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public interface HeadMountDragonOwner<E extends URDragonEntity, R extends EntityRenderState> {
    GeoRenderState getHeadMountDragonRenderState();
    void setHeadMountDragonRenderState(GeoRenderState dragon);
    EntityRenderer<E, R> getHeadMountDragonRenderer();
    void setHeadMountDragonRenderer(EntityRenderer<E, R> renderer);
}
