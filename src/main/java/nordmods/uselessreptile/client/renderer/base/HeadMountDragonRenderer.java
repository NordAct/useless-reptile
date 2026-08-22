package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.player.Player;
import nordmods.uselessreptile.common.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.layers.HeadMountDragonRenderLayer;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public abstract class HeadMountDragonRenderer<T extends URDragonEntity & HeadMountDragon> extends URDragonEntityRenderer<T> {
    public HeadMountDragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void extractRenderState(T animatable, LivingEntityRenderState renderState, float tickDelta) {
        super.extractRenderState(animatable, renderState, tickDelta);
        renderState.setStateData(URStateDataTypes.DRAGON_IS_RIDING_PLAYER, animatable.getVehicle() instanceof Player);
        renderState.setStateData(URStateDataTypes.DRAGON_UUID, animatable.getUUID());
    }

    @Override
    public boolean shouldRender(T entity, @NonNull Frustum frustum, double d, double e, double f) {
        if (entity.getVehicle() instanceof Player player
                && (HeadMountDragonRenderLayer.ON_HEAD.contains(entity.getUUID())
                || (player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson())))
            return false;
        return super.shouldRender(entity, frustum, d, e, f);
    }

    @Override
    public <SL extends LivingEntityRenderState> void setupRotations(SL state, PoseStack poseStack, float bodyYaw, float scale) {
        if (state.getStateData(URStateDataTypes.DRAGON_IS_RIDING_PLAYER, false)) {
            if (state.isUpsideDown) {
                poseStack.translate(0, (state.boundingBoxHeight + 0.1f) / scale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        } else super.setupRotations(state, poseStack, bodyYaw, scale);
    }
}
