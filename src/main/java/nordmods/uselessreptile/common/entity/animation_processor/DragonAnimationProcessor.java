package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.network.s2c.SyncBoneTransformsPayload;

import java.util.HashMap;
import java.util.Map;

public class DragonAnimationProcessor<T extends URDragonEntity> extends AnimationProcessor<T> {
    private static final URDragonEntityModelProvider MODEL_PROVIDER = new URDragonEntityModelProvider();
    private Map<String, SyncBoneTransformsPayload.BoneTransform> nextBoneTransforms;
    private Map<String, SyncBoneTransformsPayload.BoneTransform> boneTransforms = new HashMap<>();
    private Map<String, SyncBoneTransformsPayload.BoneTransform> prevBoneTransforms = new HashMap<>();
    public DragonAnimationProcessor(T animatable) {
        super(animatable);
    }

    @Override
    public URDragonEntityModelProvider getModelProvider() {
        return MODEL_PROVIDER;
    }

    @Override
    public float getAnimationTime() {
        return animatable.tickCount / 20f;
    }

    @Override
    public void updateBRState() {
        super.updateBRState();
        DragonAssetCache assetCache = animatable.getAssetCache();
        state.setStateData(URStateDataTypes.ASSET_CACHE, assetCache);
        Identifier dragonId = animatable.getDragonId();
        state.setStateData(URStateDataTypes.DRAGON_ID, dragonId);
        fillDragonCache(
                assetCache,
                animatable.getDragonVariant(),
                dragonId,
                animatable.getName().getString(),
                animatable.getVariant(),
                getModelProvider().getDefaultModel(dragonId),
                getModelProvider().getDefaultAnimation(dragonId)
        );
    }

    @Override
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, T animatable, float tickDelta) {
        super.updateControllerVariables(builder, animatable, tickDelta);
        builder.setQuery("body_x_rotation", -Math.clamp(animatable.getXBodyRot(tickDelta), -45, 45));
        builder.setQuery("head_x_rotation", -animatable.getViewXRot(tickDelta));
        builder.setQuery("body_y_rotation", -animatable.getPreciseBodyRotation(tickDelta));
        builder.setQuery("head_y_rotation", -animatable.getViewYRot(tickDelta));
        builder.setQuery("yaw_speed", -animatable.getYBodyRotChange(tickDelta));
    }

    @Override
    public void tick() {
        if (animatable.level().isClientSide()) {
            if (nextBoneTransforms != null) {
                prevBoneTransforms = boneTransforms;
                boneTransforms = nextBoneTransforms;
                nextBoneTransforms = null;
            }
        } else {
            super.tick();
        }
        if (animatable.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : PlayerLookup.tracking(serverLevel, animatable.blockPosition()))
                SyncBoneTransformsPayload.send(player, animatable);
        }
    }

    protected void fillDragonCache(
            DragonAssetCache assetCache,
            DragonVariant variant,
            Identifier dragonId,
            String dragonName,
            String variantName,
            Identifier defaultModel,
            Identifier defaultAnimation
    ) {

        //model
        if (assetCache.getModelLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(variant, animatable.level().registryAccess()).modelData().model();
            if (ServerModelManager.instance().hasModel(id)) {
                assetCache.setModelLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find model {} for {} ({}) of variant {}. Default will be used instead",
                        id,
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setModelLocationCache(defaultModel);
            }
        }

        //animation cache
        if (assetCache.getAnimationLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(variant, animatable.level().registryAccess()).modelData().animation();
            if (ServerAnimationManager.instance().hasAnimations(id)) {
                assetCache.setAnimationLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find animation {} for {} ({}) of variant {}. Default will be used instead",
                        id,
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setAnimationLocationCache(defaultAnimation);
            }
        }
    }

    public void handleSyncBoneTransformsPayload(SyncBoneTransformsPayload payload) {
        if (nextBoneTransforms != null) return;
        nextBoneTransforms = payload.boneTransforms();
    }

    public void syncPose(String bone, AnimatedBone.AnimationPose pose, float tickDelta) {
        SyncBoneTransformsPayload.BoneTransform prevTransform = prevBoneTransforms.get(bone);
        boolean prev = prevTransform != null;
        SyncBoneTransformsPayload.BoneTransform transform = boneTransforms.get(bone);
        boolean current = transform != null;
        pose.position().set(
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.pos().orElse(SyncBoneTransformsPayload.ZERO).x() : 0,
                        current ? transform.pos().orElse(SyncBoneTransformsPayload.ZERO).x() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.pos().orElse(SyncBoneTransformsPayload.ZERO).y() : 0,
                        current ? transform.pos().orElse(SyncBoneTransformsPayload.ZERO).y() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.pos().orElse(SyncBoneTransformsPayload.ZERO).z() : 0,
                        current ? transform.pos().orElse(SyncBoneTransformsPayload.ZERO).z() : 0
                )
        );
        pose.rotation().set(
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.rot().orElse(SyncBoneTransformsPayload.ZERO).x() : 0,
                        current ? transform.rot().orElse(SyncBoneTransformsPayload.ZERO).x() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.rot().orElse(SyncBoneTransformsPayload.ZERO).y() : 0,
                        current ? transform.rot().orElse(SyncBoneTransformsPayload.ZERO).y() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.rot().orElse(SyncBoneTransformsPayload.ZERO).z() : 0,
                        current ? transform.rot().orElse(SyncBoneTransformsPayload.ZERO).z() : 0
                )
        );
        pose.scale().set(
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.scale().orElse(SyncBoneTransformsPayload.ONE).x() : 1,
                        current ? transform.scale().orElse(SyncBoneTransformsPayload.ONE).x() : 1
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.scale().orElse(SyncBoneTransformsPayload.ONE).y() : 1,
                        current ? transform.scale().orElse(SyncBoneTransformsPayload.ONE).y() : 1
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.scale().orElse(SyncBoneTransformsPayload.ONE).z() : 1,
                        current ? transform.scale().orElse(SyncBoneTransformsPayload.ONE).z() : 1
                )
        );
    }
}
