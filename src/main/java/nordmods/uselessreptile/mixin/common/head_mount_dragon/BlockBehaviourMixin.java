package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Some stuff for dismounting head mount dragons
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void putDragonAssOff(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isShiftKeyDown() && player.getFirstPassenger() instanceof HeadMountDragon headMountDragon && headMountDragon instanceof URDragonEntity dragon) {
            dragon.stopRiding();
            dragon.setPos(pos.above().getCenter());
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void putDragonAssOff(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        if (player.isShiftKeyDown() && player.getFirstPassenger() instanceof HeadMountDragon headMountDragon && headMountDragon instanceof URDragonEntity dragon) {
            dragon.stopRiding();
            dragon.setPos(pos.above().getCenter());
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
