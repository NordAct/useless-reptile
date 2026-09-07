package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

import java.util.EnumSet;

public class DragonLookAtEntityGoal extends LookAtPlayerGoal {
    public DragonLookAtEntityGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        super(mob, lookAtType, lookDistance);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
    }
}
