package nordmods.uselessreptile.mixin.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.util.ShootingPointCommandRenderer;
import nordmods.uselessreptile.client.util.duck.ShootingPointCommandRendererHelper;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin implements ShootingPointCommandRendererHelper {
    @Shadow
    private boolean wasUsed;
    @Unique
    private final List<ShootingPointCommandRenderer.ShootingPointCommand> shootingPointCommands = new ArrayList<>();

    @Override
    public List<ShootingPointCommandRenderer.ShootingPointCommand> useless_reptile$getShootingPointCommands() {
        return shootingPointCommands;
    }

    @Override
    public void useless_reptile$submitShootingPoint(PoseStack matrices, LivingEntityRenderState renderState, ShootingPoint shootingPoint) {
        wasUsed = true;
        shootingPointCommands.add(new ShootingPointCommandRenderer.ShootingPointCommand(new Matrix4f(matrices.last().pose()), renderState, shootingPoint));
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void clearShootingPointCommands(CallbackInfo ci) {
        shootingPointCommands.clear();
    }
}
