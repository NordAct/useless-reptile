package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class DragonCallBackGoal extends Goal {
    protected final URDragonEntity entity;
    protected LivingEntity owner;
    protected int updateCountdownTicks;
    protected double proximityRange;
    protected double prevDistance;
    protected int forceTeleportCountdown;

    public static final int MAX_CALL_DISTANCE = 512;

    public DragonCallBackGoal(URDragonEntity entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public void start() {
        updateCountdownTicks = 0;
        forceTeleportCountdown = adjustedTickDelay(100);
        prevDistance = 0;
        proximityRange = entity.getBbWidth() * 2.0f * (entity.getBbWidth() * 2.0f);
        owner = entity.getOwner();
        entity.setOrderedToSit(false);
        entity.setTarget(null);
    }

    @Override
    public boolean canUse() {
        if (!entity.isTame()) return false;
        if (entity.isLeashed() || entity.isOrderedToSit()) return false;
        if (!entity.shouldFollow) return false;
        LivingEntity player = entity.getOwner();
        if (player == null) return false;
        double distance = entity.distanceToSqr(player);
        if (distance < player.getBbWidth() * player.getBbWidth() * 9) return false;
        return distance < MAX_CALL_DISTANCE * MAX_CALL_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && owner.isAlive();
    }

    @Override
    public void stop() {
        entity.shouldFollow = false;
        owner = null;
        entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        entity.setSprinting(true);
        double distance = entity.distanceToSqr(owner);
        if (distance >= prevDistance) forceTeleportCountdown--;
        else forceTeleportCountdown = adjustedTickDelay(100);

        if (entity.isOrderedToSit()) entity.shouldFollow = false;

        checkProximity(distance);

        if (--updateCountdownTicks <= 0) {
            updateCountdownTicks = adjustedTickDelay(10);
            entity.getNavigation().moveTo(owner, 1);
            entity.setHomePoint(owner.blockPosition());
            if (URConfig.getConfig().allowDragonTeleport
                    && (distance > 4096 || distance > (proximityRange * 4) && forceTeleportCountdown <= 0)) entity.tryToTeleportToOwner();
        }

        prevDistance = distance;
    }

    protected void checkProximity(double currentDistance) {
        if (currentDistance < proximityRange && owner.onGround()) entity.shouldFollow = false;
    }
}
