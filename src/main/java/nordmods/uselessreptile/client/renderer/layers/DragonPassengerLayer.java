package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
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
//todo wait for geckolib
public class DragonPassengerLayer<T extends DragonEquipmentAnimatable, R extends GeoRenderState> extends GeoRenderLayer<T, Void, R> {
    public static final Set<UUID> PASSENGERS = new HashSet<>();
    private final BiFunction<R, GeoBone, ? extends EntityRenderState> passengerRenderStateGetter;
    private final BiFunction<R, GeoBone, EntityRenderer<? extends Entity, EntityRenderState>> passengerRenderGetter;
    private final BiFunction<R, GeoBone, UUID> passengerUUIDGetter;
    private final String boneName;

    public DragonPassengerLayer(GeoRenderer<T, Void, R> entityRendererIn, BiFunction<R, GeoBone, ? extends EntityRenderState> passengerRenderStateGetter, BiFunction<R, GeoBone, EntityRenderer<? extends Entity, EntityRenderState>> passengerRenderGetter, BiFunction<R, GeoBone, UUID> passengerUUIDGetter, String boneName) {
        super(entityRendererIn);
        this.passengerRenderStateGetter = passengerRenderStateGetter;
        this.passengerRenderGetter = passengerRenderGetter;
        this.passengerUUIDGetter = passengerUUIDGetter;
        this.boneName = boneName;
    }

    public DragonPassengerLayer(GeoRenderer<T, Void, R> entityRendererIn) {
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
    public void addPerBoneRender(R renderState, BakedGeoModel model, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        model.getBone(boneName).ifPresent(bone -> renderForBone(renderState, bone, consumer));
    }

    protected void renderForBone(R renderState, GeoBone bone, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        consumer.accept(bone, (renderState2, matrixStackIn, bone2, renderType, bufferSource,
                packedLight, packedOverlay, renderColor) -> {
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
            renderer.render(passengerState,
                    matrixStackIn,
                    bufferSource,
                    ownerState.getGeckolibData(DataTickets.PACKED_LIGHT)
            );
            PASSENGERS.add(passengerUUID);
            matrixStackIn.pop();
        });
    }

    protected static GeoRenderState getOwnerRenderState(GeoRenderState renderState) {
        return renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
    }
}