package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public class FlyingDragonAirNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T>{

    public FlyingDragonAirNavigation(T mob, Level world) {
        super(mob, world);
    }

    @Override
    protected @NonNull PathFinder createPathFinder(int range) {
        nodeEvaluator = new FlyNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (mob.hasControllingPassenger() || mob.isPassenger()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (!isDone() && target != null) {
            if (mob.horizontalCollision) {
                double yDiffNode = path.getNextNode().asVec3().y() - mob.getY();
                if (yDiffNode < 0) getMoveControl().forceFlyDown();
                if (yDiffNode > 0) getMoveControl().forceFlyUp();
            }
            followThePath();
            moveOrStop(target);
        }
        doStuckDetection(getTempMobPos());
    }
}
