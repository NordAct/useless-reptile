package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.client.util.TextureAtlasSpriteUtil;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.asset_cache.AssetCache;
import nordmods.uselessreptile.client.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.client.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.client.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.client.init.URAtlases;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.animation_processor.DragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class URDragonEntityRenderer<T extends URDragonEntity> extends BREntityRenderer<T, LivingEntityRenderState> {
    private final DragonEquipmentRenderer equipmentRenderer = new DragonEquipmentRenderer();
    private final DragonSaddleRenderer saddleRenderer = new DragonSaddleRenderer();
    public URDragonEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new URDragonEntityModelProvider());
        addRenderLayer(new URGlowingLayer(this, 1));
    }
    private static final Map<Identifier, Identifier> DEFAULT_TEXTURES = new HashMap<>();

    @Override
    protected float getShadowRadius(LivingEntityRenderState state) {
        return super.getShadowRadius(state) * state.scale;
    }

    @Override
    public void extractRenderState(T animatable, LivingEntityRenderState renderState, float tickDelta) {
        renderState.setStateData(URStateDataTypes.BODY_X_ROTATION, -animatable.getXBodyRot(tickDelta));
        renderState.setStateData(URStateDataTypes.BODY_Y_ROTATION, -animatable.getPreciseBodyRotation(tickDelta));
        renderState.setStateData(URStateDataTypes.HEAD_X_ROTATION, -animatable.getViewXRot(tickDelta));
        renderState.setStateData(URStateDataTypes.HEAD_Y_ROTATION, -animatable.getViewYRot(tickDelta));
        renderState.setStateData(URStateDataTypes.YAW_SPEED, -animatable.getYBodyRotChange(tickDelta));
        super.extractRenderState(animatable, renderState, tickDelta);
        DragonAssetCache assetCache = animatable.getAssetCache();
        renderState.setStateData(URStateDataTypes.ASSET_CACHE, assetCache);
        Identifier dragonId = animatable.getDragonId();
        renderState.setStateData(URStateDataTypes.DRAGON_ID, dragonId);
        renderState.setStateData(URStateDataTypes.ANIMATION_PROCESSOR, animatable.getAnimationProcessor());
        if (ResourceUtil.isResourceReloadFinished) {
            fillDragonCache(
                    assetCache,
                    animatable.getDragonVariant(),
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
                            animatable.getDragonEquipment().get(itemId),
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
    }

    @Override
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, T entity, float tickDelta) {
        super.updateControllerVariables(builder, entity, tickDelta);
        builder.setQuery("body_x_rotation", -entity.getXBodyRot(tickDelta));
        builder.setQuery("head_x_rotation", -entity.getViewXRot(tickDelta));
        builder.setQuery("body_y_rotation", -entity.getPreciseBodyRotation(tickDelta));
        builder.setQuery("head_y_rotation", -entity.getViewYRot(tickDelta));
        builder.setQuery("yaw_speed", -entity.getYBodyRotChange(tickDelta));
    }

    @Override
    public void adjustAnimation(BRState state, BRModel model) {
        if (!ResourceUtil.isResourceReloadFinished) return;
        DragonAnimationProcessor<? extends URDragonEntity> animationProcessor = state.getStateData(URStateDataTypes.ANIMATION_PROCESSOR);
        float tickDelta = state.getStateData(StateDataTypes.TICK_DELTA, 0f);
        model.getRootBones().forEach(bone -> bone.setVisible(true));

        if (animationProcessor != null) {
            for (AnimatedBone animatedBone : model.getBones()) {
                animationProcessor.syncPose(animatedBone.getBone().name(), animatedBone.getAnimationPose(), tickDelta);
            }
        }

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
            DragonVariant variant,
            Identifier dragonId,
            String dragonName,
            String variantName,
            Identifier defaultTexture,
            Identifier defaultModel,
            Identifier defaultAnimation
    ) {
        //texture
        if (assetCache.getTextureLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(variant, Minecraft.getInstance().level.registryAccess()).modelData().texture();
            if (ResourceUtil.doesResourceExist(id)) {
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
            Identifier id = DragonVariantUtil.getDragonModelData(variant, Minecraft.getInstance().level.registryAccess()).modelData().model();
            if (ResourceUtil.doesModelExist(id, true)) {
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
            Identifier id = DragonVariantUtil.getDragonModelData(variant, Minecraft.getInstance().level.registryAccess()).modelData().animation();
            if (ResourceUtil.doesAnimationExist(id, true)) {
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
            ModelData modelData = DragonVariantUtil.getDragonModelData(variant, Minecraft.getInstance().level.registryAccess()).modelData();
            BRModelSubmitStorage.RenderTypeProvider renderTypeProvider;
            if (modelData.translucent()) renderTypeProvider = ((_, texture) -> RenderTypes.entityTranslucent(texture)); //all translucent models can't have culling
            else renderTypeProvider = modelData.cull() ? ((_, texture) -> RenderTypes.entityCutoutCull(texture)) : ((_, texture) -> RenderTypes.entityCutout(texture));
            assetCache.setRenderTypeProviderCache(renderTypeProvider);
        }
    }

    public static DragonEquipment getDragonEquipment(
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
        if (ResourceUtil.doesResourceExist(id)) {
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
        if (ResourceUtil.doesModelExist(id, true)) {
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
        if (ResourceUtil.doesAnimationExist(id, true)) {
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
            assetCache.setRenderTypeProviderCache((_, texture) -> RenderTypes.entityTranslucent(texture));
        } else assetCache.setRenderTypeProviderCache(((_, texture) -> RenderTypes.entityCutout(texture)));

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

    public static Identifier getDefaultTexture(Identifier entity) {
        return DEFAULT_TEXTURES.computeIfAbsent(entity, (id) -> {
            List<DragonVariant> available = DragonVariant.getSameType(DragonVariantType.fromId(id),Minecraft.getInstance().level.registryAccess());
            if (available.isEmpty()) return null;
            return DragonVariantUtil.getDragonModelData(available.getFirst(), Minecraft.getInstance().level.registryAccess()).modelData().texture();
        });
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public URDragonEntityModelProvider getModelProvider() {
        return (URDragonEntityModelProvider) super.getModelProvider();
    }

    @Override
    public @Nullable TextureAtlasSprite getSpriteForTexture(Identifier texture) {
        return TextureAtlasSpriteUtil.getTextureAtlasSprite(URAtlases.ANIMATED_TEXTURES_ATLAS_MAPPER, texture);
    }
}