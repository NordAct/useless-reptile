package nordmods.uselessreptile.common.entity.server_animation_processor;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonAnimationProcessor<T extends URDragonEntity> extends ServerAnimationProcessor<T> {
    private static final URDragonEntityModelProvider MODEL_PROVIDER = new URDragonEntityModelProvider();
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
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find model for {} ({}) of variant {}. Default will be used instead",
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
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find animation for {} ({}) of variant {}. Default will be used instead",
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setAnimationLocationCache(defaultAnimation);
            }
        }
    }
}
