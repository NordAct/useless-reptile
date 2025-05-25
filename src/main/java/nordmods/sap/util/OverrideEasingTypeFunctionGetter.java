package nordmods.sap.util;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.EasingType;

import java.util.function.Function;

public interface OverrideEasingTypeFunctionGetter<T extends GeoAnimatable> {
    Function<AnimationState<T>, EasingType> getOverrideEasingTypeFunction();
}
