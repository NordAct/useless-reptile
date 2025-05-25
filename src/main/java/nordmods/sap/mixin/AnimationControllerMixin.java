package nordmods.sap.mixin;

import nordmods.sap.util.OverrideEasingTypeFunctionGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.EasingType;

import java.util.function.Function;

@Mixin(value = AnimationController.class, remap = false)
public abstract class AnimationControllerMixin<T extends GeoAnimatable> implements OverrideEasingTypeFunctionGetter<T> {
    @Shadow protected Function<AnimationState<T>, EasingType> overrideEasingTypeFunction;

    @Override
    public Function<AnimationState<T>, EasingType> getOverrideEasingTypeFunction() {
        return overrideEasingTypeFunction;
    }
}
