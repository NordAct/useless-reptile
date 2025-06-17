package nordmods.uselessreptile.client.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class URDragonModel<T extends URDragonEntity> extends GeoModel<T> {
    @Override
    public Identifier getAnimationResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(entity.getDragonId());

        AssetCache assetCache = entity.getAssetCache();
        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        Identifier dragonId = entity.getDragonId();
        String name = entity.hasCustomName() ? entity.getCustomName().getString() : null;
        String variant = entity.getVariant();
        DragonModel data = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world
        );
        if (data != null) {
            id = data.modelData().animation().orElseThrow();
            if (ResourceUtil.doesExist(id.withPrefixedPath("geckolib/animations/").withSuffixedPath(".json"))) {
                assetCache.setAnimationLocationCache(id);
                return id;
            }
        } else {
            name = entity.getDisplayName().getString();
            UselessReptile.LOGGER.warn("Failed to find animation for {} ({}) of variant {}. Default will be used instead",
                    name,
                    dragonId,
                    variant);
        }

        id = getDefaultAnimation(dragonId);
        assetCache.setAnimationLocationCache(id);
        return id;
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        Identifier dragonId = renderState.getGeckolibData(URDataTickets.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(dragonId);

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        String name = renderState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? renderState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = renderState.getGeckolibData(URDataTickets.DRAGON_VARIANT);

        DragonModel data = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world
        );
        if (data != null ) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id.withPrefixedPath("geckolib/models/").withSuffixedPath(".json"))) {
                assetCache.setModelLocationCache(id);
                return id;
            }
        } else {
            LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)renderState;
            if (name == null) name = livingOwnerState.displayName != null ? livingOwnerState.displayName.getString() : "???";
            UselessReptile.LOGGER.warn("Failed to find model for {} ({}) of variant {}. Default will be used instead",
                    name,
                    dragonId,
                    variant);
        }

        id = getDefaultModel(dragonId);
        assetCache.setModelLocationCache(id);
        return id;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier dragonId = renderState.getGeckolibData(URDataTickets.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(dragonId);

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        String name = renderState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? renderState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = renderState.getGeckolibData(URDataTickets.DRAGON_VARIANT);

        DragonModel data  = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world
        );
        if (data != null) {
            id = data.modelData().texture().withPrefixedPath("textures/").withSuffixedPath(".png");
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
                return id;
            }
        } else {
            LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)renderState;
            if (name == null) name = livingOwnerState.displayName != null ? livingOwnerState.displayName.getString() : "???";
            UselessReptile.LOGGER.warn("Failed to find texture for {} ({}) of variant {}. Default will be used instead",
                    name,
                    dragonId,
                    variant);
        }

        id = getDefaultTexture(dragonId);
        assetCache.setTextureLocationCache(id);
        return id;
    }

    protected final Identifier getDefaultTexture(Identifier entity) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", entity.toString());
        URDragonEntity dragon = (URDragonEntity) EntityType.getEntityFromData(nbtCompound, MinecraftClient.getInstance().world, SpawnReason.TRIGGERED).get();
        dragon.discard();
        return UselessReptile.id("textures/entity/"+ entity.getPath() + "/" + dragon.getDefaultVariant() + ".png");
    }

    protected final Identifier getDefaultAnimation(Identifier entity) {
        return UselessReptile.id("entity/" + entity.getPath() + "/" + entity.getPath());
    }

    protected final Identifier getDefaultModel(Identifier entity) {
        return UselessReptile.id("entity/" + entity.getPath() + "/" + entity.getPath());
    }

    @Override
    public RenderLayer getRenderType(GeoRenderState renderState, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderLayer.getEntityCutout(texture);

        DragonAssetCache assetCache = renderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        RenderLayer renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)renderState;
        Identifier dragonId = renderState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = livingOwnerState.customName != null ? livingOwnerState.customName.getString() : null;
        String variant = renderState.getGeckolibData(URDataTickets.DRAGON_VARIANT);

        DragonModel data  = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                MinecraftClient.getInstance().world
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

    @Override
    public void prepareForRenderPass(T animatable, GeoRenderState renderState) {
        if (renderState.hasGeckolibData(URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT) && !renderState.getGeckolibData(URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT)) return;
        super.prepareForRenderPass(animatable, renderState);
    }
}
