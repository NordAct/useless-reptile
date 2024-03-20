package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.pathfinding.DragonNavigation;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.URStatusEffects;
import nordmods.uselessreptile.common.network.InstrumentSoundBoundMessageS2CPacket;
import nordmods.uselessreptile.common.util.dragon_variant.DragonVariantUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.BiConsumer;

public abstract class URDragonEntity extends TameableEntity implements GeoEntity, NamedScreenHandlerFactory {
    public int attackType = 1;
    protected double animationSpeed = 1;
    protected float rotationProgress;
    protected float heightMod = 1;
    protected float widthMod = 1;
    protected static final int TRANSITION_TICKS = 10;
    protected int baseSecondaryAttackCooldown = 20;
    protected int basePrimaryAttackCooldown = 20;
    protected int baseAccelerationDuration = 1;
    protected float pitchLimitGround = 90;
    protected float rotationSpeedGround = 180;
    protected int primaryAttackDuration = 20;
    protected int secondaryAttackDuration = 20;
    protected int baseTamingProgress = 1;
    protected int eatFromInventoryTimer = 20;
    protected float regenFromFood = 0;
    protected boolean canNavigateInFluids = false;
    protected int ticksUntilHeal = -1;
    private int healTimer = 0;
    protected final EntityGameEventHandler<URDragonEntity.JukeboxEventListener> jukeboxEventHandler = new EntityGameEventHandler<>(new URDragonEntity.JukeboxEventListener
            (new EntityPositionSource
                    (this, getStandingEyeHeight()), GameEvent.JUKEBOX_PLAY.getRange()));
    protected @Nullable BlockPos jukeboxPos;
    private static final UUID DRAGON_ARMOR_BONUS_ID = UUID.fromString("c9e68951-e06e-4f5d-8aeb-cf3a09c2638e");
    protected SimpleInventory inventory = new SimpleInventory(URDragonScreenHandler.maxStorageSize);

    //asset location caching so mod doesn't have to make stupid amount of string operations and map references each frame
    @Environment(EnvType.CLIENT) private final AssetCache assetCache = new AssetCache();

    protected URDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        navigation = new DragonNavigation(this, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(MOVING_BACKWARDS, false);
        dataTracker.startTracking(IS_SITTING, false);
        dataTracker.startTracking(DANCING, false);
        dataTracker.startTracking(TURNING_STATE, (byte)0);//1 - left, 2 - right, 0 - straight
        dataTracker.startTracking(TAMING_PROGRESS, 1);
        dataTracker.startTracking(SPEED_MODIFIER, 1f);
        dataTracker.startTracking(MOUNTED_OFFSET, 0.35f);
        dataTracker.startTracking(HEIGHT_MODIFIER, 1f);
        dataTracker.startTracking(WIDTH_MODIFIER, 1f);
        dataTracker.startTracking(SECONDARY_ATTACK_COOLDOWN, 0);
        dataTracker.startTracking(PRIMARY_ATTACK_COOLDOWN, 0);
        dataTracker.startTracking(ACCELERATION_DURATION, 0);
        dataTracker.startTracking(BOUNDED_INSTRUMENT_SOUND, "");
        dataTracker.startTracking(VARIANT, "");
    }

    public static final TrackedData<Boolean> MOVING_BACKWARDS = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_SITTING = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> DANCING = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Byte> TURNING_STATE = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> TAMING_PROGRESS = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Float> SPEED_MODIFIER = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> MOUNTED_OFFSET = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> HEIGHT_MODIFIER = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> WIDTH_MODIFIER = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> SECONDARY_ATTACK_COOLDOWN = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> PRIMARY_ATTACK_COOLDOWN = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> ACCELERATION_DURATION = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<String> BOUNDED_INSTRUMENT_SOUND = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<String> VARIANT = DataTracker.registerData(URDragonEntity.class, TrackedDataHandlerRegistry.STRING);

    public boolean isSecondaryAttack() {return getSecondaryAttackCooldown() > getMaxSecondaryAttackCooldown() - secondaryAttackDuration;} //old melee
    public int getSecondaryAttackCooldown() {return  dataTracker.get(SECONDARY_ATTACK_COOLDOWN);}
    public void setSecondaryAttackCooldown(int state) {dataTracker.set(SECONDARY_ATTACK_COOLDOWN, state);}

    public boolean isPrimaryAttack() {return getPrimaryAttackCooldown() > getMaxPrimaryAttackCooldown() - primaryAttackDuration;} //old range
    public void setPrimaryAttackCooldown(int state) {dataTracker.set(PRIMARY_ATTACK_COOLDOWN, state);}
    public int getPrimaryAttackCooldown() {return  dataTracker.get(PRIMARY_ATTACK_COOLDOWN);}

    public int getAccelerationDuration() {return dataTracker.get(ACCELERATION_DURATION);}
    public void setAccelerationDuration(int state) {dataTracker.set(ACCELERATION_DURATION, state);}

    public boolean isMovingBackwards() {return dataTracker.get(MOVING_BACKWARDS);}
    public void setMovingBackwards(boolean state) {dataTracker.set(MOVING_BACKWARDS, state);}

    public boolean isDancing() {return dataTracker.get(DANCING);}
    public void setDancing(boolean state) {dataTracker.set(DANCING, state);}

    public boolean isMoving() {return getVelocity().getZ() != 0 || getVelocity().getX() != 0;}

    public boolean getIsSitting() {return dataTracker.get(IS_SITTING);}
    public void setIsSitting(boolean state) {
        dataTracker.set(IS_SITTING, state);
        setSitting(state);
        setTarget(null);
    }

    public String getVariant() {return dataTracker.get(VARIANT);}
    public void setVariant(String state) {dataTracker.set(VARIANT, state);}

    public byte getTurningState() {return dataTracker.get(TURNING_STATE);}
    public void setTurningState(byte state) {dataTracker.set(TURNING_STATE, state);}

    public int getTamingProgress() {return dataTracker.get(TAMING_PROGRESS);}
    public void setTamingProgress(int state) {dataTracker.set(TAMING_PROGRESS, state);}

    public float getSpeedMod() {return dataTracker.get(SPEED_MODIFIER);}
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
            tag.putBoolean("Sitting", getIsSitting());
            tag.putString("BoundedInstrumentSound", getBoundedInstrumentSound());
        }
        if (inventory != null && isTamed()) {
            final NbtList inv = new NbtList();
            for (int i = 0; i < inventory.size(); i++) {
                inv.add(inventory.getStack(i).writeNbt(new NbtCompound()));
            }
            tag.put("Inventory", inv);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        dataTracker.set(VARIANT, tag.getString("Variant"));

        if (!isTamed()) setTamingProgress(tag.getByte("TamingProgress"));
        else setBoundedInstrumentSound(tag.getString("BoundedInstrumentSound"));

        setIsSitting(tag.getBoolean("Sitting"));
        if (tag.contains("Inventory")) {
            final NbtList inv = tag.getList("Inventory", 10);
            inventory = new SimpleInventory(inv.size());
            for (int i = 0; i < inv.size(); i++) {
                inventory.setStack(i, ItemStack.fromNbt(inv.getCompound(i)));
            }
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (getWorld().isClient)
            if (CUSTOM_NAME.equals(data) || VARIANT.equals(data)) assetCache.cleanCache();
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = new PassiveData(false);
        setTamingProgress(baseTamingProgress);
        DragonVariantUtil.assignVariant(world, this);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    //I can't believe that this yarn bug is still a thing
    @Override
    public EntityView method_48926() {
        return getWorld();
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
        public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
            Vec3i vec3i;
            if (emitterPos != null) vec3i = new Vec3i((int) emitterPos.x, (int) emitterPos.y, (int) emitterPos.z);
            else return false;

            boolean isJukebox = false;
            if (URDragonEntity.this.jukeboxPos != null) isJukebox = world.getBlockState(URDragonEntity.this.jukeboxPos).isOf(Blocks.JUKEBOX);
            if (event == GameEvent.JUKEBOX_PLAY) {
                URDragonEntity.this.updateJukeboxPos(new BlockPos(vec3i), true);
                return true;
            } else if (event == GameEvent.JUKEBOX_STOP_PLAY || !isJukebox) {
                URDragonEntity.this.updateJukeboxPos(new BlockPos(vec3i), false);
                return true;
            } else {
                return false;
            }
        }
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        World var3 = this.getWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            callback.accept(this.jukeboxEventHandler, serverWorld);
        }

    }

    public void updateJukeboxPos(BlockPos jukeboxPos, boolean playing) {
        if (playing) {
            if (!this.isDancing()) {
                this.jukeboxPos = jukeboxPos;
                this.setDancing(this.getTarget() == null);
            }
        } else {
            this.jukeboxPos = null;
            this.setDancing(false);
        }

    }

    protected void updateEquipment() {}

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(widthMod, heightMod);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (isTamed()) {
            if (isFavoriteFood(itemStack) && getHealth() != getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH)) {
                eatFood(getWorld(), itemStack);
                heal(regenFromFood);
                return ActionResult.SUCCESS;
            }
        }

        if (isTamed() && isOwnerOrCreative(player)) {
            if (itemStack.isOf(Items.POTION) && player.isSneaking()) {
                boolean hasHarmful = false;
                boolean hasBeneficial = false;
                for (StatusEffectInstance effect : PotionUtil.getPotionEffects(itemStack)) {
                    StatusEffectCategory effectType = effect.getEffectType().getCategory();
                    if (effectType == StatusEffectCategory.BENEFICIAL) hasBeneficial = true;
                    if (effectType == StatusEffectCategory.HARMFUL) hasHarmful = true;
                }
                if (hasBeneficial || !hasHarmful) {
                    //we make the copy cuz else for some fucking reason minecraft will subtract effect time from potion itself too
                    //делаем копию т.к. иначе по какой-то ебейшей причине кубач будет убавлять время действия эффектов самого зелья также
                    for (StatusEffectInstance effect : PotionUtil.getPotionEffects(itemStack)) addStatusEffect(new StatusEffectInstance(effect));
                    if (!player.isCreative()) player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                    playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1, 1);
                    return ActionResult.SUCCESS;
                }
            }

            if (isInstrument(itemStack) && !player.isSneaking()) {
                String sound = getInstrument(itemStack);
                if (!getBoundedInstrumentSound().equals(sound)) setBoundedInstrumentSound(sound);
                else setBoundedInstrumentSound("");
                //Takes instrument tag "namespace:key" and converts it into "instrument.namespace.key" aka translation key if sound is bounded
                Text instrumentSound = Text.translatable(getBoundedInstrumentSound().isEmpty() ?
                        "other.uselessreptile.none" : getInstrumentSoundKey(getBoundedInstrumentSound()));
                if (player instanceof ServerPlayerEntity serverPlayer) InstrumentSoundBoundMessageS2CPacket.send(serverPlayer, this, instrumentSound);
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
        }
        return ActionResult.PASS;
    }

    protected boolean isInteractableItem(ItemStack itemStack) {
        return itemStack.isOf(Items.POTION) || itemStack.isOf(Items.STICK) || isInstrument(itemStack) || isFavoriteFood(itemStack);
    }

    private String getInstrumentSoundKey(String sound) {
        int pos = sound.indexOf(":");
        String key = sound.substring(pos + 1);
        String namespace = sound.substring(0, pos);
        return "instrument." + namespace + "." + key;
    }

    public boolean isInstrument(ItemStack itemStack) {
        return itemStack.getNbt() != null && itemStack.getNbt().contains("instrument");
    }

    public String getInstrument(ItemStack itemStack) {
        return itemStack.getNbt().getString("instrument");
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) getWorld().playSound(getX(), getY(),getZ(), sound, SoundCategory.NEUTRAL, volume, pitch,true);
    }

    public float getWidthModTransSpeed() {
        return (float) (0.22 * animationSpeed);
    }
    public float getHeightModTransSpeed() {
        return (float) (0.3 * animationSpeed);
    }
    public float getMountedOffsetTransSpeed() {
        return (float) (0.125 * animationSpeed);
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        float currentYaw = getYaw() % 360;
        float destinationYaw = yaw % 360;
        //т.к. у игрока поворот измеряется от -180 до 180, а у других энтити от 0 до 360, то приведенная ниже дичь необходима
        //due player having rotation from -180 to 180 while all other entities have it from 0 to 360, this check is necessary
        if (destinationYaw < 0) destinationYaw += 360;
        float yawDiff = currentYaw - destinationYaw;
        if (yawDiff != 0) {
            if (yawDiff > 180) yawDiff -= 360;
            else if (yawDiff < -180) yawDiff +=360;

            if (yawDiff < -getRotationSpeed()) {
                currentYaw += getRotationSpeed();
                setTurningState((byte)2);
            }
            else if (yawDiff > getRotationSpeed()) {
                currentYaw -= getRotationSpeed();
                setTurningState((byte)1);
            }
            else currentYaw = destinationYaw;

        } else {
            setTurningState((byte)0);
        }
        prevYaw = bodyYaw = getYaw();
        super.setRotation(currentYaw, pitch);
        headYaw = currentYaw;
    }

    protected void setHitboxModifiers(float destinationHeight, float destinationWidth, float destinationMountedOffset) {
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

        this.heightMod = heightMod;
        this.widthMod = widthMod;
        calculateDimensions();
    }

    public float getRotationSpeed() {
        return rotationSpeedGround * calcSpeedMod();
    }
    public float getPitchLimit() {
        return pitchLimitGround;
    }

    public float getMaxAccelerationDuration() {
        return baseAccelerationDuration * calcSpeedMod();
    }

    protected float calcCooldownMod() {
        float mod = 1;
        if (hasStatusEffect(StatusEffects.SLOWNESS)) mod *= 1 + 0.1 * (getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() + 1);
        if (hasStatusEffect(StatusEffects.SPEED)) mod *= 1 - 0.1 * MathHelper.clamp(getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1, 1, 9);
        if (hasStatusEffect(URStatusEffects.SHOCK)) mod /= 2;
        return mod;
    }

    protected float calcSpeedMod() {
        double baseSpeed = getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        return (float) (getMovementSpeed() / baseSpeed);
    }

    public int getMaxSecondaryAttackCooldown() {
        return (int) (baseSecondaryAttackCooldown * calcCooldownMod());
    }
    public int getMaxPrimaryAttackCooldown() {
        return  (int) (basePrimaryAttackCooldown * calcCooldownMod());
    }

    @Override
    public void tick() {
        super.tick();
        updateEquipment();
        updateRotationProgress();
        animationSpeed = calcSpeedMod();
        setStepHeight(1);

        if (getSecondaryAttackCooldown() > 0) setSecondaryAttackCooldown(getSecondaryAttackCooldown() - 1);
        if (getPrimaryAttackCooldown() > 0) setPrimaryAttackCooldown(getPrimaryAttackCooldown() - 1);

        if (ticksUntilHeal > -1 && --healTimer <= 0) {
            heal(1);
            healTimer = getTicksUntilHeal();
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isTamed() && this.age > 2400;
    }

    protected boolean isOwnerOrCreative(PlayerEntity player) {return isOwner(player) || player.isCreative();}

    protected void updateArmorBonus(int armorBonus) {
        if (!getWorld().isClient()) {
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(DRAGON_ARMOR_BONUS_ID);
            if (armorBonus != 0) {
                getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(DRAGON_ARMOR_BONUS_ID, "Dragon armor bonus", armorBonus, EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    @SuppressWarnings("SameReturnValue")
    protected <A extends GeoEntity> PlayState loopAnim(String anim, software.bernie.geckolib.core.animation.AnimationState<A> event) {
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

    public boolean isTargetFriendly(LivingEntity target) {
        if (target instanceof TameableEntity tameable) return tameable.getOwner() == getOwner();
        if (target instanceof PlayerEntity player) return player == getOwner();
        return false;
    }

    public boolean isClientSpectator() {
        if (MinecraftClient.getInstance().player != null) return MinecraftClient.getInstance().player.isSpectator();
        else return false;
    }

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, getMountedOffset(), 0.0F);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (inventory != null) {
            for(int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
                    dropStack(itemStack);
                }
            }

        }
    }

    protected void updateBanner() {
        if (isTamed() && inventory != null) {
            ItemStack banner = inventory.getStack(4);
            if (banner.getItem() instanceof BannerItem) equipStack(EquipmentSlot.OFFHAND, banner);
        }
    }

    public boolean isFavoriteFood(ItemStack itemStack){
        return false;
    }

    public boolean isTamingItem(ItemStack itemStack){
        return isFavoriteFood(itemStack);
    }

    public float getHealthRegenFromFood() {
        return regenFromFood;
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
        return getTarget() != null && getTarget().isInsideWaterOrBubbleColumn() && canNavigateInFluids;
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
                if (rotationProgress < TRANSITION_TICKS) rotationProgress++;
            }
            case 2 -> {
                if (rotationProgress > -TRANSITION_TICKS) rotationProgress--;
            }
            default -> {
                if (rotationProgress != 0) {
                    if (rotationProgress > 0) rotationProgress--;
                    else  rotationProgress++;
                }
            }
        }
    }

    //making public for sake of debug render
    @Override
    public Box getAttackBox() {
        return super.getAttackBox();
    }

    public Box getSecondaryAttackBox() {
        return null;
    }

    protected static URMobAttributesConfig getAttributeConfig() {
        return URMobAttributesConfig.getConfig();
    }

    protected int getTicksUntilHeal() {
        return ticksUntilHeal;
    }

    //I give no fuck how this happened to be so important for spawning
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0;
    }

    @Environment(EnvType.CLIENT)
    public AssetCache getAssetCache() {
        return assetCache;
    }
}
