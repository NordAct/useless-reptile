package nordmods.uselessreptile.client.util.duck;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public interface HeadMountDragonRenderState<E extends URDragonEntity, R extends EntityRenderState> {
    default GeoRenderState useless_reptile$getHeadMountDragonRenderState() {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setHeadMountDragonRenderState(GeoRenderState dragon) {
        throw new AssertionError("Implemented in mixin");
    }
    default EntityRenderer<E, R> useless_reptile$getHeadMountDragonRenderer() {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setHeadMountDragonRenderer(EntityRenderer<E, R> renderer) {
        throw new AssertionError("Implemented in mixin");
    }
}
