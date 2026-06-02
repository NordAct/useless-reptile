package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public record HeadMountUseCondition(boolean isRidingPlayer) implements UseCondition{
    public static final MapCodec<HeadMountUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("is_riding_player").forGetter(HeadMountUseCondition::isRidingPlayer)
    ).apply(i, HeadMountUseCondition::new));
    @Override
    public UseConditionType<?> getType() {
        return null;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (entity instanceof HeadMountDragon headMountDragon) {
            return entity.vehicle instanceof Player;
        } else {
            throw new IllegalStateException("Cannot use " + getType().getId() + " on non HeadMountDragon entity");
        }
    }
}
