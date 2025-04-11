package nordmods.uselessreptile.client.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DragonEqupmentModel extends GeoModel<DragonEquipmentAnimatable> {
    public static final Identifier DEFAULT_ANIMATION = UselessReptile.id("entity/empty");
    public static final Identifier DEFAULT_MODEL = UselessReptile.id("entity/empty");
    public static final Identifier DEFAULT_TEXTURE = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

    @Override
    @Nullable
    public Identifier getModelResource(GeoRenderState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_MODEL;

        AssetCache assetCache = renderState.getOrDefaultGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE, null) ;
        if (assetCache == null) return DEFAULT_MODEL;
        GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
        LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;

        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        Identifier dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = livingOwnerState.customName != null ? livingOwnerState.customName.getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        Identifier itemId = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world,
                itemId
        );
        if (data != null) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id.withPrefixedPath("geckolib/models/").withSuffixedPath(".json"))) {
                assetCache.setModelLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to find model for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    name,
                    dragonId,
                    variant);
        }
        assetCache.setModelLocationCache(DEFAULT_MODEL);
        return DEFAULT_MODEL;
    }

    @Override
    @Nullable
    public Identifier getTextureResource(GeoRenderState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_TEXTURE;

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE);
        if (assetCache == null) return DEFAULT_MODEL;
        GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
        LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;

        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        Identifier dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = livingOwnerState.customName != null ? livingOwnerState.customName.getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        Identifier itemId = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world,
                itemId
        );
        if (data != null) {
            id = data.modelData().texture();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to find texture for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    name,
                    dragonId,
                    variant);
        }
        assetCache.setTextureLocationCache(DEFAULT_TEXTURE);
        return DEFAULT_TEXTURE;
    }

    @Override
    @Nullable
    public Identifier getAnimationResource(DragonEquipmentAnimatable animatable) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_ANIMATION;

        GeoRenderState ownerState = animatable.ownerRenderState;
        LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;
        AssetCache assetCache = animatable.getAssetCache();

        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        Identifier dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = livingOwnerState.customName != null ? livingOwnerState.customName.getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        Identifier itemId = Registries.ITEM.getId(animatable.item);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world,
                itemId
        );
        if (data != null && data.modelData().animation().isPresent()) {
            id = data.modelData().animation().get();
            if (ResourceUtil.doesExist(id.withPrefixedPath("geckolib/animations/").withSuffixedPath(".json"))) {
                assetCache.setAnimationLocationCache(id);
                return id;
            } else UselessReptile.LOGGER.warn("Failed to find animation for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    name,
                    dragonId,
                    variant);
        }

        assetCache.setAnimationLocationCache(DEFAULT_ANIMATION);
        return DEFAULT_ANIMATION;
    }

    @Override
    public RenderLayer getRenderType(GeoRenderState renderState, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderLayer.getEntityCutout(texture);

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE);
        GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
        LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;

        RenderLayer renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        Identifier dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = livingOwnerState.customName != null ? livingOwnerState.customName.getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        Identifier itemId = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world,
                itemId
        );
        if (data != null) {
            ModelData modelData = data.modelData();
            if (modelData.translucent()) renderType = RenderLayer.getEntityTranslucent(texture); //all translucent models can't have culling
            else renderType = modelData.cull() ? RenderLayer.getEntityCutout(texture) : RenderLayer.getEntityCutoutNoCull(texture);
            assetCache.setRenderTypeCache(renderType);
            return renderType;
        }

        renderType = RenderLayer.getEntityCutout(texture);
        assetCache.setRenderTypeCache(renderType);
        return renderType;
    }
}
