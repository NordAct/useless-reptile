package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.projectile.ShockwaveSphere;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;
import nordmods.uselessreptile.common.init.UREntities;

import java.util.List;
import java.util.Optional;

public class ShockwaveAttackAbility extends ShotAttackAbility {
    private final float maxRadius;
    private final float radiusChangeSpeed;
    private final float power;
    private final float damageScaling;
    private final int color;
    public static final MapCodec<ShockwaveAttackAbility> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonAbilityData.MAP_CODEC.forGetter(ShockwaveAttackAbility::getCommonAbilityData),
            Data.MAP_CODEC.forGetter(ShockwaveAttackAbility::getTriggerableAbilityData),
            CompoundTag.CODEC.fieldOf("projectile_entity_nbt").forGetter(c -> c.projectileEntityNbt),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("max_radius").forGetter(c -> c.maxRadius),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("radius_change_speed").forGetter(c -> c.radiusChangeSpeed),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("power").forGetter(c -> c.power),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("damage_scaling").forGetter(c -> c.damageScaling),
            Codec.INT.fieldOf("color").forGetter(c -> c.color)
    ).apply(i, ShockwaveAttackAbility::new));

    public ShockwaveAttackAbility(CommonDragonAbilityData commonAbilityData, Data triggerableAbilityData, CompoundTag projectileEntityNbt, float maxRadius, float radiusChangeSpeed, float power, float damageScaling, int color) {
        super(commonAbilityData, triggerableAbilityData, UREntities.SHOCKWAVE_SPHERE, projectileEntityNbt, AnchorPoint.ENTITY_POS, Optional.empty(), Vec3.ZERO, 1, 0, 0);
        this.maxRadius = maxRadius;
        this.radiusChangeSpeed = radiusChangeSpeed;
        this.power = power;
        this.damageScaling = damageScaling;
        this.color = color;
    }

    @Override
    public DragonAbilityType<?> getType() {
        return URDragonAbilityTypes.SHOCKWAVE_ATTACK;
    }

    @Override
    protected Vec3 getPos(URDragonEntity entity) {
        return entity.position().add(0, entity.getBbHeight()/2f, 0);
    }

    @Override
    protected Projectile createProjectile(ServerLevel level) {
        ShockwaveSphere result = (ShockwaveSphere) super.createProjectile(level);
        result.setRadiusChangeSpeed(radiusChangeSpeed);
        result.setMaxRadius(maxRadius);
        result.setColor(color);
        result.damageScaling = damageScaling;
        result.power = power;

        return result;
    }

    @Override
    public boolean canUseUncontrolled(DragonAbilityHolder holder) {
        URDragonEntity entity = holder.getEntity();
        if (!(getCommonAbilityData().conditions().controlledByRider().isEmpty() || !getCommonAbilityData().conditions().controlledByRider().get())) return false;
        if (entity.getLastAttacker() == null && entity.getTarget() == null) return false;

        boolean canUse = false;
        double attackDistance = maxRadius * maxRadius * 0.49;

        List<Entity> projectiles = entity.level().getEntities(entity, new AABB(entity.blockPosition()).inflate(Math.sqrt(attackDistance)), c -> {
            if (!(c instanceof Projectile projectile)) return false;
            if (projectile.getOwner() instanceof LivingEntity livingEntity) {
                if (!entity.canAttack(livingEntity)) return false;
            }
            return !projectile.getDeltaMovement().equals(Vec3.ZERO);
        });
        if (!projectiles.isEmpty()) canUse = true;

        if (!canUse && entity.getTarget() != null) {
            LivingEntity target = entity.getTarget();
            double targetDistance = entity.distanceToSqr(target);
            boolean isTargetExposed = ServerExplosion.getSeenPercent(entity.position(), target) < 0.1;
            canUse = attackDistance < targetDistance && isTargetExposed;
        }

        return canUse;
    }
}
