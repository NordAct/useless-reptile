package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public class FlyingDragonLandNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T> {
    public FlyingDragonLandNavigation(T entity, Level world) {
        super(entity, world);
    }

    @Override
    protected @NonNull PathFinder createPathFinder(int range) {
        nodeEvaluator = new DragonWalkNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.isPassenger() || entity.isOrderedToSit()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (isInProgress() && target != null) {
            double yDiffNode = path.getNextNode().asVec3().y() - entity.getY();
            double yDiffTarget = target.getY() - entity.getY();
            double xDiffTarget = entity.getX() - target.getX();
            xDiffTarget *= xDiffTarget;
            double zDiffTarget = entity.getZ() - target.getZ();
            zDiffTarget *= zDiffTarget;
            boolean shouldFlyUp = (target.getY() - path.getEndNode().y) > entity.maxUpStep() || yDiffNode > entity.maxUpStep() || yDiffTarget > 8 || Math.sqrt(xDiffTarget + zDiffTarget) > entity.getWanderRadius().radius;
            if ((path.getNextNodeIndex() > 0 || (tick - lastStuckCheck > 50)) && !entity.hasTargetInWater()) checkFlight(shouldFlyUp);
            followThePath();
            moveOrStop(target);
        }
        doStuckDetection(getTempMobPos());
    }
}
