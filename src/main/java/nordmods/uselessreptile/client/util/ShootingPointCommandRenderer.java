package nordmods.uselessreptile.client.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import org.joml.Matrix4f;

public class ShootingPointCommandRenderer {
    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate bufferSource) {
        for (ShootingPointCommand shootingPointCommand : queue.useless_reptile$getShootingPointCommands()) {
            LivingEntityRenderState renderState = shootingPointCommand.renderState;
            ShootingPoint point = shootingPointCommand.shootingPoint;
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix(shootingPointCommand.pose);
            VertexConsumer buf = bufferSource.getBuffer(RenderLayer.getLines());
            Vec3d pos = point.pos().subtract(new Vec3d(renderState.x, renderState.y, renderState.z));
            Vec3d rot = point.rotation().rotateY(MathHelper.PI);
            VertexRendering.drawVector(matrixStack, buf, pos.toVector3f(), rot.multiply(-5, 5, -5), -0x00FFF1);
        }
    }

    public record ShootingPointCommand(Matrix4f pose, LivingEntityRenderState renderState, ShootingPoint shootingPoint) {}
}
