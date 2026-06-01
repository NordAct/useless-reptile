package nordmods.uselessreptile.common.entity.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URKeyMappings;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.dragon_ability.NoopAbility;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.misc.Placeholder;
import nordmods.uselessreptile.common.network.c2s.KeyInputPayload;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class URRideableDragonEntity extends URDragonEntity implements HasCustomInventoryScreen {
    public static final Identifier RIDER_BONUS = UselessReptile.id("rider_bonus");
    private DragonAbilityHolder primaryRiderAbility = createNoopHolder();
    private DragonAbilityHolder secondaryRiderAbility = createNoopHolder();

    protected URRideableDragonEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
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

    protected void updateRiderAbilities() {
        List<DragonAbilityHolder> available = getAvailableAbilities();
        primaryRiderAbility = available.stream()
                .filter(
                        a -> a.getAbility().getCommonAbilityData().attackType().isPresent() &&
                        a.getAbility().getCommonAbilityData().attackType().get() == AttackType.PRIMARY &&
                        a.getCooldown() <= 0
                )
                .findFirst()
                .orElse(available.stream()
                        .filter(
                                a -> a.getAbility().getCommonAbilityData().attackType().isPresent() &&
                                a.getAbility().getCommonAbilityData().attackType().get() == AttackType.PRIMARY
                        )
                        .findFirst()
                        .orElseGet(this::createNoopHolder)
                );
        secondaryRiderAbility = available.stream()
                .filter(
                        a -> a.getAbility().getCommonAbilityData().attackType().isPresent() &&
                                a.getAbility().getCommonAbilityData().attackType().get() == AttackType.SECONDARY &&
                                a.getCooldown() <= 0
                ).findFirst()
                .orElse(available.stream()
                        .filter(
                                a -> a.getAbility().getCommonAbilityData().attackType().isPresent() &&
                                        a.getAbility().getCommonAbilityData().attackType().get() == AttackType.SECONDARY
                        ).findFirst()
                        .orElseGet(this::createNoopHolder)
                );
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame() && !isInteractableItem(itemStack) && !player.isShiftKeyDown() && !level().isClientSide()) {
            if (hasSaddle() && player.startRiding(this)) {
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void addPassenger(Entity entity) {
        // don't mind some personalized insults
        if (entity.getVehicle() != this) throw new IllegalStateException("Person who caused this, have you considered reading vanilla code?");

        List<Entity> passengersList = Lists.newArrayList(passengers);

        if (passengersList.isEmpty()) { //if no passengers, just add directly
            if (entity instanceof LivingEntity living && isOwnedBy(living)) {
                passengersList.add(entity);
            } else {
                Placeholder placeholder = new Placeholder(level());
                level().addFreshEntity(placeholder);
                placeholder.vehicle = this;
                passengersList.add(placeholder);
                passengersList.add(entity);
            }
        } else { // if there are passengers, check for placeholders and try to replace them
            if (entity instanceof LivingEntity living && isOwnedBy(living)) {
                Entity first = passengersList.getFirst();
                if (first instanceof Placeholder) {
                    first.vehicle = null;
                    first.discard();
                } else {
                    first.stopRiding();
                }
                passengersList.removeFirst();
                passengersList.addFirst(entity);
            } else {
                boolean set = false;
                for (int i = 1; i < passengersList.size(); i++) {
                    Entity passenger = passengersList.get(i);
                    if (passenger instanceof Placeholder) {
                        set = true;
                        passenger.vehicle = null;
                        passenger.discard();
                        passengersList.set(i, entity);
                    }
                }
                if (!set) passengersList.add(entity);
            }
        }

        passengers = ImmutableList.copyOf(passengersList);
    }

    @Override
    protected void removePassenger(Entity entity) {
        if (entity.getVehicle() == this) throw new IllegalStateException("Person who caused this, have you considered reading vanilla code?");

        if (passengers.size() == 1 && passengers.getFirst() == entity) { //if single passenger, just clear instantly
            passengers = ImmutableList.of();
        } else { //if not, swap with placeholder
            List<Entity> passengersList = Lists.newArrayList(passengers);

            if (passengersList.getLast() == entity) { //no need for a placeholder if it's last passenger
                passengersList.removeLast();
            } else { //here goes placeholder
                passengersList.replaceAll(entity1 -> {
                    if (entity1 == entity) {
                        Placeholder placeholder = new Placeholder(level());
                        level().addFreshEntity(placeholder);
                        placeholder.vehicle = this;
                        return placeholder;
                    }
                    return entity1;
                });
            }
            if (passengersList.stream().allMatch(passenger -> passenger instanceof Placeholder)) { //if all placeholders, just clear
                passengersList.forEach(passenger -> {
                    passenger.vehicle = null;
                    passenger.discard();
                });
                passengersList = List.of();
            }

            passengers = ImmutableList.copyOf(passengersList);
        }

        entity.boardingCooldown = 60;
    }

    @Override
    protected boolean canAddPassenger(@NonNull Entity entity) {
        long count = getPassengers()
                .stream()
                .filter(passenger -> !(passenger instanceof Placeholder))
                .count()
                + (!getPassengers().isEmpty() && getFirstPassenger() instanceof Placeholder && !(entity instanceof LivingEntity living && isOwnedBy(living)) //first placeholder always reserved for rider-owner
                || getPassengers().isEmpty() && !(entity instanceof LivingEntity living && isOwnedBy(living)) ? 1 : 0);
        return count < getMaxPassengerCount();
    }

    @Override
    public void ejectPassengers() { //had to override this due to packet handling issue
        passengers.reverse().forEach(Entity::stopRiding);
    }

    @Override
    public boolean isLocalInstanceAuthoritative() { //accessed via AT, overriden to make server side movement logic work
        if (hasControllingPassenger()
                && (getControllingPassenger() instanceof Player player && player.isLocalPlayer() || !level().isClientSide())) return true;
        return super.isLocalInstanceAuthoritative();
    }

    @Override
    public void travel(@NonNull Vec3 movementInput) {
        if (level() instanceof ServerLevel) {
            boolean hasRider = hasControllingPassenger();
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
    protected @NonNull Vec3 getRiddenInput(@NonNull Player rider, @NonNull Vec3 movementInput) {
        return super.getRiddenInput(rider, updateMovementInput(rider, movementInput));
    }

    @Override
    protected void tickRidden(@NonNull Player rider, @NonNull Vec3 movementInput) {
        updateRiderAbilities();
        if (level().isClientSide() && getControllingPassenger() instanceof LocalPlayer player) {
            boolean isSprintPressed = player.input.keyPresses.sprint();
            boolean isMoveForwardPressed = player.input.keyPresses.forward();
            boolean isJumpPressed = (player.input.keyPresses.jump())
                    || (URClientConfig.getConfig().upDownCameraControl
                        && hasVerticalInput()
                        && player.getXRot() < -URClientConfig.getConfig().upDownCameraPitchThreshold);
            boolean isMoveBackPressed = player.input.keyPresses.backward();
            boolean isDownPressed = URKeyMappings.FLY_DOWN_KEY.isDown()
                    || (URClientConfig.getConfig().upDownCameraControl
                        && hasVerticalInput()
                        && player.getXRot() > URClientConfig.getConfig().upDownCameraPitchThreshold);
            boolean isSecondaryAttackPressed = URKeyMappings.SECONDARY_ATTACK_KEY.isDown();
            boolean isPrimaryAttackPressed = URKeyMappings.PRIMARY_ATTACK_KEY.isDown();
            boolean freeLook = URKeyMappings.FREE_LOOK_KEY.isDown();

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
                        new KeyInputPayload(isJumpPressed,
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
        if (isPrimaryAttackPressed()) primaryRiderAbility.use();
        if (isSecondaryAttackPressed()) secondaryRiderAbility.use();
        super.tickRidden(rider, movementInput);
    }

    @Override
    public void updateEquipment() {
        super.updateEquipment();
        ItemStack saddle = getInventory().getItem(0);
        setItemSlot(EquipmentSlot.SADDLE, saddle);
    }

    public boolean hasSaddle() {
        return isSaddle(getInventory().getItem(0));
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
    public void openCustomInventoryScreen(@NonNull Player player) {
        if (player instanceof ServerPlayer serverPlayer && isOwnedBy(player)) {
            serverPlayer.uselessreptile$openDragonInventoryScreen(this);
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

    public int getMaxPassengerCount() {
        ItemStack saddle = getItemBySlot(EquipmentSlot.SADDLE);
        Identifier id = BuiltInRegistries.ITEM.getKey(saddle.getItem());
        if (saddle.isEmpty() || !getDragonEquipment().containsKey(id)) return 0;
        return getDragonEquipment().get(id).passengerPositions().orElse(List.of()).size();
    }

    @Override
    protected @NonNull Vec3 getPassengerAttachmentPoint(@NonNull Entity passenger, @NonNull EntityDimensions dimensions, float scaleFactor) {
        int ordinal = getPassengers().size();
        Vec3 offset = Vec3.ZERO;
        while (--ordinal > -1) {
            if (getPassengers().get(ordinal) == passenger) break;
        }
        if (ordinal > -1) {
            ItemStack saddle = getItemBySlot(EquipmentSlot.SADDLE);
            Identifier id = BuiltInRegistries.ITEM.getKey(saddle.getItem());
            if (!saddle.isEmpty() && getDragonEquipment().containsKey(id)) {
                List<Vec3> positions = getDragonEquipment().get(id).passengerPositions().orElseThrow();
                offset = positions.get(Math.min(ordinal, positions.size() - 1));
            }
        }
        return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor).add(offset.yRot(-getYRot() * Mth.DEG_TO_RAD));
    }

    protected DragonAbilityHolder createNoopHolder() {
        return new DragonAbilityHolder(new NoopAbility(), this);
    }

    public enum AttackType implements StringRepresentable {
        PRIMARY("primary"),
        SECONDARY("secondary")
        ;
        private final String name;

        AttackType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
