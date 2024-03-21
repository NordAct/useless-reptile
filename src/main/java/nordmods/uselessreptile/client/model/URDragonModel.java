package nordmods.uselessreptile.client.model;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_data.ModelDataUtil;
import nordmods.uselessreptile.client.util.model_data.base.DragonModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.model.GeoModel;

public abstract class URDragonModel<T extends URDragonEntity> extends GeoModel<T> {
    private final String defaultVariant;

    protected URDragonModel(String defaultVariant) {
        this.defaultVariant = defaultVariant;
    }

    @Override
    public Identifier getAnimationResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(entity);

        DragonModelData data = entity.getAssetCache().getDragonModelData();
        if (data != null && ResourceUtil.doesExist(data.modelData().animation())) return data.modelData().animation();

        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            data = ModelDataUtil.getNametagDragonModelData(entity);
            if (data != null && ResourceUtil.doesExist(data.modelData().animation())) return data.modelData().animation();
        }

        data = ModelDataUtil.getVariantDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().animation())) return data.modelData().animation();

        return getDefaultAnimation(entity);
    }

    @Override
    public Identifier getModelResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(entity);

        DragonModelData data = entity.getAssetCache().getDragonModelData();
        if (data != null && ResourceUtil.doesExist(data.modelData().model())) return data.modelData().model();

        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            data = ModelDataUtil.getNametagDragonModelData(entity);
            if (data != null && ResourceUtil.doesExist(data.modelData().model())) return data.modelData().model();
        }

        data = ModelDataUtil.getVariantDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().model())) return data.modelData().model();

        return getDefaultModel(entity);
    }

    @Override
    public Identifier getTextureResource(T entity){
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(entity);

        DragonModelData data = entity.getAssetCache().getDragonModelData();
        if (data != null && ResourceUtil.doesExist(data.modelData().texture())) return data.modelData().texture();

        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            data = ModelDataUtil.getNametagDragonModelData(entity);
            if (data != null && ResourceUtil.doesExist(data.modelData().texture())) return data.modelData().texture();
        }

        data = ModelDataUtil.getVariantDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().texture())) return data.modelData().texture();

        return getDefaultTexture(entity);
    }

    protected final Identifier getDefaultTexture(T entity) {
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ entity.getDragonID() + "/" + defaultVariant + ".png");
    }

    protected final Identifier getDefaultAnimation(T entity) {
        return new Identifier(UselessReptile.MODID, "animations/entity/" + entity.getDragonID() + "/" + entity.getDragonID() + ".animation.json");
    }

    protected final Identifier getDefaultModel(T entity) {
        return new Identifier(UselessReptile.MODID, "geo/entity/" + entity.getDragonID() + "/" + entity.getDragonID() + ".geo.json");
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture) {
        return RenderLayer.getEntityCutout(texture);
    }

}
