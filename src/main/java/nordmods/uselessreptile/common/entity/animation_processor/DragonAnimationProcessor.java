package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.common.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.entity.dragon_equipment.SaddleEquipment;
import nordmods.uselessreptile.common.entity.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.common.init.URStateDataTypes;

import java.util.List;

public class DragonAnimationProcessor<T extends URDragonEntity> extends AnimationProcessor<T> {
    private static final URDragonEntityModelProvider MODEL_PROVIDER = new URDragonEntityModelProvider();
    private final DragonAssetCache assetCache = new DragonAssetCache();
    private final CloneAnimationController cloneController = new CloneAnimationController();
    private final List<BRAnimationController> cloneControllerList = List.of(cloneController);
    public DragonAnimationProcessor(T animatable) {
        super(animatable);
    }

    @Override
    public URDragonEntityModelProvider getModelProvider() {
        return MODEL_PROVIDER;
    }

    @Override
    public float getAnimationTime() {
        return animatable.tickCount / 20f;
    }

    @Override
    public void updateBRState() {
        state.setStateData(URStateDataTypes.BODY_X_ROTATION, -animatable.getXBodyRot(1));
        state.setStateData(URStateDataTypes.BODY_Y_ROTATION, -animatable.getPreciseBodyRotation(1));
        state.setStateData(URStateDataTypes.HEAD_X_ROTATION, -animatable.getViewXRot(1));
        state.setStateData(URStateDataTypes.HEAD_Y_ROTATION, -animatable.getViewYRot(1));
        state.setStateData(URStateDataTypes.YAW_SPEED, -animatable.getYBodyRotChange(1));
        super.updateBRState();
        state.setStateData(URStateDataTypes.ASSET_CACHE, assetCache);
        Identifier dragonId = animatable.getDragonId();
        state.setStateData(URStateDataTypes.DRAGON_ID, dragonId);
        fillDragonCache(
                assetCache,
                animatable.getDragonVariant(),
                dragonId,
                animatable.getName().getString(),
                animatable.getVariant(),
                getModelProvider().getDefaultModel(dragonId),
                getModelProvider().getDefaultAnimation(dragonId)
        );
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
                        animatable,
                        slot,
                        itemStack,
                        itemId,
                        equipmentAssetCache,
                        animatable.getDragonEquipment().get(itemId),
                        dragonId,
                        animatable.getName().getString(),
                        animatable.getVariant(),
                        getModelProvider().getDefaultModel(dragonId),
                        getModelProvider().getDefaultAnimation(dragonId)
                );
                assetCache.setEquipment(slot, dragonEquipment);
            }
            dragonEquipment.ownerState = state;
        }
    }

    private static DragonEquipment getDragonEquipment(
            URDragonEntity owner,
            EquipmentSlot slot,
            ItemStack itemStack,
            Identifier itemId,
            EquipmentAssetCache assetCache,
            EquipmentModelData.Equipment equipment,
            Identifier dragonId,
            String dragonName,
            String variantName,
            Identifier defaultModel,
            Identifier defaultAnimation
    ) {

        if (equipment == null) {
            assetCache.setCanRender(false);
            return new DragonEquipment(owner, itemStack, assetCache, slot);
        }


        //model
        Identifier id = equipment.modelData().model();
        if (ServerModelManager.instance().hasModel(id)) {
            assetCache.setModelLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find model {} for equipment ({}) for {} ({}) of variant {}",
                    id,
                    itemId,
                    dragonName,
                    dragonId,
                    variantName
            );
            assetCache.setModelLocationCache(defaultModel);
        }


        //animation cache
        id = equipment.modelData().animation();
        if (ServerAnimationManager.instance().hasAnimations(id)) {
            assetCache.setAnimationLocationCache(id);
        } else {
            UselessReptile.LOGGER.warn("Failed to find animation {} for equipment ({}) for {} ({}) of variant {}",
                    id,
                    itemId,
                    dragonName,
                    dragonId,
                    variantName
            );
            assetCache.setAnimationLocationCache(defaultAnimation);
        }

        return equipment.passengerPositions().isPresent() && !equipment.passengerPositions().get().isEmpty()
                ? new SaddleEquipment(owner, itemStack, assetCache, slot)
                : new DragonEquipment(owner, itemStack, assetCache, slot);
    }

    protected void fillDragonCache(
            DragonAssetCache assetCache,
            DragonVariant variant,
            Identifier dragonId,
            String dragonName,
            String variantName,
            Identifier defaultModel,
            Identifier defaultAnimation
    ) {

        //model
        if (assetCache.getModelLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(variant, animatable.level().registryAccess()).modelData().model();
            if (ServerModelManager.instance().hasModel(id)) {
                assetCache.setModelLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find model {} for {} ({}) of variant {}. Default will be used instead",
                        id,
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setModelLocationCache(defaultModel);
            }
        }

        //animation cache
        if (assetCache.getAnimationLocationCache() == null) {
            Identifier id = DragonVariantUtil.getDragonModelData(variant, animatable.level().registryAccess()).modelData().animation();
            if (ServerAnimationManager.instance().hasAnimations(id)) {
                assetCache.setAnimationLocationCache(id);
            } else {
                UselessReptile.LOGGER.warn("Failed to find animation {} for {} ({}) of variant {}. Default will be used instead",
                        id,
                        dragonName,
                        dragonId,
                        variantName
                );
                assetCache.setAnimationLocationCache(defaultAnimation);
            }
        }
    }

    @Override
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, T animatable, float tickDelta) {
        super.updateControllerVariables(builder, animatable, tickDelta);
        builder.setQuery("body_x_rotation", state.getStateData(URStateDataTypes.BODY_X_ROTATION, 0f));
        builder.setQuery("head_x_rotation", state.getStateData(URStateDataTypes.HEAD_X_ROTATION, 0f));
        builder.setQuery("body_y_rotation", state.getStateData(URStateDataTypes.BODY_Y_ROTATION, 0f));
        builder.setQuery("head_y_rotation", state.getStateData(URStateDataTypes.HEAD_Y_ROTATION, 0f));
        builder.setQuery("yaw_speed", state.getStateData(URStateDataTypes.YAW_SPEED, 0f));
    }

    @Override
    public void postAnimation() {
        super.postAnimation();
        state.setStateData(URStateDataTypes.BONE_TRANSFORMS, BoneTransform.collectBoneTransforms(getModel().getBones()));
    }

    @Override
    public void tick() {
        if (animatable.level().isClientSide()) cloneController.copyFrom(animatable);
        super.tick();
    }

    @Override
    public List<BRAnimationController> getAnimationControllers() {
        return animatable.level().isClientSide() ? cloneControllerList : super.getAnimationControllers();
    }
}
