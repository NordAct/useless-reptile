package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.navigation.PathTime;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonCallBackGoal extends Goal {
    protected final URDragonEntity entity;
    protected LivingEntity owner;
    protected int updateCountdownTicks;
    protected int ticksToStop;

    public static final int MAX_CALL_DISTANCE = 512;

    public DragonCallBackGoal(URDragonEntity entity) {
        this.entity = entity;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public void start() {
        updateCountdownTicks = 0;
        owner = entity.getOwner();
        entity.setIsSitting(false);
        ticksToStop = 0;
        entity.setTarget(null);
    }

    @Override
    public boolean canStart() {
        if (!entity.isTamed()) return false;
        if (entity.isLeashed() || entity.isSitting()) return false;
        if (!entity.shouldFollow) return false;
        LivingEntity player = entity.getOwner();
        if (player == null) return false;
        double distance = entity.squaredDistanceTo(player);
        if (distance < player.getWidth() * player.getWidth() * 9) return false;
        return distance < MAX_CALL_DISTANCE * MAX_CALL_DISTANCE;
    }

    @Override
    public boolean shouldContinue() {
        return canStart() && owner.isAlive();
    }

    @Override
    public void stop() {
        entity.shouldFollow = false;
        owner = null;
        entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        entity.getLookControl().lookAt(owner, entity.getRotationSpeed(), entity.getPitchLimit());
        entity.setSprinting(true);
        double distance = entity.squaredDistanceTo(owner);

        if (entity.isSitting()) entity.shouldFollow = false;

        checkProximity(distance);

        if (--updateCountdownTicks <= 0) {
            updateCountdownTicks = getTickCount(10);
            entity.getNavigation().startMovingTo(owner, 1);
            entity.setHomePoint(owner.getBlockPos());
            if (URConfig.getConfig().allowDragonTeleport
                    && (distance > 4096 || entity.getNavigation() instanceof PathTime pathTime && pathTime.getPathTime() > 70)) entity.tryTeleportToOwner();
        }
    }

    protected void checkProximity(double currentDistance) {
        double maxDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);
        if (currentDistance < maxDistance && owner.isOnGround()) entity.shouldFollow = false;
    }
}
