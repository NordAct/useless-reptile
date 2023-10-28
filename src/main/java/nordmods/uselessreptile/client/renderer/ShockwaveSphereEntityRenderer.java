package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;

//dummy renderer, it just was easiest way to do it
public class ShockwaveSphereEntityRenderer extends EntityRenderer<ShockwaveSphereEntity> {
    public ShockwaveSphereEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(ShockwaveSphereEntity entity) {
        return new Identifier("textures/entity/armorstand/wood.png");
    }

    @Override
    public void render(ShockwaveSphereEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }
}
