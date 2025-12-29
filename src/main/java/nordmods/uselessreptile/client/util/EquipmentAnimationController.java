package nordmods.uselessreptile.client.util;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;

//todo sounds and stuff
public class EquipmentAnimationController extends BRAnimationController {
    public EquipmentAnimationController(Identifier initialAnimFile) {
        super(true, false);
        animationFile = initialAnimFile;
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

    public void checkAgainstOtherController(BRAnimationController controller) {
        playingAnimations.forEach((name, animation) -> {
            BRPlayingAnimation parentAnimation = controller.getAnimation(name);
            if (parentAnimation != null) {
                if (parentAnimation.isStopped())
                    animation.stop();
                animation.setPaused(parentAnimation.isPaused());
                animation.setSpeed(parentAnimation.getSpeed());
            }
        });
    }
}
