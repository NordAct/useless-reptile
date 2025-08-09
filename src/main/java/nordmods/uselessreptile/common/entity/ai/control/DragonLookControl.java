package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.util.math.MathHelper;
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

        return Math.abs(entity.getPitch() - pitch) < pitchTolerance
                && Math.abs((entity.getYawWithAdjustment() - yaw) % 360) < yawTolerance;
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
        } else {
            entity.setHeadYaw(changeAngle(entity.getHeadYaw(),entity.getBodyYaw(), entity.getRotationSpeed()));
        }

        entity.setHeadYaw(MathHelper.clampAngle(entity.getHeadYaw(),entity.getBodyYaw(), entity.getRotationSpeed()));
    }

    @Override
    public Optional<Float> getTargetPitch() {
        return super.getTargetPitch();
    }

    @Override
    public Optional<Float> getTargetYaw() {
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
