package nordmods.uselessreptile.client.model.riverpikehornonhead;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;

public class RiverPikehornOnHeadModel extends SinglePartEntityModel<RiverPikehornEntity> {
    private final ModelPart Tdragon;
    public static final EntityModelLayer PIKEHORN_ON_HEAD_LAYER = new EntityModelLayer(new Identifier(UselessReptile.MODID, "river_pikehorn"), "dragon");

    public RiverPikehornOnHeadModel(ModelPart root) {
        this.Tdragon = root.getChild("Tdragon");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData Tdragon = modelPartData.addChild("Tdragon", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -5.0F, 0.0F));

        ModelPartData dragon = Tdragon.addChild("dragon", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData body_front = dragon.addChild("body_front", ModelPartBuilder.create().uv(0, 30).cuboid(-2.0F, -0.7F, -5.5F, 4.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.5F, 0.4F));

        ModelPartData Twing_left = body_front.addChild("Twing_left", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 1.5F, -4.15F));

        ModelPartData wing_left = Twing_left.addChild("wing_left", ModelPartBuilder.create().uv(0, 28).cuboid(-2.0F, -0.5F, -0.35F, 10.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData wing_membrane_left = wing_left.addChild("wing_membrane_left", ModelPartBuilder.create().uv(20, 16).cuboid(-2.0F, 0.0F, -0.35F, 10.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData wing_cont_left = wing_left.addChild("wing_cont_left", ModelPartBuilder.create().uv(0, 8).cuboid(-1.0F, -0.2F, -0.1F, 16.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.1F, -0.25F));

        ModelPartData Twing_right = body_front.addChild("Twing_right", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 1.5F, -4.15F));

        ModelPartData wing_right = Twing_right.addChild("wing_right", ModelPartBuilder.create().uv(37, 24).cuboid(-8.0F, -0.5F, -0.35F, 10.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData wing_membrane_right = wing_right.addChild("wing_membrane_right", ModelPartBuilder.create().uv(0, 16).cuboid(-8.0F, 0.0F, -0.35F, 10.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData wing_cont_right = wing_right.addChild("wing_cont_right", ModelPartBuilder.create().uv(0, 0).cuboid(-15.0F, -0.2F, -0.1F, 16.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.1F, -0.25F));

        ModelPartData Aneck = body_front.addChild("Aneck", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.25F, -5.4F));

        ModelPartData Tneck = Aneck.addChild("Tneck", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData neck = Tneck.addChild("neck", ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, -3.1F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Ahead = neck.addChild("Ahead", ModelPartBuilder.create(), ModelTransform.pivot(0.5F, 0.05F, -2.5F));

        ModelPartData Thead = Ahead.addChild("Thead", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData head = Thead.addChild("head", ModelPartBuilder.create().uv(16, 37).cuboid(-2.0F, -0.3F, -4.1F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.2F, 0.0F));

        ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(0, 12).cuboid(-0.475F, -2.6F, -8.3F, 0.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.7F, -4.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData main_hand = head.addChild("main_hand", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -1.4F, -8.0F, -0.1309F, 0.0F, -0.4363F));

        ModelPartData blink = head.addChild("blink", ModelPartBuilder.create().uv(16, 43).cuboid(-2.0F, -0.3F, -4.1F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Ajaw = head.addChild("Ajaw", ModelPartBuilder.create(), ModelTransform.pivot(-0.5F, 1.75F, -0.05F));

        ModelPartData jaw = Ajaw.addChild("jaw", ModelPartBuilder.create().uv(37, 26).cuboid(-1.45F, -0.05F, -4.05F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData body_back = dragon.addChild("body_back", ModelPartBuilder.create().uv(31, 33).cuboid(-1.5F, -0.5F, -0.4F, 3.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.5F, 0.4F));

        ModelPartData Ttail1 = body_back.addChild("Ttail1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.5F, 4.6F));

        ModelPartData tail1 = Ttail1.addChild("tail1", ModelPartBuilder.create().uv(26, 24).cuboid(-1.0F, 0.1F, -1.0F, 2.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Ttail2 = tail1.addChild("Ttail2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.1F, 5.0F));

        ModelPartData tail2 = Ttail2.addChild("tail2", ModelPartBuilder.create().uv(14, 24).cuboid(-0.5F, -0.1F, 0.0F, 1.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData leg_left = body_back.addChild("leg_left", ModelPartBuilder.create().uv(0, 41).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(1.5F, 2.5F, 2.6F));

        ModelPartData knee_left = leg_left.addChild("knee_left", ModelPartBuilder.create().uv(0, 6).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.1F, 3.0F, 1.7F));

        ModelPartData foot_left = knee_left.addChild("foot_left", ModelPartBuilder.create().uv(0, 15).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.5F, 0.0F));

        ModelPartData talon1_left = foot_left.addChild("talon1_left", ModelPartBuilder.create().uv(26, 27).cuboid(-0.5F, -0.4F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.6F, 0.5F, -0.2F));

        ModelPartData talon2_left = foot_left.addChild("talon2_left", ModelPartBuilder.create().uv(26, 24).cuboid(-0.5F, -0.4F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.6F, 0.5F, -0.2F));

        ModelPartData leg_right = body_back.addChild("leg_right", ModelPartBuilder.create().uv(40, 7).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.5F, 2.5F, 2.6F));

        ModelPartData knee_right = leg_right.addChild("knee_right", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.1F, 3.0F, 1.7F));

        ModelPartData foot_right = knee_right.addChild("foot_right", ModelPartBuilder.create().uv(0, 12).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.5F, 0.0F));

        ModelPartData talon1_right = foot_right.addChild("talon1_right", ModelPartBuilder.create().uv(0, 21).cuboid(-0.5F, -0.4F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.6F, 0.5F, -0.2F));

        ModelPartData talon2_right = foot_right.addChild("talon2_right", ModelPartBuilder.create().uv(0, 18).cuboid(-0.5F, -0.4F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.6F, 0.5F, -0.2F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(RiverPikehornEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        getPart().traverse().forEach(ModelPart::resetTransform);
        entity.updateAnimations();
        setHeadAngle(netHeadYaw, headPitch);
        updateAnimation(entity.sitAnimation, RiverPikehornOnHeadAnimations.SIT_HEAD, ageInTicks);
        updateAnimation(entity.blinkAnimation, RiverPikehornOnHeadAnimations.BLINK, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Tdragon.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return Tdragon;
    }

    private void setHeadAngle(float yaw, float pitch) {
        getChild("Thead").get().pitch = pitch * 0.017453292F / 2;
        getChild("Thead").get().yaw = yaw * 0.017453292F / 2;
        getChild("Tneck").get().pitch = pitch * 0.017453292F / 2;
        getChild("Tneck").get().yaw = yaw * 0.017453292F / 2;
    }
}