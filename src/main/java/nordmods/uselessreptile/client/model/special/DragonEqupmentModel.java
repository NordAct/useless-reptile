package nordmods.uselessreptile.client.model.special;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class DragonEqupmentModel extends GeoModel<DragonEquipmentAnimatable> {
    public static final Identifier DEFAULT_ANIMATION = UselessReptile.id("animations/entity/empty.animation.json");
    public static final Identifier DEFAULT_MODEL = UselessReptile.id("geo/entity/empty.geo.json");
    public static final Identifier DEFAULT_TEXTURE = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

    @Override
    @Nullable
    public Identifier getModelResource(DragonEquipmentAnimatable entity, GeoRenderer<DragonEquipmentAnimatable> renderer) {
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_MODEL;

        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(entity.owner, entity.item);
        if (data != null && ResourceUtil.doesExist(data.modelData().model())) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to find model for equipment ({}) for {} ({}) of variant {}", entity.item, entity.owner.getName().getString(), entity.owner.getDragonId(), entity.owner.getVariant());
        }
        assetCache.setModelLocationCache(DEFAULT_MODEL);
        return DEFAULT_MODEL;
    }

    @Override
    @Nullable
    public Identifier getTextureResource(DragonEquipmentAnimatable entity, GeoRenderer<DragonEquipmentAnimatable> renderer) {
        AssetCache assetCache = entity.getAssetCache();
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_TEXTURE;

        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(entity.owner, entity.item);
        if (data != null && ResourceUtil.doesExist(data.modelData().texture())) {
            id = data.modelData().texture();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to find texture for equipment ({}) for {} ({}) of variant {}", entity.item, entity.owner.getName().getString(), entity.owner.getDragonId(), entity.owner.getVariant());
        }
        assetCache.setTextureLocationCache(DEFAULT_TEXTURE);
        return DEFAULT_TEXTURE;
    }

    @Override
    @Nullable
    public Identifier getAnimationResource(DragonEquipmentAnimatable entity) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_ANIMATION;

        AssetCache assetCache = entity.getAssetCache();
        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(entity.owner, entity.item);
        if (data != null && data.modelData().animation().isPresent()) {
            id = data.modelData().animation().get();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to find animation for equipment ({}) for {} ({}) of variant {}", entity.item, entity.owner.getName().getString(), entity.owner.getDragonId(), entity.owner.getVariant());
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

        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(entity.owner, entity.item);
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
