package nordmods.uselessreptile.common.util;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.sounds.SoundEvent;
import nordmods.biscuit_roll.common.animation.EffectConsumer;
import nordmods.biscuit_roll.common.animation.EntityAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class URAnimationController<E extends URDragonEntity> extends EntityAnimationController<E> {
    private final EffectConsumer<AnimationData.SoundEffect> soundEffectConsumer;
    public URAnimationController(E animatedObject, boolean singleAnimation) {
        super(animatedObject, singleAnimation);
        this.soundEffectConsumer = (effect, model, state) -> {
            URDragonEntity.SoundInfo soundInfo = animatedObject.getSoundInfo(effect.effect());
            if (soundInfo != null)
                animatedObject.playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), animatedObject.getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
        };
    }

    @Override
    protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {
        this.soundEffectConsumer.accept(soundEffect, model, state);
    }

    @Override
    public float getDefaultTransitionTime() {
        return URDragonEntity.TRANSITION_TICKS/20f;
    }
}
