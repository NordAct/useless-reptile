package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.player.Player;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.layers.HeadMountDragonRenderLayer;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderModelPositioner;

public abstract class HeadMountDragonRenderer<T extends URDragonEntity & HeadMountDragon, R extends LivingEntityRenderState & GeoRenderState> extends URDragonEntityRenderer<T, R> {
    public HeadMountDragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void submitRenderTasks(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, @Nullable RenderModelPositioner<R> modelPositioner) {
        if (renderState.getGeckolibData(URDataTickets.DRAGON_IS_RIDING_PLAYER)) {
            if (HeadMountDragonRenderLayer.ON_HEAD.contains(renderState.getGeckolibData(URDataTickets.DRAGON_UUID)))
                return;
            else if (!renderState.getGeckolibData(URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT))
                return;
        }
        super.submitRenderTasks(renderState, poseStack, renderTasks, cameraState, modelPositioner);
    }

    @Override
    protected void applyRotations(R renderState, PoseStack poseStack, float nativeScale, CameraRenderState cameraState) {
        if (renderState.getGeckolibData(URDataTickets.DRAGON_IS_RIDING_PLAYER)) {
            if (renderState.isUpsideDown) {
                poseStack.translate(0, (renderState.boundingBoxHeight + 0.1f) / nativeScale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        } else super.applyRotations(renderState, poseStack, nativeScale, cameraState);
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState, float tickDelta) {
        super.addRenderData(animatable, relatedObject, renderState, tickDelta);
        renderState.addGeckolibData(URDataTickets.DRAGON_IS_RIDING_PLAYER, animatable.getVehicle() instanceof Player);
        renderState.addGeckolibData(URDataTickets.DRAGON_UUID, animatable.getUUID());
        renderState.addGeckolibData(
                URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT,
                !(animatable.getVehicle() instanceof Player player
                        && player == Minecraft.getInstance().player
                        && Minecraft.getInstance().options.getCameraType().isFirstPerson()));
    }
}
