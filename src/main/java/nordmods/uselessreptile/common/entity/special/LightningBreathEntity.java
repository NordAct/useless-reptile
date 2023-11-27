package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;

import java.util.List;

public class LightningBreathEntity extends ProjectileEntity {
    private boolean spawnSoundPlayed = false;
    private int age;
    private final int maxAge = 40;
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
        target.damage(getDamageSources().create(DamageTypes.LIGHTNING_BOLT, getOwner()), 3f);
        target.playSound(URSounds.SHOCKWAVE_HIT, 1, random.nextFloat() + 1f);
    }

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        if (++age <= maxAge) {
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox(), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onEntityHit(entityHitResult);
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
}
