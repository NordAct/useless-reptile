package nordmods.uselessreptile.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import org.joml.Matrix4f;

public class ShootingPointCommandRenderer {
    public void render(SubmitNodeCollection queue, MultiBufferSource.BufferSource bufferSource) {
        for (ShootingPointCommand shootingPointCommand : queue.useless_reptile$getShootingPointCommands()) {
            LivingEntityRenderState renderState = shootingPointCommand.renderState;
            ShootingPoint point = shootingPointCommand.shootingPoint;
            PoseStack matrixStack = new PoseStack();
            matrixStack.mulPose(shootingPointCommand.pose);
            VertexConsumer buf = bufferSource.getBuffer(RenderType.lines());
            Vec3 pos = point.pos().subtract(new Vec3(renderState.x, renderState.y, renderState.z));
            Vec3 rot = point.rotation().yRot(Mth.PI);
            ShapeRenderer.renderVector(matrixStack, buf, pos.toVector3f(), rot.multiply(-5, 5, -5), -0x00FFF1);
        }
    }

    public record ShootingPointCommand(Matrix4f pose, LivingEntityRenderState renderState, ShootingPoint shootingPoint) {}
}
