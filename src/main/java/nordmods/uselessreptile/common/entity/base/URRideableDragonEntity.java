package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.client.init.URKeybinds;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;
import nordmods.uselessreptile.common.network.PositionSyncS2CPacket;

public abstract class URRideableDragonEntity extends URDragonEntity implements RideableInventory {
    public boolean isSecondaryAttackPressed = false;
    public boolean isPrimaryAttackPressed = false;

    protected URRideableDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(MOVE_FORWARD_PRESSED, false);
        builder.add(MOVE_BACK_PRESSED, false);
        builder.add(JUMP_PRESSED, false);
        builder.add(MOVE_DOWN_PRESSED, false);
        builder.add(SPRINT_PRESSED, false);
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
    public LivingEntity getControllingPassenger() {
        return getPassengerList().isEmpty() ? null : (LivingEntity) getPassengerList().getFirst();
    }

    public boolean canBeControlledByRider() {
        return getControllingPassenger() instanceof PlayerEntity;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (isTamed() && isOwner(player) && !isInteractableItem(itemStack)) {
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
        if (!canBeControlledByRider()) updateInputs(false, false, false, false, false);
    }

    @Override
    protected void tickControlled(PlayerEntity rider, Vec3d movementInput) {
        if (getWorld().isClient() && rider == MinecraftClient.getInstance().player) {
            boolean isSprintPressed = MinecraftClient.getInstance().options.sprintKey.isPressed();
            boolean isMoveForwardPressed = MinecraftClient.getInstance().options.forwardKey.isPressed();
            boolean isJumpPressed = MinecraftClient.getInstance().options.jumpKey.isPressed();
            boolean isMoveBackPressed = MinecraftClient.getInstance().options.backKey.isPressed();
            boolean isDownPressed = URKeybinds.flyDownKey.isUnbound() ? isSprintPressed : URKeybinds.flyDownKey.isPressed();
            isSecondaryAttackPressed = URKeybinds.secondaryAttackKey.isPressed();
            isPrimaryAttackPressed = URKeybinds.primaryAttackKey.isPressed();

            ClientPlayNetworking.send(
                    new KeyInputC2SPacket(isJumpPressed,
                            isMoveForwardPressed,
                            isMoveBackPressed,
                            isSprintPressed,
                            isSecondaryAttackPressed,
                            isPrimaryAttackPressed,
                            isDownPressed,
                            getId()));
        }
        if (getWorld() instanceof ServerWorld world) {
            setHomePoint(getBlockPos());
            //05.10.24 - I'm done trying to fix this desync. I give no clue why it even happens
            for (ServerPlayerEntity player : PlayerLookup.around(world, getBlockPos(), 512)) {
                if (player.getVehicle() == this) continue;
                PositionSyncS2CPacket.send(player, this);
            }
        }
        super.tickControlled(rider, movementInput);
    }

    @Override
    public void updateEquipment() {
        super.updateEquipment();
        ItemStack saddle = inventory.getStack(0);
        equipStack(EquipmentSlot.FEET, saddle);
    }

    public boolean hasSaddle() {
        return inventory != null ?  isSaddleItem(inventory.getStack(0)) : false;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!getWorld().isClient() && canBeControlledByRider() && isOwner(player)) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            player.openHandledScreen(this);
        }
    }

    protected void setRotation(PlayerEntity rider) {
        setRotation(rider.getYaw(), rider.getPitch());
    }

    public int vortexHornCapacity() {
        return 3;
    }

    public abstract boolean isSaddleItem(ItemStack itemStack);
}
