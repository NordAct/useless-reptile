package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;

import java.util.HashSet;
import java.util.UUID;

public class HeadMountDragonFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    public static final HashSet<UUID> ON_HEAD = new HashSet<>();

    public HeadMountDragonFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if (state instanceof HeadMountDragonOwner owner && owner.getHeadMountDragon() instanceof HeadMountDragon dragon) { //TODO replace this with render state
            if (dragon.asURDragon().isInvisible()) return;
            ON_HEAD.remove(dragon.asURDragon().getUuid());
            matrices.push();

            ModelPart head = getContextModel().head;
            head.applyTransform(matrices);

            float scale = 1 / state.baseScale;
            float offsetScale = dragon.asURDragon().getScale() / state.baseScale;
            matrices.translate(0, -0.2960000524520874 * offsetScale - 0.5 * (1 - offsetScale), 0);
            matrices.scale(-scale, -scale, scale);

            RenderUtil.renderEntity(dragon.asURDragon(), RenderUtil.getTickDelta(false), matrices, vertexConsumers, light);

            matrices.pop();
            ON_HEAD.add(dragon.asURDragon().getUuid());
        }
    }
}
