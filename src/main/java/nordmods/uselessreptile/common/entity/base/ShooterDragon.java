package nordmods.uselessreptile.common.entity.base;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import org.joml.Vector3f;

public interface ShooterDragon {
    void setShootingPoint(ShootingPoint point);
    ShootingPoint getShootingPoint();
    Vector3f getShootingPointAnchor();
    float getShootingPointDesiredPitch();
    float getShootingPointDesiredYaw();
    default float getShootingPointPitch() {
        Vector3f rot = getShootingPoint().rotation();
        double xz = Math.sqrt(rot.x * rot.x + rot.z * rot.z);
        return (float) -(Math.atan2(rot.y, xz) * Mth.RAD_TO_DEG);
    }
    default float getShootingPointYaw() {
        Vector3f rot = getShootingPoint().rotation();
        return (float) (Math.atan2(rot.z, rot.x) * Mth.RAD_TO_DEG) - 90;
    }
}
