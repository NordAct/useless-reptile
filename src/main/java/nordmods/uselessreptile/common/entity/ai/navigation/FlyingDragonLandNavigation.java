package nordmods.uselessreptile.common.entity.ai.navigation;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public class FlyingDragonLandNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingDragonBaseNavigation<T> {
    public FlyingDragonLandNavigation(T mob, Level world) {
        super(mob, world);
    }
    private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap<>();

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
            if (shouldFlyUp) {
                if (((DragonWalkNodeEvaluator)nodeEvaluator).fitsWithinBlock()) tryFly();
                else  {
                    boolean canFlyUp = collisionCache.computeIfAbsent(mob.getBoundingBox().move(new Vec3(0, mob.getBbHeight(), 0)), box -> mob.level().noCollision(mob, (AABB) box));
                    if (canFlyUp) tryFly();
                    else {
                        scheduleRecomputation();
                    }
                }
            }
            followThePath();
            moveOrStop(target);
        } else {
            collisionCache.clear();
        }
        doStuckDetection(getTempMobPos());
    }
}
