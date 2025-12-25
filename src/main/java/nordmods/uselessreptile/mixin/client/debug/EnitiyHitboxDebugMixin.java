package nordmods.uselessreptile.mixin.client.debug;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EnitiyHitboxDebugMixin {
    @Inject(method = "showHitboxes", at = @At("TAIL"))
    private void showExtraDebugInfo(Entity entity, float tickDelta, boolean bl, CallbackInfo ci) {
        if (URClientConfig.getConfig().attackBoxesInDebug && entity instanceof URDragonEntity dragon) {
            Vec3 offset = entity.getPosition(tickDelta).subtract(entity.position());
            Gizmos.cuboid(dragon.getPrimaryAttackBox().move(offset), GizmoStyle.stroke(ARGB.colorFromFloat(1, 1, 0, 1)));
            AABB secondary = dragon.getSecondaryAttackBox();
            if (secondary != null) {
                Gizmos.cuboid(secondary.move(offset), GizmoStyle.stroke(ARGB.colorFromFloat(1, 1, 0, 0.25f)));
            }
        }

        if (entity instanceof ShooterDragon shooterDragon) {
            Vec3 offset = entity.getPosition(tickDelta).subtract(entity.position());
            Vec3 pos = shooterDragon.getShootingPoint().pos().add(offset);
            Vec3 rotation = shooterDragon.getShootingPoint().rotation().scale(2);
            Gizmos.arrow(pos, pos.add(rotation), -0x00FFF1);
        }
    }
}
