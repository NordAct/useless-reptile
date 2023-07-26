package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

public class MoleclawEscapeLightGoal extends EscapeDangerGoal {

    private final MoleclawEntity mob;
    private int timer = 0;
    private int nextStrongAttackTimer = 60;
    private int attackDelay = 0;

    public MoleclawEscapeLightGoal(MoleclawEntity mob) {
        super(mob, 1);
        this.mob = mob;
    }

    @Override
    protected boolean isInDanger() {
        return mob.isPanicking() || this.mob.isOnFire();
    }

    @Override
    protected boolean findTarget() {
        Vec3i darkestSpot = null;
        int light = 30;
        double distance = 1000000;
        for (int i = 30; i > 0; i--) {
            Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 10, 3);
            Vec3i vec3i = vec3d != null ? new Vec3i((int) vec3d.x, (int) vec3d.y, (int) vec3d.z) : null;
            if (vec3i != null) {
                BlockPos blockPos = new BlockPos(vec3i.getX(), (int) (vec3i.getY() + mob.getEyeHeight(mob.getPose())), vec3i.getZ());
                BlockPos blockPos1 = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());
                boolean canFit = mob.getWorld().getBlockState(blockPos1.up(1)).isAir() && mob.getWorld().getBlockState(blockPos1.up(2)).isAir();
                double distanceToCurrent = mob.squaredDistanceTo(vec3d);
                if (MoleclawEntity.getLightAtPos(blockPos, mob) <= light && canFit && distanceToCurrent < distance) {
                    darkestSpot = vec3i;
                    light = MoleclawEntity.getLightAtPos(blockPos, mob);
                    distance = distanceToCurrent;
                }
            }
        }
        if (darkestSpot == null)  return false;

        if (distance < 0.5 && MoleclawEntity.getLightAtPos(mob.getBlockPos(), mob) == MoleclawEntity.getLightAtPos(new BlockPos(darkestSpot), mob)) {
            for (int i = 30; i > 0; i--) {
                Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 10, 3);
                Vec3i vec3i = vec3d != null ? new Vec3i((int) vec3d.x, (int) vec3d.y, (int) vec3d.z) : null;
                if (vec3i != null) {
                    BlockPos blockPos1 = new BlockPos((int) vec3d.x, (int) vec3d.y, (int) vec3d.z);
                    boolean canFit = mob.getWorld().getBlockState(blockPos1.up(1)).isAir() && mob.getWorld().getBlockState(blockPos1.up(2)).isAir();
                    if (canFit) {
                        darkestSpot = vec3i;
                        break;
                    }
                }
            }
        }

        if (darkestSpot == null)  return false;
        this.targetX = darkestSpot.getX();
        this.targetY = darkestSpot.getY();
        this.targetZ = darkestSpot.getZ();
        return true;

    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && isInDanger();
    }

    @Override
    public void tick() {
        mob.setSprinting(true);
        timer++;

        if (mob.isPrimaryAttack()) {
            if (attackDelay == 0) {
                mob.strongAttack();
                attackDelay = 100;
            }
            else attackDelay--;
        }

        if (timer >= nextStrongAttackTimer && mob.getPrimaryAttackCooldown() == 0) {
            BlockHitResult hitResult = (BlockHitResult) mob.raycast(4, 1, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                attackDelay = 4;
                mob.setPrimaryAttackCooldown(mob.getMaxPrimaryAttackCooldown());
                timer = 0;
                nextStrongAttackTimer = mob.getRandom().nextInt(21) + 40;
            }
        }
    }
}
