package nordmods.uselessreptile.common.entity.animation_processor;

import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;

public abstract class EntityAnimationProcessor<T extends Entity & BRAnimatedObject> extends AnimationProcessor<T>{
    public EntityAnimationProcessor(T animatable) {
        super(animatable);
    }

    @Override
    public float getAnimationTime() {
        return animatable.tickCount / 20f;
    }

    @Override
    public boolean isClient() {
        return animatable.level().isClientSide();
    }
}
