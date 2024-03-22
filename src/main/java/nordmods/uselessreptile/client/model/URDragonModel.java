package nordmods.uselessreptile.client.model;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.AssetCache;
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
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) {
            assetCache.setAnimationLocationCache(null);
            return getDefaultAnimation(entity);
        }

        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        DragonModelData data;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            data = ModelDataUtil.getNametagDragonModelData(entity);
            if (data != null && ResourceUtil.doesExist(data.modelData().animation())) {
                id = data.modelData().animation();
                assetCache.setAnimationLocationCache(id);
                return data.modelData().animation();
            }
        }

        data = ModelDataUtil.getVariantDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().animation())) {
            id = data.modelData().animation();
            assetCache.setAnimationLocationCache(id);
            return data.modelData().animation();
        }

        id = getDefaultAnimation(entity);
        assetCache.setAnimationLocationCache(id);
        return id;
    }

    @Override
    public Identifier getModelResource(T entity) {
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) {
            assetCache.setModelLocationCache(null);
            return getDefaultModel(entity);
        }

        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        DragonModelData data;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            data = ModelDataUtil.getNametagDragonModelData(entity);
            if (data != null && ResourceUtil.doesExist(data.modelData().model())) {
                id = data.modelData().model();
                assetCache.setModelLocationCache(id);
                return data.modelData().model();
            }
        }

        data = ModelDataUtil.getVariantDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().model())) {
            id = data.modelData().model();
            assetCache.setModelLocationCache(id);
            return data.modelData().model();
        }

        id = getDefaultModel(entity);
        assetCache.setModelLocationCache(id);
        return id;
    }

    @Override
    public Identifier getTextureResource(T entity){
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) {
            assetCache.setTextureLocationCache(null);
            return getDefaultTexture(entity);
        }

        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        DragonModelData data;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            data = ModelDataUtil.getNametagDragonModelData(entity);
            if (data != null && ResourceUtil.doesExist(data.modelData().texture())) {
                id = data.modelData().texture();
                assetCache.setTextureLocationCache(id);
                return data.modelData().texture();
            }
        }

        data = ModelDataUtil.getVariantDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().texture())) {
            id = data.modelData().texture();
            assetCache.setTextureLocationCache(id);
            return data.modelData().texture();
        }

        id = getDefaultTexture(entity);
        assetCache.setTextureLocationCache(id);
        return id;
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
    public RenderLayer getRenderType(T entity, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderLayer.getEntityCutout(texture);

        AssetCache assetCache = entity.getAssetCache();
        RenderLayer renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        DragonModelData data = ModelDataUtil.getDragonModelData(entity, assetCache.isNametagModel());
        if (data != null) {
            renderType = data.modelData().renderType();
            assetCache.setRenderTypeCache(renderType);
            return renderType;
        }

        return RenderLayer.getEntityCutout(texture);
    }

}
