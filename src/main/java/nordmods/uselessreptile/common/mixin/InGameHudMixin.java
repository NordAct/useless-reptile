package nordmods.uselessreptile.common.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    private void renderCrosshairOnDragon(DrawContext context, CallbackInfo ci) {
        if (!URConfig.getConfig().enableCrosshair) return;

        MinecraftClient client = MinecraftClient.getInstance();
        GameOptions gameOptions = client.options;
        Perspective perspective = gameOptions.getPerspective();
        boolean isFrontView = perspective.isFrontView();
        boolean isFirstPerson = perspective.isFirstPerson();
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();

        if (!(isFrontView || isFirstPerson) && client.player.getVehicle() instanceof URRideableDragonEntity) {
            if (gameOptions.debugEnabled && !gameOptions.hudHidden && !client.player.hasReducedDebugInfo() && !(Boolean)gameOptions.getReducedDebugInfo().getValue()) {
                Camera camera = client.gameRenderer.getCamera();
                MatrixStack matrixStack = RenderSystem.getModelViewStack();
                matrixStack.push();
                matrixStack.multiplyPositionMatrix(context.getMatrices().peek().getPositionMatrix());
                matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), 0.0F);
                matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));
                matrixStack.scale(-1.0F, -1.0F, -1.0F);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.renderCrosshair(10);
                matrixStack.pop();
                RenderSystem.applyModelViewMatrix();
            } else {
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
                context.drawTexture(ICONS, (scaledWidth - 15) / 2, (scaledHeight - 15) / 2, 0, 0, 15, 15);
                if (client.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
                    float f = client.player.getAttackCooldownProgress(0.0F);
                    boolean bl = false;
                    if (client.targetedEntity instanceof LivingEntity && f >= 1.0F) {
                        bl = client.player.getAttackCooldownProgressPerTick() > 5.0F;
                        bl &= client.targetedEntity.isAlive();
                    }

                    int j = scaledHeight / 2 - 7 + 16;
                    int k = scaledWidth / 2 - 8;
                    if (bl) {
                        context.drawTexture(ICONS, k, j, 68, 94, 16, 16);
                    } else if (f < 1.0F) {
                        int l = (int)(f * 17.0F);
                        context.drawTexture(ICONS, k, j, 36, 94, 16, 4);
                        context.drawTexture(ICONS, k, j, 52, 94, l, 4);
                    }
                }

                RenderSystem.defaultBlendFunc();
            }
        }
    }
}
