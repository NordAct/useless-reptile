package nordmods.uselessreptile.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonRenderState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashSet;
import java.util.UUID;

public class HeadMountDragonFeatureRenderer extends RenderLayer<AvatarRenderState, PlayerModel> {
    public static final HashSet<UUID> ON_HEAD = new HashSet<>();

    public HeadMountDragonFeatureRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, AvatarRenderState state, float limbAngle, float limbDistancee) {
        if (state instanceof HeadMountDragonRenderState owner) {
            GeoRenderState dragonState = owner.useless_reptile$getHeadMountDragonRenderState();
            if (dragonState == null) return;
            EntityRenderer<URDragonEntity, EntityRenderState> renderer = state.useless_reptile$getHeadMountDragonRenderer();
            if (dragonState.getGeckolibData(DataTickets.INVISIBLE_TO_PLAYER)) return;
            UUID dragonUUID = dragonState.getGeckolibData(URDataTickets.DRAGON_UUID);
            ON_HEAD.remove(dragonUUID);

            matrices.pushPose();
            ModelPart head = getParentModel().head;
            head.translateAndRotate(matrices);
            float scale = 1 / state.scale;
            float offsetScale = ((LivingEntityRenderState)dragonState).scale / state.scale;
            matrices.translate(0, -0.2960000524520874 * offsetScale - 0.5 * (1 - offsetScale), 0);
            matrices.scale(-scale, -scale, scale);

            if (!dragonState.hasGeckolibData(DataTickets.PACKED_LIGHT)) dragonState.addGeckolibData(DataTickets.PACKED_LIGHT, light);
            renderer.submit((EntityRenderState) dragonState, matrices, queue, RenderUtil.getCameraRenderState());

            matrices.popPose();

            ON_HEAD.add(dragonUUID);
        }
    }
}
