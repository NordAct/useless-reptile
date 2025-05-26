package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URKeybinds;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;

public abstract class URRideableDragonEntity extends URDragonEntity implements RideableInventory {
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
        builder.add(SECONDARY_ATTACK_PRESSED, false);
        builder.add(PRIMARY_ATTACK_PRESSED, false);
        builder.add(FREE_LOOK, false);
    }

    public static final TrackedData<Boolean> MOVE_FORWARD_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> MOVE_BACK_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> JUMP_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> MOVE_DOWN_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SPRINT_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SECONDARY_ATTACK_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> PRIMARY_ATTACK_PRESSED = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> FREE_LOOK = DataTracker.registerData(URRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public void updateInputs(boolean forward, boolean back, boolean jump, boolean down, boolean isSecondaryAttackPressed, boolean isPrimaryAttackPressed, boolean sprint, boolean freeLook) {
        dataTracker.set(MOVE_FORWARD_PRESSED, forward);
        dataTracker.set(MOVE_BACK_PRESSED, back);
        dataTracker.set(JUMP_PRESSED, jump);
        dataTracker.set(MOVE_DOWN_PRESSED, down);
        dataTracker.set(SECONDARY_ATTACK_PRESSED, isSecondaryAttackPressed);
        dataTracker.set(PRIMARY_ATTACK_PRESSED, isPrimaryAttackPressed);
        dataTracker.set(SPRINT_PRESSED, sprint);
        dataTracker.set(FREE_LOOK, freeLook);
    }

    public boolean isMoveForwardPressed() {return dataTracker.get(MOVE_FORWARD_PRESSED);}
    public boolean isMoveBackPressed() {return dataTracker.get(MOVE_BACK_PRESSED);}
    public boolean isJumpPressed() {return dataTracker.get(JUMP_PRESSED);}
    public boolean isDownPressed() {return dataTracker.get(MOVE_DOWN_PRESSED);}
    public boolean isSprintPressed() {return dataTracker.get(SPRINT_PRESSED);}
    public boolean isSecondaryAttackPressed() {return dataTracker.get(SECONDARY_ATTACK_PRESSED);}
    public boolean isPrimaryAttackPressed() {return dataTracker.get(PRIMARY_ATTACK_PRESSED);}
    public boolean freeLook() {return dataTracker.get(FREE_LOOK);}

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
        if (isTamed() && isOwner(player) && !isInteractableItem(itemStack) && !player.isSneaking()) {
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
        if (canBeControlledByRider()
                && (getControllingPassenger() instanceof PlayerEntity player && player.isMainPlayer() || !getWorld().isClient())) return true;
        return super.isLogicalSideForUpdatingMovement();
    }

    @Override
    public void travel(Vec3d movementInput) {
        updateMovementModifiers();
        if (getWorld() instanceof ServerWorld) {
            boolean hasRider = canBeControlledByRider();
            getLookControl().setLockRotation(hasRider);
            if (hasRider) setHomePoint(getBlockPos());
            else updateInputs(false, false, false, false, false, false, false, false);
        }
        super.travel(movementInput);
    }

    public Vec3d updateMovementInput(PlayerEntity rider, Vec3d movementInput) {
        forwardSpeed = 0;
        if (isMoveForwardPressed()) forwardSpeed = 1;
        if (isMoveBackPressed()) forwardSpeed = -1;

        double landSpeed = forwardSpeed * getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
        if (isSprintPressed()) setSprinting(true);
        setMovingBackwards(isMoveBackPressed() || (!isMoveForwardPressed() && !isMoveBackPressed() && isMovingXZ()));
        if (isMovingBackwards()) setSprinting(false);
        setRotation(rider);
        setPitch(MathHelper.clamp(rider.getPitch(), -getPitchLimit(), getPitchLimit()));
        if (isJumpPressed() && isOnGround()) jump();
        //adding some extra small number to Y velocity so on client it checks isOnGround() correctly
        return new Vec3d(0, movementInput.y  - 0.001, landSpeed);
    }

    public void updateMovementModifiers() {
        if (!isMovingXZ()) setSprinting(false);
        if (isSprinting()) setSpeedMod(1.1f);
        else setSpeedMod(1f);
        if (isMovingBackwards()) setSpeedMod(0.6f);
        float speed = (float) getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
        setMovementSpeed(speed * getSpeedModifier());
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity rider, Vec3d movementInput) {
        return super.getControlledMovementInput(rider, updateMovementInput(rider, movementInput));
    }

    @Override
    protected void tickControlled(PlayerEntity rider, Vec3d movementInput) {
        if (getWorld().isClient() && getControllingPassenger() instanceof ClientPlayerEntity player) {
            boolean isSprintPressed = player.input.playerInput.sprint();
            boolean isMoveForwardPressed = player.input.playerInput.forward();
            boolean isJumpPressed = (player.input.playerInput.jump())
                    || (URClientConfig.getConfig().upDownCameraControl
                        && hasVerticalInput()
                        && player.getPitch() < -URClientConfig.getConfig().upDownCameraPitchThreshold);
            boolean isMoveBackPressed = player.input.playerInput.backward();
            boolean isDownPressed = (URKeybinds.flyDownKey.isUnbound() ? isSprintPressed : URKeybinds.flyDownKey.isPressed())
                    || (URClientConfig.getConfig().upDownCameraControl
                        && hasVerticalInput()
                        && player.getPitch() > URClientConfig.getConfig().upDownCameraPitchThreshold);
            boolean isSecondaryAttackPressed = URKeybinds.secondaryAttackKey.isPressed();
            boolean isPrimaryAttackPressed = URKeybinds.primaryAttackKey.isPressed();
            boolean freeLook = URKeybinds.freeLookKey.isPressed();

            if (isSprintPressed != isSprintPressed()
                    || isMoveForwardPressed != isMoveForwardPressed()
                    || isJumpPressed != isJumpPressed()
                    || isMoveBackPressed != isMoveBackPressed()
                    || isDownPressed != isDownPressed()
                    || isSecondaryAttackPressed != isSecondaryAttackPressed()
                    || isPrimaryAttackPressed != isPrimaryAttackPressed()
                    || freeLook != freeLook()
            ) {
                ClientPlayNetworking.send(
                        new KeyInputC2SPacket(isJumpPressed,
                                isMoveForwardPressed,
                                isMoveBackPressed,
                                isSprintPressed,
                                isSecondaryAttackPressed,
                                isPrimaryAttackPressed,
                                isDownPressed,
                                freeLook,
                                getId()));
            }
        }
        super.tickControlled(rider, movementInput);
    }

    @Override
    public void updateEquipment() {
        super.updateEquipment();
        ItemStack saddle = inventory.getStack(0);
        equipStack(EquipmentSlot.BODY, saddle);
    }

    public boolean hasSaddle() {
        return inventory != null && isSaddleItem(inventory.getStack(0));
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!getWorld().isClient() && canBeControlledByRider() && isOwner(player)) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            player.openHandledScreen(this);
        }
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 500;
        return i >= 0 && i < inventory.size() ? StackReference.of(inventory, i) : super.getStackReference(mappedIndex);
    }

    protected void setRotation(PlayerEntity rider) {
        if (freeLook()) setRotation(getYaw(), getPitch());
        else setRotation(rider.getYaw(), rider.getPitch());
    }

    public int vortexHornCapacity() {
        return 3;
    }

    public abstract boolean isSaddleItem(ItemStack itemStack);

    public boolean hasVerticalInput() {
        return false;
    }
}
