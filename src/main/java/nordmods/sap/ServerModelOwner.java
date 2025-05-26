package nordmods.sap;

import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.ArrayList;
import java.util.List;

public interface ServerModelOwner<T extends GeoAnimatable> extends GeoAnimatable {
    GeoModel<T> getServerModel();
    int getInstanceId();
    World getServerWorld();

    default void processServerAnimation() { //todo figure out why the hell queries seem to not work and animation eventually desyncs
        if (getServerWorld().isClient()) return;

        AnimatableManager<T> manager = getAnimatableInstanceCache().getManagerForId(getInstanceId());
        manager.getAnimationControllers().forEach((controllerName, controller) -> {
            GeoRenderState renderState = new GeoRenderState.Impl();
            renderState.addGeckolibData(SAP.ANIMATION_TICKS, getTick(this));
            renderState.addGeckolibData(SAP.BONE_RESET_TIME, getBoneResetTime());

            MolangQueries.Actor<T> actor = new MolangQueries.Actor<>((T)this, renderState, new MutableObject<>(controller), getTick(this), 1, getServerWorld(), null, null);
            controller.prepareForRenderPass((T) this, manager, actor, new Reference2DoubleOpenHashMap<>(1), getTick(this), getServerModel());

            getServerModel().handleAnimations(new AnimationState<>(renderState, manager, 1, new Reference2DoubleOpenHashMap<>(0), controller));
        });
    }

    default List<GeoBone> getFullPath(GeoBone bone) {
        List<GeoBone> list = new ArrayList<>();
        list.add(bone);
        while (bone.getParent() != null) {
            list.add(bone.getParent());
            bone = bone.getParent();
        }
        return list.reversed();
    }

    default Vec3d getBonePos(String name) {
        return getBonePos(getServerModel().getAnimationProcessor().getBone(name));
    }

    default Vec3d getBonePos(GeoBone geoBone) {
        List<GeoBone> path = getFullPath(geoBone);
        Matrix4f global = new Matrix4f().identity();

        for (GeoBone bone : path) {
            GeoBone parent = bone.getParent();
            Vector3f parentPivot = parent != null ? new Vector3f(parent.getPivotX(), parent.getPivotY(), parent.getPivotZ()) : new Vector3f();
            Vector3f pivot = new Vector3f(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ()).sub(parentPivot);
            Vector3f rotation = new Vector3f(bone.getRotX(), bone.getRotY(), bone.getRotZ());
            Vector3f scale = new Vector3f(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
            Vector3f translation = new Vector3f(-bone.getPosX(), bone.getPosY(), bone.getPosZ());
            //
            global.translate(pivot);
            global.rotateZYX(rotation);
            global.translate(translation);
            global.scale(scale);
        }
        Vector4f worldPosition = new Vector4f(0, 0, 0, 1).mul(global);
        return new Vec3d(worldPosition.x/16f, worldPosition.y/16f, worldPosition.z/16f);
    }
}
