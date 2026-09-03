package nordmods.uselessreptile.common.entity.base;

import eu.pb4.common.protection.api.CommonProtection;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.asset_cache.AssetCahceOwner;
import nordmods.uselessreptile.common.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnUtil;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.entity.ai.control.DragonBodyRotationControl;
import nordmods.uselessreptile.common.entity.ai.control.DragonLookControl;
import nordmods.uselessreptile.common.entity.ai.control.LandDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.navigation.DragonNavigation;
import nordmods.uselessreptile.common.entity.animation_processor.ControllerState;
import nordmods.uselessreptile.common.entity.animation_processor.DragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.event.DragonOnItemConsumedEvent;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.item.FluteItem;
import nordmods.uselessreptile.common.item.VortexHornItem;
import nordmods.uselessreptile.common.network.URNetworkHelper;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import nordmods.uselessreptile.common.util.duck.HeadMountDragonOwner;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class URDragonEntity extends TamableAnimal implements BRAnimatedObject, MenuProvider, AssetCahceOwner {
    public static final int TRANSITION_TICKS = 10;
    protected float pitchLimitGround = 90;
    protected int eatFromInventoryTimer = 20;
    protected int ticksUntilHeal = -1;
    protected float sprintSpeedModifier = 1.1f;
    protected float backwardSpeedModifier = 0.6f;
    private int healTimer = 0;
    private BlockPos homePoint = BlockPos.ZERO;
    protected final DynamicGameEventListener<JukeboxEventListener> jukeboxEventHandler = new DynamicGameEventListener<>(new JukeboxEventListener
            (new EntityPositionSource(this, getEyeHeight()), GameEvent.JUKEBOX_PLAY.value().notificationRadius()));
    protected final DynamicGameEventListener<HornUsedEventListener> hornUsedEventHandler = new DynamicGameEventListener<>(new HornUsedEventListener
            (new EntityPositionSource(this, getEyeHeight()), URGameEvents.INSTRUMENT_USED.value().notificationRadius()));
    protected final DynamicGameEventListener<FluteUsedEventListener> fluteUsedEventHandler = new DynamicGameEventListener<>(new RiverPikehorn.FluteUsedEventListener
            (new EntityPositionSource(this, getEyeHeight()), URGameEvents.FLUTE_USED.value().notificationRadius()));
    protected @Nullable BlockPos jukeboxPos;
    private DragonInventory inventory;
    public boolean shouldFollow = false;
    protected Component defaultDisplayName;
    public static final Map<DragonVariant, Map<String, SoundInfo>> SOUND_INFO_HOLDER = new HashMap<>();
    public static final Identifier VARIANT_BONUS_MODIFIER = UselessReptile.id("variant_bonus");
    public static final Identifier SPEED_MODIFIER_BONUS = UselessReptile.id("speed_modifier");
    private DragonVariant dragonVariant;
    private boolean invalidVariant;
    private Map<Identifier, EquipmentModelData.Equipment> dragonEquipment;
    //asset location caching so mod doesn't have to make stupid amount of checks if file even exists each frame
    private final DragonAssetCache assetCache = new DragonAssetCache();
    private final URDragonAnimationController<URDragonEntity> mainController = new URDragonAnimationController<>(this, true);
    private final URDragonAnimationController<URDragonEntity> turnController = new URDragonAnimationController<>(this, true);
    private final URDragonAnimationController<URDragonEntity> attackController = new URDragonAnimationController<>(this, false) {
        @Override
        public float getDefaultTransitionTime() {
            return 0;
        }
    };
    private final URDragonAnimationController<URDragonEntity> blinkController = new URDragonAnimationController<>(this, true) {
        @Override
        public float getDefaultTransitionTime() {
            return 0;
        }
    };
    private final Map<AnimationController, BRAnimationController> controllers = Map.of(
            AnimationController.MAIN , mainController,
            AnimationController.TURN , turnController,
            AnimationController.ATTACK , attackController,
            AnimationController.BLINK , blinkController
    );
    private final List<BRAnimationController> controllerList = List.copyOf(controllers.values());
    private final Int2ObjectOpenHashMap<DragonAbilityHolder> abilityHolders = new Int2ObjectOpenHashMap<>();
    private List<DragonAbilityHolder> availableAbilities = List.of();
    private static final List<FluteItem.FluteMode> FLUTE_MODES = List.of(
            URFluteModes.CALL,
            URFluteModes.SIT_DOWN,
            URFluteModes.STAND_UP,
            URFluteModes.TARGET
    );
    public float yBodyRotChange = 0;
    private final LinkedList<Float> yBodyRotChangeO = new LinkedList<>();
    protected int maxYBodyRotChangeSamples = 10;
    public float xBodyRot = 0;
    private final LinkedList<Float> xBodyRotO = new LinkedList<>();
    protected int maxXBodyRotSamples = 10;
    private final DragonAnimationProcessor<? extends URDragonEntity> processor = createServerAnimationProcessor();

    protected URDragonEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        navigation = new DragonNavigation(this, world);
        lookControl = new DragonLookControl(this);
        moveControl = new LandDragonMoveControl<>(this);
    }

    @Override
    public final List<BRAnimationController> getAnimationControllers() {
        return controllerList;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOVING_BACKWARDS, false);
        builder.define(MOVING, false);
        builder.define(DANCING, false);
        builder.define(TURNING_STATE, TurningState.NONE);
        builder.define(TAMING_PROGRESS, 1);
        builder.define(ACCELERATION_DURATION, 0);
        builder.define(BOUNDED_INSTRUMENT_SOUND, "");
        builder.define(VARIANT, "");
        builder.define(CURRENT_ORDER, Order.FOLLOW);
        builder.define(PREVIOUS_ORDER, Order.SIT);
        builder.define(WANDER_RADIUS, WanderRadius.MEDIUM);
        builder.define(CONTROLLER_STATES, List.of());
        builder.define(EQUIPMENT_CONTROLLER_STATES, Map.of());
    }

    public static final EntityDataAccessor<Boolean> MOVING_BACKWARDS = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<TurningState> TURNING_STATE = SynchedEntityData.defineId(URDragonEntity.class, UREntityDataSerializers.TURNING_STATE);
    public static final EntityDataAccessor<Integer> TAMING_PROGRESS = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ACCELERATION_DURATION = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> BOUNDED_INSTRUMENT_SOUND = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(URDragonEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Order> CURRENT_ORDER = SynchedEntityData.defineId(URDragonEntity.class, UREntityDataSerializers.ORDER);
    public static final EntityDataAccessor<Order> PREVIOUS_ORDER = SynchedEntityData.defineId(URDragonEntity.class, UREntityDataSerializers.ORDER);
    public static final EntityDataAccessor<WanderRadius> WANDER_RADIUS = SynchedEntityData.defineId(URDragonEntity.class, UREntityDataSerializers.WANDER_RADIUS);
    public static final EntityDataAccessor<List<ControllerState>> CONTROLLER_STATES = SynchedEntityData.defineId(URDragonEntity.class, UREntityDataSerializers.CONTROLLER_STATES);
    public static final EntityDataAccessor<Map<EquipmentSlot, List<ControllerState>>> EQUIPMENT_CONTROLLER_STATES = SynchedEntityData.defineId(URDragonEntity.class, UREntityDataSerializers.EQUIPMENT_CONTROLLER_STATES);

    public int getAccelerationDuration() {return entityData.get(ACCELERATION_DURATION);}
    public void setAccelerationDuration(int state) {entityData.set(ACCELERATION_DURATION, state);}

    public boolean isMovingBackwards() {return entityData.get(MOVING_BACKWARDS);}
    public void setMovingBackwards(boolean state) {entityData.set(MOVING_BACKWARDS, state);}

    public boolean isDancing() {return entityData.get(DANCING);}
    public void setDancing(boolean state) {entityData.set(DANCING, state);}

    public boolean isMoving() {return entityData.get(MOVING);}
    public void setMoving(boolean state) {entityData.set(MOVING, state);}

    @Override
    public boolean isOrderedToSit() {
        if (!getPassengers().isEmpty()) return false;
        return getCurrentOrder() == Order.SIT;
    }

    @Override
    public void setOrderedToSit(boolean state) {
        if (state) setCurrentOrder(Order.SIT);
        else if (getCurrentOrder() == Order.SIT) setCurrentOrder(getPreviousOrder());
    }

    public Order getCurrentOrder() {return entityData.get(CURRENT_ORDER);}
    public void setCurrentOrder(Order order) {
        if (getCurrentOrder() != order) setPreviousOrder(getCurrentOrder());
        entityData.set(CURRENT_ORDER, order);
        super.setOrderedToSit(order == Order.SIT);
    }

    public Order getPreviousOrder() {return entityData.get(PREVIOUS_ORDER);}
    public void setPreviousOrder(Order state) {entityData.set(PREVIOUS_ORDER, state);}

    public WanderRadius getWanderRadius() {return entityData.get(WANDER_RADIUS);}
    public void setWanderRadius(WanderRadius state) {entityData.set(WANDER_RADIUS, state);}

    public String getVariant() {return entityData.get(VARIANT);}
    public void setVariant(String state) {entityData.set(VARIANT, state);}

    public TurningState getTurningState() {return entityData.get(TURNING_STATE);}
    public void setTurningState(TurningState state) {entityData.set(TURNING_STATE, state);}

    public int getTamingProgress() {return entityData.get(TAMING_PROGRESS);}
    public void setTamingProgress(int state) {entityData.set(TAMING_PROGRESS, state);}

    public String getBoundedInstrumentSound() {return  entityData.get(BOUNDED_INSTRUMENT_SOUND);}
    public void setBoundedInstrumentSound(String state) {entityData.set(BOUNDED_INSTRUMENT_SOUND, state);}

    @NonNull
    public DragonVariant getDragonVariant() {
        if (dragonVariant == null) {
            dragonVariant = DragonVariant.get(getVariantType(), getVariant(), level().registryAccess());
            if (dragonVariant == null) {
                dragonVariant = DragonVariant.get(getVariantType(), getDefaultVariant(), level().registryAccess());
                invalidVariant = true;
            }
        }
        return dragonVariant;
    }

    public Map<Identifier, EquipmentModelData.Equipment> getDragonEquipment() {
        if (dragonEquipment == null) {
            dragonEquipment = DragonVariantUtil.getEquipmentModelDataMap(getDragonVariant(), level().registryAccess());
        }
        return dragonEquipment;
    }


    @Override
    public void addAdditionalSaveData(@NonNull ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", getVariant());

        int[] coords = {getHomePoint().getX(), getHomePoint().getY(), getHomePoint().getZ()};
        tag.putIntArray("HomePoint", coords);

        if (!isTame()) tag.putInt("TamingProgress", getTamingProgress());
        else tag.putString("BoundedInstrumentSound", getBoundedInstrumentSound());

        ValueOutput.TypedOutputList<ItemStackWithSlot> listAppender = tag.list("Inventory", ItemStackWithSlot.CODEC);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) listAppender.add(new ItemStackWithSlot(i, stack));
        }
        if (isTame()) {
            tag.putBoolean("Sitting", isOrderedToSit());
            tag.putInt("CurrentOrder", getCurrentOrder().ordinal());
            tag.putInt("PreviousOrder", getPreviousOrder().ordinal());
            tag.putInt("WanderRadius", getWanderRadius().ordinal());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        setVariant(tag.getStringOr("Variant", getDefaultVariant()));

        for (ItemStackWithSlot stackWithSlot : tag.listOrEmpty("Inventory", ItemStackWithSlot.CODEC)) {
            if (stackWithSlot.isValidInContainer(this.inventory.getContainerSize())) {
                inventory.setItem(stackWithSlot.slot(), stackWithSlot.stack());
            }
        }

        super.readAdditionalSaveData(tag);

        int[] coords = tag.getIntArray("HomePoint").orElse(new int[] {getBlockX(), getBlockY(), getBlockZ()});
        if (coords.length == 0) setHomePoint(blockPosition());
        else setHomePoint(new BlockPos(coords[0], coords[1], coords[2]));

        if (!isTame()) setTamingProgress(tag.getIntOr("TamingProgress", getBaseTamingProgress()));
        else setBoundedInstrumentSound(tag.getStringOr("BoundedInstrumentSound", ""));

        setPreviousOrder(Order.values()[tag.getIntOr("PreviousOrder", Order.SIT.ordinal())]);
        if (tag.getBooleanOr("Sitting", false)) {
            setOrderedToSit(true);
        } else {
            setCurrentOrder(Order.values()[tag.getIntOr("CurrentOrder", Order.FOLLOW.ordinal())]);
        }
        setWanderRadius(WanderRadius.values()[tag.getIntOr("WanderRadius", WanderRadius.MEDIUM.ordinal())]);
        updateEquipment();
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (VARIANT.equals(data)) {
            clearVariant();
            updateVariantModifiers();
            updateInventory();
            updateAbilities();
        }
        if (level().isClientSide()) {
            if (CONTROLLER_STATES.equals(data)) {
                ControllerState.applyControllerStates(
                        entityData.get(CONTROLLER_STATES),
                        getAnimationControllers()
                );
                for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                    DragonEquipment equipment = assetCache.getEquipment(equipmentSlot);
                    if (equipment != null && equipment.getAnimationProcessor() == null) equipment.updateAnimations();
                }
            }
            if (EQUIPMENT_CONTROLLER_STATES.equals(data)) {
                entityData.get(EQUIPMENT_CONTROLLER_STATES).forEach((equipmentSlot, controllerStates) -> {
                    DragonEquipment equipment = assetCache.getEquipment(equipmentSlot);
                    if (equipment != null) {
                        ControllerState.applyControllerStates(controllerStates, equipment.getAnimationControllers());
                        equipment.cloneAnimationController.copyFrom(this);
                        equipment.markAnimationUpdated();
                    }
                });
            }
        }
    }

    protected void updateAbilities() {
        abilityHolders.clear();
        CommonDragonVariantData variant = getDragonVariant().common();

        registryAccess().lookupOrThrow(URResourceKeys.DRAGON_ABILITIES).get(variant.abilities()).ifPresentOrElse(
                ref -> {
                    for (int i = 0; i < ref.value().size(); i++) abilityHolders.put(i, ref.value().get(i).createAbilityHolder(this));
                },
                () -> UselessReptile.LOGGER.error("Couldn't find abilities list {} for variant {} ({})", variant.abilities(), getVariant(), getDragonId())
        );
    }

    private void updateInventory() {
        DragonInventory newInventory = createInventory();
        if (inventory != null) inventory.forEach(newInventory::addItem);
        inventory = newInventory;
    }

    public void clearVariant() {
        assetCache.cleanCache();
        defaultDisplayName = null;
        dragonVariant = null;
        invalidVariant = false;
        dragonEquipment = null;
    }

    private void updateVariantModifiers() {
        AttributeMap container = getAttributes();
        registryAccess().lookupOrThrow(Registries.ATTRIBUTE).listElements().forEach(entityAttributeReference -> {
            if (container.hasAttribute(entityAttributeReference))
                container.getInstance(entityAttributeReference).removeModifier(VARIANT_BONUS_MODIFIER);
        });

        CommonDragonVariantData variant = getDragonVariant().common();
        if (variant == null) {
            UselessReptile.LOGGER.error("Couldn't find any info on variant {} ({}). No variant attribute modifiers will be applied", getVariant(), getDragonId());
            return;
        }

        variant.variantAttributeModifiers().ifPresent(id -> {
            List<AttributeModifier> modifiers = registryAccess().lookupOrThrow(URResourceKeys.DRAGON_VARIANT_ATTRIBUTE_MODIFIERS).getValue(id);
            if (modifiers != null) modifiers.forEach(entityAttributeModifier -> {
                Attribute attribute = registryAccess().lookupOrThrow(Registries.ATTRIBUTE).getValue(entityAttributeModifier.id());
                if (attribute != null) {
                    AttributeInstance entityAttributeInstance = getAttribute(
                            registryAccess()
                                    .lookupOrThrow(Registries.ATTRIBUTE)
                                    .get(entityAttributeModifier.id()).get()
                    );
                    if (entityAttributeInstance != null && !entityAttributeInstance.hasModifier(VARIANT_BONUS_MODIFIER))
                        entityAttributeInstance.addTransientModifier(new AttributeModifier(VARIANT_BONUS_MODIFIER, entityAttributeModifier.amount(), entityAttributeModifier.operation()));

                }
            });
        });
    }

    @Nullable
    public SoundInfo getSoundInfo(String name) {
        Map<String, SoundInfo> soundMap = SOUND_INFO_HOLDER.get(getDragonVariant());
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
            SOUND_INFO_HOLDER.put(getDragonVariant(), soundMap);
            return info;
        }
    }

    private SoundInfo createSoundInfo(String name) {
        DragonModelData model = DragonVariantUtil.getDragonModelData(getDragonVariant(), level().registryAccess());
        if (model != null) {
            if (model.sounds().isPresent()) {
                DragonModelData.Sound sound = model.sounds().get().stream()
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

    @Override
    public SpawnGroupData finalizeSpawn(@NonNull ServerLevelAccessor world, @NonNull DifficultyInstance difficulty, @NonNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        entityData = new AgeableMobGroupData(false);
        DragonSpawnUtil.assignAvailableVariant(this, spawnReason);
        setTamingProgress(getBaseTamingProgress());
        setHomePoint(blockPosition());
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    protected static AttributeSupplier.Builder createDragonAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MAX_HEALTH)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.FOLLOW_RANGE, 160)
                .add(Attributes.JUMP_STRENGTH)
                .add(URAttributes.DRAGON_VERTICAL_SPEED)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED)
                .add(URAttributes.DRAGON_MINING_LEVEL);

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NonNull ServerLevel world, @NonNull AgeableMob entity) {
        return null;
    }

    public BlockPos getHomePoint() {
        return homePoint;
    }

    public void setHomePoint(BlockPos homePoint) {
        this.homePoint = homePoint;
    }

    @Override
    public void tame(@NonNull Player entity) {
        super.tame(entity);
        setHomePoint(blockPosition());
    }

    @Override
    public void updateDynamicGameEventListener(@NonNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
        if (level() instanceof ServerLevel serverWorld) {
            callback.accept(jukeboxEventHandler, serverWorld);
            callback.accept(hornUsedEventHandler, serverWorld);
            callback.accept(fluteUsedEventHandler, serverWorld);
        }
        super.updateDynamicGameEventListener(callback);
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
        ItemStack head = inventory.getItem(1);
        setItemSlot(EquipmentSlot.HEAD, head);

        ItemStack body = inventory.getItem(2);
        setItemSlot(EquipmentSlot.CHEST, body);

        ItemStack tail = inventory.getItem(3);
        setItemSlot(EquipmentSlot.LEGS, tail);

        ItemStack banner = inventory.getItem(4);
        setItemSlot(EquipmentSlot.BODY, banner);

        if (getOwner() instanceof ServerPlayer serverPlayer) { //TODO i really should unhardcode this...
            if ((head.is(URItems.DRAGON_HELMET_DIAMOND) || head.is(URItems.MOLECLAW_HELMET_DIAMOND))
                    && body.is(URItems.DRAGON_CHESTPLATE_DIAMOND)
                    && tail.is(URItems.DRAGON_TAIL_ARMOR_DIAMOND)) {
                grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/equip_full_diamond_dragon_armor"));
            }

            if ((head.is(URItems.DRAGON_HELMET_NETHERITE) || head.is(URItems.MOLECLAW_HELMET_NETHERITE))
                    && body.is(URItems.DRAGON_CHESTPLATE_NETHERITE)
                    && tail.is(URItems.DRAGON_TAIL_ARMOR_NETHERITE)) {
                grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/equip_full_netherite_dragon_armor"));
            }
        }
    }

    public static boolean canDragonSpawn(EntityType<? extends Mob> type, LevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return DragonSpawnUtil.getAvailableVariants(world, pos, URRegistries.VARIANT_TYPE.getValue(EntityType.getKey(type)), spawnReason).findFirst().isPresent();
    }

    @Override
    public void onEquipItem(@NonNull EquipmentSlot slot, @NonNull ItemStack oldStack, ItemStack newStack) {
        boolean empty = newStack.isEmpty() && oldStack.isEmpty();
        if (!empty && !ItemStack.isSameItemSameComponents(oldStack, newStack) && !firstTick) {
            if (!level().isClientSide() && doesEmitEquipEvent(slot))
                URNetworkHelper.playSound(this, SoundEvents.ARMOR_EQUIP_GENERIC.value(), getSoundSource(), 1, 1, 6);
        }
        super.onEquipItem(slot, oldStack, newStack);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, @NonNull Inventory inv, @NonNull Player player) {
        return new URDragonMenu(syncId, inv, getInventory());
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(URItems.VARIANT_CHANGING_ORB) || itemStack.is(URItems.FLUTE)) return InteractionResult.PASS;
        return super.interact(player, hand, location);
    }

    @Override
    public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (isTameable()) {
            CommonDragonVariantData.TamingItem tamingItem = getTamingItem(itemStack);
            if (tamingItem != null) {
                consumeGivenItem(player, itemStack, SoundEvents.GENERIC_EAT.value(), hand);
                if (player.isCreative()) setTamingProgress(0);
                else setTamingProgress(getTamingProgress() - random.nextIntBetweenInclusive(tamingItem.tamingProgressIncrease().getFirst(), tamingItem.tamingProgressIncrease().getSecond()));
                if (getTamingProgress() <= 0) {
                    tame(player);
                    level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                } else {
                    level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                }
                setPersistenceRequired();
                return InteractionResult.SUCCESS;
            }
        }

        if (isTame()) {
            if (getHealth() != getMaxHealth()) {
                CommonDragonVariantData.FoodItem foodItem = getFoodItem(itemStack);
                if (foodItem != null) {
                    consumeGivenItem(player, itemStack, SoundEvents.GENERIC_EAT.value(), hand);
                    heal(foodItem.healingAmount());
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (isTame() && isOwnedBy(player)) {
            if (this instanceof HeadMountDragon && !player.isShiftKeyDown() && itemStack.isEmpty()) {
                dropLeash();
                startRiding(player);
                return InteractionResult.SUCCESS;
            }

            if (itemStack.getItem() instanceof PotionItem potionItem && player.isShiftKeyDown()) {
                ItemStack original = itemStack.copy();
                potionItem.finishUsingItem(itemStack, level(), this);
                consumeGivenItem(player, original, SoundEvents.GENERIC_DRINK.value(), hand);
                if (player instanceof ServerPlayer serverPlayer)
                    grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/give_potion"));
                return InteractionResult.SUCCESS;
            }

            if (isInstrument(itemStack) && !player.isShiftKeyDown() && !(itemStack.getItem() instanceof VortexHornItem)) {
                String sound = getInstrument(itemStack);
                if (!getBoundedInstrumentSound().equals(sound)) setBoundedInstrumentSound(sound);
                else setBoundedInstrumentSound("");
                Component instrumentSound = Component.translatable(getBoundedInstrumentSound().isEmpty() ?
                        "other.uselessreptile.none" : getBoundedInstrumentSound()); //might fetch keys for non-vanilla instruments incorrectly
                if (!level().isClientSide()) player.sendOverlayMessage(Component.translatable("other.uselessreptile.sound_respond", getName(), instrumentSound));
                if (level().isClientSide()) player.playSound(SoundEvents.COMPARATOR_CLICK, 0.2f, 2);
                return InteractionResult.SUCCESS;
            }

            if ((itemStack.is(Items.STICK) || isInstrument(itemStack)) && player.isShiftKeyDown()) {
                if (isOrderedToSit()) setOrderedToSit(false);
                else {
                    setOrderedToSit(true);
                    getNavigation().stop();
                    if (player instanceof ServerPlayer serverPlayer)
                        grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/sit_down_dragon"));
                }
                return InteractionResult.SUCCESS;
            }

            if (player.isShiftKeyDown() && inventory.getContainerSize() >= 0) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.uselessreptile$openDragonInventoryScreen(this);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean canShearEquipment(@NonNull Player player) {
        return false;
    }

    @Override
    public boolean startRiding(@NonNull Entity entity, boolean force, boolean event) {
        boolean result = super.startRiding(entity, force, event);
        if (this instanceof HeadMountDragon && result && entity instanceof HeadMountDragonOwner owner) {
            TagValueOutput nbtWriteView = TagValueOutput.createWithContext(UselessReptile.ERROR_REPORTER, entity.registryAccess());
            saveAsPassenger(nbtWriteView);
            owner.useless_reptile$setHeadMountDragon(nbtWriteView.buildResult());
            setPortalCooldown(0);
        }
        return result;
    }

    @Override
    public void stopRiding() {
        if (this instanceof HeadMountDragon && getVehicle() instanceof HeadMountDragonOwner owner) {
            if (owner instanceof ServerPlayer player && player.hasDisconnected()) return;
            owner.useless_reptile$setHeadMountDragon(new CompoundTag());
            setYRot(((Entity) owner).getYRot() + 180f);
        }
        super.stopRiding();
    }

    protected boolean isInteractableItem(ItemStack itemStack) {
        return itemStack.is(Items.POTION)
                || itemStack.is(Items.STICK)
                || isInstrument(itemStack)
                || getFoodItem(itemStack) != null
                || itemStack.is(URItems.VARIANT_CHANGING_ORB);
    }

    public boolean isInstrument(ItemStack itemStack) {
        return itemStack.getComponents().has(DataComponents.INSTRUMENT);
    }

    public String getInstrument(ItemStack itemStack) {
        if (!itemStack.getComponents().has(DataComponents.INSTRUMENT)) return "";
        Holder<Instrument> instrument = itemStack.getComponents().get(DataComponents.INSTRUMENT).instrument();
        boolean translatable = instrument.value().description().getContents() instanceof TranslatableContents;
        return translatable ? ((TranslatableContents) instrument.value().description().getContents()).getKey() : ((PlainTextContents)instrument.value().description().getContents()).text();
    }

    public void playSound(@NonNull SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) {
            if (level().isClientSide()) level().playLocalSound(getX(), getY(),getZ(), sound, getSoundSource(), volume, pitch,true);
            else level().playSound(null, getX(), getY(),getZ(), sound,  getSoundSource(), volume, pitch);
        }
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
    public int getHeadRotSpeed() { //todo fix inheritors
        return (int) (getGroundRotationSpeed() * getMovementSpeedModifier());
    }

    @Override
    protected @NonNull BodyRotationControl createBodyControl() {
        return new DragonBodyRotationControl<>(this);
    }

    public float getGroundRotationSpeed() {
        return (float) getAttributeValue(URAttributes.DRAGON_GROUND_ROTATION_SPEED);
    }

    public float getMaxAccelerationDuration() {
        return (float) (getAttributeValue(URAttributes.DRAGON_ACCELERATION_DURATION) * getMovementSpeedModifier());
    }

    public float getCooldownModifier() {
        float mod = 1;
        if (hasEffect(MobEffects.SLOWNESS)) mod *= (float) (1 + 0.1 * (getEffect(MobEffects.SLOWNESS).getAmplifier() + 1));
        if (hasEffect(MobEffects.SPEED)) mod *= (float) (1 - 0.1 * Mth.clamp(getEffect(MobEffects.SPEED).getAmplifier() + 1, 1, 9));
        if (hasEffect(URMobEffect.SHOCK)) mod /= 2;
        return mod;
    }

    public float getMovementSpeedModifier() {
        double baseSpeed = getBaseGroundSpeed();
        double speed = getAttributeValue(Attributes.MOVEMENT_SPEED);
        return (float) (speed / baseSpeed);
    }

    public float getAccelerationModifier() {
        return getAccelerationDuration() / getMaxAccelerationDuration();
    }

    @Override
    public void tick() {
        yBodyRotChangeO.addLast(yBodyRotChange);
        if (yBodyRotChangeO.size() >= maxYBodyRotChangeSamples) yBodyRotChangeO.removeFirst();

        xBodyRotO.addLast(xBodyRot);
        if (xBodyRotO.size() >= maxXBodyRotSamples) xBodyRotO.removeFirst();

        super.tick();
        if (!level().isClientSide()) {
            if (getOwner() != null && getCurrentOrder() == Order.FOLLOW) {
                if (distanceTo(getOwner()) > getWanderRadius().radius) {
                    shouldFollow = true;
                    setHomePoint(getOwner().blockPosition());
                }
            }
            setMoving(getDeltaMovement().z() != 0 || getDeltaMovement().x() != 0);
        }

        getAbilityHolders().values().forEach(DragonAbilityHolder::tick);

        if (ticksUntilHeal > -1 && --healTimer <= 0) {
            heal(1);
            healTimer = getTicksUntilHeal();
        }

        if (this instanceof HeadMountDragon) {
            if (getVehicle() instanceof Player player) {
                if (!player.isAlive()) stopRiding();
                getLookControl().setLockRotation(true);
                if (level().isClientSide()) {
                    yRotO = getYRot();
                    setYRot(player.getYRot());
                    TurningState turnState = TurningState.NONE;
                    float diff = yRotO - getYRot();
                    if (diff > 0) turnState = TurningState.LEFT;
                    if (diff < 0) turnState = TurningState.RIGHT;
                    setTurningState(turnState);
                }
            } else getLookControl().setLockRotation(false);
        }

        if (!level().isClientSide() && tickCount % 20 == 0) {
            boolean jukeboxReachable = false;
            if (jukeboxPos != null) jukeboxReachable = jukeboxPos.closerThan(blockPosition(), GameEvent.JUKEBOX_PLAY.value().notificationRadius());
            if (jukeboxReachable) updateJukeboxPos(jukeboxPos, true);
            else updateJukeboxPos(null, false);
        }

        availableAbilities = abilityHolders.values().stream().filter(a -> a.getAbility().canBeUsed(a)).toList();
        if (processor != null) {
            processor.tick();
            needsSync = true;
        }
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            DragonEquipment equipment = assetCache.getEquipment(equipmentSlot);
            if (equipment != null) equipment.tick();
        }
        syncAnimations();
    }

    protected void syncAnimations() {
        if (processor != null && !level().isClientSide()) {
            entityData.set(CONTROLLER_STATES, ControllerState.collectControllerStates(getAnimationControllers()));
            Map<EquipmentSlot, List<ControllerState>> equipmentControllerStates = new HashMap<>();
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                DragonEquipment equipment = getAssetCache().getEquipment(equipmentSlot);
                if (equipment != null && equipment.getAnimationProcessor() != null) equipmentControllerStates.put(equipmentSlot, ControllerState.collectControllerStates(equipment.getAnimationControllers()));
            }
            entityData.set(EQUIPMENT_CONTROLLER_STATES, equipmentControllerStates);
        }
    }

    @Override
    public void travel(@NonNull Vec3 movementInput) {
        updateMovementModifiers();
        super.travel(movementInput);
    }

    public void updateMovementModifiers() {
        if ((!isMoving())) setSprinting(false);
        float speedModifier = 1;
        if (isSprinting()) speedModifier = sprintSpeedModifier;
        else if (isMovingBackwards()) speedModifier = backwardSpeedModifier;

        AttributeInstance instance = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedModifier != 1f) {
            AttributeModifier modifier = new AttributeModifier(SPEED_MODIFIER_BONUS, speedModifier - 1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            if (!instance.hasModifier(modifier.id())) instance.addTransientModifier(modifier);
            else instance.addOrUpdateTransientModifier(modifier);
        } else instance.removeModifier(SPEED_MODIFIER_BONUS);

        float speed = (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
        setSpeed(speed * speedModifier);
    }

    protected abstract float getBaseGroundSpeed();

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return !this.isTame() && this.tickCount > 2400;
    }

    @Override
    protected void playStepSound(@NonNull BlockPos pos, @NonNull BlockState state) {
    }

    @Override
    @Deprecated
    protected SoundEvent getAmbientSound() {
        return null;
    }

    public void playAmbientSound() {
        SoundInfo soundInfo = getSoundInfo("idle");
        if (soundInfo != null) playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
    }

    @Override
    @Deprecated
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        playHurtSound(source); //don't ask
        return null;
    }

    @Override
    protected void playHurtSound(@NonNull DamageSource damageSource) {
        SoundInfo soundInfo = getSoundInfo("hurt");
        if (soundInfo != null) {
            ambientSoundTime = -getAmbientSoundInterval();
            playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        SoundInfo soundInfo = getSoundInfo("death");  //don't ask about this either
        if (soundInfo != null) playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
        return null;
    }

    @Override
    public @NonNull SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    public boolean canAttack(@Nullable LivingEntity target) {
        if (target == null) return false;
        if (isOrderedToSit()) return false;
        if (target.is(URTags.DRAGON_IMMUNE)) return false;
        if (getOwner() != null && target instanceof OwnableEntity tameable && tameable.getOwner() == getOwner()) return false;
        return super.canAttack(target);
    }

    @Override
    protected void dropEquipment(@NonNull ServerLevel world) {
        super.dropEquipment(world);
        if (inventory != null) {
            for(int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack itemStack = inventory.getItem(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    spawnAtLocation(world, itemStack);
                }
            }

        }
    }

    @Override
    public boolean isFood(@NonNull ItemStack stack) {
        return false;
    }

    public CommonDragonVariantData.@Nullable TamingItem getTamingItem(ItemStack itemStack) { //todo remove taming items for dragons that cannot be tamed with food
        DragonVariant variant = getDragonVariant();
        if (!isInvalidVariant()) {
            return variant.common().tamingItems().orElse(List.of()).stream()
                    .filter(item -> {
                        ExtraCodecs.TagOrElementLocation entryId = item.item();
                        if (entryId.tag()) return itemStack.is(TagKey.create(Registries.ITEM, entryId.id()));
                        return entryId.id().equals(itemStack.getItem().builtInRegistryHolder().key().identifier());
                    })
                    .max(Comparator.comparingInt(item -> item.priority().orElse(Integer.MIN_VALUE)))
                    .orElse(null);
        }
        return null;
    }

    public CommonDragonVariantData.@Nullable FoodItem getFoodItem(ItemStack itemStack) {
        DragonVariant variant = getDragonVariant();
        if (!isInvalidVariant()) {
            return variant.common().foodItems().orElse(List.of()).stream()
                    .filter(item -> {
                        ExtraCodecs.TagOrElementLocation entryId = item.item();
                        if (entryId.tag()) return itemStack.is(TagKey.create(Registries.ITEM, entryId.id()));
                        return entryId.id().equals(itemStack.getItem().builtInRegistryHolder().key().identifier());
                    })
                    .max(Comparator.comparingInt(item -> item.priority().orElse(Integer.MIN_VALUE)))
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
        return inventory.getItem(slot);
    }

    public boolean setStackFromSlot(int slot, ItemStack stack) {
        if (inventory == null || slot >= inventory.getContainerSize()) return false;
        inventory.setItem(slot, stack);
        return true;
    }

    public boolean hasTargetInWater() {
        return (navigation.getTargetPos() != null && level().getBlockState(navigation.getTargetPos()).liquid()
                    || getTarget() != null && getTarget().isUnderWater())
                && !isAffectedByFluids();
    }

    public final Identifier getDragonId() {
        return EntityType.getKey(getType());
    }

    @Override
    public @NonNull DragonLookControl getLookControl() {
        return (DragonLookControl) lookControl;
    }

    @Override
    public @NonNull AABB getAttackBoundingBox(double range) {
        return getPrimaryAttackBox();
    }

    public @NonNull abstract AABB getPrimaryAttackBox();

    public AABB getSecondaryAttackBox() {
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
        BlockState blockState = level().getBlockState(blockPos);
        Player rider = getOwner() instanceof URRideableDragonEntity dragon && dragon.hasControllingPassenger() ?
                (Player) getControllingPassenger() : null;
        NameAndId nameAndId = rider != null ? rider.nameAndId() : CommonProtection.UNKNOWN;
        return blockState.is(URTags.DRAGON_UNBREAKABLE) || !CommonProtection.canBreakBlock(level(), blockPos, nameAndId, rider);
    }

    public boolean canBreakBlocks() {
        return false;
    }

    @Override
    public @NonNull PathNavigation getNavigation() {
        return navigation;
    }

    @Override
    protected boolean canTeleportTo(@NonNull BlockPos pos) {
        PathType pathNodeType = getNavigation().getNodeEvaluator().getPathType(this, pos);
        if (getPathfindingMalus(pathNodeType) != 0) return false;
        if (level().getBlockState(pos.below()).getCollisionShape(level(), pos.below()).isEmpty()) {
            if (this instanceof FlyingDragon flyingDragon) flyingDragon.setFlying(true);
            else return false;
        }
        BlockPos blockPos = pos.subtract(blockPosition());
        return level().noCollision(this, getBoundingBox().move(blockPos));
    }

    @Override
    public boolean canBeLeashed() {
        return isTame();
    }

    public int vortexHornCapacity() {
        return 1;
    }

    //I have no idea how this happened to be so important for spawning
    @Override
    public float getWalkTargetValue(@NonNull BlockPos pos, @NonNull LevelReader world) {
        return 0;
    }

    public void giveItemStack(ItemStack itemStack) {
        if (!(level() instanceof ServerLevel world)) return;
        if (inventory.canAddItem(itemStack)) inventory.addItem(itemStack);
        else spawnAtLocation(world, itemStack);
    }

    public ItemStack consumeGivenItem(@Nullable LivingEntity offering, ItemStack itemStack, @Nullable SoundEvent sound, @Nullable InteractionHand hand) {
        ItemStack original = itemStack.copy();
        if (itemStack.getComponents().has(DataComponents.CONSUMABLE))
            itemStack.getComponents().get(DataComponents.CONSUMABLE).onConsume(level(), this, itemStack);
        else if (offering != null && !offering.hasInfiniteMaterials()) {
            itemStack.shrink(1);
            if (sound != null) level().playSound(this, getX(), getY(), getZ(), sound, getSoundSource());
        }
        DragonOnItemConsumedEvent.EVENT.invoker().onItemConsumed(offering, original, itemStack, hand);
        return itemStack;
    }

    @Override
    public boolean shouldBeSaved() {
        if (this instanceof HeadMountDragon && getVehicle() instanceof Player) return false;
        return super.shouldBeSaved();
    }

    @Override
    public void remove(@NonNull RemovalReason reason) {
        super.remove(reason);
        if (this instanceof HeadMountDragon && getVehicle() instanceof HeadMountDragonOwner owner && reason.shouldDestroy()) owner.useless_reptile$setHeadMountDragon(new CompoundTag());
    }

    @Override
    protected @NonNull Component getTypeName() {
        if (defaultDisplayName == null) {
            DragonVariant variant = getDragonVariant();
            if (!isInvalidVariant() && variant.common().displayNameKey().isPresent()) defaultDisplayName = Component.translatable(variant.common().displayNameKey().get());
            if (defaultDisplayName == null) defaultDisplayName = super.getTypeName();
        }
        return defaultDisplayName;
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource damageSource, float amount) {
        if (isDancing() && damageSource.getEntity() != null) updateJukeboxPos(jukeboxPos, true);
        return super.hurtServer(world, damageSource, amount);
    }


    @Override
    public boolean isInvulnerableTo(@NonNull ServerLevel world, @NonNull DamageSource damageSource) {
        if (this instanceof HeadMountDragon && getVehicle() instanceof Player && damageSource.is(DamageTypes.IN_WALL)) return true;
        return super.isInvulnerableTo(world, damageSource);
    }

    public DragonInventory getInventory() {
        return inventory;
    }

    public DragonAssetCache getAssetCache() {
        return assetCache;
    }

    @Override
    @Nullable
    public SlotAccess getSlot(int mappedIndex) {
        int i = mappedIndex - 500;
        return i >= 0 && i < inventory.getContainerSize() ? inventory.getSlot(i) : super.getSlot(mappedIndex);
    }

    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        if (yaw < 0) yaw += 360;
        float dYaw = Math.abs(Mth.wrapDegrees(getYRot()) - yaw);
        float dPitch = Math.abs(getXRot() - pitch);
        return dPitch < pitchTolerance
                && dYaw % 360 < yawTolerance;
    }

    public int getBaseTamingProgress() {
        DragonVariant variant = getDragonVariant();
        if (isInvalidVariant()) {
            UselessReptile.LOGGER.error("Couldn't find any info on variant {} ({}), base value of taming progress will be set to -1", getVariant(), getDragonId());
            return -1;
        }
        return variant.common().baseTamingProgress();
    }

    public boolean isTameable() {
        return !isTame() && getTamingProgress() >= 0;
    }

    public final boolean isSaddle(ItemStack itemStack) {
        if (!(getDragonEquipment().containsKey(BuiltInRegistries.ITEM.getKey(itemStack.getItem())))) return false;
        return getDragonEquipment().get(BuiltInRegistries.ITEM.getKey(itemStack.getItem())).slot() == DragonInventory.Slot.SADDLE;
    }

    public final boolean isHelmet(ItemStack itemStack) {
        if (!(getDragonEquipment().containsKey(BuiltInRegistries.ITEM.getKey(itemStack.getItem())))) return false;
        return getDragonEquipment().get(BuiltInRegistries.ITEM.getKey(itemStack.getItem())).slot() == DragonInventory.Slot.HELMET;
    }

    public final boolean isChestplate(ItemStack itemStack) {
        if (!(getDragonEquipment().containsKey(BuiltInRegistries.ITEM.getKey(itemStack.getItem())))) return false;
        return getDragonEquipment().get(BuiltInRegistries.ITEM.getKey(itemStack.getItem())).slot() == DragonInventory.Slot.CHESTPLATE;
    }

    public final boolean isTailArmor(ItemStack itemStack) {
        if (!(getDragonEquipment().containsKey(BuiltInRegistries.ITEM.getKey(itemStack.getItem())))) return false;
        return getDragonEquipment().get(BuiltInRegistries.ITEM.getKey(itemStack.getItem())).slot() == DragonInventory.Slot.TAIL_ARMOR;
    }

    public boolean isBanner(ItemStack itemStack) {
        return itemStack.getItem() instanceof BannerItem;
    }
    //todo make those advancements datadriven... some day somehow
    public static boolean grantTriggerableAdvancement(ServerPlayer player, Identifier advancement) {
        AdvancementHolder entry = player.level().getServer().getAdvancements().get(advancement);
        if (entry == null) return false;
        return player.getAdvancements().award(entry, "triggered_from_code");
    }

    @NonNull
    public DragonInventory createInventory() {
        return new DragonInventory(
                this,
                getStorageSize(),
                getDragonEquipment().values().stream().anyMatch(equipment -> equipment.slot() == DragonInventory.Slot.SADDLE),
                getDragonEquipment().values().stream().anyMatch(equipment -> equipment.slot() == DragonInventory.Slot.HELMET),
                getDragonEquipment().values().stream().anyMatch(equipment -> equipment.slot() == DragonInventory.Slot.CHESTPLATE),
                getDragonEquipment().values().stream().anyMatch(equipment -> equipment.slot() == DragonInventory.Slot.TAIL_ARMOR)
        );
    }

    protected abstract DragonInventory.StorageSize getStorageSize();

    public boolean isInvalidVariant() {
        return invalidVariant;
    }

    public abstract DragonVariantType<? extends DragonVariant> getVariantType();

    public List<FluteItem.FluteMode> getPermittedFluteModes() {
        return FLUTE_MODES;
    }

    public final URDragonAnimationController<URDragonEntity> getAnimationController(AnimationController controller) {
        return (URDragonAnimationController<URDragonEntity>) controllers.get(controller);
    }

    public Int2ObjectOpenHashMap<DragonAbilityHolder> getAbilityHolders() {
        return abilityHolders;
    }

    public List<DragonAbilityHolder> getAvailableAbilities() {
        return availableAbilities;
    }

    public void onAbilityActivated(DragonAbility ability) {}

    public float getYBodyRotChange(float tickDelta) {
        float sum = 0;
        for (int i = 0; i < yBodyRotChangeO.size(); i++) {
            if (i != yBodyRotChangeO.size() - 1) sum += Mth.lerp(tickDelta, yBodyRotChangeO.get(i), yBodyRotChangeO.get(i + 1));
            else sum += Mth.lerp(tickDelta, yBodyRotChangeO.get(i), yBodyRotChange);
        }
        return sum / maxYBodyRotChangeSamples;
    }

    public float getXBodyRot(float tickDelta) {
        float sum = 0;
        for (int i = 0; i < xBodyRotO.size(); i++) {
            if (i != xBodyRotO.size() - 1) sum += Mth.lerp(tickDelta, xBodyRotO.get(i), xBodyRotO.get(i + 1));
            else sum += Mth.lerp(tickDelta, xBodyRotO.get(i), xBodyRot);
        }
        return sum / maxXBodyRotSamples;
    }

    /// @return creates server animation processor for dragon
    @Nullable
    public DragonAnimationProcessor<? extends URDragonEntity> createServerAnimationProcessor() {
        return null;
    }

    /// @return server animation processor for dragon. If it exists, entity will be animated serverside and send transformed bones back to client
    @Nullable
    public DragonAnimationProcessor<? extends URDragonEntity> getAnimationProcessor() {
        return processor;
    }

    /// Makes dragons dance to jukebox
    protected class JukeboxEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public JukeboxEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public @NonNull PositionSource getListenerSource() {return this.positionSource;}

        public int getListenerRadius() {return this.range;}

        @Override
        public boolean handleGameEvent(@NonNull ServerLevel world, Holder<GameEvent> event, GameEvent.@NonNull Context emitter, @NonNull Vec3 emitterPos) {
            if (event.is(GameEvent.JUKEBOX_PLAY)) {
                updateJukeboxPos(BlockPos.containing(emitterPos), true);
                return true;
            } else if (event.is(GameEvent.JUKEBOX_STOP_PLAY)) {
                updateJukeboxPos(BlockPos.containing(emitterPos), false);
                return true;
            } else {
                return false;
            }
        }
    }

    /// Calls dragon upon using instrument. It's called "HornUsedEventListener" because initially it was used only for goat horns
    protected class HornUsedEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public HornUsedEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public @NonNull PositionSource getListenerSource() {return this.positionSource;}

        public int getListenerRadius() {return this.range;}

        @Override
        public boolean handleGameEvent(@NonNull ServerLevel world, @NonNull Holder<GameEvent> event, GameEvent.@NonNull Context emitter, @NonNull Vec3 emitterPos) {
            if (event != URGameEvents.INSTRUMENT_USED) return false;
            if (!(emitter.sourceEntity() instanceof Player player)) return false;
            if (getOwner() != player) return false;

            ItemStack stack = player.getMainHandItem();
            if (!stack.getComponents().has(DataComponents.INSTRUMENT)) stack = player.getOffhandItem();
            if (!stack.getComponents().has(DataComponents.INSTRUMENT)) return false;

            if (getInstrument(stack).equals(getBoundedInstrumentSound())) {
                setOrderedToSit(false);
                setTarget(null);
                shouldFollow = true;
                if (player instanceof ServerPlayer serverPlayer)
                    grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/use_horn"));
                return true;
            }
            return false;
        }
    }

    /// Handles flute commands
    protected class FluteUsedEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public FluteUsedEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public @NonNull PositionSource getListenerSource() {return this.positionSource;}

        public int getListenerRadius() {return this.range;}

        @Override
        public boolean handleGameEvent(@NonNull ServerLevel world, @NonNull Holder<GameEvent> event, GameEvent.@NonNull Context emitter, @NonNull Vec3 emitterPos) {
            if (event != URGameEvents.FLUTE_USED) return false;
            if (!(emitter.sourceEntity() instanceof Player player)) return false;
            if (getOwner() != player) return false;

            ItemStack stack = player.getMainHandItem();
            if (!stack.is(URItems.FLUTE)) stack = player.getOffhandItem();
            if (!stack.is(URItems.FLUTE)) return false;

            FluteItem.FluteMode mode = FluteItem.getFluteMode(stack);
            if (mode == null) return false;
            if (this instanceof GathererDragon gathererDragon && !URFluteModes.GATHER.equals(mode))
                gathererDragon.stopGathering();

            mode.action().run(URDragonEntity.this);

            return true;
        }
    }

    public record SoundInfo(Identifier id, float volume, float pitch, float pitchDeviation) { }

    public enum Order {
        FOLLOW,
        STAY,
        SIT;

        public static final StreamCodec<ByteBuf, Order> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);
    }

    public enum WanderRadius {
        SMALL(8),
        MEDIUM(20),
        BIG(32);

        public final int radius;

        public static final StreamCodec<ByteBuf, WanderRadius> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

        WanderRadius(int radius) {
            this.radius = radius;
        }
    }

    public enum AnimationController implements StringRepresentable {
        MAIN("main"),
        TURN("turn"),
        ATTACK("attack"),
        BLINK("blink")
        ;

        private final String name;

        public static final StreamCodec<ByteBuf, AnimationController> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

        AnimationController(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }

    public enum TurningState implements StringRepresentable {
        NONE("none"),
        LEFT("left"),
        RIGHT("right")
        ;

        private final String name;

        public static final StreamCodec<ByteBuf, TurningState> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

        TurningState(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }
}
