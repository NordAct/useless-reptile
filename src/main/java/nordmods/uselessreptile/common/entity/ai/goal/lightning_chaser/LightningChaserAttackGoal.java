package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.dragon_ability.type.ShotAttackAbility;
import nordmods.uselessreptile.common.entity.LightningChaser;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonAttackGoal;
import nordmods.uselessreptile.common.init.URTags;

import java.util.Optional;

public class LightningChaserAttackGoal extends DragonAttackGoal { //todo test it in combat properly
    private final LightningChaser mob;

    public LightningChaserAttackGoal(LightningChaser mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.hasSurrendered() || mob.getShouldBailOut()) return false;
        return super.canUse();
    }

    @Override
    protected void tickMovement() {
        mob.setSprinting(true);

        double distance = mob.distanceToSqr(target);
        double yDiff = target.getY() - mob.getY();
        if (yDiff > mob.getBbHeight() && !mob.isFlying()) mob.startToFly();
        Optional<ShotAttackAbility> shotAttackAbility = mob.getAvailableAbilities().stream()
                .filter(a -> a.getAbility().is(URTags.RANGED_ATTACK) && a.getAbility().is(URTags.BREAKS_BLOCKS) && a.getAbility() instanceof ShotAttackAbility)
                .map(a -> (ShotAttackAbility)a.getAbility())
                .findFirst();
        if (shotAttackAbility.isEmpty()) {
            shotAttackAbility = mob.getAvailableAbilities().stream()
                    .filter(a -> a.getAbility().is(URTags.RANGED_ATTACK) && a.getAbility() instanceof ShotAttackAbility)
                    .map(a -> (ShotAttackAbility)a.getAbility())
                    .findFirst();
        }

        boolean hasRangeAttack = shotAttackAbility.isPresent();
        boolean canUseRangedAttack = hasRangeAttack && (mob.canGrief() && shotAttackAbility.get().is(URTags.BREAKS_BLOCKS) || mob.hasLineOfSight(target));
        double desiredY = target.getY() + (canUseRangedAttack ? 2 : 0) + target.getBbHeight();
        if (mob.onGround() && !mob.getSensing().hasLineOfSight(target) && hasRangeAttack) mob.forceFlightNextTick();
        mob.getLookControl().setLookAt(target);
        double minAttackDistance = hasRangeAttack ? shotAttackAbility.get().getAiAttackDistance() * 0.3 : 0;
        double maxAttackDistance = hasRangeAttack ? shotAttackAbility.get().getAiAttackDistance() * 0.9 : 0;

        if (canUseRangedAttack && distance < minAttackDistance) { //too close
            mob.getNavigation().stop();
            getMoveControl().moveBack();
            if (target instanceof Player player && yDiff < player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
                    || target instanceof Mob mob && mob.isWithinMeleeAttackRange(this.mob)
                    || mob.getLastAttacker() instanceof Mob attacker && attacker.isWithinMeleeAttackRange(mob)) {
                if (!mob.isFlying()) { //try jump/back off
                    mob.forceFlightNextTick();
                    Vec3 vec3d = mob.calculateViewVector(Mth.clamp(-mob.getXRot(), -10, 10), mob.getYRot() - 180);
                    mob.push(vec3d.scale(2));
                } else {
                    getMoveControl().forceFlyUp();
                    getMoveControl().moveBack();
                }
            }
        } else if (canUseRangedAttack && distance < maxAttackDistance) { //within range
            if (!mob.getLookControl().canLookAtTarget()) {
                double distanceXZ = Math.pow(target.getX() - mob.getX(), 2) * Math.pow(target.getZ() - mob.getZ(), 2);
                double divergence = Math.max(0, (distanceXZ - maxAttackDistance / 8f) * 0.25);
                if (distanceXZ < maxAttackDistance / 8f) getMoveControl().moveBack();
                else if (desiredY + divergence < mob.getY() || desiredY - divergence > mob.getY()) getMoveControl().moveBack();
                else {
                    if (yDiff > mob.getBbHeight()) getMoveControl().forceFlyUp();
                    else if (yDiff < -mob.getBbHeight()) getMoveControl().forceFlyDown();
                }
            } else {//try compensate momentum
                if (mob.getDeltaMovement().y > 0) getMoveControl().forceFlyDown();
                else if (mob.getDeltaMovement().y < 0) getMoveControl().forceFlyUp();
            }
        } else mob.getNavigation().moveTo(target.getX(), desiredY, target.getZ(), 1); //out of reach/can't be damaged by range attack
    }

    private FlyingDragonMoveControl<LightningChaser> getMoveControl() {
        return (FlyingDragonMoveControl<LightningChaser>) mob.getMoveControl();
    }
}
