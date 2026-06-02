package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;
import nordmods.uselessreptile.common.init.URTags;

import java.util.List;

public class MeleeAttackAbility extends TriggerableAbility {
    protected final boolean aoe;
    protected final Vec3 attackBoxCenterOffset;
    protected final boolean moveBoxVertically;
    protected final float attackBoxWidth;
    protected final float attackBoxHeight;

    public static final MapCodec<MeleeAttackAbility> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonAbilityData.MAP_CODEC.forGetter(MeleeAttackAbility::getCommonAbilityData),
            TriggerableAbility.Data.MAP_CODEC.forGetter(MeleeAttackAbility::getTriggerableAbilityData),
            Codec.BOOL.fieldOf("aoe").forGetter(c -> c.aoe),
            Vec3.CODEC.fieldOf("attack_box_center_offset").forGetter(c -> c.attackBoxCenterOffset),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("attack_box_width").forGetter(c -> c.attackBoxWidth),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("attack_box_height").forGetter(c -> c.attackBoxHeight),
            Codec.BOOL.fieldOf("move_box_vertically").forGetter(c -> c.moveBoxVertically)
    ).apply(i, MeleeAttackAbility::new));

    public MeleeAttackAbility(CommonDragonAbilityData common, TriggerableAbility.Data triggerableAbilityData, boolean aoe, Vec3 attackBoxCenterOffset, float attackBoxWidth, float attackBoxHeight, boolean moveBoxVertically) {
        super(common, triggerableAbilityData);
        this.aoe = aoe;
        this.attackBoxCenterOffset = attackBoxCenterOffset;
        this.moveBoxVertically = moveBoxVertically;
        this.attackBoxWidth = attackBoxWidth;
        this.attackBoxHeight = attackBoxHeight;
    }

    @Override
    public DragonAbilityType<?> getType() {
        return URDragonAbilityTypes.MELEE_ATTACK;
    }

    @Override
    public void trigger(DragonAbilityHolder holder) {
        URDragonEntity entity = holder.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            AABB attackBox = getAttackBox(holder);
            List<Entity> list = entity.level().getEntities(
                    entity,
                    attackBox,
                    target -> !entity.getPassengers().contains(target)
                            && !target.is(entity)
                            && (target instanceof LivingEntity livingEntity && entity.canAttack(livingEntity) || !(target instanceof LivingEntity))
                            && !target.is(URTags.DRAGON_IMMUNE));

            if (aoe) {
                Entity target = null;
                if (!list.isEmpty()) {
                    target = list.getFirst();
                    for (Entity entry : list) {
                        if (entity.distanceToSqr(entry) < entity.distanceToSqr(target)) target = entry;
                    }
                }
                if (target != null && !entity.getPassengers().contains(target)) {
                    AABB targetBox = target.getBoundingBox();
                    if (targetBox.intersects(attackBox)) entity.doHurtTarget(level, target);
                }
            } else {
                if (!list.isEmpty()) for (Entity target: list) {
                    AABB targetBox = target.getBoundingBox();
                    if (targetBox.intersects(attackBox)) entity.doHurtTarget(level, target);
                }
            }
        }
    }

    @Override
    public boolean canUse(DragonAbilityHolder holder) {
        if (!holder.getEntity().hasControllingPassenger() &&
                (holder.getEntity().getTarget() == null ||
                        !holder.getEntity().getTarget().getBoundingBox().intersects(getAttackBox(holder))
                )
        ) return false;
        return super.canUse(holder);
    }

    public AABB getAttackBox(DragonAbilityHolder holder) {
        URDragonEntity entity = holder.getEntity();
        float scale = entity.getScale();
        return new AABB(
                -attackBoxWidth / 2 * scale,
                0,
                -attackBoxWidth / 2 * scale,
                attackBoxWidth / 2 * scale,
                attackBoxHeight * scale,
                attackBoxWidth / 2 * scale
        ).move(
                ShotAttackAbility.rotateVec(attackBoxCenterOffset, entity.position(), moveBoxVertically ? entity.getXRot() : 0, entity.getYRot())
        );
    }

    public int getDebugAttackBoxColor() {
        return 0xFFFF00FF;
    }
}
