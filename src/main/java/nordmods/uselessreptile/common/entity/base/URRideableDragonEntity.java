package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import nordmods.uselessreptile.client.init.URKeybinds;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.KeyInputSyncS2CPacket;
import nordmods.uselessreptile.common.network.PosSyncS2CPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class URRideableDragonEntity extends URDragonEntity implements RideableInventory {
    public boolean isSecondaryAttackPressed = false;
    public boolean isPrimaryAttackPressed = false;
    public static Set<UUID> passengers = new HashSet<>();

    protected URRideableDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        stepHeight = 1;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(MOVE_FORWARD_PRESSED, false);
        dataTracker.startTracking(MOVE_BACK_PRESSED, false);
        dataTracker.startTracking(JUMP_PRESSED, false);
        dataTracker.startTracking(MOVE_DOWN_PRESSED, false);
        dataTracker.startTracking(SPRINT_PRESSED, false);
    }

    public static final TrackedData<Boolean> MOVE_FORWARD_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> MOVE_BACK_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> JUMP_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> MOVE_DOWN_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SPRINT_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public void updateInputs(boolean forward, boolean back, boolean jump, boolean down, boolean sprint) {
        dataTracker.set(MOVE_FORWARD_PRESSED, forward);
        dataTracker.set(MOVE_BACK_PRESSED, back);
        dataTracker.set(JUMP_PRESSED, jump);
        dataTracker.set(MOVE_DOWN_PRESSED, down);
        dataTracker.set(SPRINT_PRESSED, sprint);
    }

    public boolean isMoveForwardPressed() {return dataTracker.get(MOVE_FORWARD_PRESSED);}
    public boolean isMoveBackPressed() {return dataTracker.get(MOVE_BACK_PRESSED);}
    public boolean isJumpPressed() {return dataTracker.get(JUMP_PRESSED);}
    public boolean isDownPressed() {return dataTracker.get(MOVE_DOWN_PRESSED);}
    public boolean isSprintPressed() {return dataTracker.get(SPRINT_PRESSED);}

    @Override
    public LivingEntity getPrimaryPassenger() {
        return getPassengerList().isEmpty() ? null : (LivingEntity) getPassengerList().get(0);
    }

    public boolean canBeControlledByRider() {
        return getPrimaryPassenger() instanceof PlayerEntity;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (isTamed() && isOwnerOrCreative(player) && !isInteractableItem(itemStack)) {
            if (!hasPassengers() && hasSaddle()) {
                if (isSitting()) setIsSitting(false);
                else if (!getWorld().isClient()) player.startRiding(this);
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        if (canBeControlledByRider()) return true;
        return this.canMoveVoluntarily();
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient() && getPrimaryPassenger() == MinecraftClient.getInstance().player) {
            boolean isSprintPressed = MinecraftClient.getInstance().options.sprintKey.isPressed();
            boolean isMoveForwardPressed = MinecraftClient.getInstance().options.forwardKey.isPressed();
            boolean isJumpPressed = MinecraftClient.getInstance().options.jumpKey.isPressed();
            boolean isMoveBackPressed = MinecraftClient.getInstance().options.backKey.isPressed();
            boolean isDownPressed = URKeybinds.flyDownKey.isUnbound() ? isSprintPressed : URKeybinds.flyDownKey.isPressed();
            updateInputs(isMoveForwardPressed, isMoveBackPressed, isJumpPressed, isDownPressed, isSprintPressed);

            isSecondaryAttackPressed = URKeybinds.secondaryAttackKey.isPressed();
            isPrimaryAttackPressed = URKeybinds.primaryAttackKey.isPressed();
        }
        if (getPrimaryPassenger() == null) updateInputs(false, false, false, false, false);

        if (getWorld() instanceof ServerWorld world && canBeControlledByRider())
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) {
                KeyInputSyncS2CPacket.send(player, this);
                //PosSyncS2CPacket.send(player, this);
            }
    }


    protected void updateSaddle() {
        if (isTamed() && inventory != null) {
            ItemStack saddle = inventory.getStack(0);
            if (hasSaddle()) equipStack(EquipmentSlot.FEET, saddle);
        }
    }

    public boolean hasSaddle() {
        if (isTamed() && inventory != null) {
            ItemStack saddle = inventory.getStack(0);
            boolean isSaddle = saddle.getItem() == Items.SADDLE;
            if (isSaddle) equipStack(EquipmentSlot.FEET, saddle);
            return isSaddle;
        } else return false;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return player.getAbilities().creativeMode && super.canBeLeashedBy(player);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!getWorld().isClient() && canBeControlledByRider() && isOwnerOrCreative(player)) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            player.openHandledScreen(this);
        }
    }
}
