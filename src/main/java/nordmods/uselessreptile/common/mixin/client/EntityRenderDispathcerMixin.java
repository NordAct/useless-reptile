package nordmods.uselessreptile.common.mixin.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.common.entity.multipart.MultipartDragon;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispathcerMixin {

    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void renderDragonPart(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci){
        if (entity instanceof MultipartDragon dragon) {
            double x = -MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            double y = -MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
            double z = -MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
            URDragonPart[] parts = dragon.getParts();

            for(URDragonPart part : parts) {
                matrices.push();
                double g = x + MathHelper.lerp(tickDelta, part.lastRenderX, part.getX());
                double h = y + MathHelper.lerp(tickDelta, part.lastRenderY, part.getY());
                double i = z + MathHelper.lerp(tickDelta, part.lastRenderZ, part.getZ());
                matrices.translate(g, h, i);
                WorldRenderer.drawBox(matrices, vertices,
                        part.getBoundingBox().offset(-part.getX(), -part.getY(), -part.getZ()), 0.25F, 1.0F, 0.0F, 1.0F);
                matrices.pop();
            }
        }
    }
}
