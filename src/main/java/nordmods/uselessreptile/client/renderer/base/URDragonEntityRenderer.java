package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.asset_cache.AssetCache;
import nordmods.uselessreptile.client.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.client.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.client.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.*;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public abstract class URDragonEntityRenderer<T extends URDragonEntity> extends BREntityRenderer<T, LivingEntityRenderState> {
    private final DragonEquipmentRenderer equipmentRenderer = new DragonEquipmentRenderer();
    private final DragonSaddleRenderer saddleRenderer = new DragonSaddleRenderer();
    public URDragonEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new URDragonEntityModelProvider());
        addRenderLayer(new URGlowingLayer(this, 1));
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState state) {
        return super.getShadowRadius(state) * state.scale;
    }

    @Override
    public void extractRenderState(T animatable, LivingEntityRenderState renderState, float tickDelta) {
        super.extractRenderState(animatable, renderState, tickDelta);
        DragonAssetCache assetCache = animatable.getAssetCache();
        renderState.setStateData(URStateDataTypes.ASSET_CACHE, assetCache);
        Identifier dragonId = animatable.getDragonId();
        renderState.setStateData(URStateDataTypes.DRAGON_ID, dragonId);

        if (ResourceUtil.isResourceReloadFinished) {
            fillDragonCache(animatable, assetCache, dragonId);
            getModel(renderState).getBones().forEach(animatedBone -> setBoneVisibility(renderState, animatedBone, true, false));

            //equipment
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack itemStack = animatable.getItemBySlot(slot);
                if (itemStack.isEmpty()) {
                    animatable.getAssetCache().setEquipment(slot, null);
                    continue;
                }

                DragonEquipment dragonEquipment = assetCache.getEquipment(slot);
                //create new equipment if none exists or items don't match
                if (dragonEquipment == null || dragonEquipment.itemStack != itemStack) {
                    EquipmentAssetCache equipmentAssetCache = new EquipmentAssetCache();
                    dragonEquipment = getDragonEquipment(animatable, BuiltInRegistries.ITEM.getKey(itemStack.getItem()), equipmentAssetCache, itemStack, dragonId);
                    assetCache.setEquipment(slot, dragonEquipment);
                }
                for (String hidBone : dragonEquipment.getAssetCache().getHidBones()) {
                    setBoneVisibility(renderState, getModel(renderState).getBone(hidBone), false, false);
                }
                dragonEquipment.ownerRenderState = renderState;
            }
        }

        if (animatable instanceof ShooterDragon shooterDragon) renderState.setStateData(URStateDataTypes.DRAGON_SHOOTING_POINT, shooterDragon.getShootingPoint());
    }

    private void fillDragonCache(T animatable, DragonAssetCache assetCache, Identifier dragonId) {
        //texture
        if (assetCache.getTextureLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(animatable.getDragonDisplayVariant(), Minecraft.getInstance().level).modelData().texture();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find texture for {} ({}) of variant {}. Default will be used instead",
                        animatable.getName().getString(),
                        dragonId,
                        animatable.getVariant()
                );
                assetCache.setTextureLocationCache(getDefaultTexture(dragonId));
            }
        }

        //model
        if (assetCache.getModelLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(animatable.getDragonDisplayVariant(), Minecraft.getInstance().level).modelData().model();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find model for {} ({}) of variant {}. Default will be used instead",
                        animatable.getName().getString(),
                        dragonId,
                        animatable.getVariant()
                );
                assetCache.setModelLocationCache(getModelProvider().getDefaultModel(dragonId));
            }
        }

        //animation cache
        if (assetCache.getAnimationLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(animatable.getDragonDisplayVariant(), Minecraft.getInstance().level).modelData().animation();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find animation for {} ({}) of variant {}. Default will be used instead",
                        animatable.getName().getString(),
                        dragonId,
                        animatable.getVariant()
                );
                assetCache.setAnimationLocationCache(getModelProvider().getDefaultAnimation(dragonId));
            }
        }

        //render type
        if (assetCache.getRenderTypeProviderCache() == null) {
            ModelData modelData = DragonVariantUtil.getDragonModelData(animatable.getDragonDisplayVariant(), Minecraft.getInstance().level).modelData();
            BRModelSubmitStorage.RenderTypeProvider renderTypeProvider;
            if (modelData.translucent()) renderTypeProvider = ((state, texture) -> RenderTypes.entityTranslucent(texture)); //all translucent models can't have culling
            else renderTypeProvider = modelData.cull() ? ((state, texture) ->RenderTypes.entityCutout(texture)) : ((state, texture) ->RenderTypes.entityCutoutNoCull(texture));
            assetCache.setRenderTypeProviderCache(renderTypeProvider);
        }
    }

    private DragonEquipment getDragonEquipment(T animatable, Identifier itemId, EquipmentAssetCache equipmentAssetCache, ItemStack itemStack, Identifier dragonId) {
        EquipmentModelData.Equipment data = animatable.getDragonDisplayEquipment().get(itemId);

        if (data == null) {
            equipmentAssetCache.setCanRender(false);
            return new DragonEquipment(itemStack, equipmentAssetCache, false);
        }


        Identifier id = data.modelData().texture();
        if (ResourceUtil.doesExist(id)) {
            equipmentAssetCache.setTextureLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find texture for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    animatable.getName().getString(),
                    dragonId,
                    animatable.getVariant()
            );
            equipmentAssetCache.setTextureLocationCache(DragonEquipmentRenderer.DEFAULT_TEXTURE);
        }


        //model
        id = data.modelData().model();
        if (ResourceUtil.doesExist(id)) {
            equipmentAssetCache.setModelLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find model for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    animatable.getName().getString(),
                    dragonId,
                    animatable.getVariant()
            );
            equipmentAssetCache.setModelLocationCache(equipmentRenderer.getModelProvider().getDefaultModel(dragonId));
        }


        //animation cache
        id = data.modelData().animation();
        if (ResourceUtil.doesExist(id)) {
            equipmentAssetCache.setAnimationLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find animation for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    animatable.getName().getString(),
                    dragonId,
                    animatable.getVariant()
            );
            equipmentAssetCache.setAnimationLocationCache(equipmentRenderer.getModelProvider().getDefaultAnimation(dragonId));
        }

        //render type
        if (data.modelData().translucent()) {
            equipmentAssetCache.setRenderTypeProviderCache((state, texture) -> RenderTypes.entityTranslucent(texture));
        } else equipmentAssetCache.setRenderTypeProviderCache(((state, texture) -> RenderTypes.entityCutoutNoCull(texture)));

        data.hidBones().ifPresent(bones -> equipmentAssetCache.setHidBones(bones.toArray(new String[0])));

        return new DragonEquipment(itemStack, equipmentAssetCache, data.maxPassengers().isPresent() && data.maxPassengers().get() > 0);
    }

    @Override
    public void afterSubmit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            DragonEquipment equipment = ((DragonAssetCache)state.getStateData(URStateDataTypes.ASSET_CACHE)).getEquipment(slot);
            if (equipment != null && equipment.getAssetCache().canRender()) {
                DragonEquipmentRenderer usedRenderer = equipment.isSaddle ? saddleRenderer : equipmentRenderer;
                usedRenderer.submitObjectOrdered(equipment, poseStack, submitNodeCollector, cameraRenderState, state.getStateData(StateDataTypes.TICK_DELTA), 1);
            }
        }
    }

    @Override
    public RenderType getRenderType(BRState renderState, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderTypes.entityCutout(texture);
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getRenderTypeProviderCache().getRenderType(renderState, texture);
    }

    @Override
    public Identifier getTextureId(BRState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(renderState.getStateData(URStateDataTypes.DRAGON_ID));
        return renderState.getStateData(URStateDataTypes.ASSET_CACHE).getTextureLocationCache();
    }

    protected final Identifier getDefaultTexture(Identifier entity) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putString("id", entity.toString());
        URDragonEntity dragon = (URDragonEntity) EntityType.create(TagValueInput.create(UselessReptile.ERROR_REPORTER,  Minecraft.getInstance().level.registryAccess(), nbtCompound), Minecraft.getInstance().level, EntitySpawnReason.TRIGGERED).get();
        dragon.discard();
        return UselessReptile.id("textures/entity/"+ entity.getPath() + "/" + dragon.getDefaultVariant() + ".png");
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public URDragonEntityModelProvider getModelProvider() {
        return (URDragonEntityModelProvider) super.getModelProvider();
    }
}