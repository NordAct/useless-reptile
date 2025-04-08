package nordmods.uselessreptile.client.model;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class URDragonModel<T extends URDragonEntity> extends GeoModel<T> {
    @Override
    public Identifier getAnimationResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(entity);

        AssetCache assetCache = entity.getAssetCache();
        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        DragonModel data  = DragonVariantUtil.getDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().animation().orElseThrow())) {
            id = data.modelData().animation().get();
            assetCache.setAnimationLocationCache(id);
            return id;
        } else UselessReptile.LOGGER.warn("Failed to find animation for {} ({}) of variant {}. Default will be used instead", entity.getName().getString(), entity.getDragonId(), entity.getVariant());

        id = getDefaultAnimation(entity);
        assetCache.setAnimationLocationCache(id);
        return id;
    }

    @Override
    public Identifier getModelResource(T entity, GeoRenderer<T> renderer) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(entity);

        AssetCache assetCache = entity.getAssetCache();
        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        DragonModel data  = DragonVariantUtil.getDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().model())) {
            id = data.modelData().model();
            assetCache.setModelLocationCache(id);
            return id;
        } else UselessReptile.LOGGER.warn("Failed to find model for {} ({}) of variant {}. Default will be used instead", entity.getName().getString(), entity.getDragonId(), entity.getVariant());


        id = getDefaultModel(entity);
        assetCache.setModelLocationCache(id);
        return id;
    }

    @Override
    public Identifier getTextureResource(T entity, GeoRenderer<T> renderer) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(entity);

        AssetCache assetCache = entity.getAssetCache();
        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        DragonModel data = DragonVariantUtil.getDragonModelData(entity);
        if (data != null && ResourceUtil.doesExist(data.modelData().texture())) {
            id = data.modelData().texture();
            assetCache.setTextureLocationCache(id);
            return id;
        } else UselessReptile.LOGGER.warn("Failed to find texture for {} ({}) of variant {}. Default will be used instead", entity.getName().getString(), entity.getDragonId(), entity.getVariant());

        id = getDefaultTexture(entity);
        assetCache.setTextureLocationCache(id);
        return id;
    }

    protected final Identifier getDefaultTexture(T entity) {
        return UselessReptile.id("textures/entity/"+ entity.getDragonIdPath() + "/" + entity.getDefaultVariant() + ".png");
    }

    protected final Identifier getDefaultAnimation(T entity) {
        return UselessReptile.id("animations/entity/" + entity.getDragonIdPath() + "/" + entity.getDragonIdPath() + ".animation.json");
    }

    protected final Identifier getDefaultModel(T entity) {
        return UselessReptile.id("geo/entity/" + entity.getDragonIdPath() + "/" + entity.getDragonIdPath() + ".geo.json");
    }

    @Override
    public RenderLayer getRenderType(T entity, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderLayer.getEntityCutout(texture);

        DragonAssetCache assetCache = entity.getAssetCache();
        RenderLayer renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        DragonModel data = DragonVariantUtil.getDragonModelData(entity);
        if (data != null) {
            ModelData modelData = data.modelData();
            if (modelData.cull()) renderType = modelData.translucent() ? RenderUtil.getEntityTranslucentCull(texture) : RenderLayer.getEntityCutout(texture);
            else renderType = modelData.translucent() ? RenderLayer.getEntityTranslucent(texture) : RenderLayer.getEntityCutoutNoCull(texture);
            assetCache.setRenderTypeCache(renderType);
            return renderType;
        }

        renderType = RenderLayer.getEntityCutout(texture);
        assetCache.setRenderTypeCache(renderType);
        return renderType;
    }

}
