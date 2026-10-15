package nordmods.uselessreptile.common.entity.ai.goal.common.flying;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.Nullable;

public class FlyingDragonFlyAroundGoal<T extends URDragonEntity & FlyingDragon> extends WaterAvoidingRandomStrollGoal {
    protected final T mob;
    protected final int range;
    @Nullable protected Vec3 startingPos;

    public FlyingDragonFlyAroundGoal(T entity, int range) {
        super(entity, 1);
        this.mob = entity;
        this.range = range;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.onGround()) return false;
        if (startingPos != null) {
            if (mob.distanceToSqr(startingPos) > mob.distanceToSqr(wantedX, wantedY, wantedZ)) return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isFlying()) return false;
        if (!mob.isTame() && !mob.shouldFlyDown()) trigger();
        return super.canUse();
    }

    @Override
    public void stop() {
        startingPos = null;
        super.stop();
    }

    protected BlockPos returnToNormalHeight() {
        return mob.blockPosition().below((int) (mob.getY() - 319));
    }

    @Nullable
    protected BlockPos findRandomAirSpot() {
        return RandomPos.generateRandomPosTowardDirection(mob, range, mob.getRandom(), RandomPos.generateRandomDirection(mob.getRandom(), range, 10));
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        BlockPos target = findRandomAirSpot();
        if (target != null) {
            startingPos = mob.position();
            return target.getCenter();
        }
        else return null;
    }

}
