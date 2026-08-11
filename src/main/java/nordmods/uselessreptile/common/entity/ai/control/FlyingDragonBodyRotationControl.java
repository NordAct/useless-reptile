package nordmods.uselessreptile.common.entity.ai.control;

import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonBodyRotationControl<T extends URDragonEntity & FlyingDragon> extends DragonBodyRotationControl<T>{
    public FlyingDragonBodyRotationControl(T mob) {
        super(mob);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        dragon.xBodyRot = !dragon.isFlying() || !dragon.isMoving() || dragon.isMovingBackwards() ? 0 : Math.clamp(dragon.getDeltaMovement().rotation().x, -45, 45);
        if (!dragon.level().isClientSide()) {
            if (dragon.xBodyRot < -3) dragon.setTiltState(FlyingDragon.TiltState.UP);
            else if (dragon.xBodyRot > 3) dragon.setTiltState(FlyingDragon.TiltState.DOWN);
            else dragon.setTiltState(FlyingDragon.TiltState.NONE);
        }
    }
}
