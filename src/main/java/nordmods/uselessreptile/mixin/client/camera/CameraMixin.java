package nordmods.uselessreptile.mixin.client.camera;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private Entity focusedEntity;

    @Shadow public abstract Vector3f getHorizontalPlane();

    @Shadow public abstract Vec3d getPos();

    @Shadow private BlockView area;

    @Shadow protected abstract void moveBy(float f, float g, float h);

    @Unique private static final int ROUNDS = 1000;

    @Inject(method = "update", at = @At(value = "TAIL"))
    public void offsetCameraDistance(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!URClientConfig.getConfig().enableCameraOffset) return;
        if (this.focusedEntity.getVehicle() instanceof URRideableDragonEntity dragonEntity && thirdPerson) {
            float scale = focusedEntity instanceof  LivingEntity livingEntity ? livingEntity.getScale() : 1;

            float distanceToCameraOffset = -URClientConfig.getConfig().cameraDistanceOffset * dragonEntity.getScale() * scale;
            float verticalOffset = URClientConfig.getConfig().cameraVerticalOffset * dragonEntity.getScale() * scale;
            float horizontalOffset = -URClientConfig.getConfig().cameraHorizontalOffset * dragonEntity.getScale() * scale;

            if (verticalOffset != 0) moveUntilCollision(0, verticalOffset, 0);
            if (horizontalOffset != 0) moveUntilCollision(0, 0, horizontalOffset);
            if (distanceToCameraOffset != 0) moveUntilCollision(distanceToCameraOffset, 0 ,0);
        }
    }

    @Unique
    private void moveUntilCollision(float x, float y, float z) { //ain't pretty, but it works
        float dx = x / ROUNDS;
        float dy = y / ROUNDS;
        float dz = z / ROUNDS;
        double dl = new Vec3d(dx, dy, dz).length();
        for(int i = 0; i < ROUNDS; ++i) {
            float h = (float)((i & 1) * 2 - 1);
            float j = (float)((i >> 1 & 1) * 2 - 1);
            float k = (float)((i >> 2 & 1) * 2 - 1);
            Vec3d vec3d = getPos().add(h * 0.1F, j * 0.1F, k * 0.1F);
            Vec3d vec3d2 = vec3d.add((new Vec3d(getHorizontalPlane())).multiply(-dl));
            HitResult hitResult = area.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, focusedEntity));
            if (hitResult.getType() == HitResult.Type.MISS) moveBy(dx, dy, dz);
            else {
                moveBy(-dx, -dy, -dz);
                break;
            }
        }
    }
}
