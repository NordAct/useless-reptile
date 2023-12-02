package nordmods.uselessreptile.client.model.special;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class LightningBreathEntityModel extends SinglePartEntityModel<LightningBreathEntity> {
    public static final EntityModelLayer LIGHTNING_BREATH_MODEL = new EntityModelLayer(new Identifier(UselessReptile.MODID, "lightning_breath"), "lightning_breath");
    private final ModelPart beam;
    public LightningBreathEntityModel(ModelPart root) {
        this.beam = root.getChild("beam1");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData beam1 = modelPartData.addChild("beam1", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam2 = beam1.addChild("beam2", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam3 = beam2.addChild("beam3", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam4 = beam3.addChild("beam4", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam5 = beam4.addChild("beam5", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam6 = beam5.addChild("beam6", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam7 = beam6.addChild("beam7", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam8 = beam7.addChild("beam8", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam9 = beam8.addChild("beam9", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam10 = beam9.addChild("beam10", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam11 = beam10.addChild("beam11", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam12 = beam11.addChild("beam12", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam13 = beam12.addChild("beam13", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam14 = beam13.addChild("beam14", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam15 = beam14.addChild("beam15", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam16 = beam15.addChild("beam16", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam17 = beam16.addChild("beam17", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam18 = beam17.addChild("beam18", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam19 = beam18.addChild("beam19", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam20 = beam19.addChild("beam20", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam21 = beam20.addChild("beam21", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam22 = beam21.addChild("beam22", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam23 = beam22.addChild("beam23", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam24 = beam23.addChild("beam24", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).mirrored().cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam25 = beam24.addChild("beam25", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam26 = beam25.addChild("beam26", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam27 = beam26.addChild("beam27", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam28 = beam27.addChild("beam28", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam29 = beam28.addChild("beam29", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData beam30 = beam29.addChild("beam30", ModelPartBuilder.create().uv(0, -16).cuboid(0.0F, -8.0F, -16.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(-16, 16).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.0F, 0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(LightningBreathEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        beam.yaw = (float) Math.toRadians(netHeadYaw);
        beam.pitch = (float) Math.toRadians(headPitch);
    }

    @Override
    public ModelPart getPart() {
        return beam;
    }
}