package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonCallBackGoal<T extends URDragonEntity> extends Goal {
    protected final T mob;
    protected LivingEntity owner;
    protected int updateCountdownTicks;
    protected double proximityRange;
    protected double prevDistance;
    protected int forceTeleportCountdown;

    public static final int MAX_CALL_DISTANCE = 512;

    public DragonCallBackGoal(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public void start() {
        updateCountdownTicks = 0;
        forceTeleportCountdown = adjustedTickDelay(100);
        prevDistance = 0;
        proximityRange = mob.getBbWidth() * 2.0f * (mob.getBbWidth() * 2.0f);
        owner = mob.getOwner();
        mob.setTarget(null);
    }

    @Override
    public boolean canUse() {
        if (!mob.isTame()) return false;
        if (mob.isLeashed() || mob.isOrderedToSit()) return false;
        if (!mob.shouldFollow) return false;
        if (mob.getTarget() != null) return false;
        LivingEntity player = mob.getOwner();
        if (player == null) return false;
        double distance = mob.distanceToSqr(player);
        if (distance < mob.getWanderRadius().radius * 0.3f) return false;
        return distance < MAX_CALL_DISTANCE * MAX_CALL_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && owner.isAlive();
    }

    @Override
    public void stop() {
        mob.shouldFollow = false;
        owner = null;
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        mob.setSprinting(true);
        double distance = mob.distanceToSqr(owner);
        if (distance >= prevDistance) forceTeleportCountdown--;
        else forceTeleportCountdown = adjustedTickDelay(100);

        if (mob.isOrderedToSit()) mob.shouldFollow = false;

        checkProximity(distance);

        if (--updateCountdownTicks <= 0) {
            updateCountdownTicks = adjustedTickDelay(10);
            mob.getNavigation().moveTo(owner, 1);
            mob.setHomePoint(owner.blockPosition());
            if (URConfig.getConfig().allowDragonTeleport
                    && (distance > mob.getWanderRadius().radius * mob.getWanderRadius().radius * 4 || distance > (proximityRange * 4) && forceTeleportCountdown <= 0)) mob.tryToTeleportToOwner();
        }

        mob.getLookControl().setLookAt(owner);

        prevDistance = distance;
    }

    protected void checkProximity(double currentDistance) {
        if (currentDistance < proximityRange) mob.shouldFollow = false;
    }
}
