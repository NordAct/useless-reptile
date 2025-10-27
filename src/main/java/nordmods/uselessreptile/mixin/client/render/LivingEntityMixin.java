package nordmods.uselessreptile.mixin.client.render;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    //Fix for when ridden dragon (URideableDragonEntity) gets its velocity reset on client-side of other player. This is needed for correct animation playing
    @ModifyArg(method = "travelControlled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private Vec3d clientVelocityResetFix(Vec3d par1) {
        if (getControllingPassenger() instanceof OtherClientPlayerEntity) return getVelocity();
        return par1;
    }
}