package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import nordmods.uselessreptile.common.entity.MagmamuncherEntity;

import java.util.EnumSet;
import java.util.Set;
//todo drop when magma is eaten
public class MagmamuncherEatMagmaGoal extends Goal {
    private final MagmamuncherEntity entity;
    private int timer;
    private Direction offset = null;

    public MagmamuncherEatMagmaGoal(MagmamuncherEntity entity) {
        this.entity = entity;
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
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
        timer = getTickCount(20);
    }

    @Override
    public void stop() {
        timer = 0;
        entity.eatMagmaCooldown = MagmamuncherEntity.EAT_MAGMA_COOLDOWN_AVERAGE + entity.getRandom().nextBetween(-20*10, 20*10);
        entity.setMagmaBlockPos(BlockPos.ORIGIN);
        entity.setEatingMagma(false);
        entity.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && timer > 0;
    }

    @Override
    public void tick() {
        timer++;

        BlockPos targetPos = entity.getMagmaBlockPos();
        double dist = targetPos.toCenterPos().squaredDistanceTo(entity.getPos());
        if (dist < MagmamuncherEntity.DISTANCE_TO_EAT * MagmamuncherEntity.DISTANCE_TO_EAT) {
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
            if (dist > MagmamuncherEntity.DISTANCE_TO_EAT * MagmamuncherEntity.DISTANCE_TO_EAT) //bandaid fix for a dumbass not getting close enough to eat its magma
                entity.addVelocity(targetPos.toCenterPos().subtract(entity.getPos()).normalize().multiply(0.1));
            entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getMagmaBlockPos().toCenterPos());
        }
    }

    private void locateClosestBlock() {
        double dist = Double.MAX_VALUE;
        for (BlockPos blockPos : BlockPos.iterate(entity.getBoundingBox().expand(5, 5 ,5))) {
            blockPos = blockPos.toImmutable();
            double newDist = blockPos.getSquaredDistance(entity.getBlockPos());
            if (newDist > dist) continue;

            if (!(entity.getWorld().getBlockState(blockPos).getBlock().equals(Blocks.MAGMA_BLOCK))) continue;

            offset = null;
            if (entity.getWorld().getBlockState(blockPos.up()).isAir())
                offset = Direction.UP;
            else if (entity.getWorld().getBlockState(blockPos.south()).getCollisionShape(entity.getWorld(), blockPos.south()).isEmpty())
                offset = Direction.SOUTH;
            else if (entity.getWorld().getBlockState(blockPos.north()).getCollisionShape(entity.getWorld(), blockPos.north()).isEmpty())
                offset = Direction.NORTH;
            else if (entity.getWorld().getBlockState(blockPos.east()).getCollisionShape(entity.getWorld(), blockPos.east()).isEmpty())
                offset = Direction.EAST;
            else if (entity.getWorld().getBlockState(blockPos.west()).getCollisionShape(entity.getWorld(), blockPos.west()).isEmpty())
                offset = Direction.WEST;
            if (offset == null) continue;

            if (entity.getWorld().getBlockState(blockPos.down(1)).getCollisionShape(entity.getWorld(), blockPos.down(1)).isEmpty()
                && entity.getWorld().getBlockState(blockPos.down(2)).getCollisionShape(entity.getWorld(), blockPos.down(2)).isEmpty())
                continue;

            if (entity.getNavigation().findPathTo(Set.of(blockPos.offset(offset)), 16) != null) {
                dist = newDist;
                entity.setMagmaBlockPos(blockPos);
            }

        }
    }
}
