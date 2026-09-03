package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.data.UseCondition;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MeleeAttackAbility extends TriggerableAbility {
    protected final boolean aoe;
    protected final List<MobEffectInstance> attackEffects;
    protected final boolean setOnFire;
    protected final List<ConditionedAttackBox> attackBox;

    public static final MapCodec<MeleeAttackAbility> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonAbilityData.MAP_CODEC.forGetter(MeleeAttackAbility::getCommonAbilityData),
            TriggerableAbility.Data.MAP_CODEC.forGetter(MeleeAttackAbility::getTriggerableAbilityData),
            Codec.BOOL.fieldOf("aoe").forGetter(c -> c.aoe),
            MobEffectInstance.CODEC.listOf().fieldOf("attack_effects").forGetter(c -> c.attackEffects),
            Codec.BOOL.fieldOf("set_on_fire").forGetter(c -> c.setOnFire),
            ConditionedAttackBox.CODEC.listOf().fieldOf("attack_box").forGetter(c -> c.attackBox)
    ).apply(i, MeleeAttackAbility::new));

    public MeleeAttackAbility(CommonDragonAbilityData common, TriggerableAbility.Data triggerableAbilityData, boolean aoe, List<MobEffectInstance> attackEffects, boolean setOnFire, List<ConditionedAttackBox> attackBox) {
        super(common, triggerableAbilityData);
        this.aoe = aoe;
        this.attackEffects = attackEffects;
        this.setOnFire = setOnFire;
        this.attackBox = attackBox;
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
                if (!list.isEmpty()) for (Entity target: list) {
                    AABB targetBox = target.getBoundingBox();
                    if (targetBox.intersects(attackBox) && entity.doHurtTarget(level, target)) {
                        if (setOnFire) target.igniteForSeconds((float) (0.5f * entity.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                        if (target instanceof LivingEntity livingEntity) attackEffects.forEach(e -> livingEntity.addEffect(new MobEffectInstance(e)));
                    }
                }
            } else {
                Entity target = null;
                if (!list.isEmpty()) {
                    target = list.getFirst();
                    for (Entity entry : list) {
                        if (entity.distanceToSqr(entry) < entity.distanceToSqr(target)) target = entry;
                    }
                }
                if (target != null && !entity.getPassengers().contains(target)) {
                    AABB targetBox = target.getBoundingBox();
                    if (targetBox.intersects(attackBox) && entity.doHurtTarget(level, target)) {
                        if (setOnFire) target.igniteForSeconds((float) (0.5f * entity.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                        if (target instanceof LivingEntity livingEntity) attackEffects.forEach(e -> livingEntity.addEffect(new MobEffectInstance(e)));
                    }
                }
            }
        }
    }



    @Override
    public boolean canUseUncontrolled(DragonAbilityHolder holder) {
        if (!holder.getEntity().hasControllingPassenger() &&
                (holder.getEntity().getTarget() == null ||
                        !holder.getEntity().getTarget().getBoundingBox().intersects(getAttackBox(holder))
                )
        ) return false;
        return super.canUseUncontrolled(holder);
    }

    public AABB getAttackBox(DragonAbilityHolder holder) {
        URDragonEntity entity = holder.getEntity();
        ConditionedAttackBox box = null;
        for (int i = 0; i < attackBox.size(); i++) {
            ConditionedAttackBox temp = attackBox.get(i);
            if (UseCondition.testAll(temp.conditions, holder.getEntity())) {
                box = attackBox.get(i);
                break;
            }
        }

        if (box == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("Could not find any attack box for ");
            builder.append(getType().getId());
            builder.append(" of dragon ").append(entity.getName().getString());
            builder.append(" (").append(entity.getDragonId()).append(" of variant ").append(entity.getVariant()).append(")");
            builder.append(" in its current state");
            throw new IllegalStateException(builder.toString());
        }

        float scale = entity.getScale();
        Vec3 offset = switch (box.verticalBoxMovement) {
            case NONE -> ShotAttackAbility.rotateVec(box.centerOffset, entity.position(), 0, entity.getYRot());
            case SMOOTH -> ShotAttackAbility.rotateVec(box.centerOffset, entity.position(), entity.getXRot(), entity.getYRot());
            case SNAPPED -> {
                float y = 0;
                if (entity.getXRot() > 25) y = -box.height/4f;
                if (entity.getXRot() < -25) y = box.height/4f;
                yield ShotAttackAbility.rotateVec(box.centerOffset, entity.position(), 0, entity.getYRot()).add(0, y, 0);
            }
        };
        return new AABB(
                -box.width / 2 * scale,
                0,
                -box.width / 2 * scale,
                box.width / 2 * scale,
                box.height * scale,
                box.width / 2 * scale
        ).move(offset);
    }

    public int getDebugAttackBoxColor() {
        return 0xFFFF00FF;
    }

    public record ConditionedAttackBox(
            float width,
            float height,
            Vec3 centerOffset,
            VerticalAttackBoxMovement verticalBoxMovement,
            List<UseCondition> conditions
            ) {
        public static final Codec<ConditionedAttackBox> CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("width").forGetter(c -> c.width),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("height").forGetter(c -> c.height),
                Vec3.CODEC.fieldOf("center_offset").forGetter(c -> c.centerOffset),
                StringRepresentable.fromEnum(ConditionedAttackBox.VerticalAttackBoxMovement::values).fieldOf("vertical_box_movement").forGetter(c -> c.verticalBoxMovement),
                UseCondition.CODEC.listOf().fieldOf("conditions").forGetter(c -> c.conditions)
        ).apply(i, ConditionedAttackBox::new));

        public enum VerticalAttackBoxMovement implements StringRepresentable{
            NONE("none"),
            SNAPPED("snapped"),
            SMOOTH("smooth")
            ;

            private final String name;

            VerticalAttackBoxMovement(String name) {
                this.name = name;
            }

            @Override
            public @NonNull String getSerializedName() {
                return name;
            }
        }
    }
}
