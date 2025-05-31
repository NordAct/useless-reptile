package nordmods.sap.mixin;

import nordmods.sap.util.AnimationControllerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.Set;
import java.util.function.Function;

@Mixin(value = AnimationController.class, remap = false)
public abstract class AnimationControllerMixin<T extends GeoAnimatable> implements AnimationControllerAccessor<T> {
    @Shadow protected Function<AnimationState<T>, EasingType> overrideEasingTypeFunction;

    @Shadow protected abstract Set<Variable> getUsedVariables();

    @Override
    public Function<AnimationState<T>, EasingType> useless_reptile$getOverrideEasingTypeFunction() {
        return overrideEasingTypeFunction;
    }

    @Override
    public Set<Variable> useless_reptile$getUsedVariables() {
        return getUsedVariables();
    }

    //@ModifyVariable(method = "beginTick", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //private Map<String, GeoBone> filterUsedBones(Map<String, GeoBone> value, @Local(argsOnly = true) AnimationState<T> state) {
    //    if (!state.hasData(SAP.PROCESSABLE_BONES)) return value;
    //    Map<String, GeoBone> newMap = new Object2ObjectOpenHashMap<>();
    //    Collection<String> processableBones = state.getData(SAP.PROCESSABLE_BONES);
    //    processableBones.forEach(bone -> newMap.put(bone, value.get(bone)));
    //    return newMap;
    //}
}
