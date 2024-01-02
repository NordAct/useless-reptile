package nordmods.uselessreptile.common.entity.ai.pathfinding;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonMoveControl<T extends URDragonEntity & FlyingDragon> extends MoveControl {

    private final T entity;

    public FlyingDragonMoveControl(T entity) {
        super(entity);
        this.entity = entity;
    }

    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle()) return;

        double diffX = targetX - entity.getX();
        double diffY = targetY - entity.getY();
        double diffZ = targetZ - entity.getZ();
        double distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;
        float destinationYaw = (float)(MathHelper.atan2(diffZ, diffX) * 57.2957763671875D) - 90.0F;
        boolean swimming = entity.isTouchingWater() && entity.canNavigateInFluids();

        if (Double.isNaN(entity.getVelocity().y)) entity.setVelocity(entity.getVelocity().x, 0, entity.getVelocity().z);
        int accelerationDuration = (int) MathHelper.clamp(entity.getAccelerationDuration() * 1.5, 0, entity.getMaxAccelerationDuration());
        if (accelerationDuration < 0) accelerationDuration = 0;
        float accelerationModifier = (float)accelerationDuration/entity.getMaxAccelerationDuration();
        if (accelerationModifier > 1.5) accelerationModifier = 1.5f;
        entity.setGliding(accelerationModifier > 1);

        if (this.state == MoveControl.State.STRAFE) {
            state = State.WAIT;
        } else if (state == State.MOVE_TO) {
            state = State.WAIT;
            if (distanceSquared < 2.500000277905201E-7D) {
                entity.setUpwardSpeed(0.0F);
                entity.setForwardSpeed(0.0F);
                return;
            }
            byte tiltState = 0;
            entity.setMovingBackwards(false);

            if (accelerationDuration < entity.getMaxAccelerationDuration()) accelerationDuration++;
            if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration--;

            entity.setRotation(destinationYaw, entity.getPitch());

            float speed;
            if (entity.isFlying() && !swimming) {
                speed = (float) entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED) * accelerationModifier;
                if (entity.isTouchingWater() || entity.getRecentDamageSource() == entity.getDamageSources().lava()) entity.getJumpControl().setActive();
            } else speed = (float) entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            entity.setMovementSpeed(speed * entity.getSpeedMod());

            double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
            if (Math.abs(diffY) > 9.999999747378752E-6D || Math.abs(distance) > 9.999999747378752E-6D) {
                float destinationPitch = (float)(-(MathHelper.atan2(diffY, distance) * 57.2957763671875D));
                entity.setPitch(wrapDegrees(entity.getPitch(), destinationPitch, entity.getPitchLimit()));
                entity.setUpwardSpeed(0);
                double verticalAccelerationModifier = MathHelper.clamp(accelerationModifier, 0.25, 1.5);

                if (entity.isTouchingWater() && entity.hasTargetInWater() || !entity.isTouchingWater()) {
                    double divergence = Math.max(0, (distance - (entity.getWidthMod() < 2 ? 0 : 4)) * 0.5);
                    if (diffY > divergence) {
                        if (accelerationDuration > entity.getMaxAccelerationDuration() * 0.4) accelerationDuration -= 2;
                        if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration -= 2;
                        entity.setUpwardSpeed(0.2f * (float) verticalAccelerationModifier);
                        tiltState = 1;
                    }
                    if (diffY < -divergence) {
                        if (accelerationDuration < entity.getMaxAccelerationDuration() * 3) accelerationDuration += 2;
                        entity.setUpwardSpeed(-0.25f * (float) verticalAccelerationModifier);
                        tiltState = 2;
                    }
                } else {
                    if (accelerationDuration > entity.getMaxAccelerationDuration() * 0.4) accelerationDuration -= 2;
                    if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration -= 2;
                    entity.setUpwardSpeed(0.2f * (float) verticalAccelerationModifier);
                    tiltState = 1;
                }
            }
            entity.setTiltState(tiltState);
        } else if (this.state == MoveControl.State.JUMPING) {
            this.entity.setMovementSpeed((float)this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            if (this.entity.isOnGround()) {
                this.state = MoveControl.State.WAIT;
            }
        } else {
            entity.setUpwardSpeed(0.0F);
            entity.setForwardSpeed(0.0F);
            entity.setMovingBackwards(true);
            accelerationDuration /= 2;
            if (!entity.isMoving()) accelerationDuration = 0;
        }
        entity.setAccelerationDuration(accelerationDuration);
    }

}
