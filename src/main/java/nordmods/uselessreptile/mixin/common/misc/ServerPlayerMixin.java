package nordmods.uselessreptile.mixin.common.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import nordmods.uselessreptile.common.network.s2c.OpenDragonInventoryPayload;
import nordmods.uselessreptile.common.util.duck.DragonInventoryHelper;
import nordmods.uselessreptile.mixin.common.lightning_chaser.PlayerMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Saves LC spawn timer and forces non-controlling passenger to open its own inventory instead of dragon's
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin implements DragonInventoryHelper {
    @Shadow
    public abstract void closeContainer();

    @Shadow
    protected abstract void nextContainerCounter();

    @Shadow
    private int containerCounter;

    @Shadow
    protected abstract void initMenu(AbstractContainerMenu abstractContainerMenu);

    private ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V", at = @At("TAIL"))
    private void copySemaData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        useless_reptile$setTimer(oldPlayer.useless_reptile$getTimer());
    }

    @Override
    public void uselessreptile$openDragonInventoryScreen(URDragonEntity dragon) {
        if (containerMenu != inventoryMenu) closeContainer();

        nextContainerCounter();
        OpenDragonInventoryPayload.send((ServerPlayer) (Object)this, dragon, containerCounter);
        containerMenu = new URDragonMenu(containerCounter, getInventory(), dragon.getInventory());
        initMenu(this.containerMenu);
    }
}
