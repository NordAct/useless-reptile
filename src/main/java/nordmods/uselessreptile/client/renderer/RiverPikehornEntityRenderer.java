package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RiverPikehornEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends HeadMountDragonEntityRenderer<RiverPikehornEntity, R> {
    public RiverPikehornEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        //TODO
        //addRenderLayer(new DragonMainHandItemLayer<>(this));
        shadowRadius = 0.4f;
    }
}
