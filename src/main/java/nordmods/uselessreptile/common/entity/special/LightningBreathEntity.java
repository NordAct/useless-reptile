package nordmods.uselessreptile.common.entity.special;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.network.SyncLightningBreathRotationsS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightningBreathEntity extends ProjectileEntity implements ProjectileDamageHelper {
    private boolean spawnSoundPlayed = false;
    private int age;
    public static final int MAX_AGE = 10;
    public static final int MAX_LENGTH = 50;
    public float prevAlpha = 0.5f;
    public final LightningBreathBolt[] lightningBreathBolts = new LightningBreathBolt[5];

    public LightningBreathEntity(EntityType<? extends ProjectileEntity> entityType, World world, Entity owner) {
        super(entityType, world);
        age = 0;
        setOwner(owner);
    }

    public LightningBreathEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        this(entityType, world, null);
    }

    public LightningBreathEntity(World world, Entity owner) {
        this(UREntities.LIGHTNING_BREATH_ENTITY, world, owner);
    }

    public static final TrackedData<Integer> BEAM_LENGTH = DataTracker.registerData(LightningBreathEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public void setBeamLength(int state) {dataTracker.set(BEAM_LENGTH, state);}
    public int getBeamLength() {return dataTracker.get(BEAM_LENGTH);}

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BEAM_LENGTH, 0);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!(getWorld() instanceof ServerWorld serverWorld)) return;
        Entity target = entityHitResult.getEntity();
        DamageSource source = getDamageSources().create(DamageTypes.LIGHTNING_BOLT, getOwner());
        if (target instanceof LivingEntity livingEntity && livingEntity.isInvulnerableTo(serverWorld, source)) return;
        if (target.damage(serverWorld, source, getResultingDamage())) {
            target.playSound(URSounds.SHOCKWAVE_HIT, 1, random.nextFloat() + 1f);
            boolean wasOnFireBefore = target.isOnFire();
            LightningEntity fakeLightningSoINoNullPointerExceptionWouldHappenIHope = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
            target.onStruckByLightning(serverWorld, fakeLightningSoINoNullPointerExceptionWouldHappenIHope);
            if (!wasOnFireBefore) {
                target.setFireTicks(0);
                target.setOnFire(false);
            }
            if (target instanceof LivingEntity livingEntity)
                livingEntity.addStatusEffect(new StatusEffectInstance(URStatusEffects.SHOCK, 400, 0, false, false), getOwner());
        }
    }

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        if (++age <= MAX_AGE) {
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox(), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onEntityHit(entityHitResult);
            }

            if (getOwner() instanceof URDragonEntity dragon && !dragon.canBreakBlocks()) return;

            Iterable<BlockPos> blocks = BlockPos.iterateOutwards(getBlockPos(), 2, 1, 2);
            float harnessLimit = 3;
            List<FallingBlockEntity> fallingBlockEntities = new ArrayList<>();
            for (BlockPos blockPos : blocks) {
                BlockState blockState = getWorld().getBlockState(blockPos);
                if (getOwner() instanceof URDragonEntity dragon && dragon.isBlockProtected(blockPos)) continue;
                float hardness = blockState.getHardness(getWorld(), blockPos);
                if (hardness < 0) continue;
                if (hardness == 0 || blockState.isIn(URTags.LIGHTNING_BREATH_ALWAYS_BREAKS)) {
                    boolean shouldDrop = getRandom().nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                    getWorld().breakBlock(blockPos, shouldDrop, this);
                    continue;
                }
                harnessLimit -= hardness;
                if (harnessLimit < 0) break;
                FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(getWorld(), blockPos, blockState);
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
                Vec3d velocity = getBlockPos()
                        .toCenterPos()
                        .subtract(fallingBlockEntity.getBlockPos().toCenterPos())
                        .add(getRandom().nextFloat() - 0.5f, 1, getRandom().nextFloat() - 0.5f)
                        .normalize()
                        .multiply(0.75);
                fallingBlockEntity.setVelocity(velocity);
            });
            if (!sorted.isEmpty()) discard();
        } else discard();
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSounds.SHOCKWAVE, 0.25f, 1);
            spawnSoundPlayed = true;
        }
    }

    private boolean canTarget(Entity target) {
        if (target instanceof EntityPart part) target = part.owner;
        if (target.getType().isIn(URTags.DRAGON_IMMUNE)) return false;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof Tameable tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return false;
        if (target instanceof Tameable tameableEntity && tameableEntity.getOwner() == ownerOwner) return false;

        return true;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public float getDefaultDamage() {
        return 16;
    }

    @Override
    public float getDamageScaling() {
        return 2;
    }

    public static void createBeam(@NotNull Entity owner, float pitch, float yaw, Vec3d startPos) {
        Vec3d rot = owner.getRotationVector(pitch, yaw);
        ArrayList<Integer> ids = new ArrayList<>();
        LightningBreathEntity firstSegment = null;
        World world = owner.getWorld();

        for (int i = 1; i <= LightningBreathEntity.MAX_LENGTH; i++) {
            LightningBreathEntity lightningBreathEntity = new LightningBreathEntity(world, owner);
            lightningBreathEntity.setPosition(startPos.add(rot.multiply(i)));
            lightningBreathEntity.setVelocity(Vec3d.ZERO);
            lightningBreathEntity.setOwner(owner);
            world.spawnEntity(lightningBreathEntity);
            if (i == 1) firstSegment = lightningBreathEntity;

            ids.add(lightningBreathEntity.getId());

            Box box = lightningBreathEntity.getBoundingBox().shrink(0.5f, 0.5f, 0.5f);
            boolean collides = BlockPos.stream(box).noneMatch(pos -> {
                BlockState blockState = world.getBlockState(pos);
                return blockState.isIn(URTags.LIGHTNING_BREATH_ALWAYS_BREAKS) || blockState.getHardness(world, pos) == 0;
            }) || !world.getOtherEntities(lightningBreathEntity, lightningBreathEntity.getBoundingBox(), entity -> {
                LivingEntity ownerOwner = lightningBreathEntity.getOwner() instanceof Tameable tameable ? tameable.getOwner() : null;
                if (entity instanceof Tameable tameable && tameable.getOwner() != null && tameable.getOwner() == ownerOwner)
                    return false;
                if (owner.getControllingPassenger() == entity) return false;
                return entity instanceof LivingEntity;
            }).isEmpty();
            if (collides) break;
        }

        firstSegment.setBeamLength(ids.size());

        int[] array = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) array[i] = ids.get(i);

        if (world instanceof ServerWorld serverWorld)
            for (ServerPlayerEntity player : PlayerLookup.tracking(serverWorld, owner.getBlockPos()))
                SyncLightningBreathRotationsS2CPacket.send(player, array, pitch, yaw);
    }

    public static class LightningBreathBolt {
        public final List<Segment> segments = new ArrayList<>();

        public record Segment (Vector3f startPoint, Vector3f endPoint) {}
    }
}
