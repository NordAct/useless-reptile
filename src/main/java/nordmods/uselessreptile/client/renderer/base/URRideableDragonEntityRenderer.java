package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class URRideableDragonEntityRenderer<T extends URRideableDragonEntity> extends URDragonEntityRenderer<T>{
    public URRideableDragonEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void extractRenderState(T animatable, LivingEntityRenderState renderState, float tickDelta) {
        super.extractRenderState(animatable, renderState, tickDelta);
        List<Boolean> shouldRenderToClient = new ArrayList<>();
        List<? super EntityRenderState> renderStates = new ArrayList<>();
        List<EntityRenderer<? super Entity, ? super EntityRenderState>> renderers = new ArrayList<>();
        List<UUID> uuids = new ArrayList<>();
        List<Vec3> attachmentPos = new ArrayList<>();
        animatable.getPassengers().forEach(passenger -> {
            EntityRenderer<? super Entity, ? super EntityRenderState> renderer = RenderUtil.getEntityRenderer(passenger);
            renderers.add(renderer);
            renderStates.add(renderer.createRenderState(passenger, tickDelta));
            uuids.add(passenger.getUUID());
            attachmentPos.add(passenger.getVehicleAttachmentPoint(animatable));
            shouldRenderToClient.add(passenger == Minecraft.getInstance().player ?
                    URClientConfig.getConfig().renderPassengers.canRenderSelf() && !Minecraft.getInstance().options.getCameraType().isFirstPerson():
                    URClientConfig.getConfig().renderPassengers.canRenderOthers());
        });
        renderState.setStateData(URStateDataTypes.PASSENGERS_RENDER_STATE, renderStates);
        renderState.setStateData(URStateDataTypes.PASSENGERS_RENDERERS, renderers);
        renderState.setStateData(URStateDataTypes.PASSENGERS_UUID, uuids);
        renderState.setStateData(URStateDataTypes.PASSENGERS_ATTACHMENT_POS, attachmentPos);
        renderState.setStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, shouldRenderToClient);
    }
}
