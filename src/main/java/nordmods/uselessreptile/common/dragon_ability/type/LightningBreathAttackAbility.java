package nordmods.uselessreptile.common.dragon_ability.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.dragon_ability.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.projectile.LightningBreath;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;
import nordmods.uselessreptile.common.init.UREntities;

import java.util.Optional;

public class LightningBreathAttackAbility extends ShotAttackAbility {
    private final float damageScaling;
    private final int color;
    private final int maxLength;
    private final int maxAge;
    private final int blockBreakPower;
    public static final MapCodec<LightningBreathAttackAbility> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonAbilityData.MAP_CODEC.forGetter(LightningBreathAttackAbility::getCommonAbilityData),
            Data.MAP_CODEC.forGetter(LightningBreathAttackAbility::getTriggerableAbilityData),
            StringRepresentable.fromEnum(AnchorPoint::values).fieldOf("anchor_point").forGetter(c -> c.anchorPoint),
            Codec.STRING.optionalFieldOf("multipart_box_name").forGetter(c -> c.multipartBoxName),
            Vec3.CODEC.fieldOf("anchor_point_offset").forGetter(c -> c.anchorPointOffset),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("damage_scaling").forGetter(c -> c.damageScaling),
            Codec.INT.fieldOf("color").forGetter(c -> c.color),
            ExtraCodecs.POSITIVE_INT.fieldOf("max_length").forGetter(c -> c.maxLength),
            ExtraCodecs.POSITIVE_INT.fieldOf("max_age").forGetter(c -> c.maxAge),
            ExtraCodecs.POSITIVE_INT.fieldOf("block_break_power").forGetter(c -> c.blockBreakPower)
    ).apply(i, LightningBreathAttackAbility::new));

    public LightningBreathAttackAbility(CommonDragonAbilityData commonAbilityData, Data triggerableAbilityData, AnchorPoint anchorPoint, Optional<String> multipartBoxName, Vec3 anchorPointOffset, float damageScaling, int color, int maxLength, int maxAge, int blockBreakPower) {
        super(commonAbilityData, triggerableAbilityData, UREntities.LIGHTNING_BREATH, new CompoundTag(), anchorPoint, multipartBoxName, anchorPointOffset, 1, 0, 0, (int)(maxLength * 0.95f));
        this.damageScaling = damageScaling;
        this.color = color;
        this.maxLength = maxLength;
        this.maxAge = maxAge;
        this.blockBreakPower = blockBreakPower;
    }

    @Override
    public void trigger(DragonAbilityHolder holder) {
        URDragonEntity entity = holder.getEntity();
        float pitch = entity.getXRot();
        float yaw =  entity.getYHeadRot();
        Vec3 pos = getPos(entity);

        if (entity.getControllingPassenger() == null && anchorPoint != AnchorPoint.EYES) {
            Vec3 rot = new Vec3(
                    entity.getLookControl().getWantedX(),
                    entity.getLookControl().getWantedY(),
                    entity.getLookControl().getWantedZ()
            ).subtract(pos).normalize();
            Vec2 rad = rot.rotation();
            pitch = rad.x;
            yaw = rad.y;
        }

        LightningBreath.createBeam(
                entity,
                pitch,
                yaw,
                pos,
                maxLength,
                maxAge,
                damageScaling,
                color,
                blockBreakPower
        );
    }

    @Override
    protected boolean isLookingAtTarget(DragonAbilityHolder holder) {
        return holder.getEntity().canGrief() ? holder.getEntity().getLookControl().isLookingAtTarget() : super.isLookingAtTarget(holder);
    }

    @Override
    public DragonAbilityType<?> getType() {
        return URDragonAbilityTypes.LIGHTNING_BREATH_ATTACK;
    }
}
