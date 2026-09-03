package nordmods.uselessreptile.common.entity.dragon_equipment;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.resource_managers.BRAnimationManager;
import nordmods.biscuit_roll.common.state.BRState;

//todo sounds and stuff
public class EquipmentAnimationController extends BRAnimationController {
    private final DragonEquipment equipment;
    public EquipmentAnimationController(DragonEquipment equipment) {
        super(false);
        this.equipment = equipment;
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
    public AnimationData getAnimationData(String animation) {
        return this.animationFile == null ? null : BRAnimationManager.getAnimationManager(equipment.owner.level().isClientSide()).getAnimation(this.animationFile, animation, false);
    }
}
