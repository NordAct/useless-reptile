package nordmods.uselessreptile.common.entity.ai.control;

import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.Optional;

public class DragonLookControl<T extends URDragonEntity> extends DragonRotationControl<T>{
    public DragonLookControl(T dragon) {
        super(dragon);
    }

    @Override
    public void tick() { //todo fix head rot being choppy. Probably because game already tries to sync it
        if (lockRotation) return;

        dragon.yHeadRot = dragon.getServerHeadYaw();
        dragon.setXRot(dragon.getServerHeadPitch());

        if (!dragon.level().isClientSide()) {
            if (rotationCooldown > 0) {
                --rotationCooldown;
                Optional<Float> targetYaw = getYRotD();
                dragon.setLookTargetYaw(targetYaw);
                targetYaw.ifPresent(yaw -> dragon.setServerHeadYaw(rotateTowards(dragon.yHeadRot, yaw, dragon.getControllingPassenger() != null ? dragon.getMaxHeadYRot() : dragon.getHeadRotSpeed())));
                getXRotD().ifPresent(pitch -> dragon.setServerHeadPitch(rotateTowards(dragon.getXRot(), pitch, dragon.getControllingPassenger() != null ? dragon.getMaxHeadXRot() : dragon.getHeadPitchSpeed())));
                dragon.needsSync = true;
            } else {
                dragon.setLookTargetYaw(Optional.empty());
                dragon.setServerHeadYaw(rotateTowards(dragon.yHeadRot, dragon.yBodyRot, dragon.getHeadRotSpeed()));
                dragon.setServerHeadPitch(rotateTowards(dragon.getXRot(), 0, dragon.getHeadPitchSpeed()));
            }
        }
    }

    public boolean isLookingAtTarget(float pitchTolerance, float yawTolerance) {
        float pitch = getXRotD().orElse(0f);
        float yaw = getYRotD().orElse(0f);
        return dragon.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    public boolean isLookingAtTarget() {
        return isLookingAtTarget(dragon.getHeadPitchSpeed() , dragon.getHeadRotSpeed());
    }

    public boolean canLookAtTarget() {
        return canLookAtTarget(dragon.getMaxHeadXRot() / 1.25f);
    }

    public boolean canLookAtTarget(float pitchTolerance) {
        float pitch = getXRotD().orElse(0f);
        return Math.abs(pitch) < pitchTolerance;
    }
}
