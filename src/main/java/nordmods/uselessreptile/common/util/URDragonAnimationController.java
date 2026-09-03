package nordmods.uselessreptile.common.util;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.sounds.SoundEvent;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.resource_managers.BRAnimationManager;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class URDragonAnimationController<E extends URDragonEntity> extends BRAnimationController {
    private final E dragon;
    public URDragonAnimationController(E dragon, boolean singleAnimation) {
        super(singleAnimation);
        this.dragon = dragon;
    }

    @Override
    protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {
        if (dragon.getAnimationProcessor() != null && dragon.level().isClientSide()) return;
        URDragonEntity.SoundInfo soundInfo = dragon.getSoundInfo(soundEffect.effect());
        if (soundInfo != null)
            dragon.playSound(
                    SoundEvent.createVariableRangeEvent(soundInfo.id()),
                    soundInfo.volume(),
                    dragon.getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()
                    )
            );
    }

    @Override
    protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel brModel, BRState brState) {

    }

    @Override
    protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel brModel, BRState brState) {

    }

    @Override
    public float getDefaultTransitionTime() {
        return URDragonEntity.TRANSITION_TICKS/20f;
    }

    public boolean isPlayingAbilityAnimation(URDragonEntity.AnimationController controller) {
        for (DragonAbilityHolder holder : dragon.getAbilityHolders().values()) {
            if (!holder.getAbility().isActive(holder)) continue;
            for (CommonDragonAbilityData.ConditionedAnimation conditionedAnimation : holder.getAbility().getCommonAbilityData().animations()) {
                if (conditionedAnimation.controller() != controller) continue;
                for (String animation : conditionedAnimation.animations()) {
                    if (playingAnimations.containsKey(animation) && !playingAnimations.get(animation).isTransitioningOut()) return true;
                }
            }
        }
        return false;
    }

    @Override
    public AnimationData getAnimationData(String animation) {
        return this.animationFile == null ? null : BRAnimationManager.getAnimationManager(dragon.level().isClientSide()).getAnimation(this.animationFile, animation);
    }

    @Override
    public void playAnimation(String animation) {
        if (dragon.getAnimationProcessor() == null || getAnimation(animation) == null) super.playAnimation(animation);
    }
}
