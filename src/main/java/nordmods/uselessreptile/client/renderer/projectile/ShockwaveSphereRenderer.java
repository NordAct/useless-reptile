package nordmods.uselessreptile.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.projectile.ShockwaveSphere;
import org.jspecify.annotations.NonNull;

public class ShockwaveSphereRenderer extends BREntityRenderer<ShockwaveSphere, ShockwaveSphereRenderer.ShockwaveSpereEntityRenderState> {
    private static final Identifier TEXTURE = UselessReptile.id("textures/entity/shockwave_sphere/shockwave.png");
    private static final Identifier MODEL = UselessReptile.id("biscuit_roll/models/entity/shockwave/shockwave.geo.json");

    public ShockwaveSphereRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BRModelProvider() {
            @Override
            public Identifier getModelId(BRState state) {
                return MODEL;
            }

            @Override
            public Identifier getAnimationId(BRState state) {
                throw new UnsupportedOperationException("If you see this please send a bug report");
            }
        });
    }

    @Override
    public @NonNull ShockwaveSpereEntityRenderState createRenderState() {
        return new ShockwaveSpereEntityRenderState();
    }

    @Override
    public void submit(ShockwaveSpereEntityRenderState state, PoseStack matrixStack, @NonNull SubmitNodeCollector commandQueue, @NonNull CameraRenderState cameraRenderState) {

        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(state.alpha / 2f * 180f));
        matrixStack.scale(state.radius);
        submitBRModel(state.copyWithStuff(Mth.clamp(state.alpha, 0, 1), state.radius), matrixStack, commandQueue, cameraRenderState);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-state.alpha / 1.5f * 180f));
        submitBRModel(state.copyWithStuff(state.alpha/1.5f, state.radius), matrixStack, commandQueue, cameraRenderState);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(state.alpha * 180f));
        submitBRModel(state.copyWithStuff(state.alpha/2f, state.radius/1.5f), matrixStack, commandQueue, cameraRenderState);
        matrixStack.popPose();
    }

    @Override
    public void extractRenderState(@NonNull ShockwaveSphere entity, @NonNull ShockwaveSpereEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.radius = Mth.lerp(tickDelta, entity.getPrevRadius(), entity.getCurrentRadius());
        float alpha = Mth.clamp(1f - (state.ageInTicks < 3 ? 0 : state.radius / entity.getMaxRadius()), 0f, 1f);
        state.alpha = Mth.lerp(tickDelta, entity.prevAlpha, alpha);
        entity.prevAlpha = state.alpha;
        state.color = entity.getColor();
        state.setStateData(ClientStateDataTypes.COLOR, ARGB.color(state.alpha, state.color));
        state.setStateData(ClientStateDataTypes.LIGHT, LightCoordsUtil.FULL_BRIGHT);
    }

    @Override
    public void adjustAnimation(BRState state, BRModel model) {
        model.getRootBones().forEach(b -> b.getAnimationPose().scale().mul(((ShockwaveSpereEntityRenderState)state).radius));
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.entityTranslucentEmissive(texture, true);
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return TEXTURE;
    }

    public static class ShockwaveSpereEntityRenderState extends EntityRenderState {
        public float alpha = 1;
        public float radius;
        public int color;

        private ShockwaveSpereEntityRenderState copyWithStuff(float alpha, float radius) {
            ShockwaveSpereEntityRenderState state = new ShockwaveSpereEntityRenderState();
            getDataMap().forEach(((stateDataType, holder) -> state.setStateData((StateDataType) stateDataType, holder.value())));
            state.color = this.color;
            state.alpha = alpha;
            state.radius = radius;
            state.setStateData(ClientStateDataTypes.COLOR, ARGB.color(state.alpha, state.color));
            return state;
        }
    }
}
