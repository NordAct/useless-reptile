package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.dragon_ability.type.MeleeAttackAbility;
import nordmods.uselessreptile.common.entity.ai.control.URDragonMoveControl;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URTags;

import java.util.EnumSet;
import java.util.Optional;

public class DragonAttackGoal extends Goal {
    protected final URDragonEntity mob;
    protected LivingEntity target;
    protected final double maxSearchDistance;
    protected int notMovingTimer = 0;

    public DragonAttackGoal(URDragonEntity mob) {
        this.mob = mob;
        this.maxSearchDistance = mob.getAttributeValue(Attributes.FOLLOW_RANGE) * mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        target = mob.getTarget();
    }

    @Override
    public boolean canUse() {
        if (mob.hasControllingPassenger()) return false;
        if (!mob.canAttack(mob.getTarget())) {
            mob.setTarget(null);
            return false;
        }
        target = mob.getTarget();
        return target != null && (mob.distanceToSqr(target) < maxSearchDistance);
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (!target.isAlive() || target.isRemoved()) return false;
        return super.canContinueToUse();
    }

    @Override
    public void stop() {
        target = null;
        mob.setTarget(null);
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() { //todo make attack anims synced with client
        tickMovement();
        if (!mob.isMoving()) notMovingTimer++;

        Optional<DragonAbilityHolder> holder = mob.getAvailableAbilities()
                .stream()
                .filter(a -> a.getAbility().is(URTags.ATTACK) && a.getAbility().canUse(a))
                .findAny();
        if (holder.isPresent()) {
            holder.get().use();
            return;
        }

        holder = mob.getAvailableAbilities()
                .stream()
                .filter(a -> a.getAbility().is(URTags.DEFENCE) && a.getAbility().canUse(a))
                .findAny();

        if (holder.isPresent()) {
            holder.get().use();
            return;
        }

        holder = mob.getAvailableAbilities()
                .stream()
                .filter(a -> a.getCooldown() <= 0)
                .findAny();
        if (holder.isPresent()) {
            DragonAbilityHolder dragonAbilityHolder = holder.get();
            if (dragonAbilityHolder.getAbility() instanceof MeleeAttackAbility meleeAttackAbility) {
                double attackDistance = meleeAttackAbility.getAttackBox(dragonAbilityHolder).getCenter().distanceToSqr(mob.position());
                double distance = mob.distanceToSqr(target);
                if (attackDistance > distance && mob.getMoveControl() instanceof URDragonMoveControl control) control.moveBack();
            }
        }


        if (notMovingTimer > 60 && mob.canBreakBlocks()) {
            holder = mob.getAvailableAbilities()
                    .stream()
                    .filter(a -> a.getAbility().is(URTags.BREAKS_BLOCKS) && a.getCooldown() <= 0)
                    .findAny();

            if (holder.isPresent()) {
                holder.get().use();
                notMovingTimer = 0;
                return;
            }
        }
    }

    protected void tickMovement() {
        mob.setSprinting(true);
        mob.getNavigation().moveTo(target, 1);
        mob.getLookControl().setLookAt(target);
    }
}