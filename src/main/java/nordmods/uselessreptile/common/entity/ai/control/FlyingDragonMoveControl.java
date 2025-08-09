package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonMoveControl<T extends URDragonEntity & FlyingDragon> extends MoveControl {
    private final T entity;
    private boolean forceFlyUp = false;
    private boolean forceFlyDown = false;

    public FlyingDragonMoveControl(T entity) {
        super(entity);
        this.entity = entity;
    }

    public void moveBack() {
        state = State.STRAFE;
    }

    public void notMove() {
        state = State.WAIT;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle()) return;

        double diffX = targetX - entity.getX();
        double diffY = targetY - entity.getY();
        double diffZ = targetZ - entity.getZ();
        double distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;
        double distanceXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float destinationYaw = (float)(MathHelper.atan2(diffZ, diffX) * 57.2957763671875D) - 90.0F;
        boolean inWater = entity.isTouchingWater() && !entity.canNavigateInFluids();

        if (Double.isNaN(entity.getVelocity().y)) entity.setVelocity(entity.getVelocity().x, 0, entity.getVelocity().z);
        int accelerationDuration = entity.getAccelerationDuration();
        if (accelerationDuration < 0) accelerationDuration = 0;
        float accelerationModifier = (float)accelerationDuration/entity.getMaxAccelerationDuration();
        if (accelerationModifier > 1.5) accelerationModifier = 1.5f;
        entity.setMovingBackwards(false);
        entity.setTiltState((byte) 0);
        float verticalAccelerationModifier = MathHelper.clamp(accelerationModifier, 0.25f, 1.5f);
        float speed = getMovementSpeed(accelerationModifier, inWater);

        switch (state) {
            case STRAFE -> { //there's no strafe for dragons, but it's used for backwards movement
                state = State.WAIT;
                entity.setMovingBackwards(true);

                if (accelerationDuration > entity.getMaxAccelerationDuration() * 0.25) accelerationDuration -= 2;
                else accelerationDuration++;

                entity.setRotation(destinationYaw, entity.getPitch());
                entity.setMovementSpeed(-speed);
            }
            case MOVE_TO -> {
                state = State.WAIT;
                if (distanceSquared < 2.500000277905201E-7D) {
                    entity.setUpwardSpeed(0.0F);
                    entity.setForwardSpeed(0.0F);
                    return;
                }
                if (entity.isLookingAtDirection(entity.getPitch(), destinationYaw, entity.getPitchLimit(), Math.max(50, entity.getRotationSpeed() * 2))) {
                    if (accelerationDuration < entity.getMaxAccelerationDuration()) accelerationDuration++;
                    if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration--;

                    entity.setMovementSpeed(speed);
                } else entity.setForwardSpeed(0.0F);
                entity.setRotation(destinationYaw, entity.getPitch());
            }
            case JUMPING -> {
                entity.setMovementSpeed(speed);
                if (entity.isOnGround()) state = State.WAIT;
            }
            default -> {
                entity.setUpwardSpeed(0.0F);
                entity.setForwardSpeed(0.0F);
                entity.setMovingBackwards(entity.isMoving());
                accelerationDuration /= 2;
                if (!entity.isMoving()) accelerationDuration = 0;
            }
        }

        if (entity.isFlying()) {
            if (isFlyDirectionEnforced()) {
                if (forceFlyUp) accelerationDuration = flyUp(accelerationDuration, verticalAccelerationModifier);
                if (forceFlyDown) accelerationDuration = flyDown(accelerationDuration, verticalAccelerationModifier);
            } else if (Math.abs(diffY) > 9.999999747378752E-6D || Math.abs(distanceXZ) > 9.999999747378752E-6D) {
                float destinationPitch = (float)(-(MathHelper.atan2(diffY, distanceXZ) * 57.2957763671875D));
                entity.setPitch(wrapDegrees(entity.getPitch(), destinationPitch, entity.getPitchLimit()));
                entity.setUpwardSpeed(0);

                if (entity.isTouchingWater() && entity.hasTargetInWater() || !entity.isTouchingWater()) {
                    double divergence = Math.clamp(Math.max(0, (distanceXZ - (entity.getWidthMod() < 2 ? 0 : 4)) * 0.5), 0, 3);
                    if (diffY > divergence) accelerationDuration = flyUp(accelerationDuration, verticalAccelerationModifier);
                    if (diffY < -divergence) accelerationDuration = flyDown(accelerationDuration, verticalAccelerationModifier);
                } else accelerationDuration = flyUp(accelerationDuration, verticalAccelerationModifier);
            }
        }
        entity.setAccelerationDuration(accelerationDuration);
        forceFlyUp = false;
        forceFlyDown = false;
    }

    public void forceFlyUp() {
         forceFlyUp = true;
    }

    public void forceFlyDown() {
        forceFlyDown = true;
    }

    private int flyUp (int accelerationDuration, float verticalAccelerationModifier) {
        if (accelerationDuration > entity.getMaxAccelerationDuration() * 0.4) accelerationDuration -= 2;
        if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration -= 2;
        entity.setUpwardSpeed(entity.getVerticalSpeed() * verticalAccelerationModifier);
        entity.setTiltState((byte) 1);
        return accelerationDuration;
    }

    private int flyDown (int accelerationDuration, float verticalAccelerationModifier) {
        if (accelerationDuration < entity.getMaxAccelerationDuration() * 3) accelerationDuration += 2;
        entity.setUpwardSpeed(-entity.getVerticalSpeed() * verticalAccelerationModifier * 1.3f);
        entity.setTiltState((byte) 2);
        return accelerationDuration;
    }

    private boolean isFlyDirectionEnforced() {
        return forceFlyDown || forceFlyUp;
    }

    private float getMovementSpeed(float accelerationModifier, boolean inWater) {
        float speed;
        if (entity.isFlying()) {
            speed = (float) entity.getAttributeValue(EntityAttributes.FLYING_SPEED) * accelerationModifier;
            if (inWater || entity.getRecentDamageSource() == entity.getDamageSources().lava()) entity.getJumpControl().setActive();
        } else speed = (float) entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
        return speed;
    }
}
