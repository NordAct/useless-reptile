package nordmods.uselessreptile.client.model.special;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_data.ModelDataUtil;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;
import nordmods.uselessreptile.client.util.model_data.base.ModelData;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class DragonEqupmentModel extends GeoModel<DragonEquipmentAnimatable> {
    private static final Identifier DEFAULT_ANIMATION = UselessReptile.id("animations/entity/empty.animation.json");

    @Override
    @Nullable
    public Identifier getModelResource(DragonEquipmentAnimatable entity, GeoRenderer<DragonEquipmentAnimatable> renderer) {
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) return null;

        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(entity.owner, entity.item);
        if (data != null && data.modelData().model().isPresent()) {
            id = data.modelData().model().get();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to get model for equipment ({}) for {} ({}) of variant {}", entity.item, entity.owner.getName().getString(), entity.owner.getDragonID(), entity.owner.getVariant());
        }

        return null;
    }

    @Override
    @Nullable
    public Identifier getTextureResource(DragonEquipmentAnimatable entity, GeoRenderer<DragonEquipmentAnimatable> renderer) {
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) return null;

        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(entity.owner, entity.item);
        if (data != null) {
            id = data.modelData().texture();
            assetCache.setTextureLocationCache(id);
            return id;
        } else UselessReptile.LOGGER.warn("Failed to get texture for equipment ({}) for {} ({}) of variant {}", entity.item, entity.owner.getName().getString(), entity.owner.getDragonID(), entity.owner.getVariant());

        return null;
    }

    @Override
    @Nullable
    public Identifier getAnimationResource(DragonEquipmentAnimatable entity) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_ANIMATION;

        AssetCache assetCache = entity.getAssetCache();
        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(entity.owner, entity.item);
        if (data != null && data.modelData().animation().isPresent()) {
            id = data.modelData().animation().get();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to get animation for equipment ({}) for {} ({}) of variant {}", entity.item, entity.owner.getName().getString(), entity.owner.getDragonID(), entity.owner.getVariant());
        }

        assetCache.setAnimationLocationCache(DEFAULT_ANIMATION);
        return DEFAULT_ANIMATION;
    }

    @Override
    public RenderLayer getRenderType(DragonEquipmentAnimatable entity, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderLayer.getEntityCutout(texture);

        AssetCache assetCache = entity.getAssetCache();
        RenderLayer renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(entity.owner, entity.item);
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
