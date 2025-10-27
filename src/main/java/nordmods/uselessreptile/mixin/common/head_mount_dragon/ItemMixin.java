package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void putDragonAssOff(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isSneaking() && player.getFirstPassenger() instanceof HeadMountDragon headMountDragon && headMountDragon instanceof URDragonEntity dragon) {
            dragon.stopRiding();
            dragon.setPosition(context.getBlockPos().up().toCenterPos());
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}