package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.LookControl;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
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
        return isLookingAtTarget(entity.getPitchLimit(), entity.getRotationSpeed());
    }

    public boolean isLookingAtTarget(float pitchTolerance, float yawTolerance) {
        float pitch = getXRotD().orElse(0f);
        float yaw = getYRotD().orElse(0f);
        return entity.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    public boolean canLookAtTarget() {
        return canLookAtTarget(entity.getPitchLimit() / 1.25f);
    }

    public boolean canLookAtTarget(float pitchTolerance) {
        float pitch = getXRotD().orElse(0f);
        return Math.abs(pitch) < pitchTolerance;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger()) return;
        if (lockRotation) return;
        if (lookAtCooldown > 0) {
            --lookAtCooldown;
            getYRotD().ifPresent(yaw -> {
                float pitch = getXRotD().orElse(0f);
                entity.setRot(yaw, pitch);
            });
        }
    }

    @Override
    public @NonNull Optional<Float> getXRotD() {
        if (entity instanceof ShooterDragon shooterDragon) {
            double x = this.wantedX - shooterDragon.getShootingPoint().pos().x();
            double y = this.wantedY - shooterDragon.getShootingPoint().pos().y();
            double z = this.wantedZ - shooterDragon.getShootingPoint().pos().z();
            double distZX = Math.sqrt(x * x + z * z);
            return !(Math.abs(y) > 1.0E-5F) && !(Math.abs(distZX) > 1.0E-5F) ?
                    Optional.empty()
                    : Optional.of((float)(-(Mth.atan2(y, distZX) * 180 / Math.PI)));
        }
        return super.getXRotD();
    }

    @Override
    public @NonNull Optional<Float> getYRotD() {
        if (entity instanceof ShooterDragon shooterDragon) {
            double x = this.wantedX - shooterDragon.getShootingPoint().pos().x();
            double z = this.wantedZ - shooterDragon.getShootingPoint().pos().z();
            return !(Math.abs(x) > 1.0E-5F) && !(Math.abs(z) > 1.0E-5F)
                    ? Optional.empty()
                    : Optional.of((float)(Mth.atan2(z, x) * 180 /Math.PI) - 90);
        }
        return super.getYRotD();
    }

    public void setLockRotation(boolean state) {
        lockRotation = state;
    }

    @Override
    public void setLookAt(@NonNull Entity target, float maxYawChange, float maxPitchChange) {
        if (entity.getSensing().hasLineOfSight(target)) super.setLookAt(target, maxYawChange, maxPitchChange);
    }
}
