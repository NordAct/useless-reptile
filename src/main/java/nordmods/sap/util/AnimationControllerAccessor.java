package nordmods.sap.util;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.Collection;
import java.util.function.Function;

public interface AnimationControllerAccessor<T extends GeoAnimatable> {
    Function<AnimationState<T>, EasingType> useless_reptile$getOverrideEasingTypeFunction();
    Collection<Variable> useless_reptile$getUsedVariables();
}
