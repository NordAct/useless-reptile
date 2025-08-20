package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import nordmods.uselessreptile.common.entity.MagmamuncherEntity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MagmamuncherEatMagmaGoal extends Goal {
    private final MagmamuncherEntity entity;
    private int timer;
    private Direction offset = null;
    private final List<BlockPos> invalidPos = new ArrayList<>();

    public MagmamuncherEatMagmaGoal(MagmamuncherEntity entity) {
        this.entity = entity;
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!entity.canBreakBlocks()) return false;

        if (entity.hasVehicle() || entity.isSitting() || entity.getTarget() != null) return false;

        if (entity.eatMagmaCooldown > 0) return false;

        if (entity.getMagmaBlockPos() == BlockPos.ORIGIN) locateClosestBlock();
        if (entity.getMagmaBlockPos() == BlockPos.ORIGIN || offset == null) {
            entity.eatMagmaCooldown = 20*10;
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        timer = getTickCount(40);
        invalidPos.clear();
    }

    @Override
    public void stop() {
        timer = 0;
        entity.eatMagmaCooldown = MagmamuncherEntity.EAT_MAGMA_COOLDOWN_AVERAGE + entity.getRandom().nextBetween(-20*10, 20*10);
        entity.setMagmaBlockPos(BlockPos.ORIGIN);
        entity.setEatingMagma(false);
        invalidPos.clear();
        entity.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && timer > 0;
    }

    @Override
    public void tick() {
        if (entity.isEatingMagma()) entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getMagmaBlockPos().toCenterPos());

        BlockPos targetPos = entity.getMagmaBlockPos();
        double dist = targetPos.toCenterPos().squaredDistanceTo(entity.getPos());
        if (dist < MagmamuncherEntity.DISTANCE_TO_EAT * MagmamuncherEntity.DISTANCE_TO_EAT && entity.getLookControl().isLookingAtTarget()) {
            entity.setEatingMagma(true);
        }
        targetPos = targetPos.offset(offset);
        if (!entity.getWorld().getBlockState(entity.getMagmaBlockPos()).getBlock().equals(Blocks.MAGMA_BLOCK)) {
            entity.setMagmaBlockPos(BlockPos.ORIGIN);
            offset = null;
            return;
        }
        entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1);
        if (entity.getNavigation().isIdle()) {
            //bandaid fix for a dumbass not getting close enough to eat its magma
            if (dist > MagmamuncherEntity.DISTANCE_TO_EAT * MagmamuncherEntity.DISTANCE_TO_EAT && dist < 3) {
                entity.addVelocity(targetPos.toCenterPos().subtract(entity.getPos()).normalize().multiply(0.1));
                return;
            } else if (entity.getNavigation().getTargetPos() != null && entity.getNavigation().getTargetPos().getY() < entity.getMagmaBlockPos().getY()) {
                invalidPos.add(entity.getMagmaBlockPos());
                entity.setMagmaBlockPos(BlockPos.ORIGIN);
                entity.getNavigation().stop();
                offset = null;
            }
        }
        if (!entity.isEatingMagma() && entity.getNavigation().isIdle()) timer--;
    }

    private void locateClosestBlock() {
        int dist = Integer.MAX_VALUE;
        for (BlockPos blockPos : BlockPos.iterate(entity.getBoundingBox().expand(10, 10 ,10))) {
            blockPos = blockPos.toImmutable();
            if (invalidPos.contains(blockPos)) continue;

            //taxicab distance with some weights... because this idiot tries to pathfind where it can't
            int dx =  Math.abs(blockPos.getX() - entity.getBlockX());
            int dy = Math.abs(blockPos.getY() - entity.getBlockY());
            int dz = Math.abs(blockPos.getZ() - entity.getBlockZ());
            int newDist = dx + dy * (dy > 1 ? 2 : 0) + dz;
            if (newDist > dist) continue;

            if (!(entity.getWorld().getBlockState(blockPos).getBlock().equals(Blocks.MAGMA_BLOCK))) continue;

            offset = null;
            if (entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.up())) == 0)
                offset = Direction.UP;
            else if (entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.down())) == 0)
                offset = Direction.DOWN;
            else if (entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.south())) == 0)
                offset = Direction.SOUTH;
            else if (entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.north())) == 0)
                offset = Direction.NORTH;
            else if (entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.east())) == 0)
                offset = Direction.EAST;
            else if (entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.west())) == 0)
                offset = Direction.WEST;
            if (offset == null) continue;

            if (offset == Direction.DOWN
                    && entity.getNavigation().getNodeMaker().getDefaultNodeType(entity, blockPos.down(2)) != PathNodeType.BLOCKED) continue;

            Path path = entity.getNavigation().findPathTo(Set.of(blockPos.offset(offset)), 16);
            if (path == null) continue;

            boolean invalid = false;
            for (int i = 0; i < path.getLength(); i++) {
                if (entity.getPathfindingPenalty(path.getNode(i).type) > 0) {
                    invalid = true;
                    break;
                }
            }
            if (invalid) continue;

            dist = newDist;
            entity.setMagmaBlockPos(blockPos);
            timer = getTickCount(40);
        }
    }
}
