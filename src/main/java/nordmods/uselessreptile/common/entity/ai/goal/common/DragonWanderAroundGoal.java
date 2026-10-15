package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.Nullable;

public class DragonWanderAroundGoal<T extends URDragonEntity> extends WaterAvoidingRandomStrollGoal {
    protected final T mob;

    public DragonWanderAroundGoal(T mob) {
        super(mob, 1);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.isDancing()) return false;
        if (mob.isOrderedToSit()) return false;
        if (mob instanceof FlyingDragon flyingDragon)
            if (flyingDragon.isFlying()) return false;
        return super.canUse();
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        Vec3 result = super.getPosition();
        if (result != null) {
            if (mob.getNavigation().createPath(result.x, result.y, result.z, 1) == null) return null;
        }
        return result;
    }
}
