package nordmods.uselessreptile.common.entity.projectile;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import org.jspecify.annotations.NonNull;

//I really REALLY wish Mojang separated AbstractArrow's movement and collision from behaviour of being pickable item
public abstract class URMovingProjectile extends AbstractArrow {
    protected int lifeLimit = -1;
    private int life;
    protected URMovingProjectile(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void tickDespawn() {
        if (lifeLimit == -1) return;
        ++life;
        if (life >= lifeLimit) {
            discard();
        }
    }

    @Override
    protected boolean canHitEntity(@NonNull Entity entity) {
        if (entity instanceof EntityPart entityPart && entityPart.owner == getOwner()) return false;
        return super.canHitEntity(entity);
    }

    @Override
    protected @NonNull ItemStack getDefaultPickupItem() { //this is a pain in my ass
        return Items.BROWN_DYE.getDefaultInstance();
    }

    @Override
    public void playSound(@NonNull SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) level().playLocalSound(getX(), getY(),getZ(), sound, getOwner() != null ? getOwner().getSoundSource() : SoundSource.NEUTRAL, volume, pitch,true);
    }


    @Override
    public void tick() {
        super.tick();
        tickDespawn();
    }

    public void spawnEffectParticles(int amount, int color) {
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        for (int j = 0; j < amount; ++j) {
            level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, r, g, b), getRandomX(0.5), getRandomY(), getRandomZ(0.5), r, g, b);
        }
    }
}
