package nordmods.uselessreptile.mixin.client.render;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }
    //Fix for when ridden dragon (URideableDragonEntity) gets its velocity reset on client-side of other player. This is needed for correct animation playing
    @ModifyArg(method = "travelRidden", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 clientVelocityResetFix(Vec3 par1) {
        if (getControllingPassenger() instanceof RemotePlayer) return getDeltaMovement();
        return par1;
    }
}