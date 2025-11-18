package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URKeybinds;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;

public abstract class URRideableDragonEntity extends URDragonEntity implements HasCustomInventoryScreen {
    public static final ResourceLocation RIDER_BONUS = UselessReptile.id("rider_bonus");

    protected URRideableDragonEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOVE_FORWARD_PRESSED, false);
        builder.define(MOVE_BACK_PRESSED, false);
        builder.define(JUMP_PRESSED, false);
        builder.define(MOVE_DOWN_PRESSED, false);
        builder.define(SPRINT_PRESSED, false);
        builder.define(SECONDARY_ATTACK_PRESSED, false);
        builder.define(PRIMARY_ATTACK_PRESSED, false);
        builder.define(FREE_LOOK, false);
    }

    public static final EntityDataAccessor<Boolean> MOVE_FORWARD_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> MOVE_BACK_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> JUMP_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> MOVE_DOWN_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SPRINT_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SECONDARY_ATTACK_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> PRIMARY_ATTACK_PRESSED = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FREE_LOOK = SynchedEntityData.defineId(URRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);

    public void updateInputs(boolean forward, boolean back, boolean jump, boolean down, boolean isSecondaryAttackPressed, boolean isPrimaryAttackPressed, boolean sprint, boolean freeLook) {
        entityData.set(MOVE_FORWARD_PRESSED, forward);
        entityData.set(MOVE_BACK_PRESSED, back);
        entityData.set(JUMP_PRESSED, jump);
        entityData.set(MOVE_DOWN_PRESSED, down);
        entityData.set(SECONDARY_ATTACK_PRESSED, isSecondaryAttackPressed);
        entityData.set(PRIMARY_ATTACK_PRESSED, isPrimaryAttackPressed);
        entityData.set(SPRINT_PRESSED, sprint);
        entityData.set(FREE_LOOK, freeLook);
    }

    public boolean isMoveForwardPressed() {return entityData.get(MOVE_FORWARD_PRESSED);}
    public boolean isMoveBackPressed() {return entityData.get(MOVE_BACK_PRESSED);}
    public boolean isJumpPressed() {return entityData.get(JUMP_PRESSED);}
    public boolean isDownPressed() {return entityData.get(MOVE_DOWN_PRESSED);}
    public boolean isSprintPressed() {return entityData.get(SPRINT_PRESSED);}
    public boolean isSecondaryAttackPressed() {return entityData.get(SECONDARY_ATTACK_PRESSED);}
    public boolean isPrimaryAttackPressed() {return entityData.get(PRIMARY_ATTACK_PRESSED);}
    public boolean freeLook() {return entityData.get(FREE_LOOK);}

    @Override
    public LivingEntity getControllingPassenger() {
        return getPassengers().isEmpty() ? null : (LivingEntity) getPassengers().getFirst();
    }

    public boolean canBeControlledByRider() {
        return getControllingPassenger() instanceof Player;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame() && isOwnedBy(player) && !isInteractableItem(itemStack) && !player.isShiftKeyDown()) {
            if (!isVehicle() && hasSaddle()) {
                if (isOrderedToSit()) setOrderedToSit(false);
                else if (!level().isClientSide()) player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isLocalInstanceAuthoritative() {
        if (canBeControlledByRider()
                && (getControllingPassenger() instanceof Player player && player.isLocalPlayer() || !level().isClientSide())) return true;
        return super.isLocalInstanceAuthoritative();
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (level() instanceof ServerLevel) {
            boolean hasRider = canBeControlledByRider();
            updateRiderBonus(hasRider);
            getLookControl().setLockRotation(hasRider);
            if (hasRider) setHomePoint(blockPosition());
            else updateInputs(false, false, false, false, false, false, false, false);
        }
        super.travel(movementInput);
    }

    public Vec3 updateMovementInput(Player rider, Vec3 movementInput) {
        zza = 0;
        if (isMoveForwardPressed()) zza = 1;
        if (isMoveBackPressed()) zza = -1;

        double landSpeed = zza * getAttributeValue(Attributes.MOVEMENT_SPEED);
        if (isSprintPressed()) setSprinting(true);
        setMovingBackwards(isMoveBackPressed() || (!isMoveForwardPressed() && !isMoveBackPressed() && isMoving()));
        if (isMovingBackwards()) setSprinting(false);
        setRotation(rider);
        setXRot(Mth.clamp(rider.getXRot(), -getPitchLimit(), getPitchLimit()));
        if (isJumpPressed() && onGround()) jumpFromGround();
        //adding some extra small number to Y velocity so on client it checks isOnGround() correctly
        return new Vec3(0, movementInput.y  - 0.001, landSpeed);
    }

    @Override
    protected Vec3 getRiddenInput(Player rider, Vec3 movementInput) {
        return super.getRiddenInput(rider, updateMovementInput(rider, movementInput));
    }

    @Override
    protected void tickRidden(Player rider, Vec3 movementInput) {
        if (level().isClientSide() && getControllingPassenger() instanceof LocalPlayer player) {
            boolean isSprintPressed = player.input.keyPresses.sprint();
            boolean isMoveForwardPressed = player.input.keyPresses.forward();
            boolean isJumpPressed = (player.input.keyPresses.jump())
                    || (URClientConfig.getConfig().upDownCameraControl
                        && hasVerticalInput()
                        && player.getXRot() < -URClientConfig.getConfig().upDownCameraPitchThreshold);
            boolean isMoveBackPressed = player.input.keyPresses.backward();
            boolean isDownPressed = URKeybinds.FLY_DOWN_KEY.isDown()
                    || (URClientConfig.getConfig().upDownCameraControl
                        && hasVerticalInput()
                        && player.getXRot() > URClientConfig.getConfig().upDownCameraPitchThreshold);
            boolean isSecondaryAttackPressed = URKeybinds.SECONDARY_ATTACK_KEY.isDown();
            boolean isPrimaryAttackPressed = URKeybinds.PRIMARY_ATTACK_KEY.isDown();
            boolean freeLook = URKeybinds.FREE_LOOK_KEY.isDown();

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
        super.tickRidden(rider, movementInput);
    }

    @Override
    public void updateEquipment() {
        super.updateEquipment();
        ItemStack saddle = getInventory().getItem(0);
        setItemSlot(EquipmentSlot.SADDLE, saddle);
    }

    public boolean hasSaddle() {
        return getInventory() != null && isSaddle(getInventory().getItem(0));
    }

    protected void updateRiderBonus(boolean hasRider) {
        float mult = URMobAttributesConfig.getConfig().riddenDragonGroundSpeedMultiplier;
        if (mult == 1) return;

        AttributeInstance entityAttributeInstance = getAttribute(Attributes.MOVEMENT_SPEED);
        if (hasRider) {
            if (!entityAttributeInstance.hasModifier(RIDER_BONUS))
                entityAttributeInstance.addTransientModifier(new AttributeModifier(RIDER_BONUS, mult, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        } else entityAttributeInstance.removeModifier(RIDER_BONUS);
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!level().isClientSide() && canBeControlledByRider() && isOwnedBy(player)) {
            GUIEntityToRenderS2CPacket.send((ServerPlayer) player, this);
            player.openMenu(this);
        }
    }

    protected void setRotation(Player rider) {
        if (freeLook()) setRot(getYRot(), getXRot());
        else setRot(rider.getYRot(), rider.getXRot());
    }

    public int vortexHornCapacity() {
        return 3;
    }

    public boolean hasVerticalInput() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }
}
