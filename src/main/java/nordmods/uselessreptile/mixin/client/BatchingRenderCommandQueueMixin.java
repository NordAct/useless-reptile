package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import nordmods.uselessreptile.client.util.ShootingPointCommandRenderer;
import nordmods.uselessreptile.client.util.duck.ShootingPointCommandRendererHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BatchingRenderCommandQueue.class)
public class BatchingRenderCommandQueueMixin implements ShootingPointCommandRendererHelper {
    @Shadow
    private boolean hasCommands;
    @Unique
    private final List<ShootingPointCommandRenderer.ShootingPointCommand> shootingPointCommands = new ArrayList<>();

    @Override
    public List<ShootingPointCommandRenderer.ShootingPointCommand> useless_reptile$getShootingPointCommands() {
        return shootingPointCommands;
    }

    @Override
    public void useless_reptile$submitShootingPoint(MatrixStack matrices, LivingEntityRenderState renderState, ShootingPoint shootingPoint) {
        hasCommands = true;
        shootingPointCommands.add(new ShootingPointCommandRenderer.ShootingPointCommand(new Matrix4f(matrices.peek().getPositionMatrix()), renderState, shootingPoint));
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void clearShootingPointCommands(CallbackInfo ci) {
        shootingPointCommands.clear();
    }
}
