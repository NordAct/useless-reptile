package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.Magmamuncher;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MagmamuncherEatMagmaGoal extends Goal {
    private final Magmamuncher entity;
    private int timer;
    private Direction offset = null;
    private final List<BlockPos> invalidPos = new ArrayList<>();

    public MagmamuncherEatMagmaGoal(Magmamuncher entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!entity.canBreakBlocks()) return false;

        if (entity.isPassenger() || entity.isOrderedToSit() || entity.getTarget() != null) return false;

        if (entity.eatMagmaCooldown > 0) return false;

        if (entity.getMagmaBlockPos() == BlockPos.ZERO) locateClosestBlock();
        if (entity.getMagmaBlockPos() == BlockPos.ZERO || offset == null) {
            entity.eatMagmaCooldown = 20*10;
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
        entity.eatMagmaCooldown = Magmamuncher.EAT_MAGMA_COOLDOWN_AVERAGE + entity.getRandom().nextIntBetweenInclusive(-20*10, 20*10);
        entity.setMagmaBlockPos(BlockPos.ZERO);
        entity.setEatingMagma(false);
        invalidPos.clear();
        entity.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && timer > 0;
    }

    @Override
    public void tick() {
        if (entity.isEatingMagma()) entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(entity.getMagmaBlockPos()));

        BlockPos targetPos = entity.getMagmaBlockPos();
        double dist = Vec3.atCenterOf(targetPos).distanceToSqr(entity.position());
        if (dist < Magmamuncher.DISTANCE_TO_EAT * Magmamuncher.DISTANCE_TO_EAT && entity.getLookControl().isLookingAtTarget()) {
            entity.setEatingMagma(true);
        }
        targetPos = targetPos.relative(offset);
        if (!entity.level().getBlockState(entity.getMagmaBlockPos()).getBlock().equals(Blocks.MAGMA_BLOCK)) {
            entity.setMagmaBlockPos(BlockPos.ZERO);
            offset = null;
            return;
        }
        entity.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1);
        if (entity.getNavigation().isDone()) {
            //bandaid fix for a dumbass not getting close enough to eat its magma
            if (dist > Magmamuncher.DISTANCE_TO_EAT * Magmamuncher.DISTANCE_TO_EAT && dist < 3) {
                entity.push(Vec3.atCenterOf(targetPos).subtract(entity.position()).normalize().scale(0.1));
                return;
            } else if (entity.getNavigation().getTargetPos() != null && entity.getNavigation().getTargetPos().getY() < entity.getMagmaBlockPos().getY()) {
                invalidPos.add(entity.getMagmaBlockPos());
                entity.setMagmaBlockPos(BlockPos.ZERO);
                entity.getNavigation().stop();
                offset = null;
            }
        }
        if (!entity.isEatingMagma() && entity.getNavigation().isDone()) timer--;
    }

    private void locateClosestBlock() {
        int dist = Integer.MAX_VALUE;
        for (BlockPos blockPos : BlockPos.betweenClosed(entity.getBoundingBox().inflate(10, 10 ,10))) {
            blockPos = blockPos.immutable();
            if (invalidPos.contains(blockPos)) continue;

            //taxicab distance with some weights... because this idiot tries to pathfind where it can't
            int dx =  Math.abs(blockPos.getX() - entity.getBlockX());
            int dy = Math.abs(blockPos.getY() - entity.getBlockY());
            int dz = Math.abs(blockPos.getZ() - entity.getBlockZ());
            int newDist = dx + dy * (dy > 1 ? 2 : 0) + dz;
            if (newDist > dist) continue;

            if (!(entity.level().getBlockState(blockPos).getBlock().equals(Blocks.MAGMA_BLOCK))) continue;

            offset = null;
            if (entity.getPathfindingMalus(entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.above())) == 0)
                offset = Direction.UP;
            else if (entity.getPathfindingMalus(entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.below())) == 0)
                offset = Direction.DOWN;
            else if (entity.getPathfindingMalus(entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.south())) == 0)
                offset = Direction.SOUTH;
            else if (entity.getPathfindingMalus(entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.north())) == 0)
                offset = Direction.NORTH;
            else if (entity.getPathfindingMalus(entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.east())) == 0)
                offset = Direction.EAST;
            else if (entity.getPathfindingMalus(entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.west())) == 0)
                offset = Direction.WEST;
            if (offset == null) continue;

            if (offset == Direction.DOWN
                    && entity.getNavigation().getNodeEvaluator().getPathType(entity, blockPos.below(2)) != PathType.BLOCKED) continue;

            Path path = entity.getNavigation().createPath(Set.of(blockPos.relative(offset)), 16);
            if (path == null) continue;

            boolean invalid = false;
            for (int i = 0; i < path.getNodeCount(); i++) {
                if (entity.getPathfindingMalus(path.getNode(i).type) > 0) {
                    invalid = true;
                    break;
                }
            }
            if (invalid) continue;

            dist = newDist;
            entity.setMagmaBlockPos(blockPos);
            timer = adjustedTickDelay(40);
        }
    }
}
