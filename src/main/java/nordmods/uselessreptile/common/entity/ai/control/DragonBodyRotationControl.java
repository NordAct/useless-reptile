package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class DragonBodyRotationControl<T extends URDragonEntity> extends BodyRotationControl {
    protected final T dragon;
    public DragonBodyRotationControl(T mob) {
        super(mob);
        dragon = mob;
    }

    @Override
    public void clientTick() {
        dragon.yHeadRot = Mth.rotateIfNecessary(dragon.yHeadRot, dragon.yBodyRot, dragon.getMaxHeadYRot());
        if (!(dragon instanceof URRideableDragonEntity rideableDragon) || !rideableDragon.freeLook())
            dragon.setYRot(dragon.yBodyRot = Mth.rotateIfNecessary(dragon.getYHeadRot(), dragon.yBodyRot, dragon.getHeadRotSpeed()));

        dragon.yBodyRotChange = Mth.degreesDifference(dragon.yBodyRotO, dragon.yBodyRot);

        if (!dragon.level().isClientSide()) {
            if (dragon.yBodyRotChange < 0) dragon.setTurningState(URDragonEntity.TurningState.LEFT);
            else if (dragon.yBodyRotChange > 0) dragon.setTurningState(URDragonEntity.TurningState.RIGHT);
            else dragon.setTurningState(URDragonEntity.TurningState.NONE);
        }
    }
}
