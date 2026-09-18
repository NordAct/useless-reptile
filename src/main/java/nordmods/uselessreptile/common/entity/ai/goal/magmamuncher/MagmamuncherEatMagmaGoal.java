package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import nordmods.uselessreptile.common.entity.Magmamuncher;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MagmamuncherEatMagmaGoal extends Goal {
    private final Magmamuncher mob;
    private int timer;
    private Direction offset = null;
    private final List<BlockPos> invalidPos = new ArrayList<>();

    public MagmamuncherEatMagmaGoal(Magmamuncher mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!mob.canBreakBlocks()) return false;

        if (mob.isPassenger() || mob.isOrderedToSit() || mob.getTarget() != null) return false;

        if (mob.eatMagmaCooldown > 0) return false;

        if (mob.getMagmaBlockPos() == BlockPos.ZERO) locateClosestBlock();
        if (mob.getMagmaBlockPos() == BlockPos.ZERO || offset == null) {
            mob.eatMagmaCooldown = 20*10;
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        timer = adjustedTickDelay(40);
        invalidPos.clear();
    }

    @Override
    public void stop() {
        timer = 0;
        mob.eatMagmaCooldown = Magmamuncher.EAT_MAGMA_COOLDOWN_AVERAGE + mob.getRandom().nextIntBetweenInclusive(-20*10, 20*10);
        mob.setMagmaBlockPos(BlockPos.ZERO);
        mob.setEatingMagma(false);
        invalidPos.clear();
        mob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && timer > 0;
    }

    @Override
    public void tick() {
        if (mob.isEatingMagma()) mob.lookAt(EntityAnchorArgument.Anchor.EYES, mob.getMagmaBlockPos().getCenter());

        BlockPos targetPos = mob.getMagmaBlockPos();
        double dist = targetPos.getCenter().distanceToSqr(mob.position());
        if (dist < Magmamuncher.DISTANCE_TO_EAT * Magmamuncher.DISTANCE_TO_EAT && mob.getLookControl().isLookingAtTarget()) {
            mob.setEatingMagma(true);
        }
        targetPos = targetPos.relative(offset);
        if (!mob.level().getBlockState(mob.getMagmaBlockPos()).getBlock().equals(Blocks.MAGMA_BLOCK)) {
            mob.setMagmaBlockPos(BlockPos.ZERO);
            offset = null;
            return;
        }
        mob.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1);
        if (mob.getNavigation().isDone()) {
            //bandaid fix for a dumbass not getting close enough to eat its magma
            if (dist > Magmamuncher.DISTANCE_TO_EAT * Magmamuncher.DISTANCE_TO_EAT && dist < 3) {
                mob.push(targetPos.getCenter().subtract(mob.position()).normalize().scale(0.1));
                return;
            } else if (mob.getNavigation().getTargetPos() != null && mob.getNavigation().getTargetPos().getY() < mob.getMagmaBlockPos().getY()) {
                invalidPos.add(mob.getMagmaBlockPos());
                mob.setMagmaBlockPos(BlockPos.ZERO);
                mob.getNavigation().stop();
                offset = null;
            }
        }
        if (!mob.isEatingMagma() && mob.getNavigation().isDone()) timer--;
    }

    private void locateClosestBlock() {
        int dist = Integer.MAX_VALUE;
        for (BlockPos blockPos : BlockPos.betweenClosed(mob.getBoundingBox().inflate(10, 10 ,10))) {
            blockPos = blockPos.immutable();
            if (invalidPos.contains(blockPos)) continue;

            //taxicab distance with some weights... because this idiot tries to pathfind where it can't
            int dx =  Math.abs(blockPos.getX() - mob.getBlockX());
            int dy = Math.abs(blockPos.getY() - mob.getBlockY());
            int dz = Math.abs(blockPos.getZ() - mob.getBlockZ());
            int newDist = dx + dy * (dy > 1 ? 2 : 0) + dz;
            if (newDist > dist) continue;

            if (!(mob.level().getBlockState(blockPos).getBlock().equals(Blocks.MAGMA_BLOCK))) continue;

            offset = null;
            if (mob.getPathfindingMalus(mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.above())) == 0)
                offset = Direction.UP;
            else if (mob.getPathfindingMalus(mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.below())) == 0)
                offset = Direction.DOWN;
            else if (mob.getPathfindingMalus(mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.south())) == 0)
                offset = Direction.SOUTH;
            else if (mob.getPathfindingMalus(mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.north())) == 0)
                offset = Direction.NORTH;
            else if (mob.getPathfindingMalus(mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.east())) == 0)
                offset = Direction.EAST;
            else if (mob.getPathfindingMalus(mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.west())) == 0)
                offset = Direction.WEST;
            if (offset == null) continue;

            if (offset == Direction.DOWN
                    && mob.getNavigation().getNodeEvaluator().getPathType(mob, blockPos.below(2)) != PathType.BLOCKED) continue;

            Path path = mob.getNavigation().createPath(Set.of(blockPos.relative(offset)), 16);
            if (path == null) continue;

            boolean invalid = false;
            for (int i = 0; i < path.getNodeCount(); i++) {
                if (mob.getPathfindingMalus(path.getNode(i).type) > 0) {
                    invalid = true;
                    break;
                }
            }
            if (invalid) continue;

            dist = newDist;
            mob.setMagmaBlockPos(blockPos);
            timer = adjustedTickDelay(40);
        }
    }
}
