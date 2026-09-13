package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.LookControl;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class WrappedDragonLookControl<T extends URDragonEntity> extends LookControl {
    private final DragonLookControl<T> dragonLookControl;
    public WrappedDragonLookControl(DragonLookControl<T> dragonLookControl) {
        super(dragonLookControl.getDragon());
        this.dragonLookControl = dragonLookControl;
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }

    public boolean isLookingAtTarget() {
        return dragonLookControl.isLookingAtTarget();
    }

    public boolean canLookAtTarget() {
        return dragonLookControl.canLookAtTarget();
    }

    @Override
    public void tick() {}

    public void setLockRotation(boolean state) {
        dragonLookControl.setLockRotation(state);
    }

    @Override
    public void setLookAt(@NonNull Entity target, float maxYawChange, float maxPitchChange) {
        if (mob.getSensing().hasLineOfSight(target)) dragonLookControl.setRotationTarget(target);
    }

    @Override
    public void setLookAt(final @NonNull Entity target) {
        if (mob.getSensing().hasLineOfSight(target)) dragonLookControl.setRotationTarget(target);
    }

    @Override
    public void setLookAt(double x, double y, double z) {
        dragonLookControl.setRotationTarget(x, y, z);
    }

    @Override
    public void setLookAt(double x, double y, double z, float yMaxRotSpeed, float xMaxRotAngle) {
        dragonLookControl.setRotationTarget(x, y, z);
    }

    @Override
    public @NonNull Optional<Float> getYRotD() {
        return dragonLookControl.getYRotD();
    }

    @Override
    protected @NonNull Optional<Float> getXRotD() {
        return dragonLookControl.getXRotD();
    }

    @Override
    public double getWantedX() {
        return dragonLookControl.getWantedX();
    }

    @Override
    public double getWantedY() {
        return dragonLookControl.getWantedY();
    }

    @Override
    public double getWantedZ() {
        return dragonLookControl.getWantedZ();
    }

    public DragonLookControl<T> getLookControl() {
        return dragonLookControl;
    }
}
