package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public class FlyingDragonLandNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T> {
    public FlyingDragonLandNavigation(T mob, Level world) {
        super(mob, world);
    }

    @Override
    protected @NonNull PathFinder createPathFinder(int range) {
        nodeEvaluator = new DragonWalkNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (mob.hasControllingPassenger() || mob.isPassenger() || mob.isOrderedToSit()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (isInProgress() && target != null) {
            double yDiffNode = path.getNextNode().asVec3().y() - mob.getY();
            double yDiffTarget = target.getY() - mob.getY();
            double xDiffTarget = mob.getX() - target.getX();
            xDiffTarget *= xDiffTarget;
            double zDiffTarget = mob.getZ() - target.getZ();
            zDiffTarget *= zDiffTarget;
            boolean shouldFlyUp = (target.getY() - path.getEndNode().y) > mob.maxUpStep() || yDiffNode > mob.maxUpStep() || yDiffTarget > 8 || Math.sqrt(xDiffTarget + zDiffTarget) > mob.getWanderRadius().radius;
            if ((path.getNextNodeIndex() > 0 || (tick - lastStuckCheck > 50)) && !mob.hasTargetInWater()) checkFlight(shouldFlyUp);
            followThePath();
            moveOrStop(target);
        }
        doStuckDetection(getTempMobPos());
    }
}
