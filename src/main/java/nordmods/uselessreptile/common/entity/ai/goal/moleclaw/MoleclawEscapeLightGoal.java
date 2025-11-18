package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import nordmods.uselessreptile.common.entity.Moleclaw;

public class MoleclawEscapeLightGoal extends PanicGoal {

    private final Moleclaw mob;
    private int timer = 0;
    private int nextStrongAttackTimer = 60;

    public MoleclawEscapeLightGoal(Moleclaw mob) {
        super(mob, 1);
        this.mob = mob;
    }

    @Override
    protected boolean shouldPanic() {
        return mob.isPanicking() || this.mob.isOnFire();
    }

    @Override
    protected boolean findRandomPosition() {
        Vec3i darkestSpot = null;
        int light = 30;
        double distance = 1000000;
        for (int i = 30; i > 0; i--) {
            Vec3 vec3d = DefaultRandomPos.getPos(this.mob, 10, 3);
            Vec3i vec3i = vec3d != null ? new Vec3i((int) vec3d.x, (int) vec3d.y, (int) vec3d.z) : null;
            if (vec3i != null) {
                BlockPos blockPos = new BlockPos(vec3i.getX(), (int) (vec3i.getY() + mob.getEyeHeight(mob.getPose())), vec3i.getZ());
                BlockPos blockPos1 = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());
                boolean canFit = mob.level().getBlockState(blockPos1.above(1)).isAir() && mob.level().getBlockState(blockPos1.above(2)).isAir();
                double distanceToCurrent = mob.distanceToSqr(vec3d);
                if (Moleclaw.getLightAtPos(blockPos, mob) <= light && canFit && distanceToCurrent < distance) {
                    darkestSpot = vec3i;
                    light = Moleclaw.getLightAtPos(blockPos, mob);
                    distance = distanceToCurrent;
                }
            }
        }
        if (darkestSpot == null)  return false;

        if (distance < 0.5 && Moleclaw.getLightAtPos(mob.blockPosition(), mob) == Moleclaw.getLightAtPos(new BlockPos(darkestSpot), mob)) {
            for (int i = 30; i > 0; i--) {
                Vec3 vec3d = DefaultRandomPos.getPos(this.mob, 10, 3);
                Vec3i vec3i = vec3d != null ? new Vec3i((int) vec3d.x, (int) vec3d.y, (int) vec3d.z) : null;
                if (vec3i != null) {
                    BlockPos blockPos1 = new BlockPos((int) vec3d.x, (int) vec3d.y, (int) vec3d.z);
                    boolean canFit = mob.level().getBlockState(blockPos1.above(1)).isAir() && mob.level().getBlockState(blockPos1.above(2)).isAir();
                    if (canFit) {
                        darkestSpot = vec3i;
                        break;
                    }
                }
            }
        }

        if (darkestSpot == null)  return false;
        this.posX = darkestSpot.getX();
        this.posY = darkestSpot.getY();
        this.posZ = darkestSpot.getZ();
        return true;

    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && shouldPanic();
    }

    @Override
    public void tick() {
        mob.setSprinting(true);
        timer++;

        if (timer >= nextStrongAttackTimer && mob.getPrimaryAttackCooldown() == 0) {
            for (VoxelShape shape : mob.level().getBlockCollisions(mob, mob.getSecondaryAttackBox())) {
                if (!shape.isEmpty()) {
                    mob.scheduleStrongAttack();
                    timer = 0;
                    nextStrongAttackTimer = mob.getRandom().nextInt(21) + 40;
                    break;
                }
            }
        }
    }
}
