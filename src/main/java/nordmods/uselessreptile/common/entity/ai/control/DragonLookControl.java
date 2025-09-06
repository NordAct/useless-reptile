package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.Optional;

public class DragonLookControl extends LookControl {
    protected final URDragonEntity entity;
    private boolean lockRotation;
    public DragonLookControl(URDragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    protected boolean shouldStayHorizontal() {
        return false;
    }

    public boolean isLookingAtTarget() {
        return isLookingAtTarget(entity.getPitchLimit(), entity.getRotationSpeed());
    }

    public boolean isLookingAtTarget(float pitchTolerance, float yawTolerance) {
        float pitch = getTargetPitch().orElse(0f);
        float yaw = getTargetYaw().orElse(0f);
        return entity.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    public boolean canLookAtTarget() {
        return canLookAtTarget(entity.getPitchLimit() / 1.25f);
    }

    public boolean canLookAtTarget(float pitchTolerance) {
        float pitch = getTargetPitch().orElse(0f);
        return Math.abs(pitch) < pitchTolerance;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger()) return;
        if (lockRotation) return;
        if (lookAtTimer > 0) {
            --lookAtTimer;
            getTargetYaw().ifPresent(yaw -> {
                float pitch = getTargetPitch().orElse(0f);
                entity.setRotation(yaw, pitch);
            });
        }
    }

    @Override
    public Optional<Float> getTargetPitch() {
        if (entity instanceof ShooterDragon shooterDragon) {
            double x = this.x - shooterDragon.getShootingPoint().pos().getX();
            double y = this.y - shooterDragon.getShootingPoint().pos().getY();
            double z = this.z - shooterDragon.getShootingPoint().pos().getZ();
            double distZX = Math.sqrt(x * x + z * z);
            return !(Math.abs(y) > 1.0E-5F) && !(Math.abs(distZX) > 1.0E-5F) ?
                    Optional.empty()
                    : Optional.of((float)(-(MathHelper.atan2(y, distZX) * 180 / Math.PI)));
        }
        return super.getTargetPitch();
    }

    @Override
    public Optional<Float> getTargetYaw() {
        if (entity instanceof ShooterDragon shooterDragon) {
            double x = this.x - shooterDragon.getShootingPoint().pos().getX();
            double z = this.z - shooterDragon.getShootingPoint().pos().getZ();
            return !(Math.abs(x) > 1.0E-5F) && !(Math.abs(z) > 1.0E-5F)
                    ? Optional.empty()
                    : Optional.of((float)(MathHelper.atan2(z, x) * 180 /Math.PI) - 90);
        }
        return super.getTargetYaw();
    }

    public void setLockRotation(boolean state) {
        lockRotation = state;
    }

    @Override
    public void lookAt(Entity target, float maxYawChange, float maxPitchChange) {
        if (entity.getVisibilityCache().canSee(target)) super.lookAt(target, maxYawChange, maxPitchChange);
    }
}
