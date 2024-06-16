package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.model.LightningChaserEntityModel;
import nordmods.uselessreptile.client.renderer.base.URDragonRenderer;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightningChaserEntityRenderer extends URDragonRenderer<LightningChaserEntity> {
    public static final Map<UUID, Vector3f> headPos = new HashMap<>();

    public LightningChaserEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new LightningChaserEntityModel());
        shadowRadius = 1.5f;
    }

    public void renderRecursively(MatrixStack poseStack, LightningChaserEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (bone.getName().equals("head")) {
            Vector3d vector3d = bone.getLocalPosition();
            headPos.put(animatable.getUuid(), new Vector3f((float) vector3d.x, (float) vector3d.y, (float) vector3d.z));
        }
    }
}
