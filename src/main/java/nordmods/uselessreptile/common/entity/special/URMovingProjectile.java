package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;

//I really REALLY wish Mojang separated PersistentProjectileEntity's movement and collision from behaviour of being pickable item
public abstract class URMovingProjectile extends PersistentProjectileEntity {
    protected int lifeLimit = -1;
    private int life;
    protected URMovingProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void age() {
        if (lifeLimit == -1) return;
        ++life;
        if (life >= lifeLimit) {
            discard();
        }
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof EntityPart entityPart && entityPart.owner == getOwner()) return false;
        return super.canHit(entity);
    }

    @Override
    protected ItemStack getDefaultItemStack() { //this is a pain in my ass
        return Items.BROWN_DYE.getDefaultStack();
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) getEntityWorld().playSoundClient(getX(), getY(),getZ(), sound, getOwner() != null ? getOwner().getSoundCategory() : SoundCategory.NEUTRAL, volume, pitch,true);
    }


    @Override
    public void tick() {
        super.tick();
        age();
    }

    public void spawnEffectParticles(int amount, int color) {
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        for (int j = 0; j < amount; ++j) {
            getEntityWorld().addParticleClient(TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), getParticleX(0.5), getRandomBodyY(), getParticleZ(0.5), r, g, b);
        }
    }
}
