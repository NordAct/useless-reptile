package nordmods.uselessreptile.client.model.special.river_pikehorn_on_head;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class RiverPikehornOnHeadAnimations {

    public static final Animation SIT_HEAD = Animation.Builder.create(8f).looping()
            .addBoneAnimation("dragon",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 4f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("body_front",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-10f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_left",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(7.14f, 10.25f, 69.48f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_membrane_left",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(-1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_membrane_left",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(17.77f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(18.41f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(18.85f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(19f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(18.85f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(18.41f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(17.77f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(16.23f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(15.59f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(15.15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(15.15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(15.59f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(16.23f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_cont_left",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(-0.15f, 0.4f, 1.5f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_cont_left",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(96.62f, -72.39f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(96.62f, -71.62f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(96.62f, -70.98f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(96.62f, -70.54f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(96.62f, -70.39f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(96.62f, -70.54f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(96.62f, -70.98f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(96.62f, -71.62f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(96.62f, -72.39f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(96.62f, -73.16f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(96.62f, -73.8f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(96.62f, -74.24f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(96.62f, -74.39f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(96.62f, -74.24f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(96.62f, -73.8f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(96.62f, -73.16f, -96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(96.62f, -72.39f, -96.32f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_right",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(7.14f, -10.25f, -69.48f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_membrane_right",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_membrane_right",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(17.77f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(18.41f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(18.85f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(19f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(18.85f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(18.41f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(17.77f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(16.23f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(15.59f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(15.15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(15.15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(15.59f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(16.23f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_cont_right",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0.15f, 0.4f, 1.5f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("wing_cont_right",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(96.62f, 72.39f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(96.62f, 71.62f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(96.62f, 70.98f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(96.62f, 70.54f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(96.62f, 70.39f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(96.62f, 70.54f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(96.62f, 70.98f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(96.62f, 71.62f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(96.62f, 72.39f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(96.62f, 73.16f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(96.62f, 73.8f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(96.62f, 74.24f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(96.62f, 74.39f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(96.62f, 74.24f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(96.62f, 73.8f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(96.62f, 73.16f, 96.32f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(96.62f, 72.39f, 96.32f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("body_back",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-17.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("tail1",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -0.4f, 0.4f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("tail1",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(-24.27f, 0.77f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(-25f, 1.41f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(-24.27f, 1.85f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(-22.5f, 2f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(-20.73f, 1.85f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(-20f, 1.41f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(-20.73f, 0.77f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(-24.27f, -0.77f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(-25f, -1.41f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(-24.27f, -1.85f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(-22.5f, -2f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(-20.73f, -1.85f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(-20f, -1.41f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(-20.73f, -0.77f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("tail2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -0.1f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("tail2",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(-24.27f, 1.53f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(-25f, 2.83f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(-24.27f, 3.7f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(-22.5f, 4f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(-20.73f, 3.7f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(-20f, 2.83f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(-20.73f, 1.53f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(-24.27f, -1.53f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(-25f, -2.83f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(-24.27f, -3.7f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(-22.5f, -4f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(-20.73f, -3.7f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(-20f, -2.83f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(-20.73f, -1.53f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("leg_left",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("leg_left",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-9.85f, -1.73f, -9.85f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("knee_left",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("knee_left",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("foot_left",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-19.72f, 3.4f, 9.41f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("leg_right",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("leg_right",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-9.85f, 1.73f, 9.85f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("knee_right",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("knee_right",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("foot_right",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(-19.72f, -3.4f, -9.41f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("neck",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(13.47f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(12.17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(11.3f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(11f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(11.3f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(12.17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(13.47f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(16.53f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(17.83f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(18.7f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(19f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(18.7f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(17.83f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(16.53f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("head",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(14f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(15.15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(16.12f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(16.77f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(17f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(16.77f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3f, AnimationHelper.createRotationalVector(16.12f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5f, AnimationHelper.createRotationalVector(15.15f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(14f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createRotationalVector(12.85f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5f, AnimationHelper.createRotationalVector(11.88f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.5f, AnimationHelper.createRotationalVector(11.23f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createRotationalVector(11f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(11.23f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(11.88f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createRotationalVector(12.85f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(14f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC))).build();
    public static final Animation BLINK = Animation.Builder.create(2f).looping()
            .addBoneAnimation("blink",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(0.99f, 0.99f, 0.99f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.7083f, AnimationHelper.createScalingVector(0.99f, 0.99f, 0.99f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.75f, AnimationHelper.createScalingVector(1f, 1f, 1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.9583f, AnimationHelper.createScalingVector(1f, 1f, 1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2f, AnimationHelper.createScalingVector(0.99f, 0.99f, 0.99f),
                                    Transformation.Interpolations.LINEAR))).build();
}