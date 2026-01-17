package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /// Allows head mount dragon to actually ride the player
    @ModifyExpressionValue(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
    private boolean ignoreHeadMountDragon(boolean original) {
        return this instanceof HeadMountDragon || original;
    }
}
