package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.client.init.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispathcerMixin {

    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void renderAttackBoxes(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci){
        if (!URClientConfig.getConfig().attackBoxesInDebug) return;

        if (entity instanceof URDragonEntity dragon) {
            double x = -MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            double y = -MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
            double z = -MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

            matrices.push();
            if (dragon.getAttackBox() != null)
                WorldRenderer.drawBox(matrices, vertices,
                        dragon.getAttackBox().offset(x, y, z), 1.0F, 0.0F, 0.5F, 1.0F);
            if (dragon.getSecondaryAttackBox() != null)
                WorldRenderer.drawBox(matrices, vertices,
                        dragon.getSecondaryAttackBox().offset(x, y, z), 1.0F, 0.0F, 1.0F, 1.0F);
            matrices.pop();
        }
    }
}
