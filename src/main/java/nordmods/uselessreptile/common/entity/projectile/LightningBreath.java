package nordmods.uselessreptile.common.entity.projectile;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URMobEffect;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.network.s2c.SyncLightningBreathRotationsPayload;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LightningBreath extends Projectile implements ProjectileDamageHelper {
    private boolean spawnSoundPlayed = false;
    private int tickCount;
    public static final int MAX_AGE_TICKS = 10;
    public static final int MAX_LENGTH = 50;
    public float prevAlpha = 0.5f;
    public float damageScaling = 2;
    private float defaultDamage = 16;
    public final LightningBreathBolt[] lightningBreathBolts = new LightningBreathBolt[3];

    public LightningBreath(EntityType<? extends Projectile> entityType, Level world, Entity owner) {
        super(entityType, world);
        tickCount = 0;
        setOwner(owner);
    }

    public LightningBreath(EntityType<? extends Projectile> entityType, Level world) {
        this(entityType, world, null);
    }

    public LightningBreath(Level world, Entity owner) {
        this(UREntities.LIGHTNING_BREATH, world, owner);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("DamageScaling", damageScaling);
        output.putFloat("DefaultDamage", defaultDamage);
        output.putInt("Color", getColor());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        damageScaling = input.getFloatOr("DamageScaling", damageScaling);
        defaultDamage = input.getFloatOr("DefaultDamage", defaultDamage);
        setColor(input.getIntOr("Color", 0xFFFFFF));
    }

    public static final EntityDataAccessor<Integer> BEAM_LENGTH = SynchedEntityData.defineId(LightningBreath.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MAX_AGE = SynchedEntityData.defineId(LightningBreath.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(LightningBreath.class, EntityDataSerializers.INT);

    public void setBeamLength(int state) {entityData.set(BEAM_LENGTH, state);}
    public int getBeamLength() {return entityData.get(BEAM_LENGTH);}

    public void setMaxAge(int state) {entityData.set(MAX_AGE, state);}
    public int getMaxAge() {return entityData.get(MAX_AGE);}

    public void setColor(int state) {entityData.set(COLOR, state);}
    public int getColor() {return entityData.get(COLOR);}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BEAM_LENGTH, 0);
        builder.define(MAX_AGE, MAX_AGE_TICKS);
        builder.define(COLOR, 0xFFFFFF);
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!(level() instanceof ServerLevel serverWorld)) return;
        Entity target = entityHitResult.getEntity();
        DamageSource source = damageSources().source(DamageTypes.LIGHTNING_BOLT, getOwner());
        if (target instanceof LivingEntity livingEntity && livingEntity.isInvulnerableTo(serverWorld, source)) return;
        if (target.hurtServer(serverWorld, source, getResultingDamage())) {
            target.playSound(URSoundEvent.SHOCKWAVE_HIT, 1, random.nextFloat() + 1f);
            boolean wasOnFireBefore = target.isOnFire();
            LightningBolt fakeLightningSoNoNullPointerExceptionWouldHappenIHope = new LightningBolt(EntityType.LIGHTNING_BOLT, serverWorld);
            target.thunderHit(serverWorld, fakeLightningSoNoNullPointerExceptionWouldHappenIHope);
            if (!wasOnFireBefore) {
                target.setRemainingFireTicks(0);
                target.setSharedFlagOnFire(false);
            }
            if (target instanceof LivingEntity livingEntity)
                livingEntity.addEffect(new MobEffectInstance(URMobEffect.SHOCK, 400, 0, false, false), getOwner());
        }
    }

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        if (++tickCount <= getMaxAge()) {
            List<Entity> targets = level().getEntities(this, getBoundingBox(), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onHitEntity(entityHitResult);
            }

            if (getOwner() instanceof URDragonEntity dragon && !dragon.canBreakBlocks()) return;

            Iterable<BlockPos> blocks = BlockPos.withinManhattan(blockPosition(), 2, 1, 2);
            float harnessLimit = 3;
            List<FallingBlockEntity> fallingBlockEntities = new ArrayList<>();
            for (BlockPos blockPos : blocks) {
                BlockState blockState = level().getBlockState(blockPos);
                if (getOwner() instanceof URDragonEntity dragon && dragon.isBlockProtected(blockPos)) continue;
                float hardness = blockState.getDestroySpeed(level(), blockPos);
                if (hardness < 0) continue;
                if (hardness == 0 || blockState.is(URTags.LIGHTNING_BREATH_ALWAYS_BREAKS)) {
                    boolean shouldDrop = getRandom().nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                    level().destroyBlock(blockPos, shouldDrop, this);
                    continue;
                }
                harnessLimit -= hardness;
                if (harnessLimit < 0) break;
                FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level(), blockPos, blockState);
                fallingBlockEntities.add(fallingBlockEntity);
            }
            List<FallingBlockEntity> sorted = new ArrayList<>();

            while (!fallingBlockEntities.isEmpty()) {
                int maxY = -1000;
                FallingBlockEntity toAdd = null;
                for (FallingBlockEntity fallingBlockEntity : fallingBlockEntities) {
                    if (fallingBlockEntity.getBlockY() > maxY) {
                        maxY = fallingBlockEntity.getBlockY();
                        toAdd = fallingBlockEntity;
                    }
                }
                if (toAdd != null) {
                    sorted.add(toAdd);
                    fallingBlockEntities.remove(toAdd);
                }
            }

            sorted.forEach(fallingBlockEntity -> {
                Vec3 velocity = blockPosition()
                        .getCenter()
                        .subtract(fallingBlockEntity.blockPosition().getCenter())
                        .add(getRandom().nextFloat() - 0.5f, 1, getRandom().nextFloat() - 0.5f)
                        .normalize()
                        .scale(0.75);
                fallingBlockEntity.setDeltaMovement(velocity);
            });
            if (!sorted.isEmpty()) discard();
        } else discard();
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSoundEvent.SHOCKWAVE, 0.25f, 1);
            spawnSoundPlayed = true;
        }
    }

    private boolean canTarget(Entity target) {
        if (target instanceof EntityPart part) target = part.owner;
        if (target.is(URTags.DRAGON_IMMUNE)) return false;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof OwnableEntity tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return false;
        return !(target instanceof OwnableEntity tameableEntity) || tameableEntity.getOwner() != ownerOwner;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public float getDefaultDamage() {
        return defaultDamage;
    }

    @Override
    public float getDamageScaling() {
        return damageScaling;
    }

    public static void createBeam(@NonNull Entity owner, float pitch, float yaw, Vec3 startPos, int maxLength, int maxAge, float damageScaling, int color) {
        Vec3 rot = owner.calculateViewVector(pitch, yaw);
        ArrayList<Integer> ids = new ArrayList<>();
        LightningBreath firstSegment = null;
        Level world = owner.level();

        for (int i = 1; i <= maxLength; i++) {
            LightningBreath lightningBreathEntity = new LightningBreath(world, owner);
            lightningBreathEntity.setPos(startPos.add(rot.scale(i)));
            lightningBreathEntity.setDeltaMovement(Vec3.ZERO);
            lightningBreathEntity.setOwner(owner);
            lightningBreathEntity.setMaxAge(maxAge);
            lightningBreathEntity.setColor(color);
            lightningBreathEntity.damageScaling = damageScaling;
            world.addFreshEntity(lightningBreathEntity);
            if (i == 1) firstSegment = lightningBreathEntity;

            ids.add(lightningBreathEntity.getId());

            AABB box = lightningBreathEntity.getBoundingBox().contract(0.5f, 0.5f, 0.5f);
            boolean collides = BlockPos.betweenClosedStream(box).noneMatch(pos -> {
                BlockState blockState = world.getBlockState(pos);
                return blockState.is(URTags.LIGHTNING_BREATH_ALWAYS_BREAKS) || blockState.getDestroySpeed(world, pos) == 0;
            }) || !world.getEntities(lightningBreathEntity, lightningBreathEntity.getBoundingBox(), entity -> {
                LivingEntity ownerOwner = lightningBreathEntity.getOwner() instanceof OwnableEntity tameable ? tameable.getOwner() : null;
                if (entity instanceof OwnableEntity tameable && tameable.getOwner() != null && tameable.getOwner() == ownerOwner)
                    return false;
                if (owner.getControllingPassenger() == entity) return false;
                return entity instanceof LivingEntity;
            }).isEmpty();
            if (collides) break;
        }

        firstSegment.setBeamLength(ids.size());

        int[] array = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) array[i] = ids.get(i);

        if (world instanceof ServerLevel serverWorld)
            for (ServerPlayer player : PlayerLookup.tracking(serverWorld, owner.blockPosition()))
                SyncLightningBreathRotationsPayload.send(player, array, pitch, yaw);
    }

    public static class LightningBreathBolt {
        public final List<Segment> segments = new ArrayList<>();

        public static final class Segment {
            private final Vector3f startPoint;
            private final Vector3f endPoint;
            private boolean startingSegment;
            private boolean endingSegment;

            public Segment(Vector3f startPoint, Vector3f endPoint) {
                this.startPoint = startPoint;
                this.endPoint = endPoint;
            }

            public Vector3f startPoint() {
                return startPoint;
            }

            public Vector3f endPoint() {
                return endPoint;
            }

            public void markStarting() {
                startingSegment = true;
            }

            public void markEnding() {
                endingSegment = true;
            }

            public boolean isStartingSegment() {
                return startingSegment;
            }

            public boolean isEndingSegment() {
                return endingSegment;
            }
        }
    }
}
