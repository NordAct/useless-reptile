package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.LookControl;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class DragonLookControl extends LookControl {
    protected final URDragonEntity entity;
    private boolean lockRotation;
    public DragonLookControl(URDragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }

    public boolean isLookingAtTarget() {
        return isLookingAtTarget(entity.getMaxHeadXRot(), entity.getHeadRotSpeed());
    }

    public boolean isLookingAtTarget(float pitchTolerance, float yawTolerance) {
        float pitch = getXRotD().orElse(0f);
        float yaw = getYRotD().orElse(0f);
        return entity.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    public boolean canLookAtTarget() {
        return canLookAtTarget(entity.getMaxHeadXRot() / 1.25f);
    }

    public boolean canLookAtTarget(float pitchTolerance) {
        float pitch = getXRotD().orElse(0f);
        return Math.abs(pitch) < pitchTolerance;
    }

    @Override
    public void tick() {
        if (lockRotation) return;
        if (lookAtCooldown > 0) {
            --lookAtCooldown;
            getYRotD().ifPresent(yaw -> {
                float pitch = getXRotD().orElse(0f);
                entity.yHeadRot = rotateTowards(entity.yHeadRot, yaw, yMaxRotSpeed);
                entity.setXRot(rotateTowards(entity.getXRot(), pitch, xMaxRotAngle));
            });
        } else entity.yHeadRot = rotateTowards(entity.yHeadRot, entity.yBodyRot, yMaxRotSpeed);

        if (!entity.getNavigation().isDone())
            entity.yHeadRot = Mth.rotateIfNecessary(entity.yHeadRot, entity.yBodyRot, yMaxRotSpeed);
    }

    public void setLockRotation(boolean state) {
        lockRotation = state;
    }

    @Override
    public void setLookAt(@NonNull Entity target, float maxYawChange, float maxPitchChange) {
        if (entity.getSensing().hasLineOfSight(target)) super.setLookAt(target, maxYawChange, maxPitchChange);
    }

    @Override
    public Optional<Float> getYRotD() {
        return super.getYRotD();
    }
}
