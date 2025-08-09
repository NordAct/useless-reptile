package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonAirNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T>{

    public FlyingDragonAirNavigation(T entity, World world) {
        super(entity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        nodeMaker = new BirdPathNodeMaker();
        return new PathNodeNavigator(nodeMaker, range);
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle()) return;

        super.tick();

        BlockPos target = getTargetPos();
        if (!isIdle() && target != null) {
            continueFollowingPath();
            moveOrStop(target);
            if (!isIdle() && entity.horizontalCollision) {
                double yDiffNode = currentPath.getCurrentNode().getPos().getY() - entity.getY();
                if (yDiffNode < 0) getMoveControl().forceFlyDown();
                if (yDiffNode > 0) getMoveControl().forceFlyUp();
            }
            checkTimeouts(getPos());
        }
    }
}
