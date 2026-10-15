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
        if (path != null && path.getNodeCount() > 0 && target != null) {
            double yDiffNode = isInProgress() ? path.getNextNode().asVec3().y() - mob.getY() : 0;
            double yDiffTarget = target.getY() - mob.getY();
            double xDiffTarget = mob.getX() - target.getX();
            xDiffTarget *= xDiffTarget;
            double zDiffTarget = mob.getZ() - target.getZ();
            zDiffTarget *= zDiffTarget;
            boolean shouldFlyUp = target.distChessboard(path.getEndNode().asBlockPos()) > 2 || yDiffNode > mob.maxUpStep() || yDiffTarget > mob.maxUpStep() * 4 || Math.sqrt(xDiffTarget + zDiffTarget) > mob.getWanderRadius().radius;
            checkFlight(shouldFlyUp);
            followThePath();
            moveOrStop(target);
        }
        doStuckDetection(getTempMobPos());
    }
}
