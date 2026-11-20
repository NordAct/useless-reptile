package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.Control;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.Optional;

public abstract class DragonRotationControl<T extends URDragonEntity>  implements Control {
    protected final T dragon;
    protected int rotationCooldown;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected boolean lockRotation;

    public DragonRotationControl(T dragon) {
        this.dragon = dragon;
    }

    protected Optional<Float> getXRotD() {
        double xd = this.wantedX - this.dragon.getX();
        double yd = this.wantedY - this.dragon.getEyeY();
        double zd = this.wantedZ - this.dragon.getZ();
        double sd = Math.sqrt(xd * xd + zd * zd);
        return !(Math.abs(yd) > 1.0E-5F) && !(Math.abs(sd) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(-(Mth.atan2(yd, sd) * 180.0F / (float)Math.PI)));
    }

    protected Optional<Float> getYRotD() {
        double xd = this.wantedX - this.dragon.getX();
        double zd = this.wantedZ - this.dragon.getZ();
        return !(Math.abs(zd) > 1.0E-5F) && !(Math.abs(xd) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(Mth.atan2(zd, xd) * 180.0F / (float)Math.PI) - 90.0F);
    }

    public double getWantedX() {
        return this.wantedX;
    }

    public double getWantedY() {
        return this.wantedY;
    }

    public double getWantedZ() {
        return this.wantedZ;
    }

    public boolean isRotationInProgress() {
        return this.rotationCooldown > 0;
    }

    public void setLockRotation(boolean state) {
        lockRotation = state;
    }

    public abstract void tick();

    public void setRotationTarget(final Vec3 vec) {
        this.setRotationTarget(vec.x, vec.y, vec.z);
    }

    public void setRotationTarget(final Entity target) {
        this.setRotationTarget(target.getX(), target.getEyeY(), target.getZ());
    }

    public void setRotationTarget(final double x, final double y, final double z) {
        this.wantedX = x;
        this.wantedY = y;
        this.wantedZ = z;
        this.rotationCooldown = 2;
    }

    public T getDragon() {
        return dragon;
    }
}
