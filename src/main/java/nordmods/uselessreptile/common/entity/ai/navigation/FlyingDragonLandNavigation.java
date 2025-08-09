package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonLandNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T> {
    public FlyingDragonLandNavigation(T entity, World world) {
        super(entity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        nodeMaker = new LandPathNodeMaker();
        return new PathNodeNavigator(nodeMaker, range);
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle() || entity.isSitting()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (!isIdle() && target != null) {
            continueFollowingPath();
            moveOrStop(target);
            if (!isIdle()) {
                double yDiffNode = currentPath.getCurrentNode().getPos().getY() - entity.getY();
                double yDiffTarget = target.getY() - entity.getY();
                double xDiffTarget = Math.pow(entity.getX() - target.getX(), 2);
                double zDiffTarget = Math.pow(entity.getZ() - target.getZ(), 2);
                boolean shouldFlyUp = jumpCount > 9
                        || yDiffTarget > 3 && Math.sqrt(xDiffTarget + zDiffTarget) < 16
                        || yDiffTarget > 8
                        || Math.sqrt(xDiffTarget + zDiffTarget) > 64
                        || currentPath != null && currentPath.isFinished();
                if ((tickCount > 20 || yDiffNode > 0.5f) && entity.horizontalCollision || shouldFlyUp && !entity.hasTargetInWater()) {
                    entity.getJumpControl().setActive();
                    startToFly(shouldFlyUp);
                }
            }
        }
        checkTimeouts(getPos());
    }
}
