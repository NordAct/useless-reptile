package nordmods.uselessreptile.common.entity.base;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;

public interface ShooterDragon {
    void setShootingPoint(ShootingPoint point);
    ShootingPoint getShootingPoint();
    Vec3d getShootingPointAnchor();
    float getShootingPointDesiredPitch();
    float getShootingPointDesiredYaw();
    default float getShootingPointPitch() {
        Vec3d rot = getShootingPoint().rotation();
        double xz = Math.sqrt(rot.x * rot.x + rot.z * rot.z);
        return (float) -(Math.atan2(rot.y, xz) * MathHelper.DEGREES_PER_RADIAN);
    }
    default float getShootingPointYaw() {
        Vec3d rot = getShootingPoint().rotation();
        return (float) (Math.atan2(rot.z, rot.x) * MathHelper.DEGREES_PER_RADIAN) - 90;
    }
}
