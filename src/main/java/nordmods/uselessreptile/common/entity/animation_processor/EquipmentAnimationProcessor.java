package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.common.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.entity.model_provider.DragonEquipmentModelProvider;
import nordmods.uselessreptile.common.init.URStateDataTypes;

import java.util.List;

public class EquipmentAnimationProcessor extends AnimationProcessor<DragonEquipment> {
    private static final DragonEquipmentModelProvider MODEL_PROVIDER = new DragonEquipmentModelProvider();
    private final CloneAnimationController cloneController = new CloneAnimationController();
    private final List<BRAnimationController> cloneControllerList = List.of(cloneController);
    private final boolean isClient;
    public EquipmentAnimationProcessor(DragonEquipment animatable) {
        super(animatable);
        this.isClient = animatable.owner.level().isClientSide();
    }

    @Override
    public void updateBRState() {
        super.updateBRState();
        state.setStateData(URStateDataTypes.DRAGON_ID, animatable.ownerState.getStateData(URStateDataTypes.DRAGON_ID));
        state.setStateData(URStateDataTypes.ASSET_CACHE, animatable.getAssetCache());
    }

    @Override
    public BRModelProvider getModelProvider() {
        return MODEL_PROVIDER;
    }

    @Override
    public float getAnimationTime() {
        return animatable.ownerState.getStateData(StateDataTypes.ANIMATION_TIME, 0f);
    }

    @Override
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, DragonEquipment animatable, float tickDelta) {
        builder.setQuery("body_x_rotation", animatable.ownerState.getStateData(URStateDataTypes.BODY_X_ROTATION, 0f));
        builder.setQuery("head_x_rotation", animatable.ownerState.getStateData(URStateDataTypes.HEAD_X_ROTATION, 0f));
        builder.setQuery("body_y_rotation", animatable.ownerState.getStateData(URStateDataTypes.BODY_Y_ROTATION, 0f));
        builder.setQuery("head_y_rotation", animatable.ownerState.getStateData(URStateDataTypes.HEAD_Y_ROTATION, 0f));
        builder.setQuery("yaw_speed", animatable.ownerState.getStateData(URStateDataTypes.YAW_SPEED, 0f));
    }

    public EquipmentAssetCache getAssetCache() {
        return animatable.getAssetCache();
    }

    @Override
    public List<BRAnimationController> getAnimationControllers() {
        return isClient ? cloneControllerList : super.getAnimationControllers();
    }

    @Override
    public void tick() {
        if (isClient) {
            cloneController.copyFrom(animatable);
            if (animatable.getAssetCache().getModelLocationCache() == null) return;
        }
        super.tick();
    }
}
