package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonBodyRotationControl<T extends URDragonEntity> extends DragonRotationControl<T> {
    public DragonBodyRotationControl(T dragon) {
        super(dragon);
    }

    @Override
    public void tick() {
        if (lockRotation) return;
        dragon.setYRot(dragon.yBodyRot = dragon.getServerBodyYaw());
        dragon.yBodyRotChange = Mth.degreesDifference(dragon.yBodyRotO, dragon.yBodyRot);
        dragon.yHeadRot = Mth.rotateIfNecessary(dragon.yHeadRot, dragon.yBodyRot, dragon.getMaxHeadYRot());

        if (!dragon.level().isClientSide()) {
            if (dragon.yBodyRotChange < 0) dragon.setTurningState(URDragonEntity.TurningState.LEFT);
            else if (dragon.yBodyRotChange > 0) dragon.setTurningState(URDragonEntity.TurningState.RIGHT);
            else dragon.setTurningState(URDragonEntity.TurningState.NONE);

            if (rotationCooldown > 0) {
                --rotationCooldown;
                getYRotD().ifPresent(yaw -> dragon.setServerBodyYaw(Mth.rotateIfNecessary(yaw, dragon.yBodyRot, dragon.getHeadRotSpeed())));
            }
        }
    }

    public boolean canHeadReachTarget() {
        return rotationCooldown > 0
                || dragon.getLookTargetYaw().isPresent() && Math.abs(Mth.degreesDifference(dragon.getLookTargetYaw().orElse(dragon.yHeadRot), dragon.yBodyRot)) <= dragon.getMaxHeadYRot();
    }

    public boolean canBeRotatedOutsideMoveController() {
        return dragon.getNavigation().isDone() && dragon.getTarget() == null && !dragon.isOrderedToSit();
    }
}
