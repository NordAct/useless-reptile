package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonCallBackGoal<T extends URDragonEntity & FlyingDragon> extends DragonCallBackGoal {
    protected final T entity;

    public FlyingDragonCallBackGoal(T entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        entity.getLookControl().lookAt(owner, entity.getRotationSpeed(), entity.getPitchLimit());
        entity.setSprinting(true);
        double distance = entity.squaredDistanceTo(owner);
        double maxDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);

        if (entity.isSitting()) stopMoving();

        if (!entity.isFlying()) ticksToStop = 0;

        if (distance < maxDistance && (owner.isOnGround() || !entity.isFlying())) {
            if (entity.isFlying()) {
                if (ticksToStop > 10) stopMoving();
                else ticksToStop++;
            } else stopMoving();
        } else ticksToStop = 0;

        if (--updateCountdownTicks <= 0) {
            updateCountdownTicks = getTickCount(10);
            entity.getNavigation().startMovingTo(owner, 1);
        }
    }
}
