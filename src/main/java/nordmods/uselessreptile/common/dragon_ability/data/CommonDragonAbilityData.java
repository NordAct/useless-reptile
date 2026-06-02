package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.util.URDragonAnimationController;

import java.util.List;
import java.util.Optional;

public record CommonDragonAbilityData(
        float cooldownTimeSeconds,
        boolean blockOtherAbilitiesIfActive,
        List<ConditionedAnimation> animations,
        List<UseCondition> conditions,
        Optional<URRideableDragonEntity.AttackType> attackType
) {
    public static final MapCodec<CommonDragonAbilityData> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("cooldown_time_seconds").forGetter(CommonDragonAbilityData::cooldownTimeSeconds),
            Codec.BOOL.fieldOf("block_other_abilities_if_active").forGetter(CommonDragonAbilityData::blockOtherAbilitiesIfActive),
            ConditionedAnimation.CODEC.listOf().fieldOf("animations").forGetter(CommonDragonAbilityData::animations),
            UseCondition.CODEC.listOf().fieldOf("conditions").forGetter(CommonDragonAbilityData::conditions),
            StringRepresentable.fromEnum(URRideableDragonEntity.AttackType::values).optionalFieldOf("attack_type").forGetter(CommonDragonAbilityData::attackType)
    ).apply(i, CommonDragonAbilityData::new));
    public static final Codec<CommonDragonAbilityData> CODEC = MAP_CODEC.codec();

    public record ConditionedAnimation(
            URDragonEntity.AnimationController controller,
            List<String> animations,
            boolean canInterruptPlayingAbilityAnimation,
            List<UseCondition> conditions
    ) {
        public static final Codec<ConditionedAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
                StringRepresentable.fromEnum(URDragonEntity.AnimationController::values).fieldOf("controller").forGetter(ConditionedAnimation::controller),
                Codec.STRING.listOf().fieldOf("animations").forGetter(ConditionedAnimation::animations),
                Codec.BOOL.fieldOf("can_interrupt_playing_ability_animation").forGetter(ConditionedAnimation::canInterruptPlayingAbilityAnimation),
                UseCondition.CODEC.listOf().fieldOf("conditions").forGetter(ConditionedAnimation::conditions)
        ).apply(i, ConditionedAnimation::new));

        public boolean tryPlay(URDragonEntity entity) {
            if (animations.isEmpty()) return false;
            String animationToPlay = animations.get(entity.getRandom().nextInt(animations.size()));
            if (!canInterruptPlayingAbilityAnimation) {
                URDragonAnimationController<?> animationController = entity.getAnimationController(controller);
                for (DragonAbilityHolder abilityHolder : entity.getAbilityHolders().values()) {
                    if (abilityHolder.getAbility().getCommonAbilityData().animations().isEmpty()) continue;
                    if (!abilityHolder.getAbility().isActive(abilityHolder)) continue;
                    for (ConditionedAnimation animation : abilityHolder.getAbility().getCommonAbilityData().animations()) {
                        if (animation.controller != controller) continue;
                        for (String abilityAnimation : animation.animations()){
                            BRPlayingAnimation playingAnimation = animationController.getAnimation(abilityAnimation);
                            if (playingAnimation != null && !playingAnimation.isTransitioningOut()) return false;
                        }
                    }
                }
            }
            if (!conditions().isEmpty() && !conditions.stream().allMatch(c -> c.test(entity))) return false;
            entity.getAnimationController(controller).playAnimation(animationToPlay);
            return true;
        }
    }

}
