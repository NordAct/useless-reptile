package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashSet;
import java.util.UUID;

public class HeadMountDragonFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    public static final HashSet<UUID> ON_HEAD = new HashSet<>();

    public HeadMountDragonFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if (state instanceof HeadMountDragonOwner owner) {
            GeoRenderState dragonState = owner.getHeadMountDragonRenderState();
            if (dragonState == null) return;
            EntityRenderer<URDragonEntity, EntityRenderState> renderer = ((HeadMountDragonOwner<URDragonEntity, EntityRenderState>) state).getHeadMountDragonRenderer();
            if (dragonState.getGeckolibData(DataTickets.INVISIBLE_TO_PLAYER)) return;
            UUID dragonUUID = dragonState.getGeckolibData(URDataTickets.DRAGON_UUID);
            ON_HEAD.remove(dragonUUID);

            matrices.push();
            ModelPart head = getContextModel().head;
            head.applyTransform(matrices);
            float scale = 1 / state.baseScale;
            float offsetScale = ((LivingEntityRenderState)dragonState).baseScale / state.baseScale;
            //float offsetScale = ((LivingEntityRenderState)dragonState).baseScale;
            matrices.translate(0, -0.2960000524520874 * offsetScale - 0.5 * (1 - offsetScale), 0);
            matrices.scale(-scale, -scale, scale);

            if (!dragonState.hasGeckolibData(DataTickets.PACKED_LIGHT)) dragonState.addGeckolibData(DataTickets.PACKED_LIGHT, light);
            renderer.render((EntityRenderState) dragonState, matrices, vertexConsumers, light);

            matrices.pop();

            ON_HEAD.add(dragonUUID);
        }
    }
}
