package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;

import java.util.HashSet;
import java.util.UUID;

public class HeadMountDragonFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public static final HashSet<UUID> ON_HEAD = new HashSet<>();

    public HeadMountDragonFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.getFirstPassenger() instanceof HeadMountDragon dragon) {
            if (dragon.asURDragon().isInvisible()) return;
            ON_HEAD.remove(dragon.asURDragon().getUuid());
            matrices.push();

            ModelPart head = getContextModel().head;
            head.rotate(matrices);

            float scale = 1 / entity.getScale();
            float offsetScale = dragon.asURDragon().getScale() / entity.getScale();
            matrices.translate(0, -0.2960000524520874 * offsetScale - 0.5 * (1 - offsetScale), 0);
            matrices.scale(-scale, -scale, scale);

            RenderUtil.renderEntity(dragon.asURDragon(), tickDelta, matrices, vertexConsumers, light);

            matrices.pop();
            ON_HEAD.add(dragon.asURDragon().getUuid());
        }
    }
}
