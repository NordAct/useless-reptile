package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

// I had to redo look control for body control because vanilla's idea of body control sucks
public class WrappedDragonBodyRotationControl<T extends URDragonEntity> extends BodyRotationControl {
    private final DragonBodyRotationControl<T> bodyRotationControl;
    public WrappedDragonBodyRotationControl(DragonBodyRotationControl<T> bodyRotationControl) {
        super(bodyRotationControl.getDragon());
        this.bodyRotationControl = bodyRotationControl;
    }

    public DragonBodyRotationControl<T> getBodyRotationControl() {
        return bodyRotationControl;
    }

    @Override
    public void clientTick() {
        bodyRotationControl.tick();
    }

    public void setRotationTarget(final Vec3 vec) {
        setRotationTarget(vec.x, vec.z);
    }

    public void setRotationTarget(final Entity target) {
        bodyRotationControl.setRotationTarget(target.getX(), bodyRotationControl.getDragon().getEyeY(), target.getZ());
    }

    public void setRotationTarget(final double x, final double z) {
        bodyRotationControl.setRotationTarget(x, bodyRotationControl.getDragon().getEyeY(), z);
    }
}
