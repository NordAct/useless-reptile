package nordmods.uselessreptile.common.entity.dragon_equipment;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import nordmods.biscuit_roll.common.animation.controller.SideAwareAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.resource_managers.BRAnimationManager;
import nordmods.biscuit_roll.common.state.BRState;

//todo sounds and stuff
public class EquipmentAnimationController extends SideAwareAnimationController {
    public EquipmentAnimationController(DragonEquipment equipment) {
        super(false, () -> equipment.owner.level().isClientSide());
        animationFile = equipment.getAssetCache().getAnimationLocationCache();
    }

    @Override
    protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel brModel, BRState brState) {

    }

    @Override
    protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel brModel, BRState brState) {

    }

    @Override
    protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel brModel, BRState brState) {

    }

    @Override
    public AnimationData getAnimationData(String animation) { //silence errors since missing animations is acceptable behavior
        return animationFile == null ? null : BRAnimationManager.getAnimationManager(isClient()).getAnimation(animationFile, animation, false);
    }
}
