package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyExpressionValue(method = "startRiding(Lnet/minecraft/entity/Entity;ZZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z"))
    private boolean ignoreHeadMountDragon(boolean original) {
        return this instanceof HeadMountDragon || original;
    }
}
