package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class DragonBodyRotationControl extends BodyRotationControl {
    private final URDragonEntity dragon;
    public DragonBodyRotationControl(URDragonEntity mob) {
        super(mob);
        dragon = mob;
    }

    @Override
    public void clientTick() {
        dragon.yHeadRot = Mth.rotateIfNecessary(dragon.yHeadRot, dragon.yBodyRot, dragon.getMaxHeadYRot());
        if (!(dragon instanceof URRideableDragonEntity rideableDragon) || !rideableDragon.freeLook())
            dragon.setYRot(dragon.yBodyRot = Mth.rotateIfNecessary(dragon.getYHeadRot(), dragon.yBodyRot, dragon.getHeadRotSpeed()));

        if (!dragon.level().isClientSide()) {
            float diff = Mth.degreesDifference(dragon.yBodyRotO, dragon.yBodyRot);
            if (diff < 0) dragon.setTurningState(URDragonEntity.TurningState.LEFT);
            else if (diff > 0) dragon.setTurningState(URDragonEntity.TurningState.RIGHT);
            else dragon.setTurningState(URDragonEntity.TurningState.NONE);
        }
    }
}
