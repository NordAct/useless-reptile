package nordmods.uselessreptile.mixin.client.debug;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.dragon_ability.MeleeAttackAbility;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EnitiyHitboxDebugMixin {

    /// Shows attack boxes when hitbox debug renderer is enabled
    @Inject(method = "showHitboxes", at = @At("TAIL"))
    private void showExtraDebugInfo(Entity entity, float tickDelta, boolean bl, CallbackInfo ci) {
        if (URClientConfig.getConfig().attackBoxesInDebug && entity instanceof URDragonEntity dragon) {
            Vec3 offset = entity.getPosition(tickDelta).subtract(entity.position());
            for (DragonAbilityHolder abilityHolder : dragon.getAvailableAbilities()) {
                if (abilityHolder.getAbility() instanceof MeleeAttackAbility meleeAttackAbility) {
                    Gizmos.cuboid(meleeAttackAbility.getAttackBox(abilityHolder).move(offset), GizmoStyle.stroke(meleeAttackAbility.getDebugAttackBoxColor()));
                }
            }
        }
    }
}
