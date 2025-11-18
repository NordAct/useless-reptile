package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonAirNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T>{

    public FlyingDragonAirNavigation(T entity, Level world) {
        super(entity, world);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        nodeEvaluator = new FlyNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.isPassenger()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (!isDone() && target != null) {
            followThePath();
            moveOrStop(target);
            if (!isDone() && entity.horizontalCollision) {
                double yDiffNode = path.getNextNode().asVec3().y() - entity.getY();
                if (yDiffNode < 0) getMoveControl().forceFlyDown();
                if (yDiffNode > 0) getMoveControl().forceFlyUp();
            }
            doStuckDetection(getTempMobPos());
        }
    }
}
