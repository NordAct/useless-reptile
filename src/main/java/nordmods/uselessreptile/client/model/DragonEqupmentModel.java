package nordmods.uselessreptile.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DragonEqupmentModel extends GeoModel<DragonEquipmentAnimatable> {
    public static final ResourceLocation DEFAULT_ANIMATION = UselessReptile.id("entity/empty");
    public static final ResourceLocation DEFAULT_MODEL = UselessReptile.id("entity/empty");
    public static final ResourceLocation DEFAULT_TEXTURE = TextureAtlas.LOCATION_BLOCKS;

    @Override
    @Nullable
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_MODEL;

        AssetCache assetCache = renderState.getOrDefaultGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE, null) ;
        if (assetCache == null) return DEFAULT_MODEL;
        GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);

        ResourceLocation id = assetCache.getModelLocationCache();
        if (id != null) return id;

        ResourceLocation dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = ownerState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? ownerState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        ResourceLocation itemId = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level,
                itemId
        );
        if (data != null) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id.withPrefix("geckolib/models/").withSuffix(".json"))) {
                assetCache.setModelLocationCache(id);
                return id;
            } else {
                LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;
                if (name == null) name = livingOwnerState.nameTag != null ? livingOwnerState.nameTag.getString() : "???";
                UselessReptile.LOGGER.warn("Failed to find model for equipment ({}) for {} ({}) of variant {}",
                        itemId,
                        name,
                        dragonId,
                        variant);
            }
        }
        assetCache.setModelLocationCache(DEFAULT_MODEL);
        return DEFAULT_MODEL;
    }

    @Override
    @Nullable
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_TEXTURE;

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE);
        if (assetCache == null) return DEFAULT_MODEL;
        GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);

        ResourceLocation id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        ResourceLocation dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = ownerState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? ownerState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        ResourceLocation itemId = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level,
                itemId
        );
        if (data != null) {
            id = data.modelData().texture().withPrefix("textures/").withSuffix(".png");
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
                return id;
            } else {
                LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;
                if (name == null) name = livingOwnerState.nameTag != null ? livingOwnerState.nameTag.getString() : "???";
                UselessReptile.LOGGER.warn("Failed to find texture for equipment ({}) for {} ({}) of variant {}",
                        itemId,
                        name,
                        dragonId,
                        variant);
            }
        }
        assetCache.setTextureLocationCache(DEFAULT_TEXTURE);
        return DEFAULT_TEXTURE;
    }

    @Override
    @Nullable
    public ResourceLocation getAnimationResource(DragonEquipmentAnimatable animatable) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_ANIMATION;

        GeoRenderState ownerState = animatable.ownerRenderState;
        AssetCache assetCache = animatable.getAssetCache();

        ResourceLocation id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        ResourceLocation dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = ownerState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? ownerState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(animatable.item);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level,
                itemId
        );
        if (data != null && data.modelData().animation().isPresent()) {
            id = data.modelData().animation().get();
            if (ResourceUtil.doesExist(id.withPrefix("geckolib/animations/").withSuffix(".json"))) {
                assetCache.setAnimationLocationCache(id);
                return id;
            } else {
                LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)ownerState;
                if (name == null) name = livingOwnerState.nameTag != null ? livingOwnerState.nameTag.getString() : "???";
                UselessReptile.LOGGER.warn("Failed to find animation for equipment ({}) for {} ({}) of variant {}",
                        itemId,
                        name,
                        dragonId,
                        variant);
            }
        }

        assetCache.setAnimationLocationCache(DEFAULT_ANIMATION);
        return DEFAULT_ANIMATION;
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, ResourceLocation texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderType.entityCutout(texture);

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE);
        GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);

        RenderType renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        ResourceLocation dragonId = ownerState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = ownerState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? ownerState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = ownerState.getGeckolibData(URDataTickets.DRAGON_VARIANT);
        ResourceLocation itemId = renderState.getGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID);
        DragonEquipment.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level,
                itemId
        );
        if (data != null && data.modelData().translucent()) {
            renderType = RenderType.armorTranslucent(texture);
            assetCache.setRenderTypeCache(renderType);
            return renderType;
        }

        renderType = RenderType.armorCutoutNoCull(texture);
        assetCache.setRenderTypeCache(renderType);
        return renderType;
    }
}
