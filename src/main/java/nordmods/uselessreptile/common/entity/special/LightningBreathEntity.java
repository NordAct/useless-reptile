package nordmods.uselessreptile.common.entity.special;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.CommonProtection;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URTags;

import java.util.List;

public class LightningBreathEntity extends ProjectileEntity {
    private boolean spawnSoundPlayed = false;
    private int age;

    public LightningBreathEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        age = 0;
    }

    public LightningBreathEntity(World world) {
        this(UREntities.LIGHTNING_BREATH_ENTITY, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity target = entityHitResult.getEntity();
        if (target.damage(getDamageSources().create(DamageTypes.LIGHTNING_BOLT, getOwner()), 6f))
            target.playSound(URSounds.SHOCKWAVE_HIT, 1, random.nextFloat() + 1f);
    }

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        if (++age <= 20) {
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox(), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onEntityHit(entityHitResult);
            }

            boolean shouldBreakBlocks = getOwner() instanceof URDragonEntity dragon && dragon.isTamed() ?
                    URConfig.getConfig().allowDragonGriefing.canTamedBreak() : URConfig.getConfig().allowDragonGriefing.canUntamedBreak();
            if (getWorld().isClient() || !(shouldBreakBlocks && getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))) return;

            if (getWorld().isBlockSpaceEmpty(this, getBoundingBox())) return;
            Iterable<BlockPos> blocks = BlockPos.iterateOutwards(getBlockPos(), 3, 2, 3);
            float harnessLimit = 30;
            float blockBreakLimit = 5;
            for (BlockPos blockPos : blocks) {
                BlockState blockState = getWorld().getBlockState(blockPos);
                PlayerEntity rider = getOwner() instanceof URRideableDragonEntity dragon && dragon.canBeControlledByRider() ?
                        (PlayerEntity) getControllingPassenger() : null;
                GameProfile playerId = rider != null ? rider.getGameProfile() : CommonProtection.UNKNOWN;
                if (blockState.isIn(URTags.DRAGON_UNBREAKABLE) || !CommonProtection.canBreakBlock(getWorld(), blockPos, playerId, rider)) continue;

                float hardness =  blockState.getHardness(getWorld(), blockPos);
                if (hardness < 0) continue;
                harnessLimit -= hardness;
                if (harnessLimit < 0) break;
                if (harnessLimit > 20 && --blockBreakLimit > 0) {
                    boolean shouldDrop = random.nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                    getWorld().breakBlock(blockPos, shouldDrop, this);
                } else {
                    FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(getWorld(), blockPos, blockState);
                    Vec3d velocity = getBlockPos().toCenterPos().subtract(blockPos.toCenterPos()).add(0, 1, 0).normalize().multiply(harnessLimit/30);
                    fallingBlockEntity.setVelocity(velocity);
                }

                discard();
            }
        } else discard();
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSounds.SHOCKWAVE, 1, 1);
            spawnSoundPlayed = true;
        }
    }

    private boolean canTarget(Entity target) {
        if (target.isInvulnerableTo(getDamageSources().create(DamageTypes.LIGHTNING_BOLT))) return false;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof TameableEntity tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return false;
        if (target instanceof TameableEntity tameableEntity && tameableEntity.getOwner() == ownerOwner) return false;

        return true;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}
