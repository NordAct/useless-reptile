package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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
import nordmods.biscuit_roll.common.model.BRModel;
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
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
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
            fillDragonCache(
                    assetCache,
                    animatable.getDragonDisplayVariant(),
                    dragonId,
                    animatable.getName().getString(),
                    animatable.getVariant(),
                    getDefaultTexture(dragonId),
                    getModelProvider().getDefaultModel(dragonId),
                    getModelProvider().getDefaultAnimation(dragonId)
            );
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
                    Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                    dragonEquipment = getDragonEquipment(
                            itemStack,
                            itemId,
                            equipmentAssetCache,
                            animatable.getDragonDisplayEquipment().get(itemId),
                            dragonId,
                            animatable.getName().getString(),
                            animatable.getVariant(),
                            DragonEquipmentRenderer.DEFAULT_TEXTURE,
                            equipmentRenderer.getModelProvider().getDefaultModel(dragonId),
                            equipmentRenderer.getModelProvider().getDefaultAnimation(dragonId)
                    );
                    assetCache.setEquipment(slot, dragonEquipment);
                }
                dragonEquipment.ownerRenderState = renderState;
            }
        }

        if (animatable instanceof ShooterDragon shooterDragon) renderState.setStateData(URStateDataTypes.DRAGON_SHOOTING_POINT, shooterDragon.getShootingPoint());
    }

    @Override
    public void adjustAnimation(BRState state, BRModel model) {
        if (!ResourceUtil.isResourceReloadFinished) return;
        model.getRootBones().forEach(animatedBone -> animatedBone.setVisible(true));

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            DragonEquipment equipment = ((DragonAssetCache)state.getStateData(URStateDataTypes.ASSET_CACHE)).getEquipment(slot);
            if (equipment == null) continue;
            for (String hidBone : equipment.getAssetCache().getHidBones()) {
                model.getBone(hidBone).setVisible(false);
            }
        }
    }



    public static void fillDragonCache(
            DragonAssetCache assetCache,
            DragonVariant displayVariant,
            Identifier dragonId,
            String dragonName,
            String variantName,
            Identifier defaultTexture,
            Identifier defaultModel,
            Identifier defaultAnimation
    ) {
        //texture
        if (assetCache.getTextureLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(displayVariant, Minecraft.getInstance().level).modelData().texture();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find texture for {} ({}) of variant {}. Default will be used instead",
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setTextureLocationCache(defaultTexture);
            }
        }

        //model
        if (assetCache.getModelLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(displayVariant, Minecraft.getInstance().level).modelData().model();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find model for {} ({}) of variant {}. Default will be used instead",
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setModelLocationCache(defaultModel);
            }
        }

        //animation cache
        if (assetCache.getAnimationLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(displayVariant, Minecraft.getInstance().level).modelData().animation();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find animation for {} ({}) of variant {}. Default will be used instead",
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setAnimationLocationCache(defaultAnimation);
            }
        }

        //render type
        if (assetCache.getRenderTypeProviderCache() == null) {
            ModelData modelData = DragonVariantUtil.getDragonModelData(displayVariant, Minecraft.getInstance().level).modelData();
            BRModelSubmitStorage.RenderTypeProvider renderTypeProvider;
            if (modelData.translucent()) renderTypeProvider = ((state, texture) -> RenderTypes.entityTranslucent(texture)); //all translucent models can't have culling
            else renderTypeProvider = modelData.cull() ? ((state, texture) ->RenderTypes.entityCutout(texture)) : ((state, texture) -> RenderTypes.entityCutout(texture));
            assetCache.setRenderTypeProviderCache(renderTypeProvider);
        }
    }

    private static DragonEquipment getDragonEquipment(
            ItemStack itemStack,
            Identifier itemId,
            EquipmentAssetCache assetCache,
            EquipmentModelData.Equipment equipment,
            Identifier dragonId,
            String dragonName,
            String variantName,
            Identifier defaultTexture,
            Identifier defaultModel,
            Identifier defaultAnimation
    ) {

        if (equipment == null) {
            assetCache.setCanRender(false);
            return new DragonEquipment(itemStack, assetCache, false);
        }


        Identifier id = equipment.modelData().texture();
        if (ResourceUtil.doesExist(id)) {
            assetCache.setTextureLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find texture for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    dragonName,
                    dragonId,
                    variantName
            );
            assetCache.setTextureLocationCache(defaultTexture);
        }


        //model
        id = equipment.modelData().model();
        if (ResourceUtil.doesExist(id)) {
            assetCache.setModelLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find model for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    dragonName,
                    dragonId,
                    variantName
            );
            assetCache.setModelLocationCache(defaultModel);
        }


        //animation cache
        id = equipment.modelData().animation();
        if (ResourceUtil.doesExist(id)) {
            assetCache.setAnimationLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find animation for equipment ({}) for {} ({}) of variant {}",
                    itemId,
                    dragonName,
                    dragonId,
                    variantName
            );
            assetCache.setAnimationLocationCache(defaultAnimation);
        }

        //render type
        if (equipment.modelData().translucent()) {
            assetCache.setRenderTypeProviderCache((state, texture) -> RenderTypes.entityTranslucent(texture));
        } else assetCache.setRenderTypeProviderCache(((state, texture) -> RenderTypes.entityCutout(texture)));

        equipment.hidBones().ifPresent(bones -> assetCache.setHidBones(bones.toArray(new String[0])));

        return new DragonEquipment(itemStack, assetCache, equipment.passengerPositions().isPresent() && !equipment.passengerPositions().get().isEmpty());
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