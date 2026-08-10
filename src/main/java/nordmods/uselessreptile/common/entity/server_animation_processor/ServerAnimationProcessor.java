package nordmods.uselessreptile.common.entity.server_animation_processor;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.biscuit_roll.common.util.ServerModelManager;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

// todo redo literally everything to make it work on dedicated server
// THIS IS A PROTOTYPE
// Maybe I also should move this to a library
public abstract class ServerAnimationProcessor<T extends BRAnimatedObject> {
    protected final T animatable;
    protected final BRState state = new BRState.Impl(new HashMap<>());
    @Nullable
    protected BRModel model;
    public ServerAnimationProcessor(T animatable) {
        this.animatable = animatable;
    }

    public void updateBRState() {
        state.setStateData(StateDataTypes.CONTROLLERS, animatable.getAnimationControllers());
        animatable.getAnimationControllers().forEach(controller -> updateControllerVariables(controller.getEnvironment().edit(), animatable, 1));
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        state.setStateData(StateDataTypes.ANIMATION_TIME, getAnimationTime());
    }

    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, T animatable, float tickDelta) {}

    public BRState getState() {
        return state;
    }

    public void tick() {
        updateBRState();
        BRModel model = getModel();
        if (model != null) {
            Collection<BRAnimationController> controllers = state.getStateData(StateDataTypes.CONTROLLERS);
            controllers.forEach(c -> c.update(state));
            state.setStateData(StateDataTypes.ANIMATION_ADJUSTMENT, this::adjustAnimation);
            model.applyAnimations(state);
            model.updateLocators();
            controllers.forEach(c -> c.triggerAnimationEffects(model, state));
        }
    }

    @Nullable
    public BRModel getModel() {
        if (model == null) {
            BRModelProvider modelProvider = state.getStateData(StateDataTypes.MODEL_PROVIDER);
            if (modelProvider == null) throw new IllegalStateException("Cannot get model before model provider is provided");
            model = ServerModelManager.instance().getModel(modelProvider.getModelId(state));
        }
        return model;
    }

    public abstract BRModelProvider getModelProvider();

    public abstract float getAnimationTime();

    public void adjustAnimation(BRState state, BRModel model){}
}
