package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void putDragonAssOff(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown() && player.getFirstPassenger() instanceof HeadMountDragon headMountDragon && headMountDragon instanceof URDragonEntity dragon) {
            dragon.stopRiding();
            dragon.setPos(context.getClickedPos().above().getCenter());
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}