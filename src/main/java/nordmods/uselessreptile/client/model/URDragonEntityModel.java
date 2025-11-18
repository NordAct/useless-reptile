package nordmods.uselessreptile.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
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

public class URDragonEntityModel<T extends URDragonEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getAnimationResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(entity.getDragonId());

        AssetCache assetCache = entity.getAssetCache();
        ResourceLocation id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        ResourceLocation dragonId = entity.getDragonId();
        String name = entity.hasCustomName() ? entity.getCustomName().getString() : null;
        String variant = entity.getVariant();
        DragonModel data = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null) {
            id = data.modelData().animation().orElseThrow();
            if (ResourceUtil.doesExist(id.withPrefix("geckolib/animations/").withSuffix(".json"))) {
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
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        ResourceLocation dragonId = renderState.getGeckolibData(URDataTickets.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(dragonId);

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        ResourceLocation id = assetCache.getModelLocationCache();
        if (id != null) return id;

        String name = renderState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? renderState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = renderState.getGeckolibData(URDataTickets.DRAGON_VARIANT);

        DragonModel data = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null ) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id.withPrefix("geckolib/models/").withSuffix(".json"))) {
                assetCache.setModelLocationCache(id);
                return id;
            }
        } else {
            LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)renderState;
            if (name == null) name = livingOwnerState.nameTag != null ? livingOwnerState.nameTag.getString() : "???";
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
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        ResourceLocation dragonId = renderState.getGeckolibData(URDataTickets.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(dragonId);

        AssetCache assetCache = renderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        ResourceLocation id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        String name = renderState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? renderState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = renderState.getGeckolibData(URDataTickets.DRAGON_VARIANT);

        DragonModel data  = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null) {
            id = data.modelData().texture().withPrefix("textures/").withSuffix(".png");
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
                return id;
            }
        } else {
            LivingEntityRenderState livingOwnerState = (LivingEntityRenderState)renderState;
            if (name == null) name = livingOwnerState.nameTag != null ? livingOwnerState.nameTag.getString() : "???";
            UselessReptile.LOGGER.warn("Failed to find texture for {} ({}) of variant {}. Default will be used instead",
                    name,
                    dragonId,
                    variant);
        }

        id = getDefaultTexture(dragonId);
        assetCache.setTextureLocationCache(id);
        return id;
    }

    protected final ResourceLocation getDefaultTexture(ResourceLocation entity) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putString("id", entity.toString());
        URDragonEntity dragon = (URDragonEntity) EntityType.create(TagValueInput.create(UselessReptile.ERROR_REPORTER,  Minecraft.getInstance().level.registryAccess(), nbtCompound), Minecraft.getInstance().level, EntitySpawnReason.TRIGGERED).get();
        dragon.discard();
        return UselessReptile.id("textures/entity/"+ entity.getPath() + "/" + dragon.getDefaultVariant() + ".png");
    }

    protected final ResourceLocation getDefaultAnimation(ResourceLocation entity) {
        return UselessReptile.id("entity/" + entity.getPath() + "/" + entity.getPath());
    }

    protected final ResourceLocation getDefaultModel(ResourceLocation entity) {
        return UselessReptile.id("entity/" + entity.getPath() + "/" + entity.getPath());
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, ResourceLocation texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderType.entityCutout(texture);

        DragonAssetCache assetCache = renderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        RenderType renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        ResourceLocation dragonId = renderState.getGeckolibData(URDataTickets.DRAGON_ID);
        String name = renderState.getGeckolibData(URDataTickets.DRAGON_NAME) != null ? renderState.getGeckolibData(URDataTickets.DRAGON_NAME).getString() : null;
        String variant = renderState.getGeckolibData(URDataTickets.DRAGON_VARIANT);

        DragonModel data  = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null) {
            ModelData modelData = data.modelData();
            if (modelData.translucent()) renderType = RenderType.entityTranslucent(texture); //all translucent models can't have culling
            else renderType = modelData.cull() ? RenderType.entityCutout(texture) : RenderType.entityCutoutNoCull(texture);
            assetCache.setRenderTypeCache(renderType);
            return renderType;
        }

        renderType = RenderType.entityCutout(texture);
        assetCache.setRenderTypeCache(renderType);
        return renderType;
    }

    @Override
    public void prepareForRenderPass(T animatable, GeoRenderState renderState, float partialTick) {
        if (renderState.hasGeckolibData(URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT) && !renderState.getGeckolibData(URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT)) return;
        super.prepareForRenderPass(animatable, renderState, partialTick);
    }
}
