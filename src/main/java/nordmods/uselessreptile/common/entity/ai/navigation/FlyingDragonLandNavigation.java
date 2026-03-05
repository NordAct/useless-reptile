package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public class FlyingDragonLandNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T> {
    public FlyingDragonLandNavigation(T entity, Level world) {
        super(entity, world);
    }

    @Override
    protected @NonNull PathFinder createPathFinder(int range) {
        nodeEvaluator = new WalkNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.isPassenger() || entity.isOrderedToSit()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (!isDone() && target != null) {
            double yDiffNode = path.getNextNode().asVec3().y() - entity.getY();
            double yDiffTarget = target.getY() - entity.getY();
            double xDiffTarget = entity.getX() - target.getX();
            xDiffTarget *= xDiffTarget;
            double zDiffTarget = entity.getZ() - target.getZ();
            zDiffTarget *= zDiffTarget;
            boolean shouldFlyUp = jumpCount > 9
                    || yDiffTarget > 3 && Math.sqrt(xDiffTarget + zDiffTarget) < 16
                    || yDiffTarget > 8
                    || Math.sqrt(xDiffTarget + zDiffTarget) > 64
                    || path != null && path.isDone();
            if ((tick > 20 || yDiffNode > 0.5f) && entity.horizontalCollision || shouldFlyUp && !entity.hasTargetInWater()) {
                entity.getJumpControl().jump();
                startToFly(shouldFlyUp);
            }
            followThePath();
            moveOrStop(target);
        }
        doStuckDetection(getTempMobPos());
    }
}
