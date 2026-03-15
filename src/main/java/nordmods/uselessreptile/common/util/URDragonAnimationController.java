package nordmods.uselessreptile.common.util;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.sounds.SoundEvent;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class URDragonAnimationController<E extends URDragonEntity> extends BRAnimationController {
    private final E dragon;
    public URDragonAnimationController(E dragon, boolean singleAnimation) {
        super( singleAnimation);
        this.dragon = dragon;
    }

    @Override
    protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {
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
}
