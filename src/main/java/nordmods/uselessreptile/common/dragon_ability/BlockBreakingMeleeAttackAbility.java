package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.event.DragonGetBlockMiningLevelEvent;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.List;

public class BlockBreakingMeleeAttackAbility extends MeleeAttackAbility{
    public static final MapCodec<BlockBreakingMeleeAttackAbility> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonAbilityData.MAP_CODEC.forGetter(MeleeAttackAbility::getCommonAbilityData),
            TriggerableAbility.Data.MAP_CODEC.forGetter(MeleeAttackAbility::getTriggerableAbilityData),
            Codec.BOOL.fieldOf("aoe").forGetter(c -> c.aoe),
            MobEffectInstance.CODEC.listOf().fieldOf("attack_effects").forGetter(c -> c.attackEffects),
            Codec.BOOL.fieldOf("set_on_fire").forGetter(c -> c.setOnFire),
            ConditionedAttackBox.CODEC.listOf().fieldOf("attack_box").forGetter(c -> c.attackBox)
    ).apply(i, BlockBreakingMeleeAttackAbility::new));

    public BlockBreakingMeleeAttackAbility(CommonDragonAbilityData common, TriggerableAbility.Data triggerableAbilityData, boolean aoe, List<MobEffectInstance> attackEffects, boolean setOnFire, List<ConditionedAttackBox> attackBox) {
        super(common, triggerableAbilityData, aoe, attackEffects, setOnFire, attackBox);
    }


    @Override
    public void trigger(DragonAbilityHolder holder) {
        super.trigger(holder);
        URDragonEntity entity = holder.getEntity();
        if (!entity.canBreakBlocks()) return;

        Iterable<BlockPos> blocks = BlockPos.betweenClosed(getAttackBox(holder));
        float maxMiningLevel = (float) entity.getAttributeValue(URAttributes.DRAGON_MINING_LEVEL);
        if (entity.hasEffect(MobEffects.STRENGTH)) maxMiningLevel += entity.getEffect(MobEffects.STRENGTH).getAmplifier() + 1;
        if (entity.hasEffect(MobEffects.WEAKNESS)) maxMiningLevel -= entity.getEffect(MobEffects.WEAKNESS).getAmplifier() + 1;
        for (BlockPos blockPos : blocks) {
            if (entity.isBlockProtected(blockPos)) continue;

            BlockState blockState = entity.level().getBlockState(blockPos);
            if (blockState.getBlock().defaultDestroyTime() < 0) continue;

            float miningLevel = DragonGetBlockMiningLevelEvent.EVENT.invoker().getMiningLevel(blockState);
            if (!blockState.isAir() && miningLevel <= maxMiningLevel) {
                boolean shouldDrop = entity.getRandom().nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                entity.level().destroyBlock(blockPos, shouldDrop, entity);
            }
        }
    }

    @Override
    public DragonAbilityType<?> getType() {
        return URDragonAbilityTypes.BLOCK_BREAKING_MELEE_ATTACK_ABILITY;
    }

    @Override
    public int getDebugAttackBoxColor() {
        return 0xFFFF000F;
    }
}
