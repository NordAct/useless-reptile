package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class URRideableDragonEntityRenderer<T extends URRideableDragonEntity, R extends LivingEntityRenderState & GeoRenderState> extends URDragonEntityRenderer<T, R>{
    public URRideableDragonEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState) {
        super.addRenderData(animatable, relatedObject, renderState);
        Entity passenger = animatable.getFirstPassenger();
        if (passenger != null) {
            renderState.addGeckolibData(
                    URDataTickets.PASSENGER_RENDER_STATE,
                    RenderUtil
                            .getEntityRenderer(passenger)
                            .getAndUpdateRenderState(passenger, RenderUtil
                                    .getTickDelta(false)));

            renderState.addGeckolibData(URDataTickets.PASSENGER_RENDER, RenderUtil.getEntityRenderer(passenger));
            renderState.addGeckolibData(URDataTickets.PASSENGER_UUID, passenger.getUuid());
            renderState.addGeckolibData(URDataTickets.PASSENGER_ATTACHMENT_POS, passenger.getVehicleAttachmentPos(animatable));
            renderState.addGeckolibData(
                    URDataTickets.PASSENGER_SHOULD_RENDER_TO_CLIENT,
                    passenger == MinecraftClient.getInstance().player ?
                            URClientConfig.getConfig().renderPassengers.canRenderSelf() && !MinecraftClient.getInstance().options.getPerspective().isFirstPerson():
                            URClientConfig.getConfig().renderPassengers.canRenderOthers()
                    );
        } else {
            renderState.addGeckolibData(URDataTickets.PASSENGER_RENDER_STATE, null);
            renderState.addGeckolibData(URDataTickets.PASSENGER_RENDER, null);
            renderState.addGeckolibData(URDataTickets.PASSENGER_UUID, null);
            renderState.addGeckolibData(URDataTickets.PASSENGER_ATTACHMENT_POS, null);
            renderState.addGeckolibData(URDataTickets.PASSENGER_SHOULD_RENDER_TO_CLIENT, false);
        }
    }
}
