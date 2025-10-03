package nordmods.uselessreptile.common.entity.base;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.CommonProtection;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
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
import net.minecraft.inventory.StackReference;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.AssetCahceOwner;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnUtil;
import nordmods.uselessreptile.common.entity.ai.control.DragonLookControl;
import nordmods.uselessreptile.common.entity.ai.control.LandDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.navigation.DragonNavigation;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import nordmods.uselessreptile.common.event.DragonOnItemConsumedEvent;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.item.VortexHornItem;
import nordmods.uselessreptile.common.network.URPacketHelper;
import nordmods.uselessreptile.common.util.duck.HeadMountDragonOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.event.KeyFrameEvent;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class URDragonEntity extends TameableEntity implements GeoEntity, NamedScreenHandlerFactory, AssetCahceOwner, InventoryChangedListener {
    protected double animationSpeed = 1;
    public static final int TRANSITION_TICKS = 10;
    protected float pitchLimitGround = 90;
    protected int primaryAttackDuration = 20;
    protected int secondaryAttackDuration = 20;
    protected int specialAttackDuration = 20;
    protected int eatFromInventoryTimer = 20;
    protected boolean canNavigateInFluids = false;
    protected int ticksUntilHeal = -1;
    protected float sprintSpeedModifier = 1.1f;
    protected float backwardSpeedModifier = 0.6f;
    private int healTimer = 0;
    private BlockPos homePoint = BlockPos.ORIGIN;
    protected final EntityGameEventHandler<JukeboxEventListener> jukeboxEventHandler = new EntityGameEventHandler<>(new JukeboxEventListener
            (new EntityPositionSource(this, getStandingEyeHeight()), GameEvent.JUKEBOX_PLAY.value().notificationRadius()));
    protected final EntityGameEventHandler<HornUsedEventListener> hornUsedEventHandler = new EntityGameEventHandler<>(new HornUsedEventListener
            (new EntityPositionSource(this, getStandingEyeHeight()), URGameEvents.INSTRUMENT_USED.value().notificationRadius()));
    protected @Nullable BlockPos jukeboxPos;
    private final DragonInventory inventory;
    public boolean shouldFollow = false;
    protected Text defaultDisplayName;
    public static final Map<EntityType<?>, Map<String ,Map<String, SoundInfo>>> SOUND_INFO_HOLDER = new HashMap<>();
    public static final Identifier VARIANT_BONUS_MODIFIER = UselessReptile.id("variant_bonus");
    public static final Identifier SPEED_MODIFIER_BONUS = UselessReptile.id("speed_modifier");


    protected URDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        navigation = new DragonNavigation(this, world);
        lookControl = new DragonLookControl(this);
        moveControl = new LandDragonMoveControl<>(this);
        inventory = createInventory();
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

    @Override
    public boolean isSitting() {return dataTracker.get(IS_SITTING);}

    @Override
    public void setSitting(boolean state) {
        dataTracker.set(IS_SITTING, state);
        super.setSitting(state);
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

    public float getMountedOffset() {return dataTracker.get(MOUNTED_OFFSET);}
    public void setMountedOffset(float state) {dataTracker.set(MOUNTED_OFFSET, state);}

    public float getHeightMod() {return dataTracker.get(HEIGHT_MODIFIER);}
    public void setHeightMod(float state) {dataTracker.set(HEIGHT_MODIFIER, state);}

    public float getWidthMod() {return dataTracker.get(WIDTH_MODIFIER);}
    public void setWidthMod(float state) {dataTracker.set(WIDTH_MODIFIER, state);}

    public String getBoundedInstrumentSound() {return  dataTracker.get(BOUNDED_INSTRUMENT_SOUND);}
    public void setBoundedInstrumentSound(String state) {dataTracker.set(BOUNDED_INSTRUMENT_SOUND, state);}

    @Override
    public void writeCustomData(WriteView tag) {
        super.writeCustomData(tag);
        tag.putString("Variant", getVariant());

        int[] coords = {getHomePoint().getX(), getHomePoint().getY(), getHomePoint().getZ()};
        tag.putIntArray("HomePoint", coords);

        if (!isTamed()) tag.putInt("TamingProgress", getTamingProgress());
        else tag.putString("BoundedInstrumentSound", getBoundedInstrumentSound());

        tag.putBoolean("Sitting", isSitting());
        if (inventory != null && isTamed()) {
            WriteView.ListAppender<StackWithSlot> listAppender = tag.getListAppender("Inventory", StackWithSlot.CODEC);
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) listAppender.add(new StackWithSlot(i, stack));
            }
        }
    }

    @Override
    public void readCustomData(ReadView tag) {
        super.readCustomData(tag);
        dataTracker.set(VARIANT, tag.getString("Variant", getDefaultVariant()));

        int[] coords = tag.getOptionalIntArray("HomePoint").orElse(new int[] {getBlockX(), getBlockY(), getBlockZ()});
        if (coords.length == 0) setHomePoint(getBlockPos());
        else setHomePoint(new BlockPos(coords[0], coords[1], coords[2]));

        if (!isTamed()) setTamingProgress(tag.getInt("TamingProgress", getBaseTamingProgress()));
        else setBoundedInstrumentSound(tag.getString("BoundedInstrumentSound", ""));

        setSitting(tag.getBoolean("Sitting", false));

        for (StackWithSlot stackWithSlot : tag.getTypedListView("Inventory", StackWithSlot.CODEC)) {
            if (stackWithSlot.isValidSlot(this.inventory.size())) {
                inventory.setStack(stackWithSlot.slot(), stackWithSlot.stack());
            }
        }
        inventory.addListener(this);

        updateEquipment();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (CUSTOM_NAME.equals(data) || VARIANT.equals(data)) {
            assetCache.cleanCache();
        }
        if (VARIANT.equals(data)) {
            removeVariantModifiers();
            applyVariantModifiers();
            defaultDisplayName = null;
        }
    }

    private void applyVariantModifiers() {
        DragonVariant variant = DragonVariant.getByVariant(getDragonId(), getVariant(), getEntityWorld());
        if (variant == null) {
            UselessReptile.LOGGER.warn("Couldn't find any info on variant {} ({}), thus variant modifiers cannot be set", getVariant(), getDragonId());
            return;
        }

        variant.variantAttributeModifiers().ifPresent(id -> {
            List<EntityAttributeModifier> modifiers = getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT_ATTRIBUTE_MODIFIERS).get(id);
            if (modifiers != null) modifiers.forEach(entityAttributeModifier -> {
                EntityAttribute attribute = getRegistryManager().getOrThrow(RegistryKeys.ATTRIBUTE).get(entityAttributeModifier.id());
                if (attribute != null) {
                    EntityAttributeInstance entityAttributeInstance = getAttributeInstance(
                            getRegistryManager()
                                    .getOrThrow(RegistryKeys.ATTRIBUTE)
                                    .getEntry(entityAttributeModifier.id()).get()
                    );
                    if (entityAttributeInstance != null && !entityAttributeInstance.hasModifier(VARIANT_BONUS_MODIFIER))
                        entityAttributeInstance.addTemporaryModifier(new EntityAttributeModifier(VARIANT_BONUS_MODIFIER, entityAttributeModifier.value(), entityAttributeModifier.operation()));

                }
            });
        });
    }

    private void removeVariantModifiers() {
        AttributeContainer container = getAttributes();
        getRegistryManager().getOrThrow(RegistryKeys.ATTRIBUTE).streamEntries().forEach(entityAttributeReference -> {
            if (container.hasAttribute(entityAttributeReference))
                container.getCustomInstance(entityAttributeReference).removeModifier(VARIANT_BONUS_MODIFIER);
        });
    }

    @Nullable
    public SoundInfo getSoundInfo(String name) {
        Map<String ,Map<String, SoundInfo>> variantMap = SOUND_INFO_HOLDER.get(getType());
        if (variantMap != null) {
            Map<String, SoundInfo> soundMap = variantMap.get(getVariant());
            if (soundMap != null) {
                if (soundMap.containsKey(name)) return soundMap.get(name);
                else {
                    SoundInfo info = createSoundInfo(name);
                    soundMap.put(name, info);
                    return info;
                }
            } else {
                soundMap = new HashMap<>();
                SoundInfo info = createSoundInfo(name);
                soundMap.put(name, info);
                variantMap.put(getVariant(), soundMap);
                return info;
            }
        } else {
            variantMap = new HashMap<>();
            Map<String, SoundInfo> soundMap = new HashMap<>();
            SoundInfo info = createSoundInfo(name);
            soundMap.put(name, info);
            variantMap.put(getVariant(), soundMap);
            SOUND_INFO_HOLDER.put(getType(), variantMap);
            return info;
        }
    }

    private SoundInfo createSoundInfo(String name) {
        DragonModel model = DragonVariantUtil.getDragonModelData(getDragonId(), hasCustomName() ? getCustomName().getString() : null, getVariant(), getEntityWorld());
        if (model != null) {
            if (model.sounds().isPresent()) {
                DragonModel.Sound sound = model.sounds().get().stream()
                        .filter(s -> s.name().equals(name))
                        .findFirst()
                        .orElse(null);

                if (sound != null) return new SoundInfo(
                        sound.id(),
                        sound.volume().orElse(1f),
                        sound.pitch().orElse(1f),
                        sound.pitchDeviation().orElse(0.125f)
                );
                else {
                    UselessReptile.LOGGER.warn("Sound {} is not defined for {} ({}) of variant {}.", name, getName().getString(), getDragonId(), getVariant());
                }
            } else {
                UselessReptile.LOGGER.warn("Could not find sound {} for {} ({}) of variant {} as no sounds are defined.", name, getName().getString(), getDragonId(), getVariant());
            }
        }
        return null;
    }


    protected <ENTITY extends GeoEntity> void soundHandler(KeyFrameEvent<ENTITY, SoundKeyframeData> event) {
        SoundInfo soundInfo = getSoundInfo(event.keyframeData().getSound());
        if (soundInfo != null) playSound(SoundEvent.of(soundInfo.id()), soundInfo.volume(), getRandom().nextTriangular(soundInfo.pitch(), soundInfo.pitchDeviation()));
    }

    @Override
    public double getBoneResetTime() {
        return TRANSITION_TICKS;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = new PassiveData(false);
        DragonSpawnUtil.assignAvailableVariant(this, spawnReason);
        setTamingProgress(getBaseTamingProgress());
        setHomePoint(getBlockPos());
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    protected static DefaultAttributeContainer.Builder createDragonAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.STEP_HEIGHT, 1)
                .add(EntityAttributes.ATTACK_DAMAGE)
                .add(EntityAttributes.ATTACK_KNOCKBACK)
                .add(EntityAttributes.MAX_HEALTH)
                .add(EntityAttributes.ARMOR)
                .add(EntityAttributes.ARMOR_TOUGHNESS)
                .add(EntityAttributes.MOVEMENT_SPEED)
                .add(EntityAttributes.FLYING_SPEED)
                .add(EntityAttributes.FOLLOW_RANGE, 160)
                .add(EntityAttributes.JUMP_STRENGTH)
                .add(URAttributes.DRAGON_VERTICAL_SPEED)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN)
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
    public void setTamedBy(PlayerEntity entity) {
        super.setTamedBy(entity);
        setHomePoint(getBlockPos());
    }

    @Override
    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        if (getEntityWorld() instanceof ServerWorld serverWorld) {
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

            if (getOwner() instanceof ServerPlayerEntity serverPlayer) { //TODO i really should unhardcode this...
                if ((head.isOf(URItems.DRAGON_HELMET_DIAMOND) || head.isOf(URItems.MOLECLAW_HELMET_DIAMOND))
                        && body.isOf(URItems.DRAGON_CHESTPLATE_DIAMOND)
                        && tail.isOf(URItems.DRAGON_TAIL_ARMOR_DIAMOND)) {
                    grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/equip_full_diamond_dragon_armor"));
                }

                if ((head.isOf(URItems.DRAGON_HELMET_NETHERITE) || head.isOf(URItems.MOLECLAW_HELMET_NETHERITE))
                        && body.isOf(URItems.DRAGON_CHESTPLATE_NETHERITE)
                        && tail.isOf(URItems.DRAGON_TAIL_ARMOR_NETHERITE)) {
                    grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/equip_full_netherite_dragon_armor"));
                }
            }
        }
    }

    public static boolean canDragonSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return DragonSpawnUtil.getAvailableVariants(world, pos, EntityType.getId(type)).findFirst().isPresent();
    }

    @Override
    public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
        boolean empty = newStack.isEmpty() && oldStack.isEmpty();
        if (!empty && !ItemStack.areItemsAndComponentsEqual(oldStack, newStack) && !firstUpdate) {
            if (!getEntityWorld().isClient() && isArmorSlot(slot))
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
        if (isTameable()) {
            DragonVariant.TamingItem tamingItem = getTamingItem(itemStack);
            if (tamingItem != null) {
                consumeGivenItem(player, itemStack, SoundEvents.ENTITY_GENERIC_EAT.value(), hand);
                if (player.isCreative()) setTamingProgress(0);
                else setTamingProgress(getTamingProgress() - random.nextBetween(tamingItem.tamingProgressIncrease().getFirst(), tamingItem.tamingProgressIncrease().getSecond()));
                if (getTamingProgress() <= 0) {
                    setTamedBy(player);
                    getEntityWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                } else {
                    getEntityWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
                }
                setPersistent();
                return ActionResult.SUCCESS;
            }
        }

        if (isTamed()) {
            if (getHealth() != getMaxHealth()) {
                DragonVariant.FoodItem foodItem = getFoodItem(itemStack);
                if (foodItem != null) {
                    consumeGivenItem(player, itemStack, SoundEvents.ENTITY_GENERIC_EAT.value(), hand);
                    heal(foodItem.healingAmount());
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (isTamed() && isOwner(player)) {
            if (this instanceof HeadMountDragon && player.isSneaking() && itemStack.isEmpty()) {
                detachLeash();
                startRiding(player);
                return ActionResult.SUCCESS;
            }

            if (itemStack.getItem() instanceof PotionItem potionItem && player.isSneaking()) {
                ItemStack original = itemStack.copy();
                potionItem.finishUsing(itemStack, getEntityWorld(), this);
                consumeGivenItem(player, original, SoundEvents.ENTITY_GENERIC_DRINK.value(), hand);
                if (player instanceof ServerPlayerEntity serverPlayer)
                    grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/give_potion"));
                return ActionResult.SUCCESS;
            }

            if (isInstrument(itemStack) && !player.isSneaking() && !(itemStack.getItem() instanceof VortexHornItem)) {
                String sound = getInstrument(itemStack);
                if (!getBoundedInstrumentSound().equals(sound)) setBoundedInstrumentSound(sound);
                else setBoundedInstrumentSound("");
                Text instrumentSound = Text.translatable(getBoundedInstrumentSound().isEmpty() ?
                        "other.uselessreptile.none" : getBoundedInstrumentSound()); //might fetch keys for non-vanilla instruments incorrectly
                if (!getEntityWorld().isClient()) player.sendMessage(Text.translatable("other.uselessreptile.sound_respond", getName(), instrumentSound), true);
                if (getEntityWorld().isClient()) player.playSound(SoundEvents.BLOCK_COMPARATOR_CLICK, 0.2f, 2);
                return ActionResult.SUCCESS;
            }

            if ((itemStack.isOf(Items.STICK) || isInstrument(itemStack)) && player.isSneaking()) {
                if (isSitting()) setSitting(false);
                else {
                    setSitting(true);
                    getNavigation().stop();
                    if (player instanceof ServerPlayerEntity serverPlayer)
                        grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/sit_down_dragon"));
                }
                return ActionResult.SUCCESS;
            }

            if (player.isSneaking() && inventory.size() > 0) {
                if (!getEntityWorld().isClient())
                    player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force, boolean event) {
        boolean result = super.startRiding(entity, force, event);
        if (this instanceof HeadMountDragon && result && entity instanceof HeadMountDragonOwner owner) {
            NbtWriteView nbtWriteView = NbtWriteView.create(UselessReptile.ERROR_REPORTER, entity.getRegistryManager());
            saveSelfData(nbtWriteView);
            owner.setHeadMountDragon(nbtWriteView.getNbt());
            setPortalCooldown(0);
        }
        return result;
    }

    @Override
    public void stopRiding() {
        if (this instanceof HeadMountDragon && getVehicle() instanceof HeadMountDragonOwner owner) {
            if (owner instanceof ServerPlayerEntity player && player.isDisconnected()) return;
            owner.setHeadMountDragon(new NbtCompound());
            setYaw(((Entity) owner).getYaw() + 180f);
        }
        super.stopRiding();
    }

    protected boolean isInteractableItem(ItemStack itemStack) {
        return itemStack.isOf(Items.POTION) || itemStack.isOf(Items.STICK) || isInstrument(itemStack) || getFoodItem(itemStack) != null;
    }

    public boolean isInstrument(ItemStack itemStack) {
        return itemStack.getComponents().contains(DataComponentTypes.INSTRUMENT);
    }

    public String getInstrument(ItemStack itemStack) {
        if (!itemStack.getComponents().contains(DataComponentTypes.INSTRUMENT)) return "";
        Optional<RegistryEntry<Instrument>> instrument = itemStack.getComponents().get(DataComponentTypes.INSTRUMENT).getInstrument(getEntityWorld().getRegistryManager());
        if (instrument.isPresent()) {
            boolean translatable = instrument.get().value().description().getContent() instanceof TranslatableTextContent;
            return translatable ? ((TranslatableTextContent) instrument.get().value().description().getContent()).getKey() : ((PlainTextContent)instrument.get().value().description().getContent()).string();
        }
        return "";
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) getEntityWorld().playSoundClient(getX(), getY(),getZ(), sound, SoundCategory.NEUTRAL, volume, pitch,true);
    }

    public float getWidthModTransSpeed() {
        return 0.22f * getScale();
    }
    public float getHeightModTransSpeed() {
        return (float) (0.3 * getScale());
    }
    public float getMountedOffsetTransSpeed() {
        return (float) (0.125 * getScale());
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
                if (!getEntityWorld().isClient()) setTurningState((byte)2);
            }
            else if (yawDiff > getRotationSpeed()) {
                currentYaw -= getRotationSpeed();
                if (!getEntityWorld().isClient()) setTurningState((byte)1);
            }
            else {
                currentYaw = destinationYaw;
                if (!getEntityWorld().isClient() && isMoving()) setTurningState((byte)0);
            }
        } else {
            if (!getEntityWorld().isClient()) setTurningState((byte)0);
        }
        lastYaw = bodyYaw = getYaw();
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
        double baseSpeed = getBaseGroundSpeed();
        double speed = getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
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
        if (!getEntityWorld().isClient()) {
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
                if (getEntityWorld().isClient()) {
                    lastYaw = getYaw();
                    setYaw(player.getYaw());
                    byte turnState = 0;
                    float diff = lastYaw - getYaw();
                    if (diff > 0) turnState = 1;
                    if (diff < 0) turnState = 2;
                    setTurningState(turnState);
                }
            } else getLookControl().setLockRotation(false);
        }

        if (!getEntityWorld().isClient() && age % 20 == 0) {
            boolean jukeboxReachable = false;
            if (jukeboxPos != null) jukeboxReachable = jukeboxPos.isWithinDistance(getBlockPos(), 9);
            if (jukeboxReachable) updateJukeboxPos(jukeboxPos, true);
            else updateJukeboxPos(null, false);
        }
    }

    protected void updateAnimationSpeed() {
        animationSpeed = MathHelper.lerp(1f/getEntityWorld().getTickManager().getTickRate(), animationSpeed, getMovementSpeedModifier());
    }

    @Override
    public void travel(Vec3d movementInput) {
        updateMovementModifiers();
        super.travel(movementInput);
    }

    public void updateMovementModifiers() {
        if ((!isMoving())) setSprinting(false);
        float speedModifier = 1;
        if (isSprinting()) speedModifier = sprintSpeedModifier;
        else if (isMovingBackwards()) speedModifier = backwardSpeedModifier;

        EntityAttributeInstance instance = getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedModifier != 1f) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(SPEED_MODIFIER_BONUS, speedModifier - 1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            if (!instance.hasModifier(modifier.id())) instance.addTemporaryModifier(modifier);
            else instance.updateModifier(modifier);
        } else instance.removeModifier(SPEED_MODIFIER_BONUS);

        float speed = (float) getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
        setMovementSpeed(speed * speedModifier);
    }

    protected abstract float getBaseGroundSpeed();

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isTamed() && this.age > 2400;
    }

    @SuppressWarnings("SameReturnValue")
    protected <A extends GeoEntity> PlayState loopAnim(String anim, AnimationTest<A> event) {
        event.controller().setAnimation(RawAnimation.begin().thenLoop(anim)); return PlayState.CONTINUE;
    }

    @SuppressWarnings("SameReturnValue")
    protected <A extends GeoEntity> PlayState playAnim(String anim, AnimationTest<A> event) {
        event.controller().setAnimation(RawAnimation.begin().then(anim, Animation.LoopType.PLAY_ONCE)); return PlayState.CONTINUE;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    @Deprecated
    protected SoundEvent getAmbientSound() {
        return null;
    }

    public void playAmbientSound() {
        SoundInfo soundInfo = getSoundInfo("idle");
        if (soundInfo != null) playSound(SoundEvent.of(soundInfo.id()), soundInfo.volume(), getRandom().nextTriangular(soundInfo.pitch(), soundInfo.pitchDeviation()));
    }

    @Override
    @Deprecated
    protected SoundEvent getHurtSound(DamageSource source) {
        playHurtSound(source); //don't ask
        return null;
    }

    @Override
    protected void playHurtSound(DamageSource damageSource) {
        SoundInfo soundInfo = getSoundInfo("hurt");
        if (soundInfo != null) {
            ambientSoundChance = -getMinAmbientSoundDelay();
            playSound(SoundEvent.of(soundInfo.id()), soundInfo.volume(), getRandom().nextTriangular(soundInfo.pitch(), soundInfo.pitchDeviation()));
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        SoundInfo soundInfo = getSoundInfo("death");
        if (soundInfo != null) playSound(SoundEvent.of(soundInfo.id()), soundInfo.volume(), getRandom().nextTriangular(soundInfo.pitch(), soundInfo.pitchDeviation()));
        return null;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean canTarget(@Nullable LivingEntity target) {
        if (target == null) return false;
        if (isSitting()) return false;
        if (getOwner() != null && target instanceof Tameable tameable && tameable.getOwner() == getOwner()) return false;
        return super.canTarget(target);
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vec3d(0, getMountedOffset(), 0);
    }

    @Override
    protected void dropInventory(ServerWorld world) {
        super.dropInventory(world);
        if (inventory != null) {
            for(int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                    dropStack(world, itemStack);
                }
            }

        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Nullable
    public DragonVariant.TamingItem getTamingItem(ItemStack itemStack) {
        DragonVariant variant = DragonVariant.getByVariant(getDragonId(), getVariant(), getEntityWorld());
        if (variant != null) {
            return variant.tamingItems().orElse(List.of()).stream()
                    .filter(tamingItem -> {
                        Codecs.TagEntryId entryId = tamingItem.item();
                        if (entryId.tag()) return itemStack.isIn(TagKey.of(RegistryKeys.ITEM, entryId.id()));
                        return entryId.id().equals(itemStack.getItem().getRegistryEntry().registryKey().getValue());
                    })
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Nullable
    public DragonVariant.FoodItem getFoodItem(ItemStack itemStack) {
        DragonVariant variant = DragonVariant.getByVariant(getDragonId(), getVariant(), getEntityWorld());
        if (variant != null) {
            return variant.foodItems().orElse(List.of()).stream()
                    .filter(tamingItem -> {
                        Codecs.TagEntryId entryId = tamingItem.item();
                        if (entryId.tag()) return itemStack.isIn(TagKey.of(RegistryKeys.ITEM, entryId.id()));
                        return entryId.id().equals(itemStack.getItem().getRegistryEntry().registryKey().getValue());
                    })
                    .findFirst()
                    .orElse(null);
        }
        return null;
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

    public boolean setStackFromSlot(int slot, ItemStack stack) {
        if (inventory == null || slot >= inventory.size()) return false;
        inventory.setStack(slot, stack);
        return true;
    }

    public boolean canNavigateInFluids() {
        return canNavigateInFluids;
    }

    public boolean hasTargetInWater() {
        return (navigation.getTargetPos() != null && getEntityWorld().getBlockState(navigation.getTargetPos()).isLiquid()
                    || getTarget() != null && getTarget().isSubmergedInWater())
                && canNavigateInFluids;
    }

    @Override
    public boolean shouldSwimInFluids() {
        return !canNavigateInFluids;
    }

    @Override
    public int getMaxLookYawChange() {
        return (int) getRotationSpeed();
    }

    public Identifier getDragonId() {
        return EntityType.getId(getType());
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
        BlockState blockState = getEntityWorld().getBlockState(blockPos);
        PlayerEntity rider = getOwner() instanceof URRideableDragonEntity dragon && dragon.canBeControlledByRider() ?
                (PlayerEntity) getControllingPassenger() : null;
        GameProfile gameProfile = rider != null ? rider.getGameProfile() : CommonProtection.UNKNOWN;
        return blockState.isIn(URTags.DRAGON_UNBREAKABLE) || !CommonProtection.canBreakBlock(getEntityWorld(), blockPos, gameProfile, rider);
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
        if (getEntityWorld().getBlockState(pos.down()).getCollisionShape(getEntityWorld(), pos.down()).isEmpty()) {
            if (this instanceof FlyingDragon flyingDragon) flyingDragon.setFlying(true);
            else return false;
        }
        BlockPos blockPos = pos.subtract(getBlockPos());
        return getEntityWorld().isSpaceEmpty(this, getBoundingBox().offset(blockPos));
    }

    @Override
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
        if (!(getEntityWorld() instanceof ServerWorld world)) return;
        if (inventory.canInsert(itemStack)) inventory.addStack(itemStack);
        else dropStack(world, itemStack);
    }

    public ItemStack consumeGivenItem(@Nullable LivingEntity offering, ItemStack itemStack, @Nullable SoundEvent sound, @Nullable Hand hand) {
        ItemStack original = itemStack.copy();
        if (itemStack.getComponents().contains(DataComponentTypes.CONSUMABLE))
            itemStack.getComponents().get(DataComponentTypes.CONSUMABLE).finishConsumption(getEntityWorld(), this, itemStack);
        else if (offering != null && !offering.isInCreativeMode()) {
            itemStack.decrement(1);
            if (sound != null) getEntityWorld().playSound(this, getX(), getY(), getZ(), sound, getSoundCategory());
        }
        DragonOnItemConsumedEvent.EVENT.invoker().onItemConsumed(offering, original, itemStack, hand);
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

    @Override
    protected Text getDefaultName() {
        if (defaultDisplayName == null) {
            DragonVariant variant = DragonVariant.getByVariant(getDragonId(), getVariant(), getEntityWorld());
            if (variant != null && variant.displayNameKey().isPresent()) defaultDisplayName = Text.translatable(variant.displayNameKey().get());
            if (defaultDisplayName == null) defaultDisplayName = super.getDefaultName();
        }
        return defaultDisplayName;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource damageSource, float amount) {
        if (isDancing() && damageSource.getAttacker() != null) updateJukeboxPos(jukeboxPos, true);
        return super.damage(world, damageSource, amount);
    }


    @Override
    public boolean isInvulnerableTo(ServerWorld world, DamageSource damageSource) {
        if (this instanceof HeadMountDragon && getVehicle() instanceof PlayerEntity && damageSource.isOf(DamageTypes.IN_WALL)) return true;
        return super.isInvulnerableTo(world, damageSource);
    }

    @Override
    public void setTamed(boolean tamed, boolean updateAttributes) {
        super.setTamed(tamed, updateAttributes);
        inventory.addListener(this);
    }

    public DragonInventory getInventory() {
        return inventory;
    }

    //asset location caching so mod doesn't have to make stupid amount of checks if file even exists each frame
    private final DragonAssetCache assetCache = new DragonAssetCache();

    public DragonAssetCache getAssetCache() {
        return assetCache;
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 500;
        return i >= 0 && i < inventory.size() ? StackReference.of(inventory, i) : super.getStackReference(mappedIndex);
    }

    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        if (yaw < 0) yaw += 360;
        float dYaw = Math.abs(MathHelper.wrapDegrees((this instanceof ShooterDragon shooterDragon ? shooterDragon.getShootingPointYaw() : getYaw()) - yaw));
        float dPitch = Math.abs((this instanceof ShooterDragon shooterDragon ? shooterDragon.getShootingPointPitch() : getPitch()) - pitch);
        return dPitch < pitchTolerance
                && dYaw % 360 < yawTolerance;
    }

    public int getBaseTamingProgress() {
        return DragonVariant.getByVariant(getDragonId(), getVariant(), getEntityWorld()).baseTamingProgress();
    }

    public boolean isTameable() {
        return !isTamed() && getTamingProgress() >= 0;
    }

    public abstract boolean isSaddle(ItemStack itemStack);

    public abstract boolean isHelmet(ItemStack itemStack);

    public abstract boolean isChestplate(ItemStack itemStack);

    public abstract boolean isTailArmor(ItemStack itemStack);

    public boolean isBanner(ItemStack itemStack) {
        return itemStack.getItem() instanceof BannerItem;
    }
    //todo make those advancements datadriven... some day somehow
    public static boolean grantTriggerableAdvancement(ServerPlayerEntity player, Identifier advancement) {
        AdvancementEntry entry = player.getEntityWorld().getServer().getAdvancementLoader().get(advancement);
        if (entry == null) return false;
        return player.getAdvancementTracker().grantCriterion(entry, "triggered_from_code");
    }

    @NotNull
    public abstract DragonInventory createInventory();

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
                setSitting(false);
                shouldFollow = true;
                if (player instanceof ServerPlayerEntity serverPlayer)
                    grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/use_horn"));
                return true;
            }
            return false;
        }
    }

    public record SoundInfo(Identifier id, float volume, float pitch, float pitchDeviation) { }
}
