package nordmods.uselessreptile.common.entity.base;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.CommonProtection;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import nordmods.uselessreptile.client.util.AssetCahceOwner;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.control.DragonLookControl;
import nordmods.uselessreptile.common.entity.ai.control.LandDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.navigation.DragonNavigation;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import nordmods.uselessreptile.common.event.DragonOnItemConsumedEvent;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URGameEvents;
import nordmods.uselessreptile.common.init.URStatusEffects;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.item.VortexHornItem;
import nordmods.uselessreptile.common.network.URPacketHelper;
import nordmods.uselessreptile.common.util.dragon_spawn.DragonSpawn;
import nordmods.uselessreptile.common.util.dragon_spawn.DragonSpawnUtil;
import nordmods.uselessreptile.common.util.duck.HeadMountDragonOwner;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class URDragonEntity extends TameableEntity implements GeoEntity, NamedScreenHandlerFactory, AssetCahceOwner, InventoryChangedListener {
    protected double animationSpeed = 1;
    public static final int TRANSITION_TICKS = 10;
    protected float pitchLimitGround = 90;
    protected int primaryAttackDuration = 20;
    protected int secondaryAttackDuration = 20;
    protected int specialAttackDuration = 20;
    protected int baseTamingProgress = 1;
    protected int eatFromInventoryTimer = 20;
    protected boolean canNavigateInFluids = false;
    protected int ticksUntilHeal = -1;
    private int healTimer = 0;
    private BlockPos homePoint = BlockPos.ORIGIN;
    protected final EntityGameEventHandler<URDragonEntity.JukeboxEventListener> jukeboxEventHandler = new EntityGameEventHandler<>(new URDragonEntity.JukeboxEventListener
            (new EntityPositionSource(this, getStandingEyeHeight()), GameEvent.JUKEBOX_PLAY.value().notificationRadius()));
    protected final EntityGameEventHandler<URDragonEntity.HornUsedEventListener> hornUsedEventHandler = new EntityGameEventHandler<>(new URDragonEntity.HornUsedEventListener
            (new EntityPositionSource(this, getStandingEyeHeight()), URGameEvents.INSTRUMENT_USED.value().notificationRadius()));
    protected @Nullable BlockPos jukeboxPos;
    protected SimpleInventory inventory = new SimpleInventory(URDragonScreenHandler.maxStorageSize);
    public boolean shouldFollow = false;

    protected URDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        navigation = new DragonNavigation(this, world);
        lookControl = new DragonLookControl(this);
        moveControl = new LandDragonMoveControl<>(this);
        inventory.addListener(this);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(MOVING_BACKWARDS, false);
        builder.add(IS_SITTING, false);
        builder.add(DANCING, false);
        builder.add(TURNING_STATE, (byte)0);//1 - left, 2 - right, 0 - straight
        builder.add(ROTATION_PROGRESS, (byte)0);
        builder.add(TAMING_PROGRESS, 1);
        builder.add(ATTACK_TYPE, 1);
        builder.add(SPEED_MODIFIER, 1f);
        builder.add(MOUNTED_OFFSET, 0.35f);
        builder.add(HEIGHT_MODIFIER, 1f);
        builder.add(WIDTH_MODIFIER, 1f);
        builder.add(SECONDARY_ATTACK_COOLDOWN, 0);
        builder.add(PRIMARY_ATTACK_COOLDOWN, 0);
        builder.add(SPECIAL_ATTACK_COOLDOWN, 0);
        builder.add(ACCELERATION_DURATION, 0);
        builder.add(BOUNDED_INSTRUMENT_SOUND, "");
        builder.add(VARIANT, "");
    }

    public static final TrackedData<Boolean> MOVING_BACKWARDS = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_SITTING = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> DANCING = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Byte> TURNING_STATE = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Byte> ROTATION_PROGRESS = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> TAMING_PROGRESS = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Float> SPEED_MODIFIER = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> MOUNTED_OFFSET = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> HEIGHT_MODIFIER = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> WIDTH_MODIFIER = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> SECONDARY_ATTACK_COOLDOWN = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> PRIMARY_ATTACK_COOLDOWN = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SPECIAL_ATTACK_COOLDOWN = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> ACCELERATION_DURATION = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> ATTACK_TYPE = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<String> BOUNDED_INSTRUMENT_SOUND = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<String> VARIANT = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.STRING);

    public boolean isSecondaryAttack() {return getSecondaryAttackCooldown() > getMaxSecondaryAttackCooldown() - secondaryAttackDuration;} //old melee
    public int getSecondaryAttackCooldown() {return  dataTracker.get(SECONDARY_ATTACK_COOLDOWN);}
    public void setSecondaryAttackCooldown(int state) {dataTracker.set(SECONDARY_ATTACK_COOLDOWN, state);}

    public boolean isPrimaryAttack() {return getPrimaryAttackCooldown() > getMaxPrimaryAttackCooldown() - primaryAttackDuration;} //old range
    public void setPrimaryAttackCooldown(int state) {dataTracker.set(PRIMARY_ATTACK_COOLDOWN, state);}
    public int getPrimaryAttackCooldown() {return  dataTracker.get(PRIMARY_ATTACK_COOLDOWN);}

    public boolean isSpecialAttack() {return getSpecialAttackCooldown() > getMaxSpecialAttackCooldown() - specialAttackDuration;}
    public void setSpecialAttackCooldown(int state) {dataTracker.set(SPECIAL_ATTACK_COOLDOWN, state);}
    public int getSpecialAttackCooldown() {return  dataTracker.get(SPECIAL_ATTACK_COOLDOWN);}

    public int getAccelerationDuration() {return dataTracker.get(ACCELERATION_DURATION);}
    public void setAccelerationDuration(int state) {dataTracker.set(ACCELERATION_DURATION, state);}

    public int getAttackType() {return dataTracker.get(ATTACK_TYPE);}
    public void setAttackType(int state) {dataTracker.set(ATTACK_TYPE, state);}

    public boolean isMovingBackwards() {return dataTracker.get(MOVING_BACKWARDS);}
    public void setMovingBackwards(boolean state) {dataTracker.set(MOVING_BACKWARDS, state);}

    public boolean isDancing() {return dataTracker.get(DANCING);}
    public void setDancing(boolean state) {dataTracker.set(DANCING, state);}

    public boolean isMoving() {return getVelocity().getZ() != 0 || getVelocity().getX() != 0;}

    public boolean getIsSitting() {return dataTracker.get(IS_SITTING);}
    public void setIsSitting(boolean state) {
        dataTracker.set(IS_SITTING, state);
        setSitting(state);
        if (state) setTarget(null);
    }

    public String getVariant() {return dataTracker.get(VARIANT);}
    public void setVariant(String state) {dataTracker.set(VARIANT, state);}

    public byte getTurningState() {return dataTracker.get(TURNING_STATE);}
    public void setTurningState(byte state) {dataTracker.set(TURNING_STATE, state);}

    public byte getRotationProgress() {return dataTracker.get(ROTATION_PROGRESS);}
    public float getNormalizedRotationProgress() {return (float)getRotationProgress()/(float)TRANSITION_TICKS;}
    public void setRotationProgress(byte state) {dataTracker.set(ROTATION_PROGRESS, state);}

    public int getTamingProgress() {return dataTracker.get(TAMING_PROGRESS);}
    public void setTamingProgress(int state) {dataTracker.set(TAMING_PROGRESS, state);}

    public float getSpeedModifier() {return dataTracker.get(SPEED_MODIFIER);}
    public void setSpeedMod(float state) {dataTracker.set(SPEED_MODIFIER, state);}

    public float getMountedOffset() {return dataTracker.get(MOUNTED_OFFSET);}
    public void setMountedOffset(float state) {dataTracker.set(MOUNTED_OFFSET, state);}

    public float getHeightMod() {return dataTracker.get(HEIGHT_MODIFIER);}
    public void setHeightMod(float state) {dataTracker.set(HEIGHT_MODIFIER, state);}

    public float getWidthMod() {return dataTracker.get(WIDTH_MODIFIER);}
    public void setWidthMod(float state) {dataTracker.set(WIDTH_MODIFIER, state);}

    public String getBoundedInstrumentSound() {return  dataTracker.get(BOUNDED_INSTRUMENT_SOUND);}
    public void setBoundedInstrumentSound(String state) {dataTracker.set(BOUNDED_INSTRUMENT_SOUND, state);}

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putString("Variant", getVariant());

        if (!isTamed()) tag.putInt("TamingProgress", getTamingProgress());
        else {
            tag.putString("BoundedInstrumentSound", getBoundedInstrumentSound());
        }

        int[] coords = {getHomePoint().getX(), getHomePoint().getY(), getHomePoint().getZ()};
        tag.putIntArray("HomePoint", coords);

        tag.putBoolean("Sitting", getIsSitting());
        if (inventory != null && isTamed()) {
            final NbtList inv = new NbtList();
            for (int i = 0; i < inventory.size(); i++) {
                inv.add(inventory.getStack(i).encodeAllowEmpty(getRegistryManager()));
            }
            tag.put("Inventory", inv);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        dataTracker.set(VARIANT, tag.getString("Variant"));

        if (!isTamed()) setTamingProgress(tag.getInt("TamingProgress"));
        else {
            setBoundedInstrumentSound(tag.getString("BoundedInstrumentSound"));
        }

        int[] coords = tag.getIntArray("HomePoint");
        if (coords.length == 0) setHomePoint(getBlockPos());
        else setHomePoint(new BlockPos(coords[0], coords[1], coords[2]));

        setIsSitting(tag.getBoolean("Sitting"));
        if (tag.contains("Inventory")) {
            final NbtList inv = tag.getList("Inventory", 10);
            inventory = new SimpleInventory(inv.size());
            for (int i = 0; i < inv.size(); i++) {
                inventory.setStack(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), inv.getCompound(i)));
            }
            inventory.addListener(this);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (getWorld().isClient)
            if (CUSTOM_NAME.equals(data) || VARIANT.equals(data)) assetCache.cleanCache();
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = new PassiveData(false);
        setTamingProgress(baseTamingProgress);
        DragonSpawnUtil.assignVariantFromList(this, DragonSpawnUtil.getAvailableVariants(world, this), spawnReason);
        setHomePoint(getBlockPos());
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    protected static DefaultAttributeContainer.Builder createDragonAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_MAX_HEALTH)
                .add(EntityAttributes.GENERIC_ARMOR)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 60)
                .add(EntityAttributes.GENERIC_JUMP_STRENGTH)
                .add(URAttributes.DRAGON_VERTICAL_SPEED)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD)
                .add(URAttributes.DRAGON_SPECIAL_ATTACK_COOLDOWN);

    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public BlockPos getHomePoint() {
        return homePoint;
    }

    public void setHomePoint(BlockPos homePoint) {
        this.homePoint = homePoint;
    }

    @Override
    public void setOwner(PlayerEntity entity) {
        super.setOwner(entity);
        setHomePoint(getBlockPos());
    }

    @Override
    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        if (getWorld() instanceof ServerWorld serverWorld) {
            callback.accept(this.jukeboxEventHandler, serverWorld);
            callback.accept(this.hornUsedEventHandler, serverWorld);
        }
        super.updateEventHandler(callback);
    }

    public void updateJukeboxPos(BlockPos jukeboxPos, boolean playing) {
        if (playing) {
            if (!isDancing()) {
                this.jukeboxPos = jukeboxPos;
                setDancing(getTarget() == null);
            }
        } else {
            this.jukeboxPos = null;
            setDancing(false);
        }

    }

    public void updateEquipment() {
        if (inventory != null) {
            ItemStack head = inventory.getStack(1);
            equipStack(EquipmentSlot.HEAD, head);

            ItemStack body = inventory.getStack(2);
            equipStack(EquipmentSlot.CHEST, body);

            ItemStack tail = inventory.getStack(3);
            equipStack(EquipmentSlot.LEGS, tail);

            ItemStack banner = inventory.getStack(4);
            equipStack(EquipmentSlot.OFFHAND, banner);
        }
    }

    public static boolean canDragonSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        List<DragonSpawn> availableVariants = DragonSpawnUtil.getAvailableVariants(world, pos, EntityType.getId(type).getPath());
        return !availableVariants.isEmpty();
    }

    @Override
    public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
        boolean empty = newStack.isEmpty() && oldStack.isEmpty();
        if (!empty && !ItemStack.areItemsAndComponentsEqual(oldStack, newStack) && !firstUpdate) {
            if (!getWorld().isClient() && isArmorSlot(slot))
                URPacketHelper.playSound(this, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(), getSoundCategory(), 1, 1, 6);
        }
        super.onEquipStack(slot, oldStack, newStack);
    }

    @Override
    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return super.getBaseDimensions(pose).scaled(getWidthMod()/getScale(), getHeightMod()/getScale());
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (isTamed()) {
            if (isFavoriteFood(itemStack) && getHealth() != getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH)) {
                consumeGivenItem(player, itemStack, SoundEvents.ENTITY_GENERIC_EAT);
                heal(getHealthRegenerationFromFood());
                return ActionResult.SUCCESS;
            }
        }

        if (isTamed() && isOwner(player)) {
            if (this instanceof HeadMountDragon && player.isSneaking() && itemStack.isEmpty()) {
                startRiding(player);
                return ActionResult.SUCCESS;
            }

            if (itemStack.getItem() instanceof PotionItem potionItem && player.isSneaking()) {
                DragonOnItemConsumedEvent.EVENT.invoker().onItemConsumed(player, itemStack);
                potionItem.finishUsing(itemStack, getWorld(), this);
                playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1, 1);
                if (!player.isCreative()) { //checking for emptiness for case if somehow potion stack size is more than 1
                    itemStack.decrement(1);
                    if (itemStack.isEmpty()) player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                    else player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
                }
                return ActionResult.SUCCESS;
            }

            if (isInstrument(itemStack) && !player.isSneaking() && !(itemStack.getItem() instanceof VortexHornItem)) {
                String sound = getInstrument(itemStack);
                if (!getBoundedInstrumentSound().equals(sound)) setBoundedInstrumentSound(sound);
                else setBoundedInstrumentSound("");
                Text instrumentSound = Text.translatable(getBoundedInstrumentSound().isEmpty() ?
                        "other.uselessreptile.none" : getInstrumentSoundKey(Identifier.of(getBoundedInstrumentSound())));
                if (!getWorld().isClient()) player.sendMessage(Text.translatable("other.uselessreptile.sound_respond", getName(), instrumentSound), true);
                if (getWorld().isClient()) player.playSound(SoundEvents.BLOCK_COMPARATOR_CLICK, 0.2f, 2);
                return ActionResult.SUCCESS;
            }

            if ((itemStack.isOf(Items.STICK) || isInstrument(itemStack)) && player.isSneaking()) {
                if (isSitting()) setIsSitting(false);
                else {
                    setIsSitting(true);
                    getNavigation().stop();
                }
                return ActionResult.SUCCESS;
            }

            if (player.isSneaking() && inventory.size() > 0) {
                if (!getWorld().isClient())
                    player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        boolean result = super.startRiding(entity, force);
        if (this instanceof HeadMountDragon && result && entity instanceof HeadMountDragonOwner owner) {
            NbtCompound nbtCompound = new NbtCompound();
            saveSelfNbt(nbtCompound);
            owner.setHeadMountDragon(nbtCompound);
            setPortalCooldown(0);
        }
        return result;
    }

    @Override
    public void stopRiding() {
        if (this instanceof HeadMountDragon && getVehicle() instanceof HeadMountDragonOwner owner) {
            if (owner instanceof ServerPlayerEntity player && player.isDisconnected()) return;
            owner.setHeadMountDragon(new NbtCompound());
        }
        super.stopRiding();
    }

    protected boolean isInteractableItem(ItemStack itemStack) {
        return itemStack.isOf(Items.POTION) || itemStack.isOf(Items.STICK) || isInstrument(itemStack) || isFavoriteFood(itemStack);
    }

    private String getInstrumentSoundKey(Identifier sound) {
        return Util.createTranslationKey("instrument", sound);
    }

    public boolean isInstrument(ItemStack itemStack) {
        return itemStack.getComponents().contains(DataComponentTypes.INSTRUMENT);
    }

    public static String getInstrument(ItemStack itemStack) {
        if (!itemStack.getComponents().contains(DataComponentTypes.INSTRUMENT)) return "";
        return itemStack.getComponents().get(DataComponentTypes.INSTRUMENT).getIdAsString();
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) getWorld().playSound(getX(), getY(),getZ(), sound, SoundCategory.NEUTRAL, volume, pitch,true);
    }

    public float getWidthModTransSpeed() {
        return (float) (0.22f * animationSpeed * getScale());
    }
    public float getHeightModTransSpeed() {
        return (float) (0.3 * animationSpeed * getScale());
    }
    public float getMountedOffsetTransSpeed() {
        return (float) (0.125 * animationSpeed * getScale());
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        float currentYaw = getYaw() % 360;
        float destinationYaw = yaw % 360;
        //т.к. у игрока поворот измеряется от -180 до 180, а у других энтити от 0 до 360, то приведенная ниже дичь необходима
        //due player having rotation from -180 to 180 while all other entities have it from 0 to 360, this check is necessary
        if (destinationYaw < 0) destinationYaw += 360;
        float yawDiff = (currentYaw - destinationYaw) % 360;
        if (yawDiff != 0) {
            if (yawDiff > 180) yawDiff -= 360;
            else if (yawDiff < -180) yawDiff +=360;

            if (yawDiff < -getRotationSpeed()) {
                currentYaw += getRotationSpeed();
                if (!getWorld().isClient()) setTurningState((byte)2);
            }
            else if (yawDiff > getRotationSpeed()) {
                currentYaw -= getRotationSpeed();
                if (!getWorld().isClient()) setTurningState((byte)1);
            }
            else currentYaw = destinationYaw;
        } else {
            if (!getWorld().isClient()) setTurningState((byte)0);
        }
        prevYaw = bodyYaw = getYaw();
        super.setRotation(currentYaw, MathHelper.clamp(pitch, -getPitchLimit(), getPitchLimit()));
        headYaw = currentYaw;
    }

    protected void setHitboxModifiers(float destinationHeight, float destinationWidth, float destinationMountedOffset) {
        destinationHeight *= getScale();
        destinationWidth *= getScale();
        destinationMountedOffset *= getScale();

        float widthMod = getWidthMod();
        float heightMod = getHeightMod();
        float mountedOffset = getMountedOffset();
        float widthDiff = widthMod - destinationWidth;
        float heightDiff = heightMod - destinationHeight;
        float mountedOffsetDiff = mountedOffset - destinationMountedOffset;

        if (widthDiff != 0) {
            if (widthDiff > getWidthModTransSpeed()) widthMod -= getWidthModTransSpeed();
            else if (widthDiff < -getWidthModTransSpeed()) widthMod += getWidthModTransSpeed();
            else widthMod = destinationWidth;
        }

        if (heightDiff != 0) {
            if (heightDiff > getHeightModTransSpeed()) heightMod -= getHeightModTransSpeed();
            else if (heightDiff < -getHeightModTransSpeed()) heightMod += getHeightModTransSpeed();
            else heightMod = destinationHeight;
        }

        if (mountedOffsetDiff != 0) {
            if (mountedOffsetDiff > getMountedOffsetTransSpeed()) mountedOffset -= getHeightModTransSpeed();
            else if (mountedOffsetDiff < -getHeightModTransSpeed()) mountedOffset += getHeightModTransSpeed();
            else mountedOffset = destinationMountedOffset;
        }

        setMountedOffset(mountedOffset);
        setHeightMod(heightMod);
        setWidthMod(widthMod);

        calculateDimensions();
    }

    //because rotation is called twice within one tick... somehow
    public float getRotationSpeed() {
        return getGroundRotationSpeed() * getMovementSpeedModifier() / 2f;
    }

    public float getGroundRotationSpeed() {
        return (float) getAttributeValue(URAttributes.DRAGON_GROUND_ROTATION_SPEED);
    }

    public float getPitchLimit() {
        return pitchLimitGround;
    }

    public float getMaxAccelerationDuration() {
        return (float) (getAttributeValue(URAttributes.DRAGON_ACCELERATION_DURATION) * getMovementSpeedModifier());
    }

    protected float getCooldownModifier() {
        float mod = 1;
        if (hasStatusEffect(StatusEffects.SLOWNESS)) mod *= (float) (1 + 0.1 * (getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() + 1));
        if (hasStatusEffect(StatusEffects.SPEED)) mod *= (float) (1 - 0.1 * MathHelper.clamp(getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1, 1, 9));
        if (hasStatusEffect(URStatusEffects.SHOCK)) mod /= 2;
        return mod;
    }

    protected float getMovementSpeedModifier() {
        double baseSpeed = getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        double speed = getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        return (float) (speed / baseSpeed);
    }

    public int getMaxSecondaryAttackCooldown() {
        return (int) (getAttributeValue(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN) * getCooldownModifier());
    }
    public int getMaxPrimaryAttackCooldown() {
        return (int) (getAttributeValue(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN) * getCooldownModifier());
    }
    public int getMaxSpecialAttackCooldown() {
        return (int) (getAttributeValue(URAttributes.DRAGON_SPECIAL_ATTACK_COOLDOWN) * getCooldownModifier());
    }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient()) {
            updateRotationProgress();
        }
        else updateAnimationSpeed();

        if (this instanceof ShooterDragon shooterDragon) {
            shooterDragon.setShootingPoint(
                    new ShootingPoint(
                            shooterDragon.getShootingPointAnchor(),
                            getRotationVector(
                                    shooterDragon.getShootingPointDesiredPitch(),
                                    shooterDragon.getShootingPointDesiredYaw()
                            )
                    )
            );
        }

        if (getSecondaryAttackCooldown() > 0) setSecondaryAttackCooldown(getSecondaryAttackCooldown() - 1);
        if (getPrimaryAttackCooldown() > 0) setPrimaryAttackCooldown(getPrimaryAttackCooldown() - 1);
        if (getSpecialAttackCooldown() > 0) setSpecialAttackCooldown(getSpecialAttackCooldown() - 1);

        if (ticksUntilHeal > -1 && --healTimer <= 0) {
            heal(1);
            healTimer = getTicksUntilHeal();
        }

        if (this instanceof HeadMountDragon) {
            if (getVehicle() instanceof PlayerEntity player) {
                if (!player.isAlive()) stopRiding();
                getLookControl().setLockRotation(true);
                if (getWorld().isClient()) {
                    prevYaw = getYaw();
                    setYaw(player.getYaw());
                    byte turnState = 0;
                    float diff = prevYaw - getYaw();
                    if (diff > 0) turnState = 1;
                    if (diff < 0) turnState = 2;
                    setTurningState(turnState);
                }
            } else getLookControl().setLockRotation(false);
        }

        if (!getWorld().isClient() && age % 20 == 0) {
            boolean jukeboxReachable = false;
            if (jukeboxPos != null) jukeboxReachable = jukeboxPos.isWithinDistance(getBlockPos(), 9);
            if (jukeboxReachable) updateJukeboxPos(jukeboxPos, true);
            else updateJukeboxPos(null, false);
        }
    }

    protected void updateAnimationSpeed() {
        animationSpeed = MathHelper.lerp(1f/getWorld().getTickManager().getTickRate(), animationSpeed, getMovementSpeedModifier());
    }

    protected abstract float getBaseGroundSpeed();

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isTamed() && this.age > 2400;
    }

    @SuppressWarnings("SameReturnValue")
    protected <A extends GeoEntity> PlayState loopAnim(String anim, AnimationState<A> event) {
        event.getController().setAnimation(RawAnimation.begin().thenLoop(anim)); return PlayState.CONTINUE;
    }

    @SuppressWarnings("SameReturnValue")
    protected <A extends GeoEntity> PlayState playAnim(String anim, AnimationState<A> event) {
        event.getController().setAnimation(RawAnimation.begin().then(anim, Animation.LoopType.PLAY_ONCE)); return PlayState.CONTINUE;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public boolean doesCollide(Box box1, Box box2) {
        VoxelShape voxelShape = VoxelShapes.cuboid(box1);
        VoxelShape voxelShape2 = VoxelShapes.cuboid(box2);
        return VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean canTarget(@Nullable LivingEntity target) {
        if (target == null) return false;
        if (isSitting()) return false;
        if (getOwner() != null && target instanceof TameableEntity tameable && tameable.getOwner() == getOwner()) return false;
        return super.canTarget(target);
    }

    //idk how else to detect Replay Mod
    public boolean isClientSpectator() {
        if (MinecraftClient.getInstance().player != null) return MinecraftClient.getInstance().player.isSpectator();
        else return false;
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vec3d(0, getMountedOffset(), 0);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (inventory != null) {
            for(int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                    dropStack(itemStack);
                }
            }

        }
    }

    public abstract boolean isFavoriteFood(ItemStack itemStack);

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    public abstract boolean isTamingItem(ItemStack itemStack);

    public float getHealthRegenerationFromFood() {
        return (float) getAttributeValue(URAttributes.DRAGON_REGENERATION_FROM_FOOD);
    }

    public void tickEatFromInventoryTimer() {
        if (eatFromInventoryTimer > 0) eatFromInventoryTimer--;
        else eatFromInventoryTimer = 200;
    }

    public int getEatFromInventoryTimer() {
        return eatFromInventoryTimer;
    }

    public ItemStack getStackFromSlot (int slot) {
        if (inventory == null) return ItemStack.EMPTY;
        return inventory.getStack(slot);
    }

    public boolean canNavigateInFluids() {
        return canNavigateInFluids;
    }

    public boolean hasTargetInWater() {
        return (navigation.getTargetPos() != null && getWorld().getBlockState(navigation.getTargetPos()).isLiquid()
                    || getTarget() != null && getTarget().isInsideWaterOrBubbleColumn())
                && canNavigateInFluids;
    }

    @Override
    protected boolean shouldSwimInFluids() {
        return !canNavigateInFluids;
    }

    @Override
    public int getMaxLookYawChange() {
        return (int) getRotationSpeed();
    }

    public String getDragonID() {
        return EntityType.getId(getType()).getPath();
    }

    private void updateRotationProgress() {
        switch (getTurningState()) {
            case 1 -> {
                if (getRotationProgress() < TRANSITION_TICKS) setRotationProgress((byte) (getRotationProgress() + 1));
            }
            case 2 -> {
                if (getRotationProgress() > -TRANSITION_TICKS) setRotationProgress((byte) (getRotationProgress() - 1));
            }
            default -> {
                if (getRotationProgress() != 0) {
                    if (getRotationProgress() > 0) setRotationProgress((byte) (getRotationProgress() - 1));
                    else setRotationProgress((byte) (getRotationProgress() + 1));
                }
            }
        }
    }

    public float getYawWithAdjustment() {
        float yaw = getYaw();
        if (!hasControllingPassenger() && getTarget() != null) {
            float targetYaw = getLookControl().getTargetYaw().orElse(0f);
            float difference = Math.clamp((yaw - targetYaw) % 360, -getRotationSpeed(), getRotationSpeed());
            return yaw + difference; //making it easier for dum-dum to aim on its own
        }
        return (yaw - getNormalizedRotationProgress() * getYawProgressLimit()) % 360;
    }

    public float getYawProgressLimit() {
        return 0;
    }

    @Override
    public DragonLookControl getLookControl() {
        return (DragonLookControl) lookControl;
    }

    //making public for sake of debug render
    @Override
    public Box getAttackBox() {
        return super.getAttackBox();
    }

    public Box getSecondaryAttackBox() {
        return null;
    }

    protected static URMobAttributesConfig attributes() {
        return URMobAttributesConfig.getConfig();
    }

    protected int getTicksUntilHeal() {
        return ticksUntilHeal;
    }

    public abstract String getDefaultVariant();

    public final boolean isBlockProtected(BlockPos blockPos) {
        BlockState blockState = getWorld().getBlockState(blockPos);
        PlayerEntity rider = getOwner() instanceof URRideableDragonEntity dragon && dragon.canBeControlledByRider() ?
                (PlayerEntity) getControllingPassenger() : null;
        GameProfile gameProfile = rider != null ? rider.getGameProfile() : CommonProtection.UNKNOWN;
        return blockState.isIn(URTags.DRAGON_UNBREAKABLE) || !CommonProtection.canBreakBlock(getWorld(), blockPos, gameProfile, rider);
    }

    public boolean canBreakBlocks() {
        return false;
    }

    @Override
    public EntityNavigation getNavigation() {
        return navigation;
    }

    @Override
    protected boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = getNavigation().getNodeMaker().getDefaultNodeType(this, pos);
        if (getPathfindingPenalty(pathNodeType) != 0) return false;
        if (getWorld().getBlockState(pos.down()).getCollisionShape(getWorld(), pos.down()).isEmpty()) {
            if (this instanceof FlyingDragon flyingDragon) flyingDragon.setFlying(true);
            else return false;
        }
        BlockPos blockPos = pos.subtract(getBlockPos());
        return getWorld().isSpaceEmpty(this, getBoundingBox().offset(blockPos));
    }

    @Override //tamed big dragons should not be leashable for performance reasons (their pathfinding keeps obliterating TPS)
    public boolean canBeLeashed() {
        return isTamed();
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        updateEquipment();
    }

    public int vortexHornCapacity() {
        return 1;
    }

    //I have no idea how this happened to be so important for spawning
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0;
    }

    public void giveItemStack(ItemStack itemStack) {
        if (inventory.canInsert(itemStack)) inventory.addStack(itemStack);
        else dropStack(itemStack);
    }

    public ItemStack consumeGivenItem(@Nullable LivingEntity user, ItemStack itemStack) {
        return consumeGivenItem(user,itemStack, null);
    }

    public ItemStack consumeGivenItem(@Nullable LivingEntity user, ItemStack itemStack, @Nullable SoundEvent sound) {
        DragonOnItemConsumedEvent.EVENT.invoker().onItemConsumed(user, itemStack);
        tryApplyFoodEffects(itemStack);
        if (user == null || !user.isInCreativeMode()) itemStack.decrement(1);
        if (sound != null) playSound(sound, 1, 1);
        return itemStack;
    }

    @Override
    public boolean shouldSave() {
        if (this instanceof HeadMountDragon && getVehicle() instanceof PlayerEntity) return false;
        return super.shouldSave();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this instanceof HeadMountDragon && getVehicle() instanceof HeadMountDragonOwner owner && reason.shouldDestroy()) owner.setHeadMountDragon(new NbtCompound());
    }

    public void tryApplyFoodEffects(ItemStack itemStack) {
        if (itemStack.getComponents().contains(DataComponentTypes.FOOD)) applyFoodEffects(itemStack.getComponents().get(DataComponentTypes.FOOD));
    }

    //asset location caching so mod doesn't have to make stupid amount of checks if file even exists each frame
    private final DragonAssetCache assetCache = new DragonAssetCache();

    public DragonAssetCache getAssetCache() {
        return assetCache;
    }

    @Override
    public void onShortLeashTick(Entity entity) {
        if (this.shouldFollowLeash() && !this.isPanicking()) {
            goalSelector.enableControl(Goal.Control.MOVE);
            float distance = this.distanceTo(entity);
            Vec3d vec3d = new Vec3d(entity.getX() - getX(), entity.getY() - getY(), entity.getZ() - getZ()).normalize().multiply(Math.max(distance - 2.0F, 0.0F));
            getNavigation().startMovingAlong(getNavigation().findPathTo(BlockPos.ofFloored(getX() + vec3d.x, getY() + vec3d.y, getZ() + vec3d.z), 0, 16), getFollowLeashSpeed());
        }
    }

    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        if (yaw < 0) yaw += 360;
        float dYaw = Math.abs(MathHelper.wrapDegrees((this instanceof ShooterDragon shooterDragon ? shooterDragon.getShootingPointYaw() : getYaw()) - yaw));
        float dPitch = Math.abs((this instanceof ShooterDragon shooterDragon ? shooterDragon.getShootingPointPitch() : getPitch()) - pitch);
        return dPitch < pitchTolerance
                && dYaw % 360 < yawTolerance;
    }


    protected class JukeboxEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public JukeboxEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public PositionSource getPositionSource() {return this.positionSource;}

        public int getRange() {return this.range;}

        @Override
        public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
            if (event.matches(GameEvent.JUKEBOX_PLAY)) {
                updateJukeboxPos(BlockPos.ofFloored(emitterPos), true);
                return true;
            } else if (event.matches(GameEvent.JUKEBOX_STOP_PLAY)) {
                updateJukeboxPos(BlockPos.ofFloored(emitterPos), false);
                return true;
            } else {
                return false;
            }
        }
    }

    protected class HornUsedEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public HornUsedEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public PositionSource getPositionSource() {return this.positionSource;}

        public int getRange() {return this.range;}

        @Override
        public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
            if (event != URGameEvents.INSTRUMENT_USED) return false;
            if (!(emitter.sourceEntity() instanceof PlayerEntity player)) return false;
            if (getOwner() != player) return false;

            ItemStack stack = player.getMainHandStack();
            if (!stack.getComponents().contains(DataComponentTypes.INSTRUMENT)) stack = player.getOffHandStack();
            if (!stack.getComponents().contains(DataComponentTypes.INSTRUMENT)) return false;

            if (getInstrument(stack).equals(getBoundedInstrumentSound())) {
                setIsSitting(false);
                shouldFollow = true;
                return true;
            }
            return false;
        }
    }
}
