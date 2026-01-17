package nordmods.uselessreptile.mixin.client.camera;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private Entity entity;

    @Shadow protected abstract void move(float f, float g, float h);

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract Vector3fc forwardVector();

    @Shadow
    private Level level;
    @Unique private static final int ROUNDS = 1000;

    /// Offsets camera by specified in config amount
    @Inject(method = "setup", at = @At(value = "TAIL"))
    public void offsetCameraDistance(Level area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!URClientConfig.getConfig().enableCameraOffset) return;
        if (this.entity.getVehicle() instanceof URRideableDragonEntity dragonEntity && thirdPerson) {
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
        double dl = new Vec3(dx, dy, dz).length();
        for(int i = 0; i < ROUNDS; ++i) {
            float h = (float)((i & 1) * 2 - 1);
            float j = (float)((i >> 1 & 1) * 2 - 1);
            float k = (float)((i >> 2 & 1) * 2 - 1);
            Vec3 vec3d = position().add(h * 0.1F, j * 0.1F, k * 0.1F);
            Vec3 vec3d2 = vec3d.add((new Vec3(forwardVector())).scale(-dl));
            HitResult hitResult = level.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (hitResult.getType() == HitResult.Type.MISS) move(dx, dy, dz);
            else {
                move(-dx, -dy, -dz);
                break;
            }
        }
    }
}
