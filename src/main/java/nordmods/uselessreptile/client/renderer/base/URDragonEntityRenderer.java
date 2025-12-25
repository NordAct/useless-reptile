package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.DragonEquipment;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URTags;

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
        if (ResourceUtil.isResourceReloadFinished) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack itemStack = animatable.getItemBySlot(slot);
                if (itemStack.isEmpty()) {
                    animatable.getAssetCache().setEquipment(slot, null);
                    continue;
                }

                DragonEquipment dragonEquipmentAnimatable = assetCache.getEquipment(slot);
                if (dragonEquipmentAnimatable == null || dragonEquipmentAnimatable.itemStack != itemStack) {
                    dragonEquipmentAnimatable = new DragonEquipment(renderState, itemStack);
                    assetCache.setEquipment(slot, dragonEquipmentAnimatable);
                }
                dragonEquipmentAnimatable.ownerRenderState = renderState;
            }
        }
        renderState.setStateData(URStateDataTypes.ASSET_CACHE, animatable.getAssetCache());
        renderState.setStateData(URStateDataTypes.DRAGON_ID, animatable.getDragonId());
        renderState.setStateData(URStateDataTypes.DRAGON_VARIANT, animatable.getVariant());
        renderState.setStateData(URStateDataTypes.DRAGON_NAME, animatable.getName());

        if (animatable instanceof ShooterDragon shooterDragon) renderState.setStateData(URStateDataTypes.DRAGON_SHOOTING_POINT, shooterDragon.getShootingPoint());
    }

    @Override
    public void afterSubmit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            DragonEquipment equipment = ((DragonAssetCache)state.getStateData(URStateDataTypes.ASSET_CACHE)).getEquipment(slot);
            if (equipment != null) {
                DragonEquipmentRenderer usedRenderer = equipment.itemStack.is(URTags.DRAGON_SADDLES) ? saddleRenderer : equipmentRenderer;
                usedRenderer.submitObjectOrdered(equipment, poseStack, submitNodeCollector, cameraRenderState, state.getStateData(StateDataTypes.TICK_DELTA), 1);
            }
        }
    }

    @Override
    public RenderType getRenderType(BRState renderState, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderTypes.entityCutout(texture);

        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        RenderType renderType = assetCache.getRenderTypeCache();
        if (renderType != null) return renderType;

        Identifier dragonId = renderState.getStateData(URStateDataTypes.DRAGON_ID);
        String name = renderState.getStateData(URStateDataTypes.DRAGON_NAME).getString();
        String variant = renderState.getStateData(URStateDataTypes.DRAGON_VARIANT);

        DragonModelData data  = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null) {
            ModelData modelData = data.modelData();
            if (modelData.translucent()) renderType = RenderTypes.entityTranslucent(texture); //all translucent models can't have culling
            else renderType = modelData.cull() ? RenderTypes.entityCutout(texture) : RenderTypes.entityCutoutNoCull(texture);
            assetCache.setRenderTypeCache(renderType);
            return renderType;
        }

        renderType = RenderTypes.entityCutout(texture);
        assetCache.setRenderTypeCache(renderType);
        return renderType;
    }

    @Override
    public Identifier getTextureId(BRState renderState) {
        Identifier dragonId = renderState.getStateData(URStateDataTypes.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(dragonId);

        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        Identifier id = assetCache.getTextureLocationCache();
        if (id != null) return id;

        String name = renderState.getStateData(URStateDataTypes.DRAGON_NAME).getString();
        String variant = renderState.getStateData(URStateDataTypes.DRAGON_VARIANT);

        DragonModelData data  = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null) {
            id = data.modelData().texture();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setTextureLocationCache(id);
                return id;
            }
        } else {
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
}