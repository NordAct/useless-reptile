package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class DragonPassengerLayer<T extends DragonEquipmentAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public static final Set<UUID> PASSENGERS = new HashSet<>();
    private final BiFunction<R, GeoBone, ? extends EntityRenderState> passengerRenderStateGetter;
    private final BiFunction<R, GeoBone, EntityRenderer<? extends Entity, EntityRenderState>> passengerRenderGetter;
    private final BiFunction<R, GeoBone, UUID> passengerUUIDGetter;
    private final String boneName;

    public DragonPassengerLayer(GeoRenderer<T, O, R> entityRendererIn, BiFunction<R, GeoBone, ? extends EntityRenderState> passengerRenderStateGetter, BiFunction<R, GeoBone, EntityRenderer<? extends Entity, EntityRenderState>> passengerRenderGetter, BiFunction<R, GeoBone, UUID> passengerUUIDGetter, String boneName) {
        super(entityRendererIn);
        this.passengerRenderStateGetter = passengerRenderStateGetter;
        this.passengerRenderGetter = passengerRenderGetter;
        this.passengerUUIDGetter = passengerUUIDGetter;
        this.boneName = boneName;
    }

    public DragonPassengerLayer(GeoRenderer<T, O, R> entityRendererIn) {
        this(entityRendererIn,
                (state, bone) -> {
            if (!bone.getName().equals("rider")) return null;
            GeoRenderState ownerState = getOwnerRenderState(state);
            if (ownerState != null) return ownerState.getOrDefaultGeckolibData(URDataTickets.PASSENGER_RENDER_STATE, null);
            return null;
            },
                (state, bone) -> {
            if (!bone.getName().equals("rider")) return null;
            GeoRenderState ownerState = getOwnerRenderState(state);
            if (ownerState != null) return ownerState.getOrDefaultGeckolibData(URDataTickets.PASSENGER_RENDER, null);
            return null;
            },
                (state, bone) -> getOwnerRenderState(state).getGeckolibData(URDataTickets.PASSENGER_UUID), "rider");
    }

    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, boolean didRenderModel, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        model.getBone(boneName).ifPresent(bone ->
                consumer.accept(bone, ((renderState1, poseStack, bone1, renderTasks, cameraState, packedLight, packedOverlay, renderColor) -> {
                    renderForBone(renderState, bone, poseStack, renderTasks, cameraState);
                })));
    }

    protected void renderForBone(R renderState, GeoBone bone, MatrixStack matrixStackIn, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraRenderState) {
            GeoRenderState ownerState = getOwnerRenderState(renderState);
            if (!ownerState.getGeckolibData(URDataTickets.PASSENGER_SHOULD_RENDER_TO_CLIENT)) return;
            EntityRenderState passengerState = passengerRenderStateGetter.apply(renderState, bone);
            if (passengerState == null) return;
            EntityRenderer<? extends Entity, EntityRenderState> renderer = passengerRenderGetter.apply(renderState, bone);
            if (renderer == null) return;

            matrixStackIn.push();
            UUID passengerUUID = passengerUUIDGetter.apply(renderState, bone);
            PASSENGERS.remove(passengerUUID);
            Vec3d vec3d = ownerState.getGeckolibData(URDataTickets.PASSENGER_ATTACHMENT_POS);
            float scale = 1/((LivingEntityRenderState)ownerState).baseScale;
            matrixStackIn.translate(vec3d.x * scale, -vec3d.y * scale, vec3d.z * scale);
            RenderUtil.translateToPivotPoint(matrixStackIn, bone);
            matrixStackIn.scale(scale, scale, scale);
            passengerState.displayName = null;
            renderer.render(
                    passengerState,
                    matrixStackIn,
                    renderTasks,
                    cameraRenderState
            );
            PASSENGERS.add(passengerUUID);
            matrixStackIn.pop();
    }

    protected static GeoRenderState getOwnerRenderState(GeoRenderState renderState) {
        return renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
    }
}